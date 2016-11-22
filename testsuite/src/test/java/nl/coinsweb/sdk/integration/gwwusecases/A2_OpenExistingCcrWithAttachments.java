package nl.coinsweb.sdk.integration.gwwusecases;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import nl.coinsweb.sdk.CoinsModel;

import nl.coinsweb.sdk.FileManager;
import nl.coinsweb.sdk.integration.IntegrationHelper;
import nl.coinsweb.sdk.integration.ZipAsserts;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * Usecase A2:
 *
 *    Create new coins container.
 *
 *
 *
 *
 * @author Bastiaan Bijl, Sysunite 2016
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class A2_OpenExistingCcrWithAttachments {

  protected static final Logger log = LoggerFactory.getLogger(A2_OpenExistingCcrWithAttachments.class);

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();





  @Test
  public void aOpenCcr() {

    File file = IntegrationHelper.getResourceFile("A2", "MBICOINS2.ccr");
    JenaCoinsContainer ccr = new JenaCoinsContainer(file.toString());
  }





  @Test
  public void bOpenNonexistingCcr() {

    expectedEx.expect(RuntimeException.class);
    expectedEx.expectMessage("Supplied file does not exist.");

    JenaCoinsContainer ccr = new JenaCoinsContainer("/tmp/coinstest/not-existing.ccr");
    ccr.close();
  }









  @Test
  public void cOpenCcr() {

    // Open test container from resources
    File file = IntegrationHelper.getResourceFile("A2", "sample2.ccr");

    // Read the container content
    JenaCoinsContainer ccr = new JenaCoinsContainer(file.toString());


    // Zip the result
    ccr.export("/tmp/coinstest/rezipped_sample.ccr");
    ccr.close();


    assertTrue(ZipAsserts.containsFiles(new File("/tmp/coinstest/rezipped_sample.ccr"), groverAttachmentsWithoutFolders, false));

  }







  @Test
  public void dCreateTwoCcrsAtTheSameTimeInMem() {
    dCreateTwoCcrsAtTheSameTime(
        new JenaCoinsContainer(true),
        new JenaCoinsContainer(true));
  }

  public void dCreateTwoCcrsAtTheSameTime(CoinsModel workspace1, CoinsModel workspace2) {

    // The one
    File file1 = IntegrationHelper.getResourceFile("A2","sample.ccr");
    JenaCoinsContainer ccr1 = new JenaCoinsContainer(file1.toString());


    // The other
    File file2 = IntegrationHelper.getResourceFile("A2","sample2.ccr");
    JenaCoinsContainer ccr2 = new JenaCoinsContainer(file2.toString());





    // Zip the result
    ccr1.export("/tmp/coinstest/rezipped_sample1.ccr");
    ccr1.close();


    assertTrue(ZipAsserts.containsFiles(new File("/tmp/coinstest/rezipped_sample1.ccr"), koekiemonsterAttachmentsWithFolders, false));



    // Zip the result
    ccr2.export("/tmp/coinstest/rezipped_sample2.ccr");
    ccr2.close();


    assertTrue(ZipAsserts.containsFiles(new File("/tmp/coinstest/rezipped_sample2.ccr"), groverAttachmentsWithoutFolders, false));


    workspace1.asExpertCoinsModel().close();
    workspace2.asExpertCoinsModel().close();
  }




  private Set<String> groverAttachmentsWithoutFolders;
  private Set<String> koekiemonsterAttachmentsWithFolders;

  private Set<RDFNode> sampleNodeRenzo;
  private Set<RDFNode> sampleNodePeter;

  @Before
  public void initAssertLists() {

    groverAttachmentsWithoutFolders = new HashSet<>();
    groverAttachmentsWithoutFolders.add("bim/sample.rdf");
    groverAttachmentsWithoutFolders.add("doc/TopBraid Composer Free v0.2.pdf");
    groverAttachmentsWithoutFolders.add("bim/repository/Cbim-2.0-RC18.rdf");
    groverAttachmentsWithoutFolders.add("bim/repository/Cbim-2.0-Units-RC3.rdf");
    groverAttachmentsWithoutFolders.add("bim/repository/Cbim-2.0-WOA-RC2.rdf");
    groverAttachmentsWithoutFolders.add("doc/Voorbeelden COINS2.0 v0_9.pdf");
    groverAttachmentsWithoutFolders.add("doc/grover.jpeg");
    groverAttachmentsWithoutFolders.add("woa/woa.rdf");


    koekiemonsterAttachmentsWithFolders = new HashSet<>();
    koekiemonsterAttachmentsWithFolders.add("bim/");
    koekiemonsterAttachmentsWithFolders.add("doc/");
    koekiemonsterAttachmentsWithFolders.add("bim/sample.rdf");
    koekiemonsterAttachmentsWithFolders.add("doc/TopBraid Composer Free v0.2.pdf");
    koekiemonsterAttachmentsWithFolders.add("bim/repository/Cbim-2.0-RC18.rdf");
    koekiemonsterAttachmentsWithFolders.add("bim/repository/Cbim-2.0-Units-RC3.rdf");
    koekiemonsterAttachmentsWithFolders.add("bim/repository/Cbim-2.0-WOA-RC2.rdf");
    koekiemonsterAttachmentsWithFolders.add("doc/Voorbeelden COINS2.0 v0_9.pdf");
    koekiemonsterAttachmentsWithFolders.add("doc/koekiemonster.jpeg");
    koekiemonsterAttachmentsWithFolders.add("woa/woa.rdf");


    sampleNodeRenzo = new HashSet<>();
    sampleNodeRenzo.add(new ResourceImpl("http://qudt.org/vocab/unit#Meter"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#BooleanProperty"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#booleanValue"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#creator"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#DateTimeProperty"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#dateTimeValue"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#expired"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#filePath"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#FloatProperty"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#floatValue"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#IntegerProperty"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#integerValue"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#LocalDocument"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#name"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#nextTrunkVersion"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#Organisation"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#Person"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#Realisation"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#StringProperty"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#stringValue"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#unit"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#versionID"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.coinsweb.nl/mijnbib"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.coinsweb.nl/mijnbib#Column"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.coinsweb.nl/sample"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.coinsweb.nl/sample#BooleanProperty_Aanwezig"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.coinsweb.nl/sample#DateTimeProperty_Vandaag"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.coinsweb.nl/sample#FloatProperty_Eenkwart"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.coinsweb.nl/sample#IntegerProperty_Geheel"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.coinsweb.nl/sample#LocalDocument_NEN-normen"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.coinsweb.nl/sample#Organisation_COINS"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.coinsweb.nl/sample#Person_Renzo"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.coinsweb.nl/sample#Person_Renzo2"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.coinsweb.nl/sample#Realisation_Column1"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.coinsweb.nl/sample#StringProperty_filepathNEN-normen"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.w3.org/2002/07/owl#imports"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.w3.org/2002/07/owl#Ontology"));
    sampleNodeRenzo.add(new ResourceImpl("http://www.w3.org/2002/07/owl#versionInfo"));

    sampleNodePeter = new HashSet<>();
    sampleNodePeter.add(new ResourceImpl("http://qudt.org/vocab/unit#Meter"));
    sampleNodePeter.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf"));
    sampleNodePeter.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#BooleanProperty"));
    sampleNodePeter.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#booleanValue"));
    sampleNodePeter.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#creator"));
    sampleNodePeter.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#DateTimeProperty"));
    sampleNodePeter.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#dateTimeValue"));
    sampleNodePeter.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#expired"));
    sampleNodePeter.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#filePath"));
    sampleNodePeter.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#FloatProperty"));
    sampleNodePeter.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#floatValue"));
    sampleNodePeter.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#IntegerProperty"));
    sampleNodePeter.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#integerValue"));
    sampleNodePeter.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#LocalDocument"));
    sampleNodePeter.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#name"));
    sampleNodePeter.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#nextTrunkVersion"));
    sampleNodePeter.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#Organisation"));
    sampleNodePeter.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#Person"));
    sampleNodePeter.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#Realisation"));
    sampleNodePeter.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#StringProperty"));
    sampleNodePeter.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#stringValue"));
    sampleNodePeter.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#unit"));
    sampleNodePeter.add(new ResourceImpl("http://www.coinsweb.nl/cbim-2.0.rdf#versionID"));
    sampleNodePeter.add(new ResourceImpl("http://www.coinsweb.nl/mijnbib"));
    sampleNodePeter.add(new ResourceImpl("http://www.coinsweb.nl/mijnbib#Column"));
    sampleNodePeter.add(new ResourceImpl("http://www.coinsweb.nl/sample"));
    sampleNodePeter.add(new ResourceImpl("http://www.coinsweb.nl/sample#BooleanProperty_Aanwezig"));
    sampleNodePeter.add(new ResourceImpl("http://www.coinsweb.nl/sample#DateTimeProperty_Vandaag"));
    sampleNodePeter.add(new ResourceImpl("http://www.coinsweb.nl/sample#FloatProperty_Eenkwart"));
    sampleNodePeter.add(new ResourceImpl("http://www.coinsweb.nl/sample#IntegerProperty_Geheel"));
    sampleNodePeter.add(new ResourceImpl("http://www.coinsweb.nl/sample#LocalDocument_NEN-normen"));
    sampleNodePeter.add(new ResourceImpl("http://www.coinsweb.nl/sample#Organisation_COINS"));
    sampleNodePeter.add(new ResourceImpl("http://www.coinsweb.nl/sample#Person_Peter"));
    sampleNodePeter.add(new ResourceImpl("http://www.coinsweb.nl/sample#Person_Peter2"));
    sampleNodePeter.add(new ResourceImpl("http://www.coinsweb.nl/sample#Realisation_Column1"));
    sampleNodePeter.add(new ResourceImpl("http://www.coinsweb.nl/sample#StringProperty_filepathNEN-normen"));
    sampleNodePeter.add(new ResourceImpl("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
    sampleNodePeter.add(new ResourceImpl("http://www.w3.org/2002/07/owl#imports"));
    sampleNodePeter.add(new ResourceImpl("http://www.w3.org/2002/07/owl#Ontology"));
    sampleNodePeter.add(new ResourceImpl("http://www.w3.org/2002/07/owl#versionInfo"));
  }

  @AfterClass
  public static void cleanup() {
    FileManager.destroyAll();
  }



}
