package nl.coinsweb.sdk;

import nl.coinsweb.sdk.cli.Run;
import org.junit.Test;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class RunValidator {



  @Test
  public void runValidator() {

    String[] args = {
        "validate",
        "-i",        "/Users/bastiaan/Desktop/WOAVoorbeeld.ccr",
    };
    Run generator = new Run();
    generator.go(args);

  }

}
