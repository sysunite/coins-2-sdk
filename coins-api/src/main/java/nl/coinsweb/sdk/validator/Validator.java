/**
 * MIT License
 *
 * Copyright (c) 2016 Bouw Informatie Raad
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 *
 **/
package nl.coinsweb.sdk.validator;


import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import nl.coinsweb.sdk.CoinsModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class Validator {

  private static final Logger log = LoggerFactory.getLogger(Validator.class);

  private HashMap<String, Profile> profiles;

  private ValidationExecutor executor;
  private CoinsModel model;
  private String selectedProfile;



  public Validator(CoinsModel model, ValidationExecutor executor, String profileName) {
    this.model = model;
    this.executor = executor;
    this.selectedProfile = profileName;

    ResourceScanner scanner = new ResourceScanner();
    this.profiles = scanner.getProfileFiles("validator/queries/");
  }

  public Set<String> listProfiles() {
    return profiles.keySet();
  }

  public void loadProfile(String profileName, InputStream stream) {
    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
    this.profiles.put(profileName, new Profile(reader));
    this.selectedProfile = profileName;
  }


  /**
   * Perform the validation process
   *
   * @param reportLocation  the location where the report.html file will be placed
   * @return true if no problems were found, false otherwise (see the generated report for reasons)
   */
  public boolean validate(Path reportLocation) {

    if(!profiles.containsKey(selectedProfile)) {
      throw new RuntimeException("The profile with name \""+selectedProfile+"\" is not registered.");
    }
    Profile profile = profiles.get(selectedProfile);
    ProfileExecution execution = executor.execute(model, profile);

    // Prepare data to transfer to the template
    Map<String, Object> data = new HashMap<>();
    data.put("filename", model.getCoinsContainer().getContainerId());
    data.put("date", new Date().toString());
    data.put("profileChecksPassed", execution.profileChecksPassed());
    data.put("validationPassed", execution.validationPassed());
    data.put("profileChecks", execution.getProfileCheckResults());
    data.put("schemaInferences", execution.getSchemaInferenceResults());
    data.put("dataInferences", execution.getDataInferenceResults());
    data.put("validationRules", execution.getValidationRuleResults());

    writeReport(reportLocation, data);
    return execution.profileChecksPassed() && execution.validationPassed();
  }

  private void writeReport(Path reportLocation, Map<String, Object> data) {

    try {

      Configuration cfg = new Configuration();
      cfg.setClassForTemplateLoading(Validator.class, "/validator/");
      Template template = cfg.getTemplate("report.html");

      File out = reportLocation.resolve("report.html").toFile();
      PrintStream printStream = new PrintStream( new FileOutputStream( out ) );
      Writer file = new PrintWriter(new OutputStreamWriter(printStream, "UTF-8"));

      template.process(data, file);
      file.flush();
      file.close();


    } catch (IOException e) {
      e.printStackTrace();
    } catch (TemplateException e) {
      log.error(e.getMessage(), e);
    }

  }

  private class ResourceScanner {

    private HashMap<String, Profile> getProfileFiles(String path) {
      HashMap<String, Profile> files = new HashMap<>();

      InputStream in = getResourceAsStream( path );
      BufferedReader br = new BufferedReader( new InputStreamReader( in ) );
      String resource;

      try {
        while( (resource = br.readLine()) != null ) {
          if(resource.endsWith(".profile")) {
            InputStream stream = getResourceAsStream("validator/queries/" + resource);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            Profile profile = new Profile(reader);
            files.put(profile.getName(), profile);
            stream.close();
          }
        }
      } catch (IOException e) {
        log.error(e.getMessage(), e);
      }

      return files;
    }

    private InputStream getResourceAsStream( String resource ) {
      final InputStream in = getContextClassLoader().getResourceAsStream( resource );
      return in == null ? getClass().getResourceAsStream( resource ) : in;
    }

    private ClassLoader getContextClassLoader() {
      return Thread.currentThread().getContextClassLoader();
    }
  }

}