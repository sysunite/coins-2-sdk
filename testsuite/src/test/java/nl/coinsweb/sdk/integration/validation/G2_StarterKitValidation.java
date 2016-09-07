package nl.coinsweb.sdk.integration.validation;

import nl.coinsweb.sdk.CoinsParty;
import nl.coinsweb.sdk.FileManager;
import nl.coinsweb.sdk.integration.DatasetAsserts;
import nl.coinsweb.sdk.integration.IntegrationHelper;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import nl.coinsweb.sdk.jena.JenaValidationExecutor;
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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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



  CoinsParty defaultPerson;

  @Before
  public void initAssertLists() {

    defaultPerson = new CoinsParty("http://sandbox.rws.nl/defaultUser");


  }

  @AfterClass
  public static void cleanup() {
    FileManager.destroyAll();
  }


  @Test
  public void readRdfFile_4_01() {

    JenaCoinsContainer model;
    try {
      File testFile = IntegrationHelper.getResourceFile("BS", "4.01" + ".rdf").getCanonicalFile();
      model = new JenaCoinsContainer(defaultPerson, testFile.toString(), "http://www.example.com/");

      File libFile = IntegrationHelper.getResourceFile("BS", "4.01" + "-lib.rdf").getCanonicalFile();
      model.addImport(libFile.getCanonicalPath(), null, true, true, true);

    } catch (IOException e) {
      e.printStackTrace();
      return;
    }

    ArrayList<File> verifyFiles = new ArrayList<>();
    verifyFiles.add(IntegrationHelper.getResourceFile("BS", "verification/" + "4.01" + ".nq"));
    verifyFiles.add(IntegrationHelper.getResourceFile("BS", "verification/cbim-2.0.nq"));
    assertTrue(DatasetAsserts.verifyCompleteContent(model, verifyFiles.iterator()));

    assertEquals("4.01", model.getContainerId());






    JenaValidationExecutor executor = new JenaValidationExecutor();
    Validator validator = new Validator(model, executor, "COINS 2.0 Lite");
    validator.validate(Paths.get("/tmp/"));




    String reportHtml;
    try {
      reportHtml = new String(Files.readAllBytes(Paths.get("/tmp/report.html")), StandardCharsets.UTF_8);
      System.out.println(reportHtml);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }


  }


  @Test
  public void readRdfFile_performanceCheck() {


    JenaCoinsContainer model = new JenaCoinsContainer("http://playground.com/");

    model.load(IntegrationHelper.getResourceFile("G2", "PerformanceContainerCOINS2.0.ccr").getAbsolutePath());




    JenaValidationExecutor executor = new JenaValidationExecutor();
    Validator validator = new Validator(model, executor, "COINS 2.0 Lite");
    validator.validate(Paths.get("/tmp/"));




    String reportHtml;
    try {
      reportHtml = new String(Files.readAllBytes(Paths.get("/tmp/report.html")), StandardCharsets.UTF_8);
      System.out.println(reportHtml);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }


  }

}