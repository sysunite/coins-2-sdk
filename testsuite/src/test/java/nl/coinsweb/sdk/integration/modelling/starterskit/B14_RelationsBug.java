package nl.coinsweb.sdk.integration.modelling.starterskit;

import nl.coinsweb.cbim.Connection;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import nl.coinsweb.sdk.jena.TDBCoinsContainer;
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
public class B14_RelationsBug {

  protected static final Logger log = LoggerFactory.getLogger(B14_RelationsBug.class);


  @Test
  public void aCreateContainer() {

    JenaCoinsContainer cc = new TDBCoinsContainer("http://sandbox.com/");




    nl.coinsweb.cbim.Object obj1 = new nl.coinsweb.cbim.Object(cc);
    nl.coinsweb.cbim.Object obj2 = new nl.coinsweb.cbim.Object(cc);

    Connection con1 = new Connection(cc);
    con1.addHasConnectedObjects(obj2);
    Connection con2 = new Connection(cc);
    con2.addHasConnectedObjects(obj1);

    obj1.addHasConnections(con1);
    obj2.addHasConnections(con2);

    Iterator iterator = obj1.getHasConnections();
    while(iterator.hasNext()){
      System.out.println(((Connection)iterator.next()).getUri());
    }

  }







}