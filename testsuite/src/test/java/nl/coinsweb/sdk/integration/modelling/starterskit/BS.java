package nl.coinsweb.sdk.integration.modelling.starterskit;

import nl.coinsweb.sdk.CoinsParty;
import nl.coinsweb.sdk.FileManager;
import nl.coinsweb.sdk.integration.DatasetAsserts;
import nl.coinsweb.sdk.integration.IntegrationHelper;
import nl.coinsweb.sdk.integration.ZipAsserts;
import nl.coinsweb.sdk.jena.InMemCoinsContainer;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import nl.coinsweb.sdk.jena.TDBCoinsContainer;
import org.apache.jena.riot.RDFFormat;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BS {

  protected static final Logger log = LoggerFactory.getLogger(BS.class);

  private Set<String> emptyZipContent;
  private Set<String> rdfZipContent;

  CoinsParty defaultPerson;

  @Before
  public void initAssertLists() {





    defaultPerson = new CoinsParty("http://sandbox.rws.nl/defaultUser");


    emptyZipContent = new HashSet<>();
    emptyZipContent.add("bim/");
    emptyZipContent.add("bim/content.rdf");
    emptyZipContent.add("bim/repository/Cbim-2.0.rdf");
    emptyZipContent.add("bim/repository/units-2.0.rdf");
    emptyZipContent.add("bim/repository/COINSWOA.rdf");
    emptyZipContent.add("doc/");
    emptyZipContent.add("woa/");

    rdfZipContent = new HashSet<>();
    rdfZipContent.add("bim/repository/Cbim-2.0.rdf");
//    rdfZipContent.add("doc/units-2.0.rdf");
//    rdfZipContent.add("doc/COINSWOA.rdf");
  }

  @AfterClass
  public static void cleanup() {
    FileManager.destroyAll();
  }






  @Test public void aReadRdfFileRdf() { readRdfFile("2.01"); }
//  @Test public void bReadRdfFileRdf() { readRdfFile("2.02"); }
//  @Test public void cReadRdfFileRdf() { readRdfFile("2.03"); }
//  @Test public void dReadRdfFileRdf() { readRdfFile("2.04"); }
//  @Test public void eReadRdfFileRdf() { readRdfFile("2.05"); }
//  @Test public void fReadRdfFileRdf() { readRdfFile("2.06"); }
//  @Test public void gReadRdfFileRdf() { readRdfFile("2.07"); }
//  @Test public void hReadRdfFileRdf() { readRdfFile("2.08"); }
//  @Test public void iReadRdfFileRdf() { readRdfFile("2.09"); }
//  @Test public void jReadRdfFileRdf() { readRdfFile("2.10"); }
//  @Test public void kReadRdfFileRdf() { readRdfFile("2.11"); }
//  @Test public void lReadRdfFileRdf() { readRdfFile("2.12"); }
//  @Test public void mReadRdfFileRdf() { readRdfFile("3.01"); }
//  @Test public void nReadRdfFileRdf() { readRdfFile("3.02"); }
//  @Test public void oReadRdfFileRdf() { readRdfFile("3.03"); }
//  @Test public void pReadRdfFileRdf() { readRdfFile("3.04"); }
//  @Test public void qReadRdfFileRdf() { readRdfFile("4.01"); }
//  @Test public void rReadRdfFileRdf() { readRdfFile("4.02"); }
//  @Test public void sReadRdfFileRdf() { readRdfFile("4.03"); }
//  @Test public void tReadRdfFileRdf() { readRdfFile("4.04"); }
//  @Test public void uReadRdfFileRdf() { readRdfFile("5.01"); }


  public void readRdfFile(String nr) {






    JenaCoinsContainer inMemModel;
    JenaCoinsContainer tdbModel;
    try {
      File testFile = IntegrationHelper.getResourceFile("BS", nr + ".rdf").getCanonicalFile();
      inMemModel = new InMemCoinsContainer(defaultPerson, testFile.toString(), "http://www.example.com/");
      tdbModel = new TDBCoinsContainer(defaultPerson, testFile.toString(), "http://www.example.com/");
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }


    ArrayList<File> verifyFiles = new ArrayList<>();
    verifyFiles.add(IntegrationHelper.getResourceFile("BS", nr + ".nq"));
    verifyFiles.add(IntegrationHelper.getResourceFile("BS", "Cbim-2.0.nq"));


    rdfZipContent.add("bim/"+nr+".rdf");


    // Create the container
    JenaCoinsContainer inMemCcr = inMemModel;
    inMemCcr.exportOwlModel("/tmp/coinstest/content.rdf", RDFFormat.RDFXML);
    inMemCcr.exportOwlModel("/tmp/coinstest/content.ttl", RDFFormat.TTL);
    inMemCcr.exportOwlModel("/tmp/coinstest/content.jsonld", RDFFormat.JSONLD);
    assertEquals(inMemCcr.getAttachments().size(), 0);
    inMemCcr.export(Paths.get("/tmp/coinstest/starterskit" + nr + "_inmem.ccr").toFile().toString());
    assertTrue(DatasetAsserts.verifyCompleteContent(inMemCcr, verifyFiles.iterator()));
    inMemCcr.close();
    assertTrue(ZipAsserts.containsFiles(new File("/tmp/coinstest/starterskit"+nr+"_inmem.ccr"), rdfZipContent, false));

    JenaCoinsContainer tdbCcr = tdbModel;
    tdbCcr.exportOwlModel("/tmp/coinstest/content.rdf", RDFFormat.RDFXML);
    tdbCcr.exportOwlModel("/tmp/coinstest/content.ttl", RDFFormat.TTL);
    tdbCcr.exportOwlModel("/tmp/coinstest/content.jsonld", RDFFormat.JSONLD);
    assertEquals(tdbCcr.getAttachments().size(), 0);
    tdbCcr.export(Paths.get("/tmp/coinstest/starterskit" + nr + "_tdb.ccr").toFile().toString());
    assertTrue(DatasetAsserts.verifyCompleteContent(tdbCcr, verifyFiles.iterator()));
    tdbCcr.close();

    assertTrue(ZipAsserts.containsFiles(new File("/tmp/coinstest/starterskit" + nr + "_tdb.ccr"), rdfZipContent, false));


    rdfZipContent.remove("bim/coinstest/"+nr+".rdf");



    // Reopen the ccr
    JenaCoinsContainer inMemCcr2 = new InMemCoinsContainer(defaultPerson, "/tmp/coinstest/starterskit"+nr+"_inmem.ccr", "http://www.example.com/");
    inMemCcr2.close();
    JenaCoinsContainer tdbCcr2 = new TDBCoinsContainer(defaultPerson, "/tmp/coinstest/starterskit"+nr+"_tdb.ccr", "http://www.example.com/");
    tdbCcr2.close();



  }



}