package nl.coinsweb.sdk.integration.modelling;

import nl.coinsweb.cbim.Assembly;
import nl.coinsweb.cbim.EntityProperty;
import nl.coinsweb.sdk.integration.DatasetAsserts;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */

public class B8_Removing {

  protected static final Logger log = LoggerFactory.getLogger(B8_Removing.class);




  @Test
  public void removeStatement() {


    JenaCoinsContainer model = new JenaCoinsContainer("http://playground.com/");

    assertEquals(7, DatasetAsserts.countTriples(model.getCoinsGraphSet().getInstanceModel()));
    model.addStatement("http://subj","http://subj","http://subj");
    assertEquals(8, DatasetAsserts.countTriples(model.getCoinsGraphSet().getInstanceModel()));
    model.removeStatement("http://subj", "http://subj", "http://subj");
    assertEquals(7, DatasetAsserts.countTriples(model.getCoinsGraphSet().getInstanceModel()));




  }

  @Test
  public void removeProperty() {



    JenaCoinsContainer model = new JenaCoinsContainer("http://playground.com/");


    assertEquals(7, DatasetAsserts.countTriples(model.getCoinsGraphSet().getInstanceModel()));

    Assembly a = new Assembly(model);


    assertEquals(10, DatasetAsserts.countTriples(model.getCoinsGraphSet().getInstanceModel()));

    EntityProperty e = new EntityProperty(model);
    e.setName("something");
    a.addHasProperties(e);


    assertEquals(15, DatasetAsserts.countTriples(model.getCoinsGraphSet().getInstanceModel()));



    a.removeProperty(e);
//    model.removeProperty(a.getUri(), e);


    assertEquals(10, DatasetAsserts.countTriples(model.getCoinsGraphSet().getInstanceModel()));
  }

  @Test
  public void removeIndividualWithProperties() {

    JenaCoinsContainer model = new JenaCoinsContainer("http://playground.com/");

    assertEquals(7, DatasetAsserts.countTriples(model.getCoinsGraphSet().getInstanceModel()));

    Assembly a = new Assembly(model);

    assertEquals(10, DatasetAsserts.countTriples(model.getCoinsGraphSet().getInstanceModel()));

    EntityProperty e = new EntityProperty(model);
    e.setName("something");
    a.addHasProperties(e);


    assertEquals(15, DatasetAsserts.countTriples(model.getCoinsGraphSet().getInstanceModel()));
    a.removeIndividualAndProperties();

    assertEquals(7, DatasetAsserts.countTriples(model.getCoinsGraphSet().getInstanceModel()));

  }

}