package nl.coinsweb.sdk.integration.modelling;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.util.iterator.ExtendedIterator;
import nl.coinsweb.sdk.CoinsParty;
import nl.coinsweb.sdk.apolda.ontology.PropertyDeclaration;
import nl.coinsweb.sdk.integration.DatasetAsserts;
import nl.coinsweb.sdk.integration.IntegrationHelper;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import nl.coinsweb.sdk.jena.JenaCoinsGraphSet;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.Iterator;

/**
 * Usecase B2:
 *
 *    Cover all the property types that exist in the core model. Do not use a generated library.
 *
 *
 *
 *
 * @author Bastiaan Bijl, Sysunite 2016
 */

public class B2_CreateAllCbimPropertyTypes {

  protected static final Logger log = LoggerFactory.getLogger(B2_CreateAllCbimPropertyTypes.class);



  @Test
  public void tryAll() {
//    TimeAsserts.sample();
////    log.info(findDescriptionProperty(OntModelSpec.OWL_MEM) ? "yes" : "no");
////    TimeAsserts.sample();
////    log.info(findDescriptionProperty(OntModelSpec.OWL_MEM_TRANS_INF) ? "yes" : "no");
////    TimeAsserts.sample();
////    log.info(findDescriptionProperty(OntModelSpec.OWL_MEM_RULE_INF) ? "yes" : "no");
////    TimeAsserts.sample();
//    log.info(findDescriptionProperty(OntModelSpec.OWL_MEM_MICRO_RULE_INF) ? "yes" : "no");
//    TimeAsserts.sample();
//    log.info(findDescriptionProperty(OntModelSpec.OWL_MEM_MINI_RULE_INF) ? "yes" : "no");
//    TimeAsserts.sample();
//    TimeAsserts.report();
////    log.info(findDescriptionProperty(OntModelSpec.OWL_DL_MEM) ? "yes" : "no");
////    TimeAsserts.sample();
////    log.info(findDescriptionProperty(OntModelSpec.OWL_DL_MEM_RDFS_INF) ? "yes" : "no");
////    TimeAsserts.sample();
////    log.info(findDescriptionProperty(OntModelSpec.OWL_DL_MEM_TRANS_INF) ? "yes" : "no");
////    TimeAsserts.sample();
//////    log.info(findDescriptionProperty(OntModelSpec.OWL_DL_MEM_RULE_INF) ? "yes" : "no");
//////    TimeAsserts.sample();
////    log.info(findDescriptionProperty(OntModelSpec.OWL_LITE_MEM) ? "yes" : "no");
////    TimeAsserts.sample();
////    log.info(findDescriptionProperty(OntModelSpec.OWL_LITE_MEM_TRANS_INF) ? "yes" : "no");
////    TimeAsserts.sample();
////    log.info(findDescriptionProperty(OntModelSpec.OWL_LITE_MEM_RDFS_INF) ? "yes" : "no");
////    TimeAsserts.sample();
//////    log.info(findDescriptionProperty(OntModelSpec.OWL_LITE_MEM_RULES_INF)?"yes":"no");
//////    TimeAsserts.sample();

  }

  public boolean findDescriptionProperty(OntModelSpec reasoner) {

    boolean contains = false;

    JenaCoinsGraphSet graphSet = new JenaCoinsGraphSet("http://empty.com/");
    graphSet.setOntModelSpec(reasoner);
    JenaCoinsContainer model = new JenaCoinsContainer(new CoinsParty("http://sandbox.coinsweb.nl/defaultUser"), graphSet, false);

    model.addImport(Paths.get(IntegrationHelper.getResourceFile("BS", "Cbim-2.0.rdf").getAbsolutePath()).toFile().toString(), null, true, true, true);

    ExtendedIterator<OntClass> classes =  model.listOntClasses();
    while(classes.hasNext()) {
      OntClass clazz = classes.next();
      if(!clazz.isAnon()) {
        Iterator<PropertyDeclaration> declarations = model.listPropertyDeclarations(clazz.getURI());
        while(declarations.hasNext()) {
          PropertyDeclaration declaration = declarations.next();
          contains |= "http://www.coinsweb.nl/cbim-2.0.rdf#description".equals(declaration.getPropertyUri());
        }
      }
    }

    log.info(Integer.toString(DatasetAsserts.countTriples(model.getCoinsGraphSet().getUnionJenaOntModel())));

    return contains;
  }
}
