package nl.coinsweb.sdk.integration.modelling;

import nl.coinsweb.cbim.*;
import nl.coinsweb.cbim.Object;

import nl.coinsweb.sdk.ExpertCoinsModel;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import org.junit.Test;
import org.qudt.schema.qudt.LengthUnit;
import org.qudt.schema.qudt.Unit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * Usecase B6:
 *
 *    Cover all routes of loading imports into the CoinsModel.
 *
 *
 *
 *
 * @author Bastiaan Bijl, Sysunite 2016
 */

public class B6_DatasetUse {

  protected static final Logger log = LoggerFactory.getLogger(B6_DatasetUse.class);


  @Test
  public void listClasses() {

    JenaCoinsContainer model = new JenaCoinsContainer();



    StringProperty sp = new StringProperty(model);

    Set<String> classes = model.listClassUris(sp.getUri());
    assertTrue(classes.contains("http://www.coinsweb.nl/cbim-2.0.rdf#SimpleProperty"));
    assertTrue(classes.contains("http://www.w3.org/2002/07/owl#Thing"));
    assertTrue(classes.contains("http://www.coinsweb.nl/cbim-2.0.rdf#StringProperty"));
    assertTrue(classes.contains("http://www.coinsweb.nl/cbim-2.0.rdf#VersionObject"));
    assertTrue(classes.contains("http://www.w3.org/2000/01/rdf-schema#Resource"));
    assertTrue(classes.contains("http://www.coinsweb.nl/cbim-2.0.rdf#Entity"));
    assertTrue(classes.contains("http://www.coinsweb.nl/cbim-2.0.rdf#Concept"));
    assertTrue(classes.contains("http://www.coinsweb.nl/cbim-2.0.rdf#EntityProperty"));


  }


  public void addDecomposition(ExpertCoinsModel model, String parentUri, String childUri) {


    Assembly assembly = new Assembly(model,parentUri,true);
    assembly.getHasContainsRelation();


    ContainsRelation hs = new ContainsRelation(model);
    hs.setHasAssembly(assembly);
    Part part = new Part(model,childUri,true);
    hs.setHasPart(part);

  }

  @Test
  public void createFloat() {

    JenaCoinsContainer model = new JenaCoinsContainer();


    Object bovenbouwweg = new Object(model);
    bovenbouwweg.addType("http://otl.rws.nl/otl#OB00016");
    bovenbouwweg.setUserID("B1");


    Object toplaag = new Object(model);
    toplaag.setUserID("TL1");


    toplaag.addType("http://otl.rws.nl/otl#OB00802");
    addDecomposition(model, bovenbouwweg.getUri(), toplaag.getUri());



    nl.coinsweb.cbim.Object asfaltPlak = new Object(model);
    asfaltPlak.addType("http://otl.rws.nl/otl#OB02859");
    addDecomposition(model, toplaag.getUri(), asfaltPlak.getUri());
    FloatProperty dikte = new FloatProperty(model);
    dikte.addType("http://otl.rws.nl/otl#OB02859-PR00501");
    dikte.setSimpleProperty(10);
    dikte.setUnit(new LengthUnit(model).as(Unit.class));



    asfaltPlak.addObject("http://otl.rws.nl/otl#OB02859-PR00501-hasproperty", dikte);

  }


}