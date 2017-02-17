package nl.coinsweb.sdk.integration.d.owlgenerator;

import com.hp.hpl.jena.ontology.OntModelSpec;
import nl.coinsweb.sdk.Namespace;
import nl.coinsweb.sdk.jena.InMemGraphSet;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
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

    InMemGraphSet graphSet = new InMemGraphSet("http://empty.com/");
    graphSet.setOntModelSpec(OntModelSpec.OWL_MEM_RDFS_INF);
    JenaCoinsContainer model = new JenaCoinsContainer(graphSet, false, true);


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

    Map<Namespace, String> mapping = engine.process(model, sourceFileNames);

  }

}