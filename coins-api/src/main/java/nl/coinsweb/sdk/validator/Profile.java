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


import nl.coinsweb.sdk.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class Profile {

  private static final Logger log = LoggerFactory.getLogger(nl.coinsweb.sdk.validator.Profile.class);

  private static HashMap<String, Profile> profiles = null;

  private String name;
  private String author;

  private List<ValidationQuery> profileChecks = new ArrayList<>();
  private List<InferenceQuery> schemaInferences = new ArrayList<>();
  private List<InferenceQuery> dataInferences = new ArrayList<>();
  private List<ValidationQuery> validationRules = new ArrayList<>();

  public Profile(BufferedReader reader) {

    String line;

    try {
      while ((line = reader.readLine()) != null) {
        line = line.trim();

        if(line.isEmpty() || line.startsWith("#"))  {
          continue;
        }

        if(line.startsWith("ProfileName")) {
          name = unquote(line.substring(line.indexOf(" ") + 1));
        }
        if(line.startsWith("ProfileAuthor")) {
          author = unquote(line.substring(line.indexOf(" ") + 1));
        }
        if(line.startsWith("<ProfileCheck>")) {
          profileChecks.add(buildValidationQuery(reader, "</ProfileCheck>"));
        }
        if(line.startsWith("<SchemaInference>")) {
          schemaInferences.add(buildInferenceQuery(reader, "</SchemaInference>"));
        }
        if(line.startsWith("<DataInference>")) {
          dataInferences.add(buildInferenceQuery(reader, "</DataInference>"));
        }
        if(line.startsWith("<ValidationRule>")) {
          validationRules.add(buildValidationQuery(reader, "</ValidationRule>"));
        }

      }
      reader.close();

    } catch (IOException e) {
      log.error("Problem reading profile file.", e);
    }
  }

  public static Profile loadProfile(String profileName) {



    if(!getProfiles().containsKey(profileName)) {
      throw new RuntimeException("The profile with name \""+profileName+"\" is not registered.");
    }
    return getProfiles().get(profileName);
  }
  private static void initProfiles() {

    Profile.profiles = new HashMap<>();

    ArrayList<String> filePaths = FileManager.listResourceFiles("validator");
    for(String filePath : filePaths) {
      if(filePath.endsWith(".profile")) {
        System.out.println("opening "+filePath);
        InputStream stream = FileManager.getResourceFileAsStream("validator/"+filePath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        Profile profile = new Profile(reader);
        Profile.profiles.put(profile.getName(), profile);
        log.info("Profile file "+filePath+" registered with this name: "+profile.getName());
        try {
          reader.close();
          stream.close();
        } catch (IOException e) {
          log.error(e.getMessage(), e);
        }
      }
    }
  }

  private static HashMap<String, Profile> getProfiles() {
    if(profiles == null) {
      initProfiles();
    }
    return profiles;
  }

  public static Set<String> listProfiles() {
    return getProfiles().keySet();
  }
  public static Profile loadProfile(String profileName, InputStream stream) {
    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
    Profile profile = new Profile(reader);
    getProfiles().put(profileName, profile);
    return profile;
  }

  private InferenceQuery buildInferenceQuery(BufferedReader reader, String endTag) {

    String reference = null;
    String description = null;
    String sparqlQuery = null;

    String line;

    try {
      while( (line = reader.readLine()) != null ) {
        line = line.trim();

        if(line.isEmpty() || line.startsWith("#"))  {
          continue;
        }

        if(line.endsWith(endTag)) {
          break;
        }

        if(line.startsWith("Reference")) {
          reference = unquote(line.substring(line.indexOf(" ") + 1));
        } else if(line.startsWith("Description")) {
          description = unquote(line.substring(line.indexOf(" ") + 1));
        }

        else if(line.startsWith("<SparqlQuery>")) {
          sparqlQuery = "";
          while( (line = reader.readLine()) != null ) {
            line = line.trim();
            if(line.endsWith("</SparqlQuery>")) {
              break;
            }
            if(line.startsWith("#"))  {
              continue;
            }
            sparqlQuery += line + "\n";
          }
        }
      }
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("The profile for a Validation Query file could not be interpreted.");
    }

    return new InferenceQuery(reference, description, sparqlQuery);
  }

  private ValidationQuery buildValidationQuery(BufferedReader reader, String endTag) {

    String reference = null;
    String description = null;
    String resultFormat = null;
    String sparqlQuery = null;

    String line;

    try {
      while( (line = reader.readLine()) != null ) {
        line = line.trim();

        if(line.isEmpty() || line.startsWith("#"))  {
          continue;
        }

        if(line.endsWith(endTag)) {
          break;
        }

        if(line.startsWith("Reference")) {
          reference = unquote(line.substring(line.indexOf(" ") + 1));
        } else if(line.startsWith("Description")) {
          description = unquote(line.substring(line.indexOf(" ") + 1));
        } else if(line.startsWith("ResultFormat")) {
          resultFormat = unquote(line.substring(line.indexOf(" ") + 1));
        }

        else if(line.startsWith("<SparqlQuery>")) {
          sparqlQuery = "";
          while( (line = reader.readLine()) != null ) {
            line = line.trim();
            if(line.endsWith("</SparqlQuery>")) {
              break;
            }
            if(line.startsWith("#"))  {
              continue;
            }
            sparqlQuery += line + "\n";
          }
        }
      }
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException("The profile for a Validation Query file could not be interpreted.");
    }

    return new ValidationQuery(reference, description, resultFormat, sparqlQuery);
  }

  private String unquote(String in) {
    while(in.startsWith("\"")) {
      in = in.substring(1).trim();
    }
    while(in.endsWith("\"")) {
      in = in.substring(0, in.length()-1).trim();
    }
    return in;
  }










  public String getName() {
    return name;
  }

  public String getAuthor() {
    return author;
  }

  public List<ValidationQuery> getProfileChecks() {
    return profileChecks;
  }

  public List<InferenceQuery> getSchemaInferences() {
    return schemaInferences;
  }

  public List<InferenceQuery> getDataInferences() {
    return dataInferences;
  }

  public List<ValidationQuery> getValidationRules() {
    return validationRules;
  }
}