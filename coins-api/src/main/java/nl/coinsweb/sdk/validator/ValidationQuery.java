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


import com.hp.hpl.jena.query.QueryParseException;
import nl.coinsweb.sdk.CoinsModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class ValidationQuery {

  private static final Logger log = LoggerFactory.getLogger(ValidationQuery.class);

  public static enum Profile { PROFILE_LITE, PROFILE_LITE_EQ };

  String query = "";

  String name;
  String description;
  String reference;
  Profile profile;

  private Iterator<Map<String,String>> resultSet;
  private boolean passed;
  private String errorMessage;

  public ValidationQuery(InputStream in) {

    BufferedReader br = new BufferedReader( new InputStreamReader( in ) );
    String line;

    try {
      while( (line = br.readLine()) != null ) {

        if(line.trim().startsWith("#name")) {
          name = line.substring(line.indexOf(":")+1).trim();
        }

        else if(line.trim().startsWith("#description")) {
          description = line.substring(line.indexOf(":")+1).trim();
        }

        else if(line.trim().startsWith("#reference")) {
          reference = line.substring(line.indexOf(":")+1).trim();
        }

        else if(line.trim().startsWith("#profile")) {
          String profileString = line.substring(line.indexOf(":")+1).trim();
          if("COINS 2.0 Lite".equals(profileString)) {
            profile = Profile.PROFILE_LITE;
          } else if("COINS 2.0 Lite+eq".equals(profileString)) {
            profile = Profile.PROFILE_LITE_EQ;
          } else {
            throw new RuntimeException("The profile for a Validation Query file could not be interpreted: "+profileString);
          }
        }

        else if(!line.trim().isEmpty()) {
          query += line + "\n";
        }
      }
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }



  public boolean executeOn(CoinsModel model) {
    try {
      resultSet = model.query(getQuery());
      passed = true; // todo: interpret result as good or bad
    } catch (QueryParseException e) {

      errorMessage = "problem executing query with reference "+getReference();
      log.error(errorMessage, e);
      log.error(getQuery());

      errorMessage += escapeHtml4("\n" + getQuery() + "\n" + e.getMessage());

      passed = false;
    }
    return getPassed();
  }



  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public Profile getProfile() {
    return profile;
  }

  public String getReference() {
    return reference;
  }

  public String getQuery() {
    return query;
  }

  public boolean getPassed() {
    return passed;
  }
  public String getErrorMessage() {
    return errorMessage;
  }

  public String toString() {
    return getName() + " " + getDescription() + " " + getReference() + " " + getProfile() + " " + getQuery();
  }

}