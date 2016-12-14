package nl.coinsweb.sdk.integration.a.gwwusecases;

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

  JenaCoinsContainer container;

  @Before
  public void initData() {
    container = new JenaCoinsContainer();
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