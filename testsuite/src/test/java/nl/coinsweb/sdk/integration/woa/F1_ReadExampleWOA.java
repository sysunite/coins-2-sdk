package nl.coinsweb.sdk.integration.woa;

import nl.coinsweb.sdk.integration.DatasetAsserts;
import nl.coinsweb.sdk.integration.IntegrationHelper;
import nl.coinsweb.sdk.jena.InMemCoinsContainer;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import nl.coinsweb.sdk.jena.TDBCoinsContainer;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class F1_ReadExampleWOA {

  protected static final Logger log = LoggerFactory.getLogger(F1_ReadExampleWOA.class);


  @Test
  public void aCreateContainer() {

    JenaCoinsContainer model = new TDBCoinsContainer("http://zhejiangdayuan.com/");

    model.load(IntegrationHelper.getResourceFile("F1", "WOAVoorbeeld.ccr").getAbsolutePath());




    DatasetAsserts.logTriples(model.getJenaModel());
  }


//  @Test
  public void bReopenCcr() {

    JenaCoinsContainer model = new InMemCoinsContainer("/tmp/coinstest/zhejiangdayuan.ccr","http://zhejiangdayuan.com/");
    DatasetAsserts.logTriples(model.getJenaModel());
  }



}