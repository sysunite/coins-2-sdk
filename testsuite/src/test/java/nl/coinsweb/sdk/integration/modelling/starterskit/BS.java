package nl.coinsweb.sdk.integration.modelling.starterskit;

import nl.coinsweb.cbim.*;
import nl.coinsweb.cbim.Object;

import nl.coinsweb.sdk.FileManager;
import nl.coinsweb.sdk.RuntimeCoinsObject;
import nl.coinsweb.sdk.integration.DatasetAsserts;
import nl.coinsweb.sdk.integration.IntegrationHelper;
import nl.coinsweb.sdk.integration.ZipAsserts;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.qudt.schema.qudt.LengthUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BS {

  protected static final Logger log = LoggerFactory.getLogger(BS.class);

  private Set<String> emptyZipContent;
  private Set<String> rdfZipContent;

  @Before
  public void initAssertLists() {


    emptyZipContent = new HashSet<>();
    emptyZipContent.add("bim/");
    emptyZipContent.add("bim/content.rdf");
    emptyZipContent.add("bim/repository/cbim-2.0.rdf");
    emptyZipContent.add("doc/");
    emptyZipContent.add("woa/woa.rdf");

    rdfZipContent = new HashSet<>();
    rdfZipContent.add("bim/repository/cbim-2.0.rdf");
    rdfZipContent.add("woa/woa.rdf");
  }

  @AfterClass
  public static void cleanup() {
    FileManager.destroyAll();
  }






  @Test 
  public void aReadRdfFileRdf() {

    // Create and test a container from rdf test input
    JenaCoinsContainer model = readRdfFile("2.01", false);

    // Do asserts on objects
    assertEquals("2.01", model.getContainerId());

    // Do asserts on objects
    Organisation coins = new Organisation(model, "http://rws.com/starterkit/2.01#Organisation_COINS");
    assertEquals("COINS", coins.getName());
    assertTrue(coins.hasAsClass("http://www.coinsweb.nl/cbim-2.0.rdf#CoinsContainerObject"));
    assertTrue(coins.canAs(CoinsContainerObject.class));

  }

  @Test 
  public void bReadRdfFileRdf() {

    // Create and test a container from rdf test input
    JenaCoinsContainer model = readRdfFile("2.02", false);

    // Do asserts on objects
    assertEquals("2.02", model.getContainerId());

    Person person = new Person(model, "http://rws.com/starterkit/2.02#Person_Ik");
    assertEquals("MijnNaam", person.getName());
    assertTrue(person.hasAsClass("http://www.coinsweb.nl/cbim-2.0.rdf#CoinsContainerObject"));
    assertTrue(person.canAs(CoinsContainerObject.class));

  }

  @Test 
  public void cReadRdfFileRdf() {

    // Create and test a container from rdf test input
    JenaCoinsContainer model = readRdfFile("2.03", false);

    // Do asserts on objects
    assertEquals("2.03", model.getContainerId());

    Person person = new Person(model, "http://rws.com/starterkit/2.03#Person_Ik");
    assertEquals("MijnNaam", person.getName());
    assertEquals(1, person.getVersionID());
    assertTrue(person.hasAsClass("http://www.coinsweb.nl/cbim-2.0.rdf#ExpiredEntity"));
    assertTrue(person.canAs(CoinsContainerObject.class));

    Person person2 = person.getNextVersion();
    assertEquals("http://rws.com/starterkit/2.03#Person_Ik2", person2.getUri());
    assertEquals("MijnVolledigeNaam", person2.getName());
    assertEquals(2, person2.getVersionID());
    assertTrue(person2.canAs(CoinsContainerObject.class));

    Person person2_again = new Person(model, "http://rws.com/starterkit/2.03#Person_Ik2");
    assertEquals("http://rws.com/starterkit/2.03#Person_Ik2", person2_again.getUri());
    assertEquals("MijnVolledigeNaam", person2_again.getName());
    assertEquals(2, person2_again.getVersionID());
    assertTrue(person2_again.canAs(CoinsContainerObject.class));

  }

  @Test 
  public void dReadRdfFileRdf() {

    // Create and test a container from rdf test input
    JenaCoinsContainer model = readRdfFile("2.04", false);

    // Do asserts on objects
    assertEquals("2.04", model.getContainerId());

    StringProperty property = new StringProperty(model, "http://rws.com/starterkit/2.04#StringProperty_filepathNEN-norm");
    assertEquals("NEN-normen.docx", property.getSimpleProperty());



  }

  @Test 
  public void eReadRdfFileRdf() {

    // Create and test a container from rdf test input
    JenaCoinsContainer model = readRdfFile("2.05", false);

    // Do asserts on objects
    assertEquals("2.05", model.getContainerId());

    BooleanProperty property = new BooleanProperty(model, "http://rws.com/starterkit/2.05#BooleanProperty_Aanwezig");
    assertEquals(false, property.getSimpleProperty().booleanValue());

  }

  @Test 
  public void fReadRdfFileRdf() {

    // Create and test a container from rdf test input
    JenaCoinsContainer model = readRdfFile("2.06", false);

    // Do asserts on objects
    assertEquals("2.06", model.getContainerId());

    DateTimeProperty property = new DateTimeProperty(model, "http://rws.com/starterkit/2.06#DateTimeProperty_Vandaag");

    Date date = null;
    try {
      date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2015-09-03T15:18:00");
    } catch (ParseException e) {
      log.error(e.getMessage(), e);
    }
    assertEquals(date, property.getSimpleProperty());

  }

  @Test 
  public void gReadRdfFileRdf() {

    // Create and test a container from rdf test input
    JenaCoinsContainer model = readRdfFile("2.07", false);

    // Do asserts on objects
    assertEquals("2.07", model.getContainerId());

    IntegerProperty property = new IntegerProperty(model, "http://rws.com/starterkit/2.07#IntegerProperty_Geheel");
    assertEquals(554, property.getSimpleProperty());

  }

  @Test 
  public void hReadRdfFileRdf() {

    // Create and test a container from rdf test input
    JenaCoinsContainer model = readRdfFile("2.08", false);

    // Do asserts on objects
    assertEquals("2.08", model.getContainerId());

    FloatProperty property = new FloatProperty(model, "http://rws.com/starterkit/2.08#FloatProperty_Eenkwart");
    assertEquals(0.25f, property.getSimpleProperty(), 0f);

  }

  @Test 
  public void iReadRdfFileRdf() {

    // Create and test a container from rdf test input
    JenaCoinsContainer model = readRdfFile("2.09", false);

    // Do asserts on objects
    assertEquals("2.09", model.getContainerId());

    FloatProperty property = new FloatProperty(model, "http://rws.com/starterkit/2.09#FloatProperty_Eenkwart");
    assertEquals(0.25f, property.getSimpleProperty(), 0f);
    assertEquals(LengthUnit.METER, property.getUnit().getUri());
    assertEquals(new LengthUnit(model, LengthUnit.METER), property.getUnit());

  }

  @Test 
  public void jReadRdfFileRdf() {

    // Create and test a container from rdf test input
    JenaCoinsContainer model = readRdfFile("2.10", false);

    // Do asserts on objects
    assertEquals("2.10", model.getContainerId());

    InternalDocumentReference doc = new InternalDocumentReference(model, "http://rws.com/starterkit/2.10#InternalDocument_NEN-normen");
    StringProperty property = doc.getFilepath();
    assertEquals(property, new StringProperty(model, "http://rws.com/starterkit/2.10#StringProperty_filepathNEN-norm"));
    assertEquals("NEN-normen.docx", property.getSimpleProperty());

  }

  @Test 
  public void kReadRdfFileRdf() {

    // Create and test a container from rdf test input
    JenaCoinsContainer model = readRdfFile("2.11", false);

    // Do asserts on objects
    assertEquals("2.11", model.getContainerId());

    InternalDocumentReference doc = new InternalDocumentReference(model, "http://rws.com/starterkit/2.11#InternalDocument_NEN-normen");
    StringProperty property = doc.getFilepath();
    assertEquals(property, new StringProperty(model, "http://rws.com/starterkit/2.11#StringProperty_filepathNEN-norm"));
    assertEquals("NEN-normen.docx", property.getSimpleProperty());

    Date date = null;
    try {
      date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2015-09-03T15:00:00");
    } catch (ParseException e) {
      log.error(e.getMessage(), e);
    }
    assertEquals(date, doc.getCreationDate());

    assertEquals("http://rws.com/starterkit/2.11#Person_Ik", doc.getCreator().getUri());

  }

  @Test 
  public void lReadRdfFileRdf() {

    // Create and test a container from rdf test input
    JenaCoinsContainer model = readRdfFile("2.12", true);

    // Do asserts on objects
    assertEquals("2.12", model.getContainerId());
    assertNotNull(model.getCoinsGraphSet().getModel("http://custom.library.com#"));

    RuntimeCoinsObject column = new RuntimeCoinsObject(model, "http://custom.library.com#Column", "http://rws.com/starterkit/2.12#Object_Column_1");
    assertEquals("Column1", column.getLiteralValue("http://www.coinsweb.nl/cbim-2.0.rdf#name", String.class));
    assertTrue(column.hasAsClass("http://www.coinsweb.nl/cbim-2.0.rdf#CoinsContainerObject"));
    assertTrue(column.canAs(CoinsContainerObject.class));

  }

  @Test 
  public void mReadRdfFileRdf() {

    // Create and test a container from rdf test input
    JenaCoinsContainer model = readRdfFile("3.01", false);

    // Do asserts on objects
    assertEquals("3.01", model.getContainerId());

    Object zitbank = new Object(model, "http://rws.com/starterkit/3.01#Object_Zitbank");
    assertEquals("Zitbank", zitbank.getName());

  }

  @Test 
  public void nReadRdfFileRdf() {

    // Create and test a container from rdf test input
    JenaCoinsContainer model = readRdfFile("3.02", false);

    // Do asserts on objects
    assertEquals("3.02", model.getContainerId());

    HashSet<Object> objects = new HashSet<>();

    Object perron = new Object(model, "http://rws.com/starterkit/3.02#Object_Perron");
    assertEquals("Perron", perron.getName());
    objects.add(perron);

    Object zitbank = new Object(model, "http://rws.com/starterkit/3.02#Object_Zitbank");
    assertEquals("Zitbank", zitbank.getName());
    objects.add(zitbank);

    Connection connection = new Connection(model, "http://rws.com/starterkit/3.02#Zitbank_staat_op_Perron_verbinding");
    Iterator<Object> iterator = connection.getHasConnectedObjects();
    while(iterator.hasNext()) {
      Object object = iterator.next();
      assertTrue(objects.contains(object));
    }

  }

  @Test 
  public void oReadRdfFileRdf() {

    // Create and test a container from rdf test input
    JenaCoinsContainer model = readRdfFile("3.03", false);

    // Do asserts on objects
    assertEquals("3.03", model.getContainerId());

    HashSet<Object> objects = new HashSet<>();

    Object perron = new Object(model, "http://rws.com/starterkit/3.03#Object_Perron");
    assertEquals("Perron", perron.getName());
    objects.add(perron);

    Object zitbank = new Object(model, "http://rws.com/starterkit/3.03#Object_Zitbank");
    assertEquals("Zitbank", zitbank.getName());
    objects.add(zitbank);

    Connection connection = new Connection(model, "http://rws.com/starterkit/3.03#Zitbank_staat_op_Perron_verbinding");
    assertEquals(1, connection.getVersionID());
    Iterator<Object> iterator = connection.getHasConnectedObjects();
    while(iterator.hasNext()) {
      Object object = iterator.next();
      assertTrue(objects.contains(object));
    }

    Connection connection2 = connection.getNextVersion();
    assertEquals(2, connection2.getVersionID());
    assertEquals("http://rws.com/starterkit/3.03#Zitbank_staat_naast_Perron_verbinding", connection2.getUri());
    Iterator<Object> iterator2 = connection2.getHasConnectedObjects();
    while(iterator2.hasNext()) {
      Object object = iterator2.next();
      assertTrue(objects.contains(object));
    }

  }

  @Test 
  public void pReadRdfFileRdf() {

    // Create and test a container from rdf test input
    JenaCoinsContainer model = readRdfFile("3.04", false);

    // Do asserts on objects
    assertEquals("3.04", model.getContainerId());

    HashSet<Part> parts = new HashSet<>();

    Part links = new Part(model, "http://rws.com/starterkit/3.04#Object_SteunLinks");
    assertEquals("SteunLinks", links.getName());
    parts.add(links);

    Part rechts = new Part(model, "http://rws.com/starterkit/3.04#Object_SteunRechts");
    assertEquals("SteunRechts", rechts.getName());
    parts.add(rechts);

    Part ligger = new Part(model, "http://rws.com/starterkit/3.04#Object_Ligger");
    assertEquals("Ligger", ligger.getName());
    parts.add(ligger);

    Assembly zitbank = new Assembly(model, "http://rws.com/starterkit/3.04#Object_Zitbank");
    assertEquals("Zitbank", zitbank.getName());
    Iterator<ContainsRelation> iterator = zitbank.getHasContainsRelation();
    while(iterator.hasNext()) {
      ContainsRelation relation = iterator.next();
      assertTrue(parts.contains(relation.getHasPart()));
    }

  }

  @Test 
  public void qReadRdfFileRdf() {

    // Create and test a container from rdf test input
    JenaCoinsContainer model = readRdfFile("4.01", true);

    // Do asserts on objects
    assertEquals("4.01", model.getContainerId());



  }
  @Test 
  public void rReadRdfFileRdf() {

    // Create and test a container from rdf test input
    JenaCoinsContainer model = readRdfFile("4.02", true);

    // Do asserts on objects
    assertEquals("4.02", model.getContainerId());



  }
  @Test 
  public void sReadRdfFileRdf() {

    // Create and test a container from rdf test input
    JenaCoinsContainer model = readRdfFile("4.03", true);

    // Do asserts on objects
    assertEquals("4.03", model.getContainerId());



  }
  @Test 
  public void tReadRdfFileRdf() {

    // Create and test a container from rdf test input
    JenaCoinsContainer model = readRdfFile("4.04", true);

    // Do asserts on objects
    assertEquals("4.04", model.getContainerId());



  }
  @Test 
  public void uReadRdfFileRdf() {

    // Create and test a container from rdf test input
    JenaCoinsContainer model = readRdfFile("5.01", true);

    // Do asserts on objects
    assertEquals("5.01", model.getContainerId());



  }


  public JenaCoinsContainer readRdfFile(String nr, boolean withLib) {

    JenaCoinsContainer model;
    try {
      File testFile = IntegrationHelper.getResourceFile("BS", nr + ".rdf").getCanonicalFile();
      model = new JenaCoinsContainer(testFile);
      if(withLib) {

        File libFile = IntegrationHelper.getResourceFile("BS", nr + "-lib.rdf").getCanonicalFile();
        model.addImport(libFile.getCanonicalPath(), null, true, true, true);
      }
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }


    ArrayList<File> verifyFiles = new ArrayList<>();
    verifyFiles.add(IntegrationHelper.getResourceFile("BS", "verification/" + nr + ".nq"));
    verifyFiles.add(IntegrationHelper.getResourceFile("BS", "verification/cbim-2.0.nq"));

    rdfZipContent.add("bim/"+nr+".rdf");
    if(withLib) {
      rdfZipContent.add("bim/repository/"+nr+"-lib.rdf");
    }

    // Create the container

    assertEquals(model.getAttachments().size(), 0);
    model.export(Paths.get("/tmp/coinstest/starterskit" + nr + "_inmem.ccr").toFile().toString());
    assertTrue(DatasetAsserts.verifyCompleteContent(model, verifyFiles.iterator()));
    model.close();
    assertTrue(ZipAsserts.containsFiles(new File("/tmp/coinstest/starterskit" + nr + "_inmem.ccr"), rdfZipContent, false));

    rdfZipContent.remove("bim/"+nr+".rdf");
    if(withLib) {
      rdfZipContent.remove("bim/repository/"+nr+"-lib.rdf");
    }


    // Reopen the ccr
    JenaCoinsContainer reopenend = new JenaCoinsContainer(new File("/tmp/coinstest/starterskit"+nr+"_inmem.ccr"));

    assertTrue(DatasetAsserts.verifyCompleteContent(reopenend, verifyFiles.iterator()));
    return reopenend;
  }



}