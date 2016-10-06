package nl.coinsweb.sdk.integration.owlgenerator;

import org.apache.jena.ontology.OntModelSpec;
import nl.coinsweb.sdk.CoinsParty;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import nl.coinsweb.sdk.jena.TDBGraphSet;
import nl.coinsweb.sdk.owlgenerator.ClassGenerateEngine;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;

/**
 * Usecase D1:
 *
 *    Validate the completeness of the owl generator.
 *
 *
 *
 *
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class D1_ClassListing {

  @Test
  public void listing() {

    // Generate java code in one place
    ClassGenerateEngine engine = new ClassGenerateEngine();

    TDBGraphSet graphSet = new TDBGraphSet("http://empty.com/");
    graphSet.setOntModelSpec(OntModelSpec.OWL_MEM_RDFS_INF);
    JenaCoinsContainer model = new JenaCoinsContainer(new CoinsParty("http://sandbox.coinsweb.nl/defaultUser"), graphSet, false);


    ArrayList<String> sourceFileNames = new ArrayList<>();
//    for(Path modelPath : options.getInputOptions()) {
//      String filePath = null;
//      try {
//        filePath = modelPath.toFile().getCanonicalPath().toString();
//      } catch (IOException e) {
//        log.error(e.getMessage(), e);
//      }
//      model.addImport(filePath, null, true, true, true);
//      log.info("adding "+filePath);
//      sourceFileNames.add(modelPath.getFileName().toString());
//    }

    Map<String, String> mapping = engine.process(model, sourceFileNames);

  }

}