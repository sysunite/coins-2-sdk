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


import nl.coinsweb.sdk.exceptions.InvalidProfileFileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class Profile {

  private static final Logger log = LoggerFactory.getLogger(nl.coinsweb.sdk.validator.Profile.class);

  private static HashMap<String, HashMap<String, Profile>> profiles = null;

  public static String PROFILE_REPO_URL = "http://www.coinsweb.nl/coins2/profiles/";

  private String name;
  private String version = "not specified";
  private String author = "not specified";

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
        if(line.startsWith("ProfileVersion")) {
          version = unquote(line.substring(line.indexOf(" ") + 1));
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

    // Perform some checks
    if(name == null) {
      throw new InvalidProfileFileException("No name specified inside the profile file.");
    }
  }

  public static Profile selectProfile(String profileName, String version) {
    if(!getProfiles().containsKey(profileName)) {
      throw new RuntimeException("The profile with name \""+profileName+"\" is not registered.");
    }
    if(!getProfiles().get(profileName).containsKey(version)) {
      throw new RuntimeException("The profile with name \""+profileName+"\" has now version "+version+" registered.");
    }
    return getProfiles().get(profileName).get(version);
  }
  public static void initProfiles() throws InvalidProfileFileException {

    profiles = new HashMap<>();

    String listUrl = PROFILE_REPO_URL+"list.txt";
    log.info("Will read from here to load profiles: "+listUrl);

    URL url;
    Scanner s;
    try {
      url = new URL(listUrl);
      s = new Scanner(url.openStream());
    } catch(MalformedURLException e) {
      log.error("Not able to load profiles from "+listUrl);
      return;
    } catch(IOException e) {
      log.error("Not able to load profiles from "+listUrl);
      return;
    }
    while(s.hasNextLine()) {

      String line = s.nextLine().trim();

      // If line starts with hash, ignore
      if(line.startsWith("#")) {
        continue;
      }

      // We are looking for the .profile extension
      if(!line.endsWith(".profile")) {
        continue;
      }
      String filename = line;

      // Build the profile
      InputStream stream;
      try {
        stream = new URL(PROFILE_REPO_URL+filename).openStream();
      } catch(MalformedURLException e) {
        log.error("Not able to load profile from "+PROFILE_REPO_URL+filename);
        continue;
      } catch(IOException e) {
        log.error("Not able to load profile from "+PROFILE_REPO_URL+filename);
        continue;
      }
      Profile profile = createProfile(stream);

      // Add it to the register
      if(!profiles.containsKey(profile.getName())) {
        profiles.put(profile.getName(), new HashMap<String, Profile>());
      }
      if(profiles.get(profile.getName()).containsKey(profile.getVersion())) {
        log.warn("A profile with filename "+filename+" contains the same version information as a profile listed earlier. Skipping this one.");
        continue;
      }
      profiles.get(profile.getName()).put(profile.getVersion(), profile);
    }
  }

  public static HashMap<String, HashMap<String, Profile>> getProfiles() {
    if(profiles == null) {
      try {
        initProfiles();
      } catch(InvalidProfileFileException e) {
        log.error("One of the profile files bundled with this coins-api.jar was invalid.");
      }
    }
    return profiles;
  }

  public static Set<String> listProfiles() {
    return getProfiles().keySet();
  }
  private static Profile createProfile(InputStream stream) throws InvalidProfileFileException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
    Profile profile = new Profile(reader);
    try {
      reader.close();
      stream.close();
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
    return profile;
  }
  public static Profile loadProfile(InputStream stream) throws InvalidProfileFileException {
    Profile profile = createProfile(stream);
    getProfiles();
    if(!profiles.containsKey(profile.getName())) {
      profiles.put(profile.getName(), new HashMap<String, Profile>());
    }
    if(profiles.get(profile.getName()).containsKey(profile.getVersion())) {
      log.warn("A profile contains the same version information as a profile listed earlier. Overriding it with the new that is manually added.");
    }
    profiles.get(profile.getName()).put(profile.getVersion(), profile);

    log.info("Profile file registered with this name and version: "+profile.getName() + " / "+profile.getVersion());
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
      throw new InvalidProfileFileException("The profile file could not be interpreted.");
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
      throw new InvalidProfileFileException("The profile file could not be interpreted.");
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

  public String getVersion() {
    return version;
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