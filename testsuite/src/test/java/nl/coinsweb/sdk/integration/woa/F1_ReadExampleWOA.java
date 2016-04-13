package nl.coinsweb.sdk.integration.woa;

import nl.coinsweb.cbim.Part;
import nl.coinsweb.sdk.exceptions.WOAAccessDeniedException;
import nl.coinsweb.sdk.integration.DatasetAsserts;
import nl.coinsweb.sdk.integration.IntegrationHelper;
import nl.coinsweb.sdk.jena.InMemCoinsContainer;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
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

    JenaCoinsContainer model = new InMemCoinsContainer("http://playground.com/");

    model.load(IntegrationHelper.getResourceFile("F1", "WOAVoorbeeld.ccr").getAbsolutePath());




    Part landhoofd = new Part(model, "http://www.buildingbits.nl/validatieContainer.rdf#_BB526node1a1hg7ekvx25");
    log.debug(landhoofd.getDescription());



    expectedEx.expect(WOAAccessDeniedException.class);
    expectedEx.expectMessage("WOA restriction blocked operation.");

    Part fundering = new Part(model, "http://www.buildingbits.nl/validatieContainer.rdf#_BB846node1a1hg7ekvx13");
    log.debug(fundering.getDescription());

    Part viaduct = new Part(model, "http://www.buildingbits.nl/validatieContainer.rdf#_BB649node1a1hg7ekvx5");
    log.debug(viaduct.getDescription());




    log.info("instance model");
    DatasetAsserts.logTriples(model.getJenaModel("http://www.buildingbits.nl/MBIContainer.rdf#"));

    log.info("woa model");
    DatasetAsserts.logTriples(model.getJenaModel("http://www.coinsweb.nl/voorbeeld#"));

    model.close();
  }




}