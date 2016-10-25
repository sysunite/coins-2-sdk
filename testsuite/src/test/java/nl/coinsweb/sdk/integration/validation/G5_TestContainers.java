package nl.coinsweb.sdk.integration.validation;

import nl.coinsweb.sdk.CoinsGraphSet;
import nl.coinsweb.sdk.CoinsParty;
import nl.coinsweb.sdk.FileManager;
import nl.coinsweb.sdk.integration.IntegrationHelper;
import nl.coinsweb.sdk.jena.FusekiGraphSet;
import nl.coinsweb.sdk.jena.InMemGraphSet;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import nl.coinsweb.sdk.jena.TDBStoreGraphSet;
import nl.coinsweb.sdk.validator.Validator;
import org.junit.AfterClass;
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
public class G5_TestContainers {

  protected static final Logger log = LoggerFactory.getLogger(G5_TestContainers.class);

  @AfterClass
  public static void cleanup() {
    FileManager.destroyAll();
  }

  public void runTest(CoinsGraphSet graphSet, int threads, String ccrFile, String reportName) {

    Validator.QUERY_THREAD_POOL_SIZE = threads;
    JenaCoinsContainer model = new JenaCoinsContainer(new CoinsParty("http://sandbox.rws.nl/defaultUser"), graphSet, false, true);
    model.load(IntegrationHelper.getResourceFile("G5", ccrFile).getAbsolutePath());
    Path reportFile = Paths.get("/tmp/"+reportName);
    Validator validator = new Validator(model, "COINS 2.0 Lite");
    validator.validate(reportFile);
  }

  @Test
  public void inmem() {

    runTest(new InMemGraphSet("http://playground.com/"), 1, "VC_CardinalityCheck.ccr",  "VC_CARD-inmem-1.html");
    runTest(new InMemGraphSet("http://playground.com/"), 4, "VC_CardinalityCheck.ccr",  "VC_CARD-inmem-4.html");

    runTest(new InMemGraphSet("http://playground.com/"), 1, "VC_COINS.ccr",             "VC_COINS-inmem-1.html");
    runTest(new InMemGraphSet("http://playground.com/"), 4, "VC_COINS.ccr",             "VC_COINS-inmem-4.html");

    runTest(new InMemGraphSet("http://playground.com/"), 1, "VC_datatypeCheck.ccr",     "VC_DATATYPE-inmem-1.html");
    runTest(new InMemGraphSet("http://playground.com/"), 4, "VC_datatypeCheck.ccr",     "VC_DATATYPE-inmem-4.html");

    runTest(new InMemGraphSet("http://playground.com/"), 1, "VC_Disjoint.ccr",          "VC_DISJOINT-inmem-1.html");
    runTest(new InMemGraphSet("http://playground.com/"), 4, "VC_Disjoint.ccr",          "VC_DISJOINT-inmem-4.html");

    runTest(new InMemGraphSet("http://playground.com/"), 1, "VC_PropertyCheck.ccr",     "VC_PROP-inmem-1.html");
    runTest(new InMemGraphSet("http://playground.com/"), 4, "VC_PropertyCheck.ccr",     "VC_PROP-inmem-4.html");

    runTest(new InMemGraphSet("http://playground.com/"), 1, "VC_Transitief.ccr",        "VC_TRANS-inmem-1.html");
    runTest(new InMemGraphSet("http://playground.com/"), 4, "VC_Transitief.ccr",        "VC_TRANS-inmem-4.html");
  }

