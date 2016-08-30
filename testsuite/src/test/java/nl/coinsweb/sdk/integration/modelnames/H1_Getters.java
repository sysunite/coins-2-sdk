package nl.coinsweb.sdk.integration.modelnames;

import nl.coinsweb.sdk.Namespace;
import nl.coinsweb.sdk.integration.DatasetAsserts;
import nl.coinsweb.sdk.integration.IntegrationHelper;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class H1_Getters {

  protected static final Logger log = LoggerFactory.getLogger(H1_Getters.class);

  @Test
  public void modelNames() {

    JenaCoinsContainer model = new JenaCoinsContainer("http://playground.com/");
    model.load(IntegrationHelper.getResourceFile("F1", "WOAVoorbeeld.ccr").getAbsolutePath());


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



    DatasetAsserts.logTriples(model.getCoinsGraphSet().getWoaModel());
    DatasetAsserts.logTriples(model.getCoinsGraphSet().getInstanceModel());

  }




}



