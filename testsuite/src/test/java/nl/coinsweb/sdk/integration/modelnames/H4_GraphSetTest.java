package nl.coinsweb.sdk.integration.modelnames;

import nl.coinsweb.sdk.CoinsParty;
import nl.coinsweb.sdk.FileManager;
import nl.coinsweb.sdk.integration.IntegrationHelper;
import nl.coinsweb.sdk.jena.FusekiGraphSet;
import nl.coinsweb.sdk.jena.InMemGraphSet;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import nl.coinsweb.sdk.jena.TDBStoreGraphSet;
import nl.coinsweb.sdk.validator.Profile;
import nl.coinsweb.sdk.validator.Validator;
import org.junit.AfterClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class H4_GraphSetTest {

  protected static final Logger log = LoggerFactory.getLogger(H4_GraphSetTest.class);


  @AfterClass
  public static void cleanup() {
    FileManager.destroyAll();
  }




  @Test
  public void inmem_graphtest() {


    InMemGraphSet graphSet = new InMemGraphSet("http://playground.com/");
    JenaCoinsContainer model = new JenaCoinsContainer(new CoinsParty("http://playground.com/"), graphSet, false, false);


    try {
      Profile profile = Profile.loadProfile(new FileInputStream(IntegrationHelper.getResourceFile("H4", "graphtest.profile")));

      Path reportFile = Paths.get("/tmp/inmem_graphtest.html");

      Validator validator = new Validator(model, profile.getName());
      validator.validate(reportFile);

    } catch (FileNotFoundException e) {
      log.error(e.getMessage(), e);
    }
  }

  @Test
  public void tdb_graphtest() {


    InMemGraphSet graphSet = new TDBStoreGraphSet("http://playground.com/");
    JenaCoinsContainer model = new JenaCoinsContainer(new CoinsParty("http://p"), graphSet, false, false);


    try {
      Profile profile = Profile.loadProfile(new FileInputStream(IntegrationHelper.getResourceFile("H4", "graphtest.profile")));

      Path reportFile = Paths.get("/tmp/tdb_graphtest.html");

      Validator validator = new Validator(model, profile.getName());
      validator.validate(reportFile);

    } catch (FileNotFoundException e) {
      log.error(e.getMessage(), e);
    }
  }

//  @Test
  public void fuseki_graphtest() {


    InMemGraphSet graphSet = new FusekiGraphSet("http://playground.com/", "http://docker:3030", "coins");
    JenaCoinsContainer model = new JenaCoinsContainer(new CoinsParty("http://p"), graphSet, false, false);


    try {
      Profile profile = Profile.loadProfile(new FileInputStream(IntegrationHelper.getResourceFile("H4", "graphtest.profile")));

      Path reportFile = Paths.get("/tmp/fuseki_graphtest.html");

      Validator validator = new Validator(model, profile.getName());
      validator.validate(reportFile);

    } catch (FileNotFoundException e) {
      log.error(e.getMessage(), e);
    }
  }

}