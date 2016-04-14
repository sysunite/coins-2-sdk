package nl.coinsweb.sdk.integration.modelling;

import nl.coinsweb.cbim.Assembly;
import nl.coinsweb.cbim.FloatProperty;
import nl.coinsweb.sdk.CoinsObject;
import nl.coinsweb.sdk.integration.DatasetAsserts;
import nl.coinsweb.sdk.jena.InMemCoinsContainer;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import org.junit.Test;
import org.qudt.schema.qudt.LengthUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */

public class B10_SettingUnits {

  protected static final Logger log = LoggerFactory.getLogger(B10_SettingUnits.class);


  @Test
  public void listUnits() {

    JenaCoinsContainer model = new InMemCoinsContainer("http://playground.com/");

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

    FloatProperty property = new FloatProperty(model);
    a.addHasProperties(property);

    property.setSimpleProperty(11.3f);
    property.setUnit("a");
    property.setUnit(new LengthUnit(model, LengthUnit.MILLIMETER));




    DatasetAsserts.logTriples(model.getJenaModel());
    assertEquals(16, DatasetAsserts.countTriples(model.getJenaModel()));



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
}