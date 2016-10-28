package nl.coinsweb.sdk.integration.modelling;

import nl.coinsweb.sdk.integration.DatasetAsserts;
import nl.coinsweb.sdk.integration.IntegrationHelper;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class B15_ReferenceModel {

  protected static final Logger log = LoggerFactory.getLogger(B15_ReferenceModel.class);


  @Test
  public void aOpenContainer() {

    JenaCoinsContainer model = new JenaCoinsContainer("http://playground.com/");

    model.load(IntegrationHelper.getResourceFile("F1", "WOAVoorbeeld.ccr").getAbsolutePath(), false);

    log.info("instance model");
    DatasetAsserts.logTriples(model.getCoinsGraphSet().getModel("http://www.buildingbits.nl/MBIContainer.rdf#"));

    log.info("woa model");
    DatasetAsserts.logTriples(model.getCoinsGraphSet().getModel("http://www.coinsweb.nl/voorbeeld#"));

    model.close();
  }




}