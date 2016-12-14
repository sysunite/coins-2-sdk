package nl.coinsweb.sdk.integration.d.owlgenerator;

import com.playground.elephant.*;

import nl.coinsweb.sdk.ExpertCoinsModel;
import nl.coinsweb.sdk.exceptions.CoinsObjectCastNotAllowedException;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Usecase D4:
 *
 *    Validate the completeness of the owl generator with the elephant example.

 *
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class D4_ElephantExample {

  protected static final Logger log = LoggerFactory.getLogger(D4_ElephantExample.class);



  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  ExpertCoinsModel model;
  Olifant olifant;
  Dier dier;

  @Before
  public void init() {

    model = new JenaCoinsContainer();
    olifant = new Olifant(model);
    dier = new Dier(model);
  }



  /**
   * Casting
   */


  @Test
  public void castUp() {

    // Cast up
    Dier dierOlifant = olifant.as(Dier.class);              // Casting up is allowed
  }

  @Test
  public void castDown() {

    // Create olifant
    Olifant olifant = new Olifant(model);
    String olifantUri = olifant.getUri();

    Dier olifantDierReloaded = new Olifant(model, olifantUri).as(Dier.class);
    Olifant olifantReloaded = olifantDierReloaded.as(Olifant.class); // Casting down is allowed if the instance has a type definition in the model

    try {

      expectedEx.expect(CoinsObjectCastNotAllowedException.class);
      expectedEx.expectMessage("Could not cast to com.playground.elephant.Olifant.");

      // Cast down
      Olifant olifantDier = dier.as(Olifant.class);           // Casting down is not allowed if there is not such a type definition

    } finally {

      // Cast down again after adding type definition
      dier.addType(Olifant.class);
      Olifant olifantDier2 = dier.as(Olifant.class);           // Casting down is allowed now
    }
  }

  @Test
  public void castDown2() {



    Dier olifantDier = new Dier(model);

    // Cast down after adding type definition
    olifantDier.addType(Olifant.class);
    Olifant olifantDier2 = olifantDier.as(Olifant.class);           // Casting down is allowed now




    Dier olifantDier3 = new Dier(model);

    // Cast down after adding type definition
    olifantDier3.addType("http://playground.com/elephant#Olifant");
    Olifant olifantDier4 = olifantDier3.as(Olifant.class);           // Casting down is allowed now





    // Create olifant
    Olifant olifant1 = new Olifant(model);
    String olifantUri = olifant1.getUri();

    Dier olifantDier5 = new Olifant(model, olifantUri).as(Dier.class);
    Olifant olifant2 = olifantDier5.as(Olifant.class); // Casting down is allowed if the instance has a type definition in the model
  }



  @Test
  public void castWrong() {



    expectedEx.expect(CoinsObjectCastNotAllowedException.class);
    expectedEx.expectMessage("Could not cast to com.playground.elephant.Tafelpoot.");

    // Incompatible cast
    olifant.as(Tafelpoot.class);
  }


  /**
   * Overloading properties
   */


  @Test
  public void overload() {

    Lichaamsdeel tand1 = new Slagtand(model).as(Lichaamsdeel.class);
    olifant.addHeeftOnderdeel(tand1.as(Slagtand.class));

    Pilaar tand2 = new Slagtand(model).as(Pilaar.class);
    olifant.addHeeftOnderdeel(tand2.as(Slagtand.class));                       // Allowed cast


    Tafel tafel = new Tafel(model);

    try {

      expectedEx.expect(CoinsObjectCastNotAllowedException.class);
      expectedEx.expectMessage("Could not cast to com.playground.elephant.Tafelpoot.");
      tafel.addHeeftOnderdeel(tand1.as(Tafelpoot.class));                        // Currently not allowed cast

    } finally {

      try {

        expectedEx.expect(CoinsObjectCastNotAllowedException.class);
        expectedEx.expectMessage("Could not cast to com.playground.elephant.Tafelpoot.");
        tafel.addHeeftOnderdeel(tand2.as(Tafelpoot.class));                        // Currently not allowed cast

      } finally {


        tand1.addType(Tafelpoot.class);
        tand2.addType("http://playground.com/elephant#Tafelpoot");


        tafel.addHeeftOnderdeel(tand1.as(Tafelpoot.class));                        // Now allowed
        tafel.addHeeftOnderdeel(tand2.as(Tafelpoot.class));                        // Now allowed

        tand1.removeType("http://playground.com/elephant#Tafelpoot");
        tand2.removeType(Tafelpoot.class);

        try {

          expectedEx.expect(CoinsObjectCastNotAllowedException.class);
          expectedEx.expectMessage("Could not cast to com.playground.elephant.Tafelpoot.");
          tafel.addHeeftOnderdeel(tand1.as(Tafelpoot.class));                        // Not anymore allowed

        } finally {

          expectedEx.expect(CoinsObjectCastNotAllowedException.class);
          expectedEx.expectMessage("Could not cast to com.playground.elephant.Tafelpoot.");
          tafel.addHeeftOnderdeel(tand2.as(Tafelpoot.class));                        // Not anymore allowed

        }
      }
    }
  }
}