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
package nl.coinsweb.sdk.cli.generate;

import nl.coinsweb.sdk.cli.CliOptions;
import nl.coinsweb.sdk.owlgenerator.Utils;
import org.apache.commons.cli.*;
import com.hp.hpl.jena.ontology.OntModelSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class GenerateOptions {

  protected static final Logger log = LoggerFactory.getLogger(GenerateOptions.class);

  private static Options options = null;

  public static Options getOptions() {
    if(options == null) {
      options = new Options();
      options.addOption("h", "help", false, "print help");

      options.addOption(new Option("jar", false, "create jars"));
      options.addOption(new Option("jarto", true, "create jars and place them in the specified folder"));
      options.addOption(new Option("sources", false, "include sources in the jar"));

      options.addOption(new Option("dll", false, "create dlls"));
      options.addOption(new Option("dllto", true, "create dlls and place them in the specified folder"));

      Option input = new Option("i", true, "input file");
      input.setArgs(Option.UNLIMITED_VALUES);
      options.addOption(input);
      options.addOption("a", true, "pointer to coins-api.dll, needed for the code generator");
      options.addOption("o", true, "output file");
      options.addOption("order", true, "namespaces to set order of generation (for generating dlls, the order is important, no backward dependency is allowed)");
      options.addOption("r", true, "use jena reasoner (e.g. OWL_MEM, OWL_MEM_RDFS_INF, ...)");
      options.addOption("q", false, "quiet, no output to the console");
      options.addOption("v", false, "verbose logging");
    }
    return options;
  }

  public static void usage() {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp( "coins-cli validate", getOptions() );
  }









  // Instance variables
  private CommandLineParser parser = new DefaultParser();
  private CommandLine cmd;



  // Constructor
  public GenerateOptions(String[] args) throws ParseException {
    cmd = parser.parse( getOptions(), args);
  }



  // External interface methods
  public boolean quietMode() { return cmd.hasOption("q"); }
  public boolean printHelpOption() { return cmd.hasOption("h"); }
  public boolean hasEncodingOption() { return cmd.hasOption("e"); }
  public boolean hasIncludeSourcesOption() { return cmd.hasOption("sources"); }
  public String getEncodingOption() { return cmd.getOptionValue("e"); }
  public boolean hasJarOption() { return cmd.hasOption("jar"); }
  public boolean hasJarPathOption() { return cmd.hasOption("jarto"); }
  public Path getJarPathOption() { return CliOptions.resolvePath(cmd.getOptionValue("jarto")); }
  public boolean hasDllOption() { return cmd.hasOption("dll"); }
  public boolean hasDllPathOption() { return cmd.hasOption("dllto"); }
  public Path getDllPathOption() { return CliOptions.resolvePath(cmd.getOptionValue("dllto")); }
  public boolean hasOrderOption() { return cmd.hasOption("order") && !getOrderOptions().isEmpty(); }
  public List<String> getOrderOptions() { return CliOptions.breakSemicolonSeparated(cmd.getOptionValue("order")); }
  public boolean hasInputOption() { return cmd.hasOption("i") && !getInputOptions().isEmpty(); }
  public List<Path> getInputOptions() { return CliOptions.resolvePaths(Arrays.asList(cmd.getOptionValues("i"))); }
  public boolean hasReasonerOption() { return cmd.hasOption("r"); }
  public String getReasonerString() { return cmd.getOptionValue("r"); }
  public OntModelSpec getReasonerOption() { return Utils.reasonerFromString(cmd.getOptionValue("r")); }
  public boolean hasApiPathOption() { return cmd.hasOption("a"); }
  public Path getApiPathOption() { return CliOptions.resolvePath(cmd.getOptionValue("a")); }
  public boolean hasOutputOption() { return cmd.hasOption("o"); }
  public Path getOutputOption() { return CliOptions.resolvePath(cmd.getOptionValue("o")); }
}