package nl.coinsweb.sdk.integration.modelnames;

import nl.coinsweb.sdk.CoinsParty;
import nl.coinsweb.sdk.Namespace;
import nl.coinsweb.sdk.integration.DatasetAsserts;
import nl.coinsweb.sdk.integration.IntegrationHelper;
import nl.coinsweb.sdk.jena.FusekiGraphSet;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import nl.coinsweb.sdk.jena.InMemGraphSet;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Dataset;
import org.apache.jena.riot.RDFFormat;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class H2_Fuseki {

  protected static final Logger log = LoggerFactory.getLogger(H2_Fuseki.class);

//  @Test
  public void modelNames() {

    InMemGraphSet graphSet = new FusekiGraphSet("http://playground.com/", "http://docker:3030", "coins");
    graphSet.setOntModelSpec(OntModelSpec.OWL_MEM);
    JenaCoinsContainer model = new JenaCoinsContainer(new CoinsParty("http://sandbox.coinsweb.nl/defaultUser"), graphSet, true, true);
    model.load(IntegrationHelper.getResourceFile("F1", "WOAVoorbeeld.ccr").getAbsolutePath(), false);


    // Check all model names
    HashSet<String> modelNames = new HashSet();
    Iterator<String> modelNamesIterator = model.getCoinsGraphSet().listModelNames();
    while(modelNamesIterator.hasNext()) {
      String modelName = modelNamesIterator.next();
      modelNames.add(modelName);
    }

    assertEquals(6, modelNames.size());
    assertTrue("Should contain graph", modelNames.contains("http://www.buildingbits.nl/MBIContainer.rdf#"));
    assertTrue("Should contain graph", modelNames.contains("http://www.coinsweb.nl/voorbeeld#"));
    assertTrue("Should contain graph", modelNames.contains("http://www.coinsweb.nl/coins2/referencemodel/BranchVersioning#"));
    assertTrue("Should contain graph", modelNames.contains("http://www.coinsweb.nl/COINSWOA.rdf#"));
    assertTrue("Should contain graph", modelNames.contains("http://www.coinsweb.nl/cbim-2.0.rdf#"));
    assertTrue("Should contain graph", modelNames.contains("http://www.coinsweb.nl/units-2.0.rdf#"));


    // Check library model names
    HashSet<String> libraryModelNames = new HashSet();
    for(Namespace ns : model.getCoinsGraphSet().getLibraryModels().keySet()) {
      libraryModelNames.add(ns.toString());
    }

    assertEquals(4, libraryModelNames.size());
    assertTrue("Should contain graph", libraryModelNames.contains("http://www.coinsweb.nl/COINSWOA.rdf#"));
    assertTrue("Should contain graph", libraryModelNames.contains("http://www.coinsweb.nl/cbim-2.0.rdf#"));
    assertTrue("Should contain graph", libraryModelNames.contains("http://www.coinsweb.nl/units-2.0.rdf#"));
    assertTrue("Should contain graph", modelNames.contains("http://www.coinsweb.nl/coins2/referencemodel/BranchVersioning#"));


    assertEquals( 199, DatasetAsserts.countTriples(model.getCoinsGraphSet().getInstanceModel()));
    assertEquals(   9, DatasetAsserts.countTriples(model.getCoinsGraphSet().getWoaModel()));
    assertEquals(1348, DatasetAsserts.countTriples(model.getCoinsGraphSet().getSchemaModel()));
    assertEquals(7646, DatasetAsserts.countTriples(model.getCoinsGraphSet().getSchemaAggregationModel()));
    assertEquals(7854, DatasetAsserts.countTriples(model.getCoinsGraphSet().getFullUnionModel()));

    Dataset dataset = model.getCoinsGraphSet().getValidationDataset();



    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    model.getCoinsGraphSet().writeFullToFile(dataset, outputStream, RDFFormat.NQUADS);
//    System.out.println(outputStream.toString());


  }




}



