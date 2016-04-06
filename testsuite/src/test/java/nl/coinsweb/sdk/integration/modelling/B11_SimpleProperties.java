package nl.coinsweb.sdk.integration.modelling;


import nl.coinsweb.cbim.Assembly;
import nl.coinsweb.cbim.IntegerProperty;
import nl.coinsweb.cbim.StringProperty;
import nl.coinsweb.sdk.integration.DatasetAsserts;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import nl.coinsweb.sdk.jena.TDBCoinsContainer;
import org.junit.Test;
import org.qudt.schema.qudt.Unit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */

public class B11_SimpleProperties {

  protected static final Logger log = LoggerFactory.getLogger(B11_SimpleProperties.class);


  @Test
  public void createProperties() {

    JenaCoinsContainer model = new TDBCoinsContainer("http://playground.com/");

    Assembly a = new Assembly(model);

    a.setDescription("baaa");


    StringProperty stringProperty = new StringProperty(model);
    a.addHasProperties(stringProperty);
    stringProperty.setSimpleProperty("Im hungry");

    IntegerProperty intProperty = new IntegerProperty(model);
    a.addHasProperties(intProperty);
    intProperty.setSimpleProperty(111);
    intProperty.setUnit(new Unit(model, "http://qudt.org/vocab/unit#Millimeter"));



    DatasetAsserts.logTriples(model.getJenaModel());
    assertEquals(21, DatasetAsserts.countTriples(model.getJenaModel()));
  }


}