package nl.coinsweb.sdk.integration.modelling;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.StmtIterator;
import nl.coinsweb.sdk.integration.DatasetAsserts;
import nl.coinsweb.sdk.integration.IntegrationHelper;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class B13_OTL {

  protected static final Logger log = LoggerFactory.getLogger(B13_OTL.class);


  @Test
  public void aCreateContainer() {

    JenaCoinsContainer model = new JenaCoinsContainer("http://sandbox.com/");


    String fileToImport =  IntegrationHelper.getResourceFile("B13", "otl-coins-subset-2016-02-09.ttl").toPath().toString();
    model.addImport(fileToImport, "http://otl.rws.nl/otl#", true, true, true);

    OntModel ontModel = model.getCoinsGraphSet().getInstanceOntModel();


    log.info("available classes:");
    Iterator<String> iterator = model.listClasses();
    while(iterator.hasNext()) {
      String name = iterator.next();
      System.out.println(name);
    }

    OntClass clazz = model.getCoinsGraphSet().getJenaOntModel("http://otl.rws.nl/otl#").getOntClass("http://otl.rws.nl/otl#OB00043");
    StmtIterator classIterator = clazz.listProperties();
    while(classIterator.hasNext()) {
      log.info(classIterator.next().toString());
    }



    DatasetAsserts.logTriples(ontModel);

    model.export("/tmp/coinstest/otl.ccr");

  }

  @Test
  public void bReloadContainer() {

    JenaCoinsContainer model = new JenaCoinsContainer("/tmp/coinstest/otl.ccr", "http://sandbox.com/");

    DatasetAsserts.logTriples(model.getCoinsGraphSet().getInstanceOntModel());
  }





}