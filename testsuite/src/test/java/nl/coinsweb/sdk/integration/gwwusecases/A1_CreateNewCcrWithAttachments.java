package nl.coinsweb.sdk.integration.gwwusecases;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import com.hp.hpl.jena.vocabulary.RDFS;
import nl.coinsweb.sdk.*;
import nl.coinsweb.sdk.integration.IntegrationHelper;
import nl.coinsweb.sdk.integration.ZipAsserts;
import nl.coinsweb.sdk.jena.InMemCoinsContainer;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import nl.coinsweb.sdk.jena.TDBCoinsContainer;
import org.apache.jena.riot.RDFFormat;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Usecase A1:
 *
 *    Create new coins container.
 *
 *
 *
 *
 * @author Bastiaan Bijl, Sysunite 2016
 */

public class A1_CreateNewCcrWithAttachments {

  protected static final Logger log = LoggerFactory.getLogger(A1_CreateNewCcrWithAttachments.class);

  private Set<String> emptyZipContent;
  private Set<String> attachmentsZipContent;
  private Set<String> rdfZipContent;
  private Set<RDFNode> nodesPlant;

  @Before
  public void initAssertLists() {

    emptyZipContent = new HashSet<>();
    emptyZipContent.add("bim/content.rdf");
    emptyZipContent.add("bim/repository/cbim-2.0.rdf");
    emptyZipContent.add("bim/repository/units-2.0.rdf");
    emptyZipContent.add("bim/repository/COINSWOA.rdf");
    emptyZipContent.add("doc/");
    emptyZipContent.add("woa/");

    attachmentsZipContent = new HashSet<>();
    attachmentsZipContent.add("bim/content.rdf");
    attachmentsZipContent.add("bim/repository/cbim-2.0.rdf");
    attachmentsZipContent.add("bim/repository/units-2.0.rdf");
    attachmentsZipContent.add("bim/repository/COINSWOA.rdf");
    attachmentsZipContent.add("doc/koekiemonster.jpeg");
    attachmentsZipContent.add("doc/plan-d117e182-6308-4826-98bd-378163d8814b (2).svg");
    attachmentsZipContent.add("woa/");

    rdfZipContent = new HashSet<>();
    rdfZipContent.add("bim/content.rdf");
    rdfZipContent.add("doc/");
    rdfZipContent.add("bim/repository/cbim-2.0.rdf");
    rdfZipContent.add("bim/repository/units-2.0.rdf");
    rdfZipContent.add("bim/repository/COINSWOA.rdf");

    nodesPlant = new HashSet();
    nodesPlant.add(new ResourceImpl("http://example.com/tree"));
    nodesPlant.add(RDFS.subClassOf);
    nodesPlant.add(new ResourceImpl("http://example.com/plants"));
  }

  @AfterClass
  public static void cleanup() {
    FileManager.destroyAll();
  }







  @Test
  public void createEmptyCcrTDB() {
    CoinsParty defaultPerson = new CoinsParty("http://sandbox.rws.nl/defaultUser");
    createEmptyCcr(new TDBCoinsContainer(defaultPerson, "http://www.example.com/"));
  }

  @Test
  public void createEmptyCcrInMem() {
    CoinsParty defaultPerson = new CoinsParty("http://sandbox.rws.nl/defaultUser");
    createEmptyCcr(new InMemCoinsContainer(defaultPerson, "http://www.example.com/"));
  }


  public void createEmptyCcr(CoinsModel workspace) {

    CoinsParty defaultPerson = new CoinsParty("http://sandbox.rws.nl/defaultUser");

    // Create the container content
    JenaCoinsContainer ccr = new InMemCoinsContainer(defaultPerson, "http://www.example.com/");
    ccr.export(Paths.get("/tmp/coinstest/empty.ccr").toFile().toString());
    ccr.close();

    assertTrue(ZipAsserts.containsFiles(new File("/tmp/coinstest/empty.ccr"), emptyZipContent, false));

    workspace.asExpertCoinsModel().close();
  }







