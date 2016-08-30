package nl.coinsweb.sdk.integration.modelling;

import com.hp.hpl.jena.vocabulary.RDFS;
import nl.coinsweb.cbim.FloatProperty;
import nl.coinsweb.sdk.AbstractCoinsObject;
import nl.coinsweb.sdk.CoinsParty;
import nl.coinsweb.sdk.RuntimeCoinsObject;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import org.apache.jena.riot.RDFFormat;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

/**
 * Usecase B1:
 *
 *    Create the most simple instance base without using a generated .dll library.
 *
 *
 * @author Bastiaan Bijl, Sysunite 2016
 */

public class B1_CreateTreeWithoutLibrary {

  protected static final Logger log = LoggerFactory.getLogger(B1_CreateTreeWithoutLibrary.class);




  @Test
  public void instantiateTree() {

    CoinsParty defaultPerson = new CoinsParty("http://sandbox.rws.nl/defaultUser");



    /**
     * Create a model with two instances
     */

    JenaCoinsContainer container = new JenaCoinsContainer(defaultPerson, "http://gardening.com/");
    AbstractCoinsObject gardenPiece_1 = new RuntimeCoinsObject(container, "http://gardening.com/Tree");
    AbstractCoinsObject gardenPiece_2 = new RuntimeCoinsObject(container, "http://gardening.com/Oak");


    container.addStatement("http://gardening.com/Oak", RDFS.subClassOf.toString(), "http://gardening.com/Tree");

    container.batchAddCoinsContainerObjectType();


    /**
     * Print the content of the file
     */
    File file = container.exportModel(container.getCoinsGraphSet().getInstanceModel(), "/tmp/coinstest/content.nq", RDFFormat.NQUADS);
    try {
      BufferedReader br = new BufferedReader(new FileReader(file));
      String line = null;
      while ((line = br.readLine()) != null) {
        log.info(line);
      }
    } catch (IOException e) {
    }

    Set<String> classes = gardenPiece_1.listClassUris();
    log.info("parent classes:");
    for(String parentClass : classes) {
      log.info(parentClass);
    }

    Set<String> classes2 = gardenPiece_2.listClassUris();
    log.info("parent classes:");
    for(String parentClass : classes2) {
      log.info(parentClass);
    }

    FloatProperty fp = new FloatProperty(container);
    fp.setSimpleProperty(12.3f);
  }







  @Test
  public void instantiateTreeBasedOnCoreModel() {

    CoinsParty defaultPerson = new CoinsParty("http://sandbox.rws.nl/defaultUser");

    /**
     * Create a model with two instances
     */

    JenaCoinsContainer container = new JenaCoinsContainer(defaultPerson, "http://gardening.com/");
    AbstractCoinsObject gardenPiece_1 = new RuntimeCoinsObject(container, "http://gardening.com/Tree");
    AbstractCoinsObject gardenPiece_2 = new RuntimeCoinsObject(container, "http://gardening.com/Oak");



    container.addStatement(gardenPiece_1.getClassUri(), RDFS.subClassOf.toString(), "http://www.coinsweb.nl/cbim-2.0.rdf#CbimObject");
    container.addStatement("http://gardening.com/Oak", RDFS.subClassOf.toString(), "http://gardening.com/Tree");


    gardenPiece_2.setLiteralValue(RDFS.label.getURI(), "hey, this is a label value");
    gardenPiece_2.setLiteralValue("http://values#hasFloat", 11.7);



    /**
     * Print the content of the file
     */
    File file = container.exportModel(container.getCoinsGraphSet().getInstanceModel(), "/tmp/coinstest/content.nq", RDFFormat.NQUADS);
    try {
      BufferedReader br = new BufferedReader(new FileReader(file));
      String line = null;
      while ((line = br.readLine()) != null) {
        log.info(line);
      }
    } catch (IOException e) {
    }

    Set<String> classes = gardenPiece_1.listClassUris();
    log.info("parent classes:");
    for(String parentClass : classes) {
      log.info(parentClass);
    }



  }


}
