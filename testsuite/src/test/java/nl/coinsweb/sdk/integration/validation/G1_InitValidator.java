package nl.coinsweb.sdk.integration.validation;

import nl.coinsweb.sdk.integration.IntegrationHelper;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import nl.coinsweb.sdk.validator.Profile;
import nl.coinsweb.sdk.validator.Validator;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class G1_InitValidator {

  protected static final Logger log = LoggerFactory.getLogger(G1_InitValidator.class);

  @BeforeClass
  public static void initProfile() {
    Profile.initProfiles();
  }

  @Test
  public void init() {

    JenaCoinsContainer model = new JenaCoinsContainer("http://playground.com/");
    model.load(IntegrationHelper.getResourceFile("F1", "WOAVoorbeeld.ccr").getAbsolutePath(), false);

    Validator validator = new Validator(model, "COINS 2.0 Lite");
    Set<String> profiles = Profile.listProfiles();

    assertEquals(2, profiles.size());
    assertTrue("Profiles should find this profile", profiles.contains("COINS 2.0 Lite"));
    assertTrue("Profiles should find this profile", profiles.contains("COINS 2.0 Lite EQ"));
    validator.validate(Paths.get("/tmp/"));

    String reportHtml;
    try {
      reportHtml = new String(Files.readAllBytes(Paths.get("/tmp/report.html")), StandardCharsets.UTF_8);
      System.out.println(reportHtml);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }

  }
}