package nl.coinsweb.sdk.integration.gwwusecases;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import com.hp.hpl.jena.vocabulary.RDFS;
import nl.coinsweb.cbim.InternalDocumentReference;
import nl.coinsweb.cbim.StringProperty;
import nl.coinsweb.sdk.*;
import nl.coinsweb.sdk.exceptions.AttachmentNotFoundException;
import nl.coinsweb.sdk.integration.DatasetAsserts;
import nl.coinsweb.sdk.integration.IntegrationHelper;
import nl.coinsweb.sdk.integration.ZipAsserts;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import org.apache.jena.riot.RDFFormat;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Before
  public void initAssertLists() {

    emptyZipContent = new HashSet<>();
    emptyZipContent.add("bim/content.rdf");
    emptyZipContent.add("bim/repository/cbim-2.0.rdf");
    emptyZipContent.add("doc/");
    emptyZipContent.add("woa/woa.rdf");

    attachmentsZipContent = new HashSet<>();
    attachmentsZipContent.add("bim/content.rdf");
    attachmentsZipContent.add("bim/repository/cbim-2.0.rdf");
    attachmentsZipContent.add("doc/koekiemonster.jpeg");
    attachmentsZipContent.add("doc/plan-d117e182-6308-4826-98bd-378163d8814b (2).svg");
    attachmentsZipContent.add("woa/woa.rdf");

    rdfZipContent = new HashSet<>();
    rdfZipContent.add("bim/content.rdf");
    rdfZipContent.add("doc/");
    rdfZipContent.add("bim/repository/cbim-2.0.rdf");
    rdfZipContent.add("woa/woa.rdf");

    nodesPlant = new HashSet();
    nodesPlant.add(new ResourceImpl("http://example.com/tree"));
    nodesPlant.add(RDFS.subClassOf);
    nodesPlant.add(new ResourceImpl("http://example.com/plants"));
  }

  @AfterClass
  public static void cleanup() {
//    FileManager.destroyAll();
  }







  @Test
  public void createEmptyCcrTDB() {

    createEmptyCcr(new JenaCoinsContainer());

  }

  @Test
  public void createEmptyCcrExistingTDB() {

    createEmptyCcr(new JenaCoinsContainer());

  }

  @Test
  public void createEmptyCcrInMem() {

    createEmptyCcr(new JenaCoinsContainer());

  }


  public void createEmptyCcr(CoinsModel workspace) {

    // Create the container content
    JenaCoinsContainer ccr = new JenaCoinsContainer();
    ccr.export(Paths.get("/tmp/coinstest/empty.zip").toFile().toString());
    ccr.close();

    assertTrue(ZipAsserts.containsFiles(new File("/tmp/coinstest/empty.zip"), emptyZipContent, false));

    workspace.asExpertCoinsModel().close();
  }







