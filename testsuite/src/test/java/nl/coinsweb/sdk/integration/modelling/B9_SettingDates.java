package nl.coinsweb.sdk.integration.modelling;

import nl.coinsweb.cbim.Assembly;
import nl.coinsweb.sdk.ModelFactory;
import nl.coinsweb.sdk.integration.DatasetAsserts;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import nl.coinsweb.sdk.jena.JenaModelFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */

public class B9_SettingDates {

  protected static final Logger log = LoggerFactory.getLogger(B9_SettingDates.class);





  @Test
  public void setDate() {

    ModelFactory factory = new JenaModelFactory();
    JenaCoinsContainer model = new JenaCoinsContainer(factory, "http://playground.com/");

    Assembly a = new Assembly(model);
    a.setModificationDate(new Date());

    DatasetAsserts.logTriples(model.getJenaModel());
    assertEquals(11, DatasetAsserts.countTriples(model.getJenaModel()));
  }



}