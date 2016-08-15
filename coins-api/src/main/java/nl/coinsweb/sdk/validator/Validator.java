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
import java.util.HashMap;
import java.util.Map;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class Validator {

  private static final Logger log = LoggerFactory.getLogger(Validator.class);

  CoinsModel model;
  HashMap<String, ValidationQuery> validationQueries;

  public Validator(CoinsModel model) {
    this.model = model;
  }

  /**
   * Read all the vq-files from the jar //todo: add a custom place to look for queries
   */
  public void init() {
    ResourceScanner scanner = new ResourceScanner();
    this.validationQueries = scanner.getResourceFiles("validator/queries/");
  }

  /**
   * Perform the validation process
   *
   * @param reportLocation  the location where the report.html file will be placed
   * @return true if no problems were found, false otherwise (see the generated report for reasons)
   */
  public boolean validate(Path reportLocation) {

    try {

      Configuration cfg = new Configuration();
      cfg.setClassForTemplateLoading(Validator.class, "/validator/");
      Template template = cfg.getTemplate("report.html");

      Map<String, Object> data = new HashMap<>();

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

    return true;

  }

  private class ResourceScanner {
    private HashMap<String, ValidationQuery> getResourceFiles(String path) {
      HashMap<String, ValidationQuery> files = new HashMap<>();

      InputStream in = getResourceAsStream( path );
      BufferedReader br = new BufferedReader( new InputStreamReader( in ) );
      String resource;

      try {
        while( (resource = br.readLine()) != null ) {
          if(resource.endsWith(".vq")) {
            files.put(resource, new ValidationQuery(getResourceAsStream("validator/queries/"+resource)));
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