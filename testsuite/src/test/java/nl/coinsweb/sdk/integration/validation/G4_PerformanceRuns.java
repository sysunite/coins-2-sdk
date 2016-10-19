package nl.coinsweb.sdk.integration.validation;

import nl.coinsweb.sdk.CoinsGraphSet;
import nl.coinsweb.sdk.CoinsParty;
import nl.coinsweb.sdk.integration.IntegrationHelper;
import nl.coinsweb.sdk.jena.InMemGraphSet;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import nl.coinsweb.sdk.jena.TDBStoreGraphSet;
import nl.coinsweb.sdk.validator.Validator;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class G4_PerformanceRuns {

  protected static final Logger log = LoggerFactory.getLogger(G4_PerformanceRuns.class);






//  @AfterClass
//  public static void cleanup() {
//    FileManager.destroyAll();
//  }



  public void runTest(CoinsGraphSet graphSet, int threads, String ccrFile, String reportName) {

    Validator.QUERY_THREAD_POOL_SIZE = threads;
    JenaCoinsContainer model = new JenaCoinsContainer(new CoinsParty("http://sandbox.rws.nl/defaultUser"), graphSet, false, true);
    model.load(IntegrationHelper.getResourceFile("G2", ccrFile).getAbsolutePath());
    Path reportFile = Paths.get("/tmp/"+reportName);
    Validator validator = new Validator(model, "COINS 2.0 Lite");
    validator.validate(reportFile);
  }

  @Test
  public void run_401() {
    runTest(new InMemGraphSet("http://playground.com/"), 1, "starterskit4.01_inmem.ccr", "401-inmem-1.html");
    runTest(new InMemGraphSet("http://playground.com/"), 4, "starterskit4.01_inmem.ccr", "401-inmem-4.html");
    runTest(new TDBStoreGraphSet("http://playground.com/"), 1, "starterskit4.01_inmem.ccr", "401-tdb-1.html");
    runTest(new TDBStoreGraphSet("http://playground.com/"), 4, "starterskit4.01_inmem.ccr", "401-tdb-4.html");
//    runTest(new FusekiGraphSet("http://playground.com/", "http://docker:3030", "coins"), 1, "starterskit4.01_inmem.ccr", "401-fuseki-1.html");
//    runTest(new FusekiGraphSet("http://playground.com/", "http://docker:3030", "coins"), 4, "starterskit4.01_inmem.ccr", "401-fuseki-4.html");
  }




  @Test
  public void run_performance() {
    runTest(new InMemGraphSet("http://playground.com/"), 1, "PerformanceContainerCOINS2.0.ccr", "perf-inmem-1.html");
    runTest(new InMemGraphSet("http://playground.com/"), 4, "PerformanceContainerCOINS2.0.ccr", "perf-inmem-4.html");
    runTest(new TDBStoreGraphSet("http://playground.com/"), 1, "PerformanceContainerCOINS2.0.ccr", "perf-tdb-1.html");
    runTest(new TDBStoreGraphSet("http://playground.com/"), 4, "PerformanceContainerCOINS2.0.ccr", "perf-tdb-4.html");
//    runTest(new FusekiGraphSet("http://playground.com/", "http://docker:3030", "coins"), 1, "PerformanceContainerCOINS2.0.ccr", "perf-fuseki-1.html");
//    runTest(new FusekiGraphSet("http://playground.com/", "http://docker:3030", "coins"), 4, "PerformanceContainerCOINS2.0.ccr", "perf-fuseki-4.html");

  }






}