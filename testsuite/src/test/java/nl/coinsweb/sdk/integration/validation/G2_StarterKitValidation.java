package nl.coinsweb.sdk.integration.validation;


import nl.coinsweb.sdk.FileManager;
import nl.coinsweb.sdk.integration.DatasetAsserts;
import nl.coinsweb.sdk.integration.IntegrationHelper;
import nl.coinsweb.sdk.jena.FusekiGraphSet;
import nl.coinsweb.sdk.jena.InMemGraphSet;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import nl.coinsweb.sdk.jena.TDBStoreGraphSet;
import nl.coinsweb.sdk.validator.Validator;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class G2_StarterKitValidation {

  protected static final Logger log = LoggerFactory.getLogger(G2_StarterKitValidation.class);




  @AfterClass
  public static void cleanup() {
    FileManager.destroyAll();
  }


  @Test
  public void readRdfFile_4_01_from_rdf() {

    JenaCoinsContainer model;
    try {
      File testFile = IntegrationHelper.getResourceFile("BS", "4.01" + ".rdf").getCanonicalFile();
      model = new JenaCoinsContainer(testFile.toString());

      File libFile = IntegrationHelper.getResourceFile("BS", "4.01" + "-lib.rdf").getCanonicalFile();
      model.addImport(libFile.getCanonicalPath(), null, true, true, true);

    } catch (IOException e) {
      e.printStackTrace();
      return;
    }

    ArrayList<File> verifyFiles = new ArrayList<>();
    verifyFiles.add(IntegrationHelper.getResourceFile("BS", "verification/4.01.nq"));
    verifyFiles.add(IntegrationHelper.getResourceFile("BS", "verification/cbim-2.0.nq"));
    assertTrue(DatasetAsserts.verifyCompleteContent(model, verifyFiles.iterator()));

    assertEquals("4.01", model.getContainerId());



    validate(model, "report_4.01_from_rdf.html");


  }


  @Test
  public void readRdfFile_2_01() {

    JenaCoinsContainer model = new JenaCoinsContainer();
    model.load(IntegrationHelper.getResourceFile("G2", "starterskit2.01_inmem.ccr").getAbsolutePath(), false);
    validate(model, "report_2.01.html");
  }
  @Test
  public void readRdfFile_2_02() {

    JenaCoinsContainer model = new JenaCoinsContainer();
    model.load(IntegrationHelper.getResourceFile("G2", "starterskit2.02_inmem.ccr").getAbsolutePath(), false);
    validate(model, "report_2.02.html");
  }
  @Test
  public void readRdfFile_2_03() {

    JenaCoinsContainer model = new JenaCoinsContainer();
    model.load(IntegrationHelper.getResourceFile("G2", "starterskit2.03_inmem.ccr").getAbsolutePath(), false);
    validate(model, "report_2.03.html");
  }
  @Test
  public void readRdfFile_2_04() {

    JenaCoinsContainer model = new JenaCoinsContainer();
    model.load(IntegrationHelper.getResourceFile("G2", "starterskit2.04_inmem.ccr").getAbsolutePath(), false);
    validate(model, "report_2.04.html");
  }
  @Test
  public void readRdfFile_2_05() {

    JenaCoinsContainer model = new JenaCoinsContainer();
    model.load(IntegrationHelper.getResourceFile("G2", "starterskit2.05_inmem.ccr").getAbsolutePath(), false);
    validate(model, "report_2.05.html");
  }
  @Test
  public void readRdfFile_2_06() {

    JenaCoinsContainer model = new JenaCoinsContainer();
    model.load(IntegrationHelper.getResourceFile("G2", "starterskit2.06_inmem.ccr").getAbsolutePath(), false);
    validate(model, "report_2.06.html");
  }
  @Test
  public void readRdfFile_2_07() {

    JenaCoinsContainer model = new JenaCoinsContainer();
    model.load(IntegrationHelper.getResourceFile("G2", "starterskit2.07_inmem.ccr").getAbsolutePath(), false);
    validate(model, "report_2.07.html");
  }
  @Test
  public void readRdfFile_2_08() {

    JenaCoinsContainer model = new JenaCoinsContainer();
    model.load(IntegrationHelper.getResourceFile("G2", "starterskit2.08_inmem.ccr").getAbsolutePath(), false);
    validate(model, "report_2.08.html");
  }
  @Test
  public void readRdfFile_2_09() {

    JenaCoinsContainer model = new JenaCoinsContainer();
    model.load(IntegrationHelper.getResourceFile("G2", "starterskit2.09_inmem.ccr").getAbsolutePath(), false);
    validate(model, "report_2.09.html");
  }
  @Test
  public void readRdfFile_2_10() {

    JenaCoinsContainer model = new JenaCoinsContainer();
    model.load(IntegrationHelper.getResourceFile("G2", "starterskit2.10_inmem.ccr").getAbsolutePath(), false);
    validate(model, "report_2.10.html");
  }
  @Test
  public void readRdfFile_2_11() {

    JenaCoinsContainer model = new JenaCoinsContainer();
    model.load(IntegrationHelper.getResourceFile("G2", "starterskit2.11_inmem.ccr").getAbsolutePath(), false);
    validate(model, "report_2.11.html");
  }
  @Test
  public void readRdfFile_2_12() {

    JenaCoinsContainer model = new JenaCoinsContainer();
    model.load(IntegrationHelper.getResourceFile("G2", "starterskit2.12_inmem.ccr").getAbsolutePath(), false);
    validate(model, "report_2.12.html");
  }
  @Test
  public void readRdfFile_3_01() {

    JenaCoinsContainer model = new JenaCoinsContainer();
    model.load(IntegrationHelper.getResourceFile("G2", "starterskit3.01_inmem.ccr").getAbsolutePath(), false);
    validate(model, "report_3.01.html");
  }
  @Test
  public void readRdfFile_3_02() {

    JenaCoinsContainer model = new JenaCoinsContainer();
    model.load(IntegrationHelper.getResourceFile("G2", "starterskit3.02_inmem.ccr").getAbsolutePath(), false);
    validate(model, "report_3.02.html");
  }
  @Test
  public void readRdfFile_3_03() {

    JenaCoinsContainer model = new JenaCoinsContainer();
    model.load(IntegrationHelper.getResourceFile("G2", "starterskit3.03_inmem.ccr").getAbsolutePath(), false);
    validate(model, "report_3.03.html");
  }
  @Test
  public void readRdfFile_3_04() {

    JenaCoinsContainer model = new JenaCoinsContainer();
    model.load(IntegrationHelper.getResourceFile("G2", "starterskit3.04_inmem.ccr").getAbsolutePath(), false);
    validate(model, "report_3.04.html");
  }
  @Test
  public void readRdfFile_4_01() {

    JenaCoinsContainer model = new JenaCoinsContainer();
    model.load(IntegrationHelper.getResourceFile("G2", "starterskit4.01_inmem.ccr").getAbsolutePath(), false);
    validate(model, "report_4.01.html");
  }
  @Test
  public void readRdfFile_tdb_4_01() {

    InMemGraphSet graphSet = new TDBStoreGraphSet("http://playground.com/");
    JenaCoinsContainer model = new JenaCoinsContainer(graphSet, false, true);
    model.load(IntegrationHelper.getResourceFile("G2", "starterskit4.01_inmem.ccr").getAbsolutePath(), false);
    validate(model, "report_tdb_4.01.html");
  }
//  @Test
  public void readRdfFile_fuseki_4_01() {

    InMemGraphSet graphSet = new FusekiGraphSet("http://playground.com/", "http://docker:3030", "coins");
    JenaCoinsContainer model = new JenaCoinsContainer(graphSet, false, true);
    model.load(IntegrationHelper.getResourceFile("G2", "starterskit4.01_inmem.ccr").getAbsolutePath(), false);
    validate(model, "report_fuseki_4.01.html");
  }



//  @Test
  public void readRdfFile_performanceCheck() {

    JenaCoinsContainer model = new JenaCoinsContainer();
    model.load(IntegrationHelper.getResourceFile("G2", "PerformanceContainerCOINS2.0.ccr").getAbsolutePath(), false);
    validate(model, "report_PerfContCOINS2.0.html");
  }




//  @Test
  public void readRdfFile_inmem_performanceCheck() {

    JenaCoinsContainer model = new JenaCoinsContainer();
    model.load(IntegrationHelper.getResourceFile("G2", "PerformanceContainerCOINS2.0.ccr").getAbsolutePath(), false);
    validate(model, "report_inmem_PerfContCOINS2.0.html");
  }
//  @Test
  public void readRdfFile_tdb_performanceCheck() {

    InMemGraphSet graphSet = new TDBStoreGraphSet("http://playground.com/");
    JenaCoinsContainer model = new JenaCoinsContainer(graphSet, false, true);
    model.load(IntegrationHelper.getResourceFile("G2", "PerformanceContainerCOINS2.0.ccr").getAbsolutePath(), false);
    validate(model, "report_tdb_PerfContCOINS2.0.html");
  }
//  @Test
  public void readRdfFile_tdb_4_core_performanceCheck() {

    InMemGraphSet graphSet = new TDBStoreGraphSet("http://playground.com/");
    JenaCoinsContainer model = new JenaCoinsContainer(graphSet, false, true);
    model.load(IntegrationHelper.getResourceFile("G2", "PerformanceContainerCOINS2.0.ccr").getAbsolutePath(), false);
    validate(model, "report_tdb_4_core_PerfContCOINS2.0.html");
  }
  //  @Test
  public void readRdfFile_fuseki_performanceCheck() {

    log.trace("will start performance tests on fuseki");
    InMemGraphSet graphSet = new FusekiGraphSet("http://playground.com/", "http://docker:3030", "coins");
    JenaCoinsContainer model = new JenaCoinsContainer(graphSet, false, true);
    model.load(IntegrationHelper.getResourceFile("G2", "PerformanceContainerCOINS2.0.ccr").getAbsolutePath(), false);
    validate(model, "report_fuseki_PerfContCOINS2.0.html");
  }




  public void validate(JenaCoinsContainer model, String reportFileName) {
    validate(model, reportFileName, "COINS 2.0 Lite");
  }
  public void validate(JenaCoinsContainer model, String reportFileName, String profileName) {

    Path reportFile = Paths.get("/tmp/"+reportFileName);

    Validator validator = new Validator(model, profileName);
    validator.validate(reportFile);
  }
}