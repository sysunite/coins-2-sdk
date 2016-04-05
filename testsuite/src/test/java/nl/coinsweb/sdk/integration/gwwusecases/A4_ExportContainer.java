package nl.coinsweb.sdk.integration.gwwusecases;

import nl.coinsweb.sdk.CoinsParty;
import nl.coinsweb.sdk.jena.InMemCoinsContainer;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

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

  String tempPath = "/tmp/A4_test";
  File containerFile = Paths.get("/tmp/A4_Test.ccr").toFile();
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

  void export() {

    container.export(containerFile.toString());
  }

  public static class EmptyExport extends A4_ExportContainer {
    @Test
    public void testEmptyContainer() {
      export();
      assertTrue("Exported container exists", containerFile.exists());
    }
  }

  public static class ExportWithData extends A4_ExportContainer {
//    @Test
    public void testLibraryImport() {
//      NCS0025090Impl object = new NCS0025090Impl(workspace);
//      NCS0025090Impl object2 = new NCS0025090Impl(workspace);
//
//      Iterator<NCS0025090> originalIterator = workspace.findCoinsObjects(NCS0025090Impl.atom);
//      int originalCount = 0;
//      while(originalIterator.hasNext()) {
//        originalCount++;
//        originalIterator.next();
//      }
//
//      assertEquals("number of created objects", 2, originalCount);
//
//      export();
//      assertTrue("exported container exists", containerFile.exists());
//
//      CoinsContainer imported = FileManager.existingCoinsContainer(new InMemCoinsModelCreator(), containerFile);
//      CoinsModel importedModel = imported.getCoinsModel("content.rdf");
//      Iterator<NCS0025090> iterator = importedModel.findCoinsObjects(NCS0025090Impl.atom);
//      int count = 0;
//      while(iterator.hasNext()) {
//        count++;
//        iterator.next();
//      }
//
//      assertEquals("number of created objects", 2, count);

    }

  }

}