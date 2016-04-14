package nl.coinsweb.sdk.integration.modelling;

import nl.coinsweb.cbim.*;
import nl.coinsweb.cbim.Object;
import nl.coinsweb.sdk.integration.DatasetAsserts;
import nl.coinsweb.sdk.integration.IntegrationHelper;
import nl.coinsweb.sdk.jena.InMemCoinsContainer;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.qudt.schema.qudt.PowerUnit;
import org.qudt.schema.qudt.Unit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class B12_PumpExample {

  protected static final Logger log = LoggerFactory.getLogger(B12_PumpExample.class);


  @Test
  public void aCreateContainer() {

    JenaCoinsContainer model = new InMemCoinsContainer("http://zhejiangdayuan.com/");

    Assembly a = new Assembly(model);


    model.addAttachment(IntegrationHelper.getResourceFile("B12", "zhejiangdayuan.jpg").toPath().toString());


    a.setDescription("One way circular water pump conjuncture.");

    Object part1 = new Object(model);
    part1.addType("http://zhejiangdayuan.com/Inlet");
    part1.addType(Part.class);

    Object part2 = new Object(model);
    part2.addType("http://zhejiangdayuan.com/Motor");
    part2.addType(Part.class);

    ContainsRelation cr = new ContainsRelation(model);
    cr.setHasAssembly(a);








    StringProperty stringProperty = new StringProperty(model);
    part1.addHasProperties(stringProperty);
    stringProperty.setSimpleProperty("Code 134");

    IntegerProperty intProperty = new IntegerProperty(model);
    part2.addHasProperties(intProperty);
    intProperty.setSimpleProperty(240);
    intProperty.setUnit(new Unit(model, PowerUnit.WATT));

    model.export("/tmp/coinstest/zhejiangdayuan.ccr");



    DatasetAsserts.logTriples(model.getJenaModel());
  }


  @Test
  public void bReopenCcr() {

    JenaCoinsContainer model = new InMemCoinsContainer("/tmp/coinstest/zhejiangdayuan.ccr","http://zhejiangdayuan.com/");
    DatasetAsserts.logTriples(model.getJenaModel());
  }



}