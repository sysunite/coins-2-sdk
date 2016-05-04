package nl.coinsweb.sdk.cli;

import com.hp.hpl.jena.ontology.OntModelSpec;
import nl.coinsweb.sdk.owlgenerator.Utils;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class CliOptions {

  protected static final Logger log = LoggerFactory.getLogger(CliOptions.class);

  public static final String ANSI_RESET  = "\u001B[0m";
  public static final String ANSI_BOLD  =  "\u001B[1m";

  public static final String ANSI_BLACK  = "\u001B[30m";
  public static final String ANSI_RED    = "\u001B[31m";
  public static final String ANSI_GREEN  = "\u001B[32m";
  public static final String ANSI_YELLOW = "\u001B[33m";
  public static final String ANSI_BLUE   = "\u001B[34m";
  public static final String ANSI_PURPLE = "\u001B[35m";
  public static final String ANSI_CYAN   = "\u001B[36m";
  public static final String ANSI_WHITE  = "\u001B[37m";

  public static final String ANSI_GB_BLACK  = "\u001B[40m";
  public static final String ANSI_GB_RED    = "\u001B[41m";
  public static final String ANSI_GB_GREEN  = "\u001B[42m";
  public static final String ANSI_GB_YELLOW = "\u001B[43m";
  public static final String ANSI_GB_BLUE   = "\u001B[44m";
  public static final String ANSI_GB_PURPLE = "\u001B[45m";
  public static final String ANSI_GB_CYAN   = "\u001B[46m";
  public static final String ANSI_GB_WHITE  = "\u001B[47m";

  public static final String JENA_ENCODINGS  = "RDF/XML | RDF/XML-ABBREV | N-TRIPLE | TURTLE | TTL | N3";






  /** Print usage message and abort */
  protected void usage() {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp( "coins-cli [ unzip | viewer | map | generate ]", options );

  }





  public boolean viewerMode() {
    if(cmd.getArgList().isEmpty()) {
      return false;
    }
    return "viewer".equals(cmd.getArgList().get(0));
  }
  public boolean unzipMode() {
    if(cmd.getArgList().isEmpty()) {
      return false;
    }
    return "unzip".equals(cmd.getArgList().get(0));
  }
  public boolean mapMode() {
    if(cmd.getArgList().isEmpty()) {
      return false;
    }
    return "map".equals(cmd.getArgList().get(0));
  }
  public boolean generateMode() {
    if(cmd.getArgList().isEmpty()) {
      return false;
    }
    return "generate".equals(cmd.getArgList().get(0));
  }





  private Path resolvePath(String path) {
    try {
      return Paths.get(new File(".").getCanonicalPath()).resolve(path);
    } catch (IOException e) {
      return null;
    }
  }

  private List<Path> resolvePaths(List<String> paths) {
    List<Path> result = new ArrayList<>();
    try {
      for(String path : paths) {
        result.add(Paths.get(new File(".").getCanonicalPath()).resolve(path));
      }
    } catch (IOException e) {
    }
    return result;
  }


  // Instance variables
  /** The list of command line arguments */
  private Options options;
  private CommandLineParser parser = new DefaultParser();
  CommandLine cmd;



  // Constructor
  public CliOptions(String[] args) {

    options = new Options();
    options.addOption("e", true, "the encoding of the output file: "+JENA_ENCODINGS);
    options.addOption("h", "help", false, "print help");

    options.addOption(new Option("jar", false, "create jars"));
    options.addOption(new Option("jarto", true, "create jars and place them in the specified folder"));
    options.addOption(new Option("sources", false, "include sources in the jar"));

    options.addOption(new Option("dll", false, "create dlls"));
    options.addOption(new Option("dllto", true, "create dlls and place them in the specified folder"));

    Option input = new Option("i", true, "input file (for generating dlls, the order is important, no backward dependency is allowed)");
    input.setArgs(Option.UNLIMITED_VALUES);
    options.addOption(input);

//    Option mappingOption = new Option("m", "mapping", true, "mapping of namespaces to package names\nhttp://example.com/set# = com.example.set http://...");
//    mappingOption.setArgs(Option.UNLIMITED_VALUES);
//    options.addOption(mappingOption);

    options.addOption("a", true, "pointer to coins-api.dll, needed for the code generator");
    options.addOption("o", true, "output file");
    options.addOption("r", true, "use jena reasoner (e.g. OWL_MEM, OWL_MEM_RDFS_INF, ...)");
    options.addOption("q", false, "quiet, no output to the console");
    options.addOption("v", false, "verbose logging");


    try {
      cmd = parser.parse( options, args);
    } catch (ParseException e) {
      System.out.println("failed to initiate");
      e.printStackTrace();
    }
  }



  // External interface methods
  public boolean quietMode() { return cmd.hasOption("q"); }
  public boolean printHelpOption() { return cmd.hasOption("h"); }
  public boolean hasEncodingOption() { return cmd.hasOption("e"); }
  public boolean hasIncludeSourcesOption() { return cmd.hasOption("sources"); }
  public String getEncodingOption() { return cmd.getOptionValue("e"); }
  public boolean hasJarOption() { return cmd.hasOption("jar"); }
  public boolean hasJarPathOption() { return cmd.hasOption("jarto"); }
  public Path getJarPathOption() { return resolvePath(cmd.getOptionValue("jarto")); }
  public boolean hasDllOption() { return cmd.hasOption("dll"); }
  public boolean hasDllPathOption() { return cmd.hasOption("dllto"); }
  public Path getDllPathOption() { return resolvePath(cmd.getOptionValue("dllto")); }
  public boolean hasInputOption() { return cmd.hasOption("i") && !getInputOptions().isEmpty(); }
  public List<Path> getInputOptions() { return resolvePaths(Arrays.asList(cmd.getOptionValues("i"))); }
  public boolean hasReasonerOption() { return cmd.hasOption("r"); }
  public String getReasonerString() { return cmd.getOptionValue("r"); }
  public OntModelSpec getReasonerOption() { return Utils.reasonerFromString(cmd.getOptionValue("r")); }
  public boolean hasApiPathOption() { return cmd.hasOption("a"); }
  public Path getApiPathOption() { return resolvePath(cmd.getOptionValue("a")); }
  public boolean hasOutputOption() { return cmd.hasOption("o"); }
  public Path getOutputOption() { return resolvePath(cmd.getOptionValue("o")); }
//  public boolean hasMappingOption() { return cmd.hasOption("mapping"); }
//  public Map<String, String> getMapping() {
//
//    Map<String, String> values = new HashMap<>();
//
//    String namespace = null;
//    boolean listening = false;
//    boolean expectEqualsChar = false;
//
//
//    // Look in the command line arguments
//    for (Iterator<String> i = Arrays.asList(cmd.getOptionValues("mapping")).iterator(); i.hasNext();) {
//
//      String argument = i.next();
//
//      // Check for escaped char
//      if(argument.startsWith("\\")) {
//        continue;
//      }
//
//      // Check for "="
//      if(expectEqualsChar) {
//        if(!argument.equals("=")) {
//          return null;
//        }
//        expectEqualsChar = false;
//        continue;
//      }
//
//      // Set first element
//      if(namespace == null) {
//        namespace = argument;
//        expectEqualsChar = true;
//        continue;
//      }
//
//      // Set second element
//      if(namespace != null && !expectEqualsChar) {
//        values.put(namespace, argument);
//        namespace = null;
//      }
//    }
//
//    return values;
//  }
}