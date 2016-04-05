package nl.coinsweb.sdk.integration.commandlineinterface;

import nl.coinsweb.sdk.cli.generate.RunGenerate;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Usecase E1:
 *
 *    Use coins-cli options
 *
 *
 *
 *
 * @author Bastiaan Bijl, Sysunite 2016
 */

public class E1_ConvertRdfToNquad {

  protected static final Logger log = LoggerFactory.getLogger(E1_ConvertRdfToNquad.class);








  @Test
  public void askHelp() {
    RunGenerate runner = new RunGenerate();
    String result = runner.getCli("coins-cli");
    log.info(result);
  }





}