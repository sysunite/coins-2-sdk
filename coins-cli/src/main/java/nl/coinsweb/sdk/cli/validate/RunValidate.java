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
package nl.coinsweb.sdk.cli.validate;

import nl.coinsweb.sdk.cli.CliOptions;
import nl.coinsweb.sdk.cli.Run;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import nl.coinsweb.sdk.validator.Profile;
import nl.coinsweb.sdk.validator.Validator;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class RunValidate {

  private static final Logger log = LoggerFactory.getLogger(RunValidate.class);



  public static void go(String[] args) {

    ValidateOptions options;
    try {
      options = new ValidateOptions(args);
    } catch (ParseException e) {
      System.out.println(e.getMessage() + "\n");
      ValidateOptions.usage();
      System.exit(1);
      return;
    }


    if(options.printHelpOption()) {
      ValidateOptions.usage();
      System.exit(1);
      return;
    }
    Run.startLoggingToFile();

    // Input file
    String inputFile;
    if(!options.hasInputOption()) {
      System.out.println("no input file specified");
      ValidateOptions.usage();
      System.exit(1);
      return;
    }
    inputFile = options.getInputOption().toString();

    // Output file
    Path reportFile;
    if(options.hasOutputOption()) {
      reportFile = options.getOutputOption();
    } else {
      reportFile = CliOptions.resolvePath("./report.html");
    }

    // Profile
    String profileName = null;
    if(options.hasCustomProfile()) {

      try {
        File profileFile = options.getCustomProfile().toFile();
        Profile profile = Profile.loadProfile(new FileInputStream(profileFile));
        profileName = profile.getName();
      } catch (FileNotFoundException e) {
        System.out.println("custom profile file was not found");
        System.exit(1);
        return;
      }
    }

    if(options.hasProfile()) {
      profileName = options.getProfile();
    }
    if(profileName == null) {
      profileName = "COINS 2.0 Lite";
    }

    JenaCoinsContainer container = new JenaCoinsContainer(inputFile, "http://www.example.com/");

    Validator validator = new Validator(container, profileName);
    validator.validate(reportFile);

    System.exit(0);
  }
}
