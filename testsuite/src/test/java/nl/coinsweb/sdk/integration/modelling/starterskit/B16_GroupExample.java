package nl.coinsweb.sdk.integration.modelling.starterskit;

import nl.coinsweb.cbim.ContainsRelation;
import nl.coinsweb.cbim.ContainsRelationGroup;
import nl.coinsweb.sdk.integration.DatasetAsserts;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */

public class B16_GroupExample {

  protected static final Logger log = LoggerFactory.getLogger(B16_GroupExample.class);


  @Test
  public void listUnitsForExample() {

    JenaCoinsContainer model = new JenaCoinsContainer();

    ContainsRelationGroup group = new ContainsRelationGroup(model);

    ContainsRelation a = new ContainsRelation(model);
    ContainsRelation b = new ContainsRelation(model);

    group.addGroups(a);
    group.addGroups(b);


    ContainsRelationGroup groupReloaded = new ContainsRelationGroup(model, group.getUri());
    Iterator i = groupReloaded.getGroups();
    while(i.hasNext()) {
      i.next();
      log.info("found one");
    }

    DatasetAsserts.logTriples(model.getCoinsGraphSet().getInstanceModel());


  }
}