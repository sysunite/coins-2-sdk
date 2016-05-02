package nl.coinsweb.sdk.integration.modelling;

import nl.coinsweb.cbim.Assembly;
import nl.coinsweb.cbim.EntityProperty;
import nl.coinsweb.sdk.ModelFactory;
import nl.coinsweb.sdk.integration.DatasetAsserts;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import nl.coinsweb.sdk.jena.JenaModelFactory;
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



    ModelFactory factory = new JenaModelFactory();
    JenaCoinsContainer model = new JenaCoinsContainer(factory, "http://playground.com/");

    assertEquals(7, DatasetAsserts.countTriples(model.getJenaModel()));
    model.addStatement("http://subj","http://subj","http://subj");
    assertEquals(8, DatasetAsserts.countTriples(model.getJenaModel()));
    model.removeStatement("http://subj", "http://subj", "http://subj");
    assertEquals(7, DatasetAsserts.countTriples(model.getJenaModel()));




  }

  @Test
  public void removeProperty() {



    ModelFactory factory = new JenaModelFactory();
    JenaCoinsContainer model = new JenaCoinsContainer(factory, "http://playground.com/");


    assertEquals(7, DatasetAsserts.countTriples(model.getJenaModel()));

    Assembly a = new Assembly(model);


    assertEquals(10, DatasetAsserts.countTriples(model.getJenaModel()));

    EntityProperty e = new EntityProperty(model);
    e.setName("something");
    a.addHasProperties(e);


    assertEquals(15, DatasetAsserts.countTriples(model.getJenaModel()));



    a.removeProperty(e);
//    model.removeProperty(a.getUri(), e);


    assertEquals(10, DatasetAsserts.countTriples(model.getJenaModel()));
  }

  @Test
  public void removeIndividualWithProperties() {

    ModelFactory factory = new JenaModelFactory();
    JenaCoinsContainer model = new JenaCoinsContainer(factory, "http://playground.com/");

    assertEquals(7, DatasetAsserts.countTriples(model.getJenaModel()));

    Assembly a = new Assembly(model);

    assertEquals(10, DatasetAsserts.countTriples(model.getJenaModel()));

    EntityProperty e = new EntityProperty(model);
    e.setName("something");
    a.addHasProperties(e);


    assertEquals(15, DatasetAsserts.countTriples(model.getJenaModel()));
    a.removeIndividualAndProperties();

    assertEquals(7, DatasetAsserts.countTriples(model.getJenaModel()));

  }

}