package nl.coinsweb.sdk.integration.modelling;

import nl.coinsweb.cbim.*;
import nl.coinsweb.sdk.CoinsObject;
import nl.coinsweb.sdk.integration.DatasetAsserts;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import org.junit.Test;
import org.qudt.schema.qudt.LengthUnit;
import org.qudt.schema.qudt.Unit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */

public class B10_SettingUnits {

  protected static final Logger log = LoggerFactory.getLogger(B10_SettingUnits.class);


  @Test
  public void listUnits() {

    JenaCoinsContainer model = new JenaCoinsContainer("http://playground.com/");

    Assembly a = new Assembly(model);





    Iterator<String> individuals = model.listIndividualUris(LengthUnit.classUri).iterator();
    while(individuals.hasNext()) {
      System.out.println(individuals.next());

//      Result:
//      http://qudt.org/vocab/unit#Parsec
//      http://qudt.org/vocab/unit#Foot
//      http://qudt.org/vocab/unit#Pica
//      http://qudt.org/vocab/unit#AstronomicalUnit
//      http://qudt.org/vocab/unit#MilLength
//      http://qudt.org/vocab/unit#Chain
//      http://qudt.org/vocab/unit#LightYear
//      http://qudt.org/vocab/unit#PlanckLength
//      http://qudt.org/vocab/unit#Angstrom
//      http://qudt.org/vocab/unit#MicroInch
//      http://qudt.org/vocab/unit#Fermi
//      http://qudt.org/vocab/unit#Meter
//      http://qudt.org/vocab/unit#Yard
//      http://qudt.org/vocab/unit#Femtometer
//      http://qudt.org/vocab/unit#FootUSSurvey
//      http://qudt.org/vocab/unit#Kilometer
//      http://qudt.org/vocab/unit#Point
//      http://qudt.org/vocab/unit#Decimeter
//      http://qudt.org/vocab/unit#Millimeter
//      http://qudt.org/vocab/unit#NauticalMile
//      http://qudt.org/vocab/unit#Centimeter
//      http://qudt.org/vocab/unit#Fathom
//      http://qudt.org/vocab/unit#Furlong
//      http://qudt.org/vocab/unit#MileInternational
//      http://qudt.org/vocab/unit#Inch
//      http://qudt.org/vocab/unit#Micrometer
//      http://qudt.org/vocab/unit#MileUSStatute
//      http://qudt.org/vocab/unit#Rod
    }

    // create
    FloatProperty property = new FloatProperty(model);
    a.addHasProperties(property);

    property.setSimpleProperty(11.3f);
    property.setUnit(new LengthUnit(model, LengthUnit.MILLIMETER));

    // read
    FloatProperty fp = a.getHasProperties().next().as(FloatProperty.class);

    assertEquals(11.3f, fp.getSimpleProperty(), 0);

    Unit u = fp.getUnit();
    assertTrue(u.canAs(LengthUnit.class));

    try {

      // create
      UriProperty uriProperty = new UriProperty(model);
      a.addHasProperties(uriProperty);
      uriProperty.setSimpleProperty(new URI("http://www.nu.nl"));

      // read
      UriProperty up = model.listProperties(a.getUri(), UriProperty.class).next();
      assertEquals(new URI("http://www.nu.nl"), up.getSimpleProperty());

    } catch (URISyntaxException e) {}


    LocatorProperty locatorProperty = new LocatorProperty(model);
    locatorProperty.addObjectValue(new Locator(model));







    DatasetAsserts.logTriples(model.getCoinsGraphSet().getInstanceModel());
    assertEquals(28, DatasetAsserts.countTriples(model.getCoinsGraphSet().getInstanceModel()));



    Iterator<Map<String, String>> result = model.query("SELECT * WHERE { GRAPH <http://www.coinsweb.nl/units-2.0.rdf#> { ?s ?p ?o}}");
    while(result.hasNext()) {
      Map<String, String> row = result.next();
      log.debug(row.toString());
    }

    result = model.query("SELECT * WHERE { GRAPH <http://playground.com/> { ?s ?p ?o}}");
    while(result.hasNext()) {
      Map<String, String> row = result.next();
      log.debug(row.toString());
    }

    Iterator<CoinsObject> iterator = a.listProperties();
    while(iterator.hasNext()) {
      CoinsObject obj = iterator.next();
      log.debug(obj.getUri() + " with class " +obj.getClassUri());
    }

  }

  @Test
  public void listUnitsForExample() {

    JenaCoinsContainer model = new JenaCoinsContainer("http://playground.com/");

    Assembly a = new Assembly(model);

    Iterator<String> individuals = model.listIndividualUris(LengthUnit.classUri).iterator();
    while(individuals.hasNext()) {
      System.out.println(individuals.next());

        // Result:
        // ...
        // http://qudt.org/vocab/unit#Kilometer
        // http://qudt.org/vocab/unit#Point
        // http://qudt.org/vocab/unit#Decimeter
        // http://qudt.org/vocab/unit#Millimeter
        // http://qudt.org/vocab/unit#NauticalMile
        // ...
    }

    FloatProperty property = new FloatProperty(model);
    a.addHasProperties(property);

    property.setSimpleProperty(11.3f);
    property.setUnit(new LengthUnit(model, LengthUnit.MILLIMETER));


    DatasetAsserts.logTriples(model.getCoinsGraphSet().getInstanceModel());
    DatasetAsserts.logTriples(model.getCoinsGraphSet().getWoaModel());

    Iterator<Map<String, String>> result =  model.query("SELECT * WHERE { GRAPH <http://playground.com/> { ?s ?p ?o}}");
    while(result.hasNext()) {
      Map<String, String> row = result.next();
      log.debug(row.toString());
    }



  }
}