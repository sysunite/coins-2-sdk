package nl.coinsweb.sdk.cli;

import nl.coinsweb.sdk.cli.generate.RunGenerate;
import nl.coinsweb.sdk.cli.map.RunMap;
import nl.coinsweb.sdk.cli.unzip.RunUnzip;
import nl.coinsweb.sdk.cli.viewer.RunViewer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

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
    go(new CliOptions(args));
  }

  protected void go( CliOptions options ) {

    this.options = options;

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
                       CliOptions.ANSI_BOLD+" COINS 2.0"+CliOptions.ANSI_RESET+"\ncommand line interface (build "+buildnr+")\n");


    if(!options.quietMode()) {
      Logger.getRootLogger().setLevel(Level.TRACE);
      String logFilePath = addFileAppender();
      if (logFilePath != null) {
        System.out.println("Logging to file " + logFilePath + "\n");
      }
    }






    if(options.printHelpOption()) {

      options.usage();



    } else if(options.viewerMode()) {

      new RunViewer().go(options);



    } else if(options.generateMode()) {

      new RunGenerate().go(options);



    } else if(options.unzipMode()) {

      new RunUnzip().go(options);



    } else if(options.mapMode()) {

      new RunMap().go(options);



    } else {

      options.usage();



    }
  }


  private String addFileAppender() {

    try {

      PatternLayout layout = new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n");
      RollingFileAppender appender = new RollingFileAppender(layout, "coins-cli.log", false);
      appender.setName("file");
      appender.setMaxBackupIndex(10);
      appender.setMaxFileSize("10MB");
      Logger.getRootLogger().addAppender(appender);


      return System.getProperty("user.dir") + "/" + "coins-cli.log";
    } catch(IOException e) {

    }
    return null;
  }
}
