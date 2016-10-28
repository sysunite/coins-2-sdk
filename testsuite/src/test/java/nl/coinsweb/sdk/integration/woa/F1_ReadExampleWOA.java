package nl.coinsweb.sdk.integration.woa;

import nl.coinsweb.cbim.Assembly;
import nl.coinsweb.cbim.Part;
import nl.coinsweb.sdk.exceptions.WOAAccessDeniedException;
import nl.coinsweb.sdk.integration.DatasetAsserts;
import nl.coinsweb.sdk.integration.IntegrationHelper;
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

    JenaCoinsContainer model = new JenaCoinsContainer("http://playground.com/");

    try {

      model.load(IntegrationHelper.getResourceFile("F1", "WOAVoorbeeld.ccr").getAbsolutePath(), false);

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
      DatasetAsserts.logTriples(model.getCoinsGraphSet().getModel("http://www.buildingbits.nl/MBIContainer.rdf#"));

      log.info("woa model");
      DatasetAsserts.logTriples(model.getCoinsGraphSet().getModel("http://www.coinsweb.nl/voorbeeld#"));

      model.close();
    }
  }


  @Test
  public void bDisabledWOA() {

    JenaCoinsContainer model = new JenaCoinsContainer("http://playground.com/");

    model.load(IntegrationHelper.getResourceFile("F1", "WOAVoorbeeld.ccr").getAbsolutePath(), false);

    model.disableWOA();

    Part landhoofd = new Part(model, "http://www.buildingbits.nl/validatieContainer.rdf#_BB526node1a1hg7ekvx25");
    log.debug(landhoofd.getDescription());

    Part fundering = new Part(model, "http://www.buildingbits.nl/validatieContainer.rdf#_BB846node1a1hg7ekvx13");
    log.debug(fundering.getDescription());

    Assembly viaduct = new Assembly(model, "http://www.buildingbits.nl/validatieContainer.rdf#_BB649node1a1hg7ekvx5");
    log.debug(viaduct.getDescription());

    log.info("instance model");
    DatasetAsserts.logTriples(model.getCoinsGraphSet().getModel("http://www.buildingbits.nl/MBIContainer.rdf#"));

    log.info("woa model");
    DatasetAsserts.logTriples(model.getCoinsGraphSet().getModel("http://www.coinsweb.nl/voorbeeld#"));

    model.close();
  }


//  @Test
//  public void cNewContainer() {
//
//    JenaCoinsContainer container = new JenaCoinsContainer("http://playground.com/");
//    Part landhoofd = new Part(container, "http://www.buildingbits.nl/validatieContainer.rdf#_BB526node1a1hg7ekvx25");
//
//    // Because the uri already exists, first we instantiate it
//    landhoofd.addType(container.getCoinsGraphSet().getWoaModel(), NoAccess.classUri);
//
//    // The object can now be instantiated as a NoAccess instance to set the layer depth
//    NoAccess noAccess = new NoAccess(container, container.getCoinsGraphSet().getWoaModel(), "http://www.buildingbits.nl/validatieContainer.rdf#_BB526node1a1hg7ekvx25");
//    noAccess.setLayerdepth(1);
//
//    for(Injector injector : container.getInjectors()) {
//      if(injector instanceof WOAInjector) {
//        ((WOAInjector)injector).buildCache();
//      }
//    }
//
//    try {
//
//      expectedEx.expect(WOAAccessDeniedException.class);
//      expectedEx.expectMessage("WOA restriction blocked operation.");
//
//      Part landhoofdAgain = new Part(container, "http://www.buildingbits.nl/validatieContainer.rdf#_BB526node1a1hg7ekvx25");
//
//    } finally {
//
//      log.info("instance model");
//      DatasetAsserts.logTriples(container.getCoinsGraphSet().getInstanceModel());
//
//      log.info("woa model");
//      DatasetAsserts.logTriples(container.getCoinsGraphSet().getWoaModel());
//
//      container.export("/tmp/newWoaContent.ccr");
//
//      container.close();
//
//
//
//
//      JenaCoinsContainer reopened = new JenaCoinsContainer("/tmp/newWoaContent.ccr", "http://playground.com/");
//
//      try {
//
//        expectedEx.expect(WOAAccessDeniedException.class);
//        expectedEx.expectMessage("WOA restriction blocked operation.");
//
//        Part landhoofdReopened = new Part(reopened, "http://www.buildingbits.nl/validatieContainer.rdf#_BB526node1a1hg7ekvx25");
//
//      } finally {
//
//        log.info("instance model");
//        DatasetAsserts.logTriples(reopened.getCoinsGraphSet().getInstanceModel());
//
//        log.info("woa model");
//        DatasetAsserts.logTriples(reopened.getCoinsGraphSet().getWoaModel());
//
//
//        container.close();
//      }
//
//    }
//
//  }

}