package nl.coinsweb.sdk.integration.b.modelling;

import nl.coinsweb.sdk.apolda.ontology.PropertyDeclaration;
import nl.coinsweb.sdk.apolda.ontology.impl.PropertyDeclarationImpl;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
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
  public void iterate() {




    JenaCoinsContainer model = new JenaCoinsContainer();


//    model.setOntModelSpec(OntModelSpec.OWL_MEM);

    Iterator<PropertyDeclaration> iterator = model.listPropertyDeclarations("http://www.coinsweb.nl/cbim-2.0.rdf#FloatProperty");
    while(iterator.hasNext()) {
      PropertyDeclaration prop = iterator.next();
      for(PropertyDeclarationImpl.Range range : prop.getRanges()) {
        log.debug(prop.getPropertyUri() + "  -->  " + range.getUri() + " (owner " + prop.getPropertyOwner() + ")");
      }
    }


  }





}
