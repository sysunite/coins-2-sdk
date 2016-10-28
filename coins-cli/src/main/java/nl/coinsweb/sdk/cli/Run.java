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
package nl.coinsweb.sdk.cli;

import nl.coinsweb.sdk.cli.generate.RunGenerate;
import nl.coinsweb.sdk.cli.map.RunMap;
import nl.coinsweb.sdk.cli.unzip.RunUnzip;
import nl.coinsweb.sdk.cli.validate.RunValidate;
import nl.coinsweb.sdk.cli.viewer.RunViewer;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import java.io.*;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Build this project as jar with dependencies and create a coins-cli file in /usr/local/bin
 *
 * #!/usr/bin/env bash
 * java -jar /..path.../coins-cli-1.0-jar-with-dependencies.jar "$@"
 *
 *
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class Run {

  private static final Logger log = Logger.getLogger(Run.class);

  CliOptions options;

  public static boolean QUIET = false;


  public static void main( String... args ) {
    new Run().go(args);
  }

  public void go( String[] args ) {

    this.options = new CliOptions(args);


    if(options.viewerMode()) {

      new RunViewer().go(args);

    } else if(options.generateMode()) {

      new RunGenerate().go(args);

    } else if(options.validateMode()) {

      new RunValidate().go(args);

    } else if(options.unzipMode()) {

      new RunUnzip().go(args);

    } else if (options.mapMode()) {

      new RunMap().go(args);

    } else {

      Run.printHeader();
      options.usage();

    }
  }


  public static void printHeader() {

    if(QUIET) {
      return;
    }

    // Load version from properties file
    Properties props = new Properties();
    String version = "";
    String buildnr = "";
    try {
      props.load(Run.class.getResourceAsStream("/coins-cli.properties"));
      version = props.get("version").toString();
      buildnr = props.get("buildnr").toString();
    } catch (IOException e) {
      System.out.println("(!) unable to read coins-cli.properties from jar");
    }

    // Print header
//    System.out.println(CliOptions.ANSI_BOLD+CliOptions.ANSI_GB_WHITE+CliOptions.ANSI_RED+")"+CliOptions.ANSI_GREEN+"}"+CliOptions.ANSI_RESET+
//        CliOptions.ANSI_BOLD+" COINS 2.0"+CliOptions.ANSI_RESET+"\ncommand line interface (version "+version+", build "+buildnr+")\n");
    System.out.println(")} COINS 2.0\ncommand line interface (version "+version+", build "+buildnr+")\n");
  }


  public static void startLoggingToFile() {
    startLoggingToFile("coins-cli.log");
  }
  public static void startLoggingToFile(String filename) {

    try {

      PatternLayout layout = new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n");
      RollingFileAppender appender = new RollingFileAppender(layout, filename, false);
      appender.setName("file");
      appender.setMaxBackupIndex(10);
      appender.setMaxFileSize("10MB");
      Logger.getRootLogger().addAppender(appender);

      if(!Run.QUIET) {
        System.out.println("Logging to file " + filename + "\n");
      }
    } catch(IOException e) {
      if(!Run.QUIET) {
        System.out.println("Failed to start logger.");
        e.printStackTrace();
      }
    }
  }


  public static String getCli(String command) {
    log.info("Will execute command: "+command);

    Runtime rt = Runtime.getRuntime();

    try {
      Process pr = rt.exec(command);
      log.info("Finished command, will read output.");

      String output = "";

      BufferedReader stdInput = new BufferedReader(new
          InputStreamReader(pr.getInputStream()));

      BufferedReader stdError = new BufferedReader(new
          InputStreamReader(pr.getErrorStream()));

      String line = null;
      while ((line = stdInput.readLine()) != null) {
        output += line + "\n";
        log.info("Read line form inputstream: "+line);
      }
      while ((line = stdError.readLine()) != null) {
        output += line + "\n";
        log.info("Read line form errorstream: "+line);
      }

      return output.trim();

    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }

    return "";
  }
  public static void runCli(String command) {
    runCli(command, null);
  }
  public static void runCli(String command, Path path) {

    Runtime rt = Runtime.getRuntime();

    try {
      String[] env = new String[]{};
      Process pr;
      if(path != null) {
        pr = rt.exec(command, env, path.toFile());
      } else {
        pr = rt.exec(command);
      }


      StreamGobbler errorGobbler = new StreamGobbler(pr.getErrorStream());
      StreamGobbler outputGobbler = new StreamGobbler(pr.getInputStream());
      errorGobbler.start();
      outputGobbler.start();
      pr.waitFor();

    } catch (InterruptedException e) {
      log.error(e.getMessage(), e);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }

  // Thanks to jitter@stackoverflow
  public static class StreamGobbler extends Thread {
    InputStream is;

    // reads everything from is until empty
    StreamGobbler(InputStream is) {
      this.is = is;
    }

    public void run() {
      try {
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line = null;
        while ( (line = br.readLine()) != null)
          log.info(line);
      } catch (IOException ioe) {
        ioe.printStackTrace();
      }
    }
  }

}