//  @Test
//  public void createCcrWithAttachmentTDB() {
//    CoinsParty defaultPerson = new CoinsParty("http://sandbox.rws.nl/defaultUser");
//    CoinsGraphSet graphSet = new TDBStoreGraphSet("http://www.example.com/");
//    createCcrWithAttachment(new JenaCoinsContainer(defaultPerson, graphSet, true));
//  }

  @Test
  public void createCcrWithAttachmentInMem() {
    createCcrWithAttachment(new JenaCoinsContainer());
  }


  public void createCcrWithAttachment(CoinsModel workspace) {




    // Create the container content
    JenaCoinsContainer ccr = new JenaCoinsContainer();

    // Add an attachment
    ccr.addAttachment(IntegrationHelper.getResourceFile("A1", "koekiemonster.jpeg").toPath().toString());
    ccr.addAttachment(IntegrationHelper.getResourceFile("A1", "plan-d117e182-6308-4826-98bd-378163d8814b (2).svg").toPath().toString());




    assertEquals(ccr.getAttachments().size(),2);
    assertTrue(ccr.getAttachments().contains("koekiemonster.jpeg"));
    assertTrue(ccr.getAttachments().contains("plan-d117e182-6308-4826-98bd-378163d8814b (2).svg"));


    // Create the container file
    ccr.export(Paths.get("/tmp/coinstest/withattachment.zip").toFile().toString());
    ccr.close();




    assertTrue(ZipAsserts.containsFiles(new File("/tmp/coinstest/withattachment.zip"), attachmentsZipContent, false));



    workspace.asExpertCoinsModel().close();
  }









  @Test
  public void createCcrWithOneLineRdf() {

    // Create the container
    JenaCoinsContainer ccr = new JenaCoinsContainer();

    // Add the one line rdf
    ExpertCoinsModel instanceModel = ccr;
    instanceModel.addStatement("http://example.com/tree", RDFS.subClassOf.toString(), "http://example.com/plants");



    // Export all the rdf data to the container
    ccr.exportModel(ccr.getCoinsGraphSet().getInstanceModel(), "/tmp/coinstest/content.rdf", RDFFormat.RDFXML);
    ccr.exportModel(ccr.getCoinsGraphSet().getInstanceModel(), "/tmp/coinstest/content.ttl", RDFFormat.TTL);
    ccr.exportModel(ccr.getCoinsGraphSet().getInstanceModel(), "/tmp/coinstest/content.jsonld", RDFFormat.JSONLD);



    assertEquals(ccr.getAttachments().size(), 0);


    // Create the container file
    ccr.export(Paths.get("/tmp/coinstest/onelineofrdf.zip").toFile().toString());
    ccr.close();



    assertTrue(ZipAsserts.containsFiles(new File("/tmp/coinstest/onelineofrdf.zip"), rdfZipContent, false));


    // Reopen the ccr
    JenaCoinsContainer ccr2 = new JenaCoinsContainer("/tmp/coinstest/onelineofrdf.zip");
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
    JenaCoinsContainer emptyCcr = new JenaCoinsContainer();


    File otlFile = IntegrationHelper.getResourceFile("A5", "otl-coins-2016-02-09.ttl");
    FileManager.registerLibrary(otlFile.toURI(), new Namespace("http://otl.rws.nl/otl#"), emptyCcr.getAvailableLibraryFiles());


    emptyCcr.exportModel();
    emptyCcr.export("/tmp/coinstest/testLinkToCore.zip");

    log.info("#will reload now");


    JenaCoinsContainer reloaded = new JenaCoinsContainer("/tmp/coinstest/testLinkToCore.zip");
  }



  @Test
  public void createDocumentInStoreButNoFilePresentAsAttachment() {


    JenaCoinsContainer model = new JenaCoinsContainer();

    InternalDocumentReference doc = new InternalDocumentReference(model);
    StringProperty fileNameProperty = new StringProperty(model);
    fileNameProperty.setSimpleProperty("nonExisting.pdf");

    assertEquals(13, DatasetAsserts.countTriples(model.getCoinsGraphSet().getInstanceModel()));

    try {

      expectedEx.expect(AttachmentNotFoundException.class);
      expectedEx.expectMessage("Adding file nonExisting.pdf not found as Attachment, not connecting (instance Model now contains an orphaned String property).");

      doc.setFilepath(fileNameProperty);

    } finally {

      DatasetAsserts.logTriples(model.getCoinsGraphSet().getInstanceModel());
      assertEquals(13, DatasetAsserts.countTriples(model.getCoinsGraphSet().getInstanceModel()));

    }
  }



  @Test
  public void createDocumentInStoreAndFilePresentAsAttachment() {

    JenaCoinsContainer model = new JenaCoinsContainer();

    model.addAttachment(IntegrationHelper.getResourceFile("A1", "koekiemonster.jpeg").toPath().toString());

    DatasetAsserts.logTriples(model.getCoinsGraphSet().getInstanceModel());
    assertEquals(11, DatasetAsserts.countTriples(model.getCoinsGraphSet().getInstanceModel()));

    InternalDocumentReference doc = new InternalDocumentReference(model);
    StringProperty fileNameProperty = new StringProperty(model);
    fileNameProperty.setSimpleProperty("koekiemonster.jpeg");

    assertEquals(19, DatasetAsserts.countTriples(model.getCoinsGraphSet().getInstanceModel()));

    doc.setFilepath(fileNameProperty);

    DatasetAsserts.logTriples(model.getCoinsGraphSet().getInstanceModel());
    assertEquals(20, DatasetAsserts.countTriples(model.getCoinsGraphSet().getInstanceModel()));
  }

}