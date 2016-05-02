package nl.coinsweb.sdk;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import nl.coinsweb.sdk.jena.JenaModelFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class JenaModelTest {

  private static final Logger log = LoggerFactory.getLogger(JenaModelTest.class);

  @Test
  public void testCoremodel() {

    URI path = null;
    try {
      path = JenaModelTest.class.getClassLoader().getResource("Cbim-2.0.rdf").toURI();
    } catch (URISyntaxException e) {
      log.error(e.getMessage(), e);
    }
    Model instanceModel = ModelFactory.createDefaultModel();
    instanceModel.read(path.toString());

    assertEquals("http://www.coinsweb.nl/cbim-2.0.rdf#", instanceModel.getNsPrefixURI(""));

  }









  @Test
  public void testOntologyHeader() {
    nl.coinsweb.sdk.ModelFactory factory = new JenaModelFactory();
    JenaCoinsContainer container = new JenaCoinsContainer(factory, "http://sandbox.coinsweb.nl/");
    log.debug(container.exportAsString());
  }


}