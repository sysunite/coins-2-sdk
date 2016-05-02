package nl.coinsweb.sdk.integration.woa;

import nl.coinsweb.cbim.Assembly;
import nl.coinsweb.cbim.Part;
import nl.coinsweb.coinswoa.NoAccess;
import nl.coinsweb.sdk.ModelFactory;
import nl.coinsweb.sdk.exceptions.WOAAccessDeniedException;
import nl.coinsweb.sdk.integration.DatasetAsserts;
import nl.coinsweb.sdk.integration.IntegrationHelper;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import nl.coinsweb.sdk.jena.JenaModelFactory;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class F1_ReadExampleWOA {

  protected static final Logger log = LoggerFactory.getLogger(F1_ReadExampleWOA.class);

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();


  @Test
  public void aOpenContainer() {

    ModelFactory factory = new JenaModelFactory();
    JenaCoinsContainer model = new JenaCoinsContainer(factory, "http://playground.com/");

    try {

      model.load(IntegrationHelper.getResourceFile("F1", "WOAVoorbeeld.ccr").getAbsolutePath());

      Part landhoofd = new Part(model, "http://www.buildingbits.nl/validatieContainer.rdf#_BB526node1a1hg7ekvx25");
      log.debug(landhoofd.getDescription());

      expectedEx.expect(WOAAccessDeniedException.class);
      expectedEx.expectMessage("WOA restriction blocked operation.");

      Part fundering = new Part(model, "http://www.buildingbits.nl/validatieContainer.rdf#_BB846node1a1hg7ekvx13");
      log.debug(fundering.getDescription());

    } finally {

      Assembly viaduct = new Assembly(model, "http://www.buildingbits.nl/validatieContainer.rdf#_BB649node1a1hg7ekvx5");
      log.debug(viaduct.getDescription());

      log.info("instance model");
      DatasetAsserts.logTriples(model.getJenaModel("http://www.buildingbits.nl/MBIContainer.rdf#"));

      log.info("woa model");
      DatasetAsserts.logTriples(model.getJenaModel("http://www.coinsweb.nl/voorbeeld#"));

      model.close();
    }
  }


  @Test
  public void bDisabledWOA() {

    ModelFactory factory = new JenaModelFactory();
    JenaCoinsContainer model = new JenaCoinsContainer(factory, "http://playground.com/");

    model.load(IntegrationHelper.getResourceFile("F1", "WOAVoorbeeld.ccr").getAbsolutePath());

    model.disableWOA();

    Part landhoofd = new Part(model, "http://www.buildingbits.nl/validatieContainer.rdf#_BB526node1a1hg7ekvx25");
    log.debug(landhoofd.getDescription());

    Part fundering = new Part(model, "http://www.buildingbits.nl/validatieContainer.rdf#_BB846node1a1hg7ekvx13");
    log.debug(fundering.getDescription());

    Assembly viaduct = new Assembly(model, "http://www.buildingbits.nl/validatieContainer.rdf#_BB649node1a1hg7ekvx5");
    log.debug(viaduct.getDescription());

    log.info("instance model");
    DatasetAsserts.logTriples(model.getJenaModel("http://www.buildingbits.nl/MBIContainer.rdf#"));

    log.info("woa model");
    DatasetAsserts.logTriples(model.getJenaModel("http://www.coinsweb.nl/voorbeeld#"));

    model.close();
  }


  @Test
  public void cNewContainer() {

    ModelFactory factory = new JenaModelFactory();
    JenaCoinsContainer model = new JenaCoinsContainer(factory, "http://playground.com/");
    Part landhoofd = new Part(model, "http://www.buildingbits.nl/validatieContainer.rdf#_BB526node1a1hg7ekvx25");

    NoAccess noAccess = new NoAccess(model, model.getWoaModel(), "http://www.buildingbits.nl/validatieContainer.rdf#_BB526node1a1hg7ekvx25");


    try {


//      Part landhoofdAgain = new Part(model, "http://www.buildingbits.nl/validatieContainer.rdf#_BB526node1a1hg7ekvx25");
//
//
//      expectedEx.expect(WOAAccessDeniedException.class);
//      expectedEx.expectMessage("WOA restriction blocked operation.");



    } finally {



      log.info("instance model");
      DatasetAsserts.logTriples(model.getJenaModel());

      log.info("woa model");
      DatasetAsserts.logTriples(model.getWoaModel());

      model.close();
    }





  }

}