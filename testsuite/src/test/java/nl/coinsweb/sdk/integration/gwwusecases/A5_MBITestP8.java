package nl.coinsweb.sdk.integration.gwwusecases;

import nl.coinsweb.cbim.Concept;
import nl.coinsweb.sdk.CoinsParty;
import nl.coinsweb.sdk.FileManager;
import nl.coinsweb.sdk.integration.IntegrationHelper;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Iterator;

/**
 * Usecase A5:
 *
 *    Do tests with  MBITestP8.rdf
 *
 *
 *
 *
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class A5_MBITestP8 {

  protected static final Logger log = LoggerFactory.getLogger(A5_MBITestP8.class);

  static CoinsParty defaultPerson = new CoinsParty("http://sandbox.rws.nl/defaultUser");
  static File container = new File("/tmp/coinstest/MBITestP8.ccr");



  /**
   * This test covers the use of the FileManager to help the various construction routes of CoinsContainers.
   *
   * 1) starting from an existing rdf-file with imports to rdf/owl/ttl libraries
   * 2) opening an existing ccr-file
   */
  @BeforeClass
  public static void createCcrFromLibraries() {

    if(container.exists()) {
      return;
    }






    // 1) A container from an rdf file

    // Access the source file to build the container from
    File instanceFile = IntegrationHelper.getResourceFile("A5", "MBITestP8.rdf");



    // Constructing this container will copy the external libraries to a temp folder
    JenaCoinsContainer ccr = new JenaCoinsContainer(instanceFile.toString(), "http://www.example.com/");




    // Prepare the file manager to be able to find external libraries
//    File coreModelFile = IntegrationHelper.getResourceFile("A5", "Cbim-2.0.rdf");
//    FileManager.registerLibrary(coreModelFile.toURI(), null, ccr.getAvailableLibraryFiles());

    File otlFile = IntegrationHelper.getResourceFile("A5", "otl-coins-2016-02-09.ttl");
    FileManager.registerLibrary(otlFile.toURI(), null, ccr.getAvailableLibraryFiles());

    // Try again to load all libraries now the otl is registered
    ccr.addNamedModelForImports(ccr.getCoinsGraphSet().getInstanceModel());


    ccr.export(container.toString());

    ccr.asExpertCoinsModel().close();



  }



  public void listInstancesInCcr() {

    // 2) Loading an existing container

    log.info("#will reload now");

    JenaCoinsContainer reloaded = new JenaCoinsContainer("/tmp/coinstest/MBITestP8.ccr", "http://www.example.com/");

    reloaded.listIndividuals();
    reloaded.listClasses();

    log.info("#will search now");

    Iterator<Concept> iterator = reloaded.listIndividuals(Concept.class);
    while(iterator.hasNext()) {
      log.info(iterator.next().getUri());
    }


  }


}