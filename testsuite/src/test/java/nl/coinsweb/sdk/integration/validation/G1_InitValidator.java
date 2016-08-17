package nl.coinsweb.sdk.integration.validation;

import nl.coinsweb.sdk.ModelFactory;
import nl.coinsweb.sdk.integration.IntegrationHelper;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import nl.coinsweb.sdk.jena.JenaModelFactory;
import nl.coinsweb.sdk.validator.ValidationQuery;
import nl.coinsweb.sdk.validator.Validator;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class G1_InitValidator {

  protected static final Logger log = LoggerFactory.getLogger(G1_InitValidator.class);

  @Test
  public void loadQueries() {

    ModelFactory factory = new JenaModelFactory();
    JenaCoinsContainer model = new JenaCoinsContainer(factory, "http://playground.com/");


    model.load(IntegrationHelper.getResourceFile("F1", "WOAVoorbeeld.ccr").getAbsolutePath());

    Validator validator = new Validator(model);
    validator.init();

    Collection<ValidationQuery> queries = validator.getValidationQueries().values();

    assertEquals(20, queries.size());

    for(ValidationQuery query : queries) {
//      System.out.println(query.getQuery());
    }

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