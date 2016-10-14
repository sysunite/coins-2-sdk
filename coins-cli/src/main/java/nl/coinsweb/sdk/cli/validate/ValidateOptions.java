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
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class ValidateOptions {

  private static final Logger log = LoggerFactory.getLogger(ValidateOptions.class);

  private static Options options = null;

  public static Options getOptions() {
    if(options == null) {
      options = new Options();
      options.addOption("c", "custom-profile", true, "custom profile file to use (e.g. ./custom.profile)");
      options.addOption("e", false, "run on empty container, for debugging profile files");
      options.addOption("f", "fuseki", true, "address of fuseki service (e.g. http://localhost:3030), please create a dataset named 'coins'");
      options.addOption("h", "help", false, "print help");
      options.addOption("o", true, "output file (default: ./report.html)");
      options.addOption("p", "profile", true, "profile to use (default: \"COINS 2.0 Lite\")");
      options.addOption("q", false, "quiet, no output to the console");
      options.addOption("v", false, "verbose logging (in the current build this can not be disabled)");
    }
    return options;
  }

  public static void usage() {
    if(!Run.QUIET) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("coins-cli validate <input file>", getOptions());
    }
  }









  // Instance variables
  private CommandLineParser parser = new DefaultParser();
  private CommandLine cmd;

  // Constructor
  public ValidateOptions(String[] args) throws ParseException {
    cmd = parser.parse( getOptions(), args);
  }



  // External interface methods
  public boolean emptyRun() { return cmd.hasOption("e"); }
  public boolean quietMode() { return cmd.hasOption("q"); }
  public boolean printHelpOption() { return cmd.hasOption("h"); }

  public boolean hasFusekiAddress() { return cmd.hasOption("f"); }
  public String getFusekiAddress() { return (!hasFusekiAddress()) ? null : cmd.getOptionValue("f"); }

  public boolean hasProfile() { return cmd.hasOption("p"); }
  public String getProfile() { return (!hasProfile()) ? null : cmd.getOptionValue("p"); }

  public boolean hasCustomProfile() { return cmd.hasOption("c"); }
  public Path getCustomProfile() { return (!hasCustomProfile()) ? null : CliOptions.resolvePath(cmd.getOptionValue("c")); }

  public boolean hasInputOption() { return cmd.getArgList().size() == 2; }
  public Path getInputOption() { return (!hasInputOption()) ? null : CliOptions.resolvePath(cmd.getArgList().get(1)); }

  public boolean hasOutputOption() { return cmd.hasOption("o"); }
  public Path getOutputOption() { return CliOptions.resolvePath(cmd.getOptionValue("o")); }
}