package nl.coinsweb.sdk;

import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class JenaModelTest {

  private static final Logger log = LoggerFactory.getLogger(JenaModelTest.class);

  @Test
  public void testOntologyHeader() {
    JenaCoinsContainer container = new JenaCoinsContainer("http://sandbox.coinsweb.nl/");
    log.debug(container.exportAsString());
  }
}