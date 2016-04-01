package nl.coinsweb.sdk.integration.modelling;

import nl.coinsweb.sdk.apolda.ontology.PropertyDeclaration;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import nl.coinsweb.sdk.jena.TDBCoinsContainer;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */

public class B7_NextTrunkVersionOverloading {

  protected static final Logger log = LoggerFactory.getLogger(B7_NextTrunkVersionOverloading.class);




  @Test
  public void iteratePerson() {



    JenaCoinsContainer model = new TDBCoinsContainer("http://playground.com/");


//    model.setReasoner(OntModelSpec.OWL_MEM);

    Iterator<PropertyDeclaration> iterator = model.listPropertyDeclarations("http://www.coinsweb.nl/cbim-2.0.rdf#Part");
    while(iterator.hasNext()) {
      PropertyDeclaration prop = iterator.next();
      log.debug(prop.getPropertyUri() + "  -->  " + prop.getRangeUri() + " (owner "+prop.getPropertyOwner()+")");
    }


  }





}
