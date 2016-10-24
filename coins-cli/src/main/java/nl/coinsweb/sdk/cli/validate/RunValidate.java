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

import nl.coinsweb.sdk.CoinsGraphSet;
import nl.coinsweb.sdk.CoinsParty;
import nl.coinsweb.sdk.cli.CliOptions;
import nl.coinsweb.sdk.cli.Run;
import nl.coinsweb.sdk.exceptions.InvalidProfileFileException;
import nl.coinsweb.sdk.jena.FusekiGraphSet;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import nl.coinsweb.sdk.jena.TDBStoreGraphSet;
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

    // Wrong arguments
    ValidateOptions options;
    try {
      options = new ValidateOptions(args);
    } catch (ParseException e) {
      System.out.println(e.getMessage() + "\n");
      Run.printHeader();
      ValidateOptions.usage();
      System.exit(1);
      return;
    }

    // Print header
    Run.QUIET = options.quietMode();
    Run.printHeader();

    // Asked for help
    if(options.printHelpOption()) {
      ValidateOptions.usage();
      System.exit(1);
      return;
    }

    // Run on empty container
    boolean emptyRun = false;
    if(options.emptyRun()) {
      emptyRun = true;
    }


    Run.startLoggingToFile();

    // Fuseki
    CoinsGraphSet graphSet;
    if(options.hasFusekiAddress()) {
      graphSet = new FusekiGraphSet("http://validation/", options.getFusekiAddress(), "coins");
      if(!((FusekiGraphSet)graphSet).checkIfDbAvailable()) {

        if(!Run.QUIET) {
          System.out.println("(!) fuseki not active at specified address, or does not have dataset with name 'coins' (address should be of form http://localhost:3030)\n");
        }
        System.exit(1);
        return;
      }
    } else {
      graphSet = new TDBStoreGraphSet("http://validation/");
    }

    // Container
    JenaCoinsContainer container;
    if(emptyRun) {
      container = new JenaCoinsContainer(new CoinsParty("http://sandbox.coinsweb.nl/defaultUser"), graphSet, false, false);
    } else {
      if (!options.hasInputOption()) {
        if(!Run.QUIET) {
          System.out.println("(!) no input file specified\n");
        }
        ValidateOptions.usage();
        System.exit(1);
        return;
      }
      String inputFile = options.getInputOption().toString();
      container = new JenaCoinsContainer(new CoinsParty("http://sandbox.coinsweb.nl/defaultUser"), graphSet, inputFile);
    }




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
        log.info("Supplied custom profile file, will try to register it.");
        File profileFile = options.getCustomProfile().toFile();
        Profile profile = Profile.loadProfile(new FileInputStream(profileFile));
        profileName = profile.getName();
      } catch (FileNotFoundException e) {
        if(!Run.QUIET) {
          System.out.println("(!) custom profile file was not found\n");
        }
        System.exit(1);
        return;
      } catch (InvalidProfileFileException e) {
        if(!Run.QUIET) {
          System.out.println("(!) the supplied custom profile file contains errors\n");
        }
        System.exit(1);
        return;
      }
    }

    if(options.hasProfile()) {
      profileName = options.getProfile();
      if(!Profile.listProfiles().contains(profileName)) {

        if(!Run.QUIET) {
          System.out.println("(!) profile name " + profileName + " was not found, please choose from:\n");
          for (String availableProfile : Profile.listProfiles()) {
            System.out.println(" - " + availableProfile);
          }
          System.out.println("");
        }
        System.exit(1);
        return;
      }
    }
    if(profileName == null) {
      profileName = "COINS 2.0 Lite";
    }


    log.info("Will init validator and start validation.");
    Validator validator = new Validator(container, profileName);

    if(options.generateHtml() && options.generateXml()) {
      validator.validate(reportFile, Validator.GENERATE_BOTH);

    } else if(options.generateHtml()) {
      validator.validate(reportFile, Validator.GENERATE_HTML);

    } else if(options.generateXml()) {
      validator.validate(reportFile, Validator.GENERATE_XML);
    }

    if(!Run.QUIET) {
      System.out.println("Validation finished, see report file "+reportFile.getFileName());
    }
    System.exit(0);
  }
}
