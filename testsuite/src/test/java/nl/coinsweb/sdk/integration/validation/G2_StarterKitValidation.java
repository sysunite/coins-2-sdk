package nl.coinsweb.sdk.integration.validation;

import nl.coinsweb.sdk.CoinsParty;
import nl.coinsweb.sdk.FileManager;
import nl.coinsweb.sdk.ModelFactory;
import nl.coinsweb.sdk.integration.DatasetAsserts;
import nl.coinsweb.sdk.integration.IntegrationHelper;
import nl.coinsweb.sdk.integration.ZipAsserts;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import nl.coinsweb.sdk.jena.JenaModelFactory;
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
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class G2_StarterKitValidation {

  protected static final Logger log = LoggerFactory.getLogger(G2_StarterKitValidation.class);

  private Set<String> emptyZipContent;
  private Set<String> rdfZipContent;

  CoinsParty defaultPerson;

  @Before
  public void initAssertLists() {

    defaultPerson = new CoinsParty("http://sandbox.rws.nl/defaultUser");

    emptyZipContent = new HashSet<>();
    emptyZipContent.add("bim/");
    emptyZipContent.add("bim/content.rdf");
    emptyZipContent.add("bim/repository/cbim-2.0.rdf");
    emptyZipContent.add("bim/repository/units-2.0.rdf");
    emptyZipContent.add("bim/repository/COINSWOA.rdf");
    emptyZipContent.add("bim/repository/BranchVersioning.rdf");
    emptyZipContent.add("doc/");
    emptyZipContent.add("woa/woa.rdf");

    rdfZipContent = new HashSet<>();
    rdfZipContent.add("bim/repository/cbim-2.0.rdf");
    rdfZipContent.add("woa/woa.rdf");
  }

  @AfterClass
  public static void cleanup() {
    FileManager.destroyAll();
  }


  @Test
  public void readRdfFile() {

    JenaCoinsContainer model;
    try {
      File testFile = IntegrationHelper.getResourceFile("BS", "4.01" + ".rdf").getCanonicalFile();
      ModelFactory factory = new JenaModelFactory();
      model = new JenaCoinsContainer(factory, defaultPerson, testFile.toString(), "http://www.example.com/");

      File libFile = IntegrationHelper.getResourceFile("BS", "4.01" + "-lib.rdf").getCanonicalFile();
      model.addImport(libFile.getCanonicalPath(), null, true, true, true);

    } catch (IOException e) {
      e.printStackTrace();
      return;
    }

    ArrayList<File> verifyFiles = new ArrayList<>();
    verifyFiles.add(IntegrationHelper.getResourceFile("BS", "verification/" + "4.01" + ".nq"));
    verifyFiles.add(IntegrationHelper.getResourceFile("BS", "verification/cbim-2.0.nq"));

    rdfZipContent.add("bim/"+"4.01"+".rdf");
    rdfZipContent.add("bim/repository/"+"4.01"+"-lib.rdf");

    // Create the container
    assertEquals(model.getAttachments().size(), 0);
    model.export(Paths.get("/tmp/coinstest/starterskit" + "4.01" + "_inmem.ccr").toFile().toString());
    assertTrue(DatasetAsserts.verifyCompleteContent(model, verifyFiles.iterator()));
    model.close();
    assertTrue(ZipAsserts.containsFiles(new File("/tmp/coinstest/starterskit" + "4.01" + "_inmem.ccr"), rdfZipContent, false));

    rdfZipContent.remove("bim/"+"4.01"+".rdf");
    rdfZipContent.remove("bim/repository/"+"4.01"+"-lib.rdf");

    // Reopen the ccr
    ModelFactory factory = new JenaModelFactory();
    JenaCoinsContainer reopenend = new JenaCoinsContainer(factory, defaultPerson, "/tmp/coinstest/starterskit"+"4.01"+"_inmem.ccr", "http://www.example.com/");

    assertTrue(DatasetAsserts.verifyCompleteContent(reopenend, verifyFiles.iterator()));

    // Do asserts on objects
    assertEquals("4.01", model.getContainerId());

    JenaValidationExecutor executor = new JenaValidationExecutor();

    Validator validator = new Validator(model, executor, "COINS-2.0-Lite");

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