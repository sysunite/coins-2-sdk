package nl.coinsweb.sdk.integration.modelling;


import nl.coinsweb.cbim.Assembly;
import nl.coinsweb.cbim.IntegerProperty;
import nl.coinsweb.cbim.StringProperty;
import nl.coinsweb.sdk.ModelFactory;
import nl.coinsweb.sdk.integration.DatasetAsserts;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import nl.coinsweb.sdk.jena.JenaModelFactory;
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

    ModelFactory factory = new JenaModelFactory();
    JenaCoinsContainer model = new JenaCoinsContainer(factory, "http://playground.com/");

    Assembly a = new Assembly(model);

    a.setDescription("F15");


    StringProperty stringProperty = new StringProperty(model);
    a.addHasProperties(stringProperty);
    stringProperty.setSimpleProperty("Delayed delivery.");

    IntegerProperty intProperty = new IntegerProperty(model);
    a.addHasProperties(intProperty);
    intProperty.setSimpleProperty(111);
    intProperty.setUnit(new Unit(model, "http://qudt.org/vocab/unit#Millimeter"));





    DatasetAsserts.logTriples(model.getCoinsGraphSet().getInstanceModel());
    assertEquals(22, DatasetAsserts.countTriples(model.getCoinsGraphSet().getInstanceModel()));

    intProperty.removeSimpleProperty(112);
    assertEquals(22, DatasetAsserts.countTriples(model.getCoinsGraphSet().getInstanceModel()));

    intProperty.removeSimpleProperty(111);
    assertEquals(21, DatasetAsserts.countTriples(model.getCoinsGraphSet().getInstanceModel()));

    a.removeAllHasProperties();
    assertEquals(19, DatasetAsserts.countTriples(model.getCoinsGraphSet().getInstanceModel()));
  }


}