package nl.coinsweb.sdk.integration.validation;

import nl.coinsweb.sdk.cli.Run;
import nl.coinsweb.sdk.integration.IntegrationHelper;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.junit.Assert.assertTrue;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class G3_RunningViaCli {

  protected static final Logger log = LoggerFactory.getLogger(G3_RunningViaCli.class);



  // Basic tests

  @Test
  public void noToolSelected() {
    String result = Run.getCli("coins-cli");

    assertTrue(result.contains("unzip"));
    assertTrue(result.contains("viewer"));
    assertTrue(result.contains("map"));
    assertTrue(result.contains("generate"));
    assertTrue(result.contains("validate"));
  }

  @Test
  public void noInputFile() {

    String result = Run.getCli("coins-cli validate");

    assertTrue(result.contains("(!) no input file specified"));
    assertHelp(result);
  }

  @Test
  public void askHelp() {

    assertHelp(Run.getCli("coins-cli validate -h"));
    assertHelp(Run.getCli("coins-cli validate --help"));
  }

  @Test
  public void emptyRun() {

    String result = Run.getCli("coins-cli validate -e");

    System.out.println(result);
  }





  // Profile tests

  @Test
  public void nonexistentProfile() {

    String result = Run.getCli("coins-cli validate -e -c notfound.profile");

    assertTrue(result.contains("(!) custom profile file was not found"));
  }

  @Test
  public void wrongProfile() {

    File wrongProfile = IntegrationHelper.getResourceFile("G3", "wrong.profile");

    String result = Run.getCli("coins-cli validate -e -c "+wrongProfile.getAbsolutePath());

    assertTrue(result.contains("(!) the supplied custom profile file contains errors"));
  }

  @Test
  public void rightProfile() {

    File rightProfile = IntegrationHelper.getResourceFile("G3", "right.profile");

    String result = Run.getCli("coins-cli validate -e -c " + rightProfile.getAbsolutePath());
    assertSuccessfulRun(result);
  }




  // Connecting fuseki

  @Test
  public void wrongFuseki() {

    String result = Run.getCli("coins-cli validate -e -f http://docker:3031");

    assertTrue(result.contains("(!) fuseki not active at specified address, or does not have dataset with name 'coins' (address should be of form http://localhost:3030)"));
  }

  @Test
  public void rightFuseki() {

    String result = Run.getCli("coins-cli validate -e -f http://192.168.99.101:3030");
    assertSuccessfulRun(result);

  }






  public void assertHelp(String result) {
    assertTrue(result.contains("coins-cli validate <input file>"));
    assertTrue(result.contains("-c"));
    assertTrue(result.contains("-e"));
    assertTrue(result.contains("-f"));
    assertTrue(result.contains("-h"));
    assertTrue(result.contains("-o"));
    assertTrue(result.contains("-p"));
    assertTrue(result.contains("-q"));
    assertTrue(result.contains("-v"));
  }

  public void assertSuccessfulRun(String result) {
    assertTrue(result.contains("Validation finished"));
  }
}