  @Test
  public void createCcrWithAttachmentTDB() {
    CoinsParty defaultPerson = new CoinsParty("http://sandbox.rws.nl/defaultUser");
    createCcrWithAttachment(new TDBCoinsContainer(defaultPerson, "http://www.example.com/"));
  }

  @Test
  public void createCcrWithAttachmentInMem() {
    CoinsParty defaultPerson = new CoinsParty("http://sandbox.rws.nl/defaultUser");
    createCcrWithAttachment(new InMemCoinsContainer(defaultPerson, "http://www.example.com/"));
  }


  public void createCcrWithAttachment(CoinsModel workspace) {




    // Create the container content
    JenaCoinsContainer ccr = new InMemCoinsContainer("http://www.example.com/");

    // Add an attachment
    ccr.addAttachment(IntegrationHelper.getResourceFile("A1", "koekiemonster.jpeg").toPath().toString());
    ccr.addAttachment(IntegrationHelper.getResourceFile("A1", "plan-d117e182-6308-4826-98bd-378163d8814b (2).svg").toPath().toString());




    assertEquals(ccr.getAttachments().size(),2);
    assertTrue(ccr.getAttachments().contains("koekiemonster.jpeg"));
    assertTrue(ccr.getAttachments().contains("plan-d117e182-6308-4826-98bd-378163d8814b (2).svg"));


    // Create the container file
    ccr.export(Paths.get("/tmp/coinstest/withattachment.ccr").toFile().toString());
    ccr.close();




    assertTrue(ZipAsserts.containsFiles(new File("/tmp/coinstest/withattachment.ccr"), attachmentsZipContent, false));



    workspace.asExpertCoinsModel().close();
  }









  @Test
  public void createCcrWithOneLineRdf() {

    // Init the coins api
    CoinsParty defaultPerson = new CoinsParty("http://sandbox.rws.nl/defaultUser");


    // Create the container
    JenaCoinsContainer ccr = new InMemCoinsContainer(defaultPerson, "http://www.example.com/");

    // Add the one line rdf
    ExpertCoinsModel instanceModel = ccr;
    instanceModel.addStatement("http://example.com/tree", RDFS.subClassOf.toString(), "http://example.com/plants");



    // Export all the rdf data to the container
    ccr.exportOwlModel("/tmp/coinstest/content.rdf", RDFFormat.RDFXML);
    ccr.exportOwlModel("/tmp/coinstest/content.ttl", RDFFormat.TTL);
    ccr.exportOwlModel("/tmp/coinstest/content.jsonld", RDFFormat.JSONLD);



    assertEquals(ccr.getAttachments().size(), 0);


    // Create the container file
    ccr.export(Paths.get("/tmp/coinstest/onelineofrdf.ccr").toFile().toString());
    ccr.close();



    assertTrue(ZipAsserts.containsFiles(new File("/tmp/coinstest/onelineofrdf.ccr"), rdfZipContent, false));


    FileManager.destroy(ccr.getInternalRef());


    // Reopen the ccr
    JenaCoinsContainer ccr2 = new InMemCoinsContainer(defaultPerson, "/tmp/coinstest/onelineofrdf.ccr", "http://www.example.com/");
    ccr2.close();
  }





  /**
   * This test covers the use of the FileManager to help the various construction routes of CoinsContainers.
   *
   * 1) starting with an empty container imports to rdf/owl/ttl libraries
   * 2) opening an existing ccr-file
   */
  @Test
  public void createEmptyCcrWithLibraries() {

    // 1) An empty container
    JenaCoinsContainer emptyCcr = new InMemCoinsContainer("http://example.com");


    File otlFile = IntegrationHelper.getResourceFile("A5", "otl-coins-2016-02-09.ttl");
    FileManager.registerLibrary(otlFile.toURI(), new Namespace("http://otl.rws.nl/otl#"), emptyCcr.getAvailableLibraryFiles());


    emptyCcr.exportOwlModel();
    emptyCcr.export("/tmp/coinstest/testLinkToCore.ccr");

    log.info("#will reload now");


    JenaCoinsContainer reloaded = new InMemCoinsContainer("/tmp/coinstest/testLinkToCore.ccr", "http://www.example.com/");
  }

}