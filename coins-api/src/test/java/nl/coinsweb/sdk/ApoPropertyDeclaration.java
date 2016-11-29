package nl.coinsweb.sdk;

import nl.coinsweb.sdk.apolda.ontology.impl.PropertyDeclarationImpl;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class ApoPropertyDeclaration {

  protected static final Logger log = LoggerFactory.getLogger(ApoPropertyDeclaration.class);

  @Test
  public void testEquals() {

    PropertyDeclarationImpl a = new PropertyDeclarationImpl();
    a.setPropertyUri("http://baab.nl/dingdong/#");
    a.addRange("http://baab.nl/bang/#","Bang");
    PropertyDeclarationImpl b = new PropertyDeclarationImpl();
    b.setPropertyUri("http://baab.nl/dingdong/#");
    b.addRange("http://baab.nl/bang/#","Bang");

    assertTrue(a.equals(b));

    log.info(""+a.hashCode());
    log.info(""+b.hashCode());
  }
}