package nl.coinsweb.sdk.cli.unzip;

import nl.coinsweb.sdk.FileManager;
import nl.coinsweb.sdk.cli.CliOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class RunUnzip {

  private static final Logger log = LoggerFactory.getLogger(RunUnzip.class);

  public static void go(CliOptions options) {

    if(!options.hasInputOption() || options.getInputOptions().isEmpty()) {
      System.out.println("no input file specified");
      return;
    }

    if(!options.hasOutputOption()) {
      System.out.println("no output location specified");
      return;
    }

    if(options.hasInputOption() && options.getInputOptions().size() > 1) {
      System.out.println("too many input files specified");
      return;
    }

    for(Path zipFile : options.getInputOptions()) {
      FileManager.unzipTo(zipFile.toFile(), options.getOutputOption().resolve(zipFile.getFileName().toString().substring(0, zipFile.getFileName().toString().length() - 4)));
    }

  }
}