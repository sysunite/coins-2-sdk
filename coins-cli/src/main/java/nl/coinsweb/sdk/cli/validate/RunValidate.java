package nl.coinsweb.sdk.cli.validate;

import nl.coinsweb.sdk.ModelFactory;
import nl.coinsweb.sdk.cli.CliOptions;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import nl.coinsweb.sdk.jena.JenaModelFactory;
import nl.coinsweb.sdk.jena.JenaValidationExecutor;
import nl.coinsweb.sdk.validator.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class RunValidate {

  private static final Logger log = LoggerFactory.getLogger(RunValidate.class);



  public static void go( CliOptions options ) {

    if(!options.hasInputOption() || options.getInputOptions().isEmpty()) {
      System.out.println("no input file specified");
      return;
    }

    if(options.hasInputOption() && options.getInputOptions().size() > 1) {
      System.out.println("too many input files specified");
      return;
    }

    Path path;
    try {
      path = Paths.get(new File(".").getCanonicalPath());
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      return;
    }

    ModelFactory factory = new JenaModelFactory();
    JenaCoinsContainer container = new JenaCoinsContainer(factory, options.getInputOptions().get(0).toString(), "http://www.example.com/");
    JenaValidationExecutor executor = new JenaValidationExecutor();

    Validator validator = new Validator(container, executor, "COINS-2.0-Lite");
    validator.validate(path);
  }
}