  @Test
  public void tdb() {

    runTest(new TDBStoreGraphSet("http://playground.com/"), 1, "VC_CardinalityCheck.ccr",  "VC_CARD-tdb-1.html");
    runTest(new TDBStoreGraphSet("http://playground.com/"), 4, "VC_CardinalityCheck.ccr",  "VC_CARD-tdb-4.html");

    runTest(new TDBStoreGraphSet("http://playground.com/"), 1, "VC_COINS.ccr",             "VC_COINS-tdb-1.html");
    runTest(new TDBStoreGraphSet("http://playground.com/"), 4, "VC_COINS.ccr",             "VC_COINS-tdb-4.html");

    runTest(new TDBStoreGraphSet("http://playground.com/"), 1, "VC_datatypeCheck.ccr",     "VC_DATATYPE-tdb-1.html");
    runTest(new TDBStoreGraphSet("http://playground.com/"), 4, "VC_datatypeCheck.ccr",     "VC_DATATYPE-tdb-4.html");

    runTest(new TDBStoreGraphSet("http://playground.com/"), 1, "VC_Disjoint.ccr",          "VC_DISJOINT-tdb-1.html");
    runTest(new TDBStoreGraphSet("http://playground.com/"), 4, "VC_Disjoint.ccr",          "VC_DISJOINT-tdb-4.html");

    runTest(new TDBStoreGraphSet("http://playground.com/"), 1, "VC_PropertyCheck.ccr",     "VC_PROP-tdb-1.html");
    runTest(new TDBStoreGraphSet("http://playground.com/"), 4, "VC_PropertyCheck.ccr",     "VC_PROP-tdb-4.html");

    runTest(new TDBStoreGraphSet("http://playground.com/"), 1, "VC_Transitief.ccr",        "VC_TRANS-tdb-1.html");
    runTest(new TDBStoreGraphSet("http://playground.com/"), 4, "VC_Transitief.ccr",        "VC_TRANS-tdb-4.html");
  }

//  @Test
  public void fuseki() {

    runTest(new FusekiGraphSet("http://playground.com/", "http://docker:3030", "coins"), 1, "VC_CardinalityCheck.ccr",  "VC_CARD-fuseki-1.html");
    runTest(new FusekiGraphSet("http://playground.com/", "http://docker:3030", "coins"), 4, "VC_CardinalityCheck.ccr",  "VC_CARD-fuseki-4.html");

    runTest(new FusekiGraphSet("http://playground.com/", "http://docker:3030", "coins"), 1, "VC_COINS.ccr",             "VC_COINS-fuseki-1.html");
    runTest(new FusekiGraphSet("http://playground.com/", "http://docker:3030", "coins"), 4, "VC_COINS.ccr",             "VC_COINS-fuseki-4.html");

    runTest(new FusekiGraphSet("http://playground.com/", "http://docker:3030", "coins"), 1, "VC_datatypeCheck.ccr",     "VC_DATATYPE-fuseki-1.html");
    runTest(new FusekiGraphSet("http://playground.com/", "http://docker:3030", "coins"), 4, "VC_datatypeCheck.ccr",     "VC_DATATYPE-fuseki-4.html");

    runTest(new FusekiGraphSet("http://playground.com/", "http://docker:3030", "coins"), 1, "VC_Disjoint.ccr",          "VC_DISJOINT-fuseki-1.html");
    runTest(new FusekiGraphSet("http://playground.com/", "http://docker:3030", "coins"), 4, "VC_Disjoint.ccr",          "VC_DISJOINT-fuseki-4.html");

    runTest(new FusekiGraphSet("http://playground.com/", "http://docker:3030", "coins"), 1, "VC_PropertyCheck.ccr",     "VC_PROP-fuseki-1.html");
    runTest(new FusekiGraphSet("http://playground.com/", "http://docker:3030", "coins"), 4, "VC_PropertyCheck.ccr",     "VC_PROP-fuseki-4.html");

    runTest(new FusekiGraphSet("http://playground.com/", "http://docker:3030", "coins"), 1, "VC_Transitief.ccr",        "VC_TRANS-fuseki-1.html");
    runTest(new FusekiGraphSet("http://playground.com/", "http://docker:3030", "coins"), 4, "VC_Transitief.ccr",        "VC_TRANS-fuseki-4.html");
  }
}