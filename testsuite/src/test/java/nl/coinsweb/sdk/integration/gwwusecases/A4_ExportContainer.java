package nl.coinsweb.sdk.integration.gwwusecases;

import nl.coinsweb.sdk.CoinsParty;
import nl.coinsweb.sdk.jena.InMemCoinsContainer;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;

/**
 * Usecase A4:
 *
 *    Export container
 *
 *
 *
 *
 * @author Gijs van der Ent, Sysunite 2016
 */
public class A4_ExportContainer {

  String tempPath = "/tmp/coinstest/A4_test";
  File containerFile = Paths.get("/tmp/coinstest/A4_Test.ccr").toFile();
  String testNameSpace = "http://www.example.com/";

  JenaCoinsContainer container;

  @Before
  public void initData() {
    CoinsParty defaultPerson = new CoinsParty("http://sandbox.rws.nl/defaultUser");
    container = new InMemCoinsContainer(defaultPerson, testNameSpace);
  }

  @After
  public void cleanup() {
    if(containerFile.exists()) {
      containerFile.delete();
    }


    File tempPathFile = new File(tempPath);
    if(tempPathFile.exists()) {
      tempPathFile.delete();
    }
  }

  @Test
  public void export() {

    container.export(containerFile.toString());
  }



}