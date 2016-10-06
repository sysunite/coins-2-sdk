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
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import java.io.File;
import java.io.IOException;
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


  public static void main( String... args ) {
    new Run().go(args);
  }

  public void go( String[] args ) {

    this.options = new CliOptions(args);

    // Load version from properties file
    Properties props = new Properties();
    String version = "";
    String buildnr = "";
    try {
      props.load(Run.class.getResourceAsStream("/coins-cli.properties"));
      version = props.get("version").toString();
      buildnr = props.get("buildnr").toString();
    } catch (IOException e) {
      System.out.println("unable to read coins-cli.properties from jar");
    }


    // Print header
    System.out.println(CliOptions.ANSI_BOLD+CliOptions.ANSI_GB_WHITE+CliOptions.ANSI_RED+")"+CliOptions.ANSI_GREEN+"}"+CliOptions.ANSI_RESET+
                       CliOptions.ANSI_BOLD+" COINS 2.0"+CliOptions.ANSI_RESET+"\ncommand line interface (version "+version+", build "+buildnr+")\n");






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

      options.usage();

    }
  }


  public static void startLoggingToFile() {

    Logger.getRootLogger().setLevel(Level.TRACE);


    try {

      PatternLayout layout = new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n");
      RollingFileAppender appender = new RollingFileAppender(layout, "coins-cli.log", false);
      appender.setName("file");
      appender.setMaxBackupIndex(10);
      appender.setMaxFileSize("10MB");
      Logger.getRootLogger().addAppender(appender);

      System.out.println("Logging to file " + System.getProperty("user.dir") + File.separator + "coins-cli.log" + "\n");
    } catch(IOException e) {

    }
  }
}
