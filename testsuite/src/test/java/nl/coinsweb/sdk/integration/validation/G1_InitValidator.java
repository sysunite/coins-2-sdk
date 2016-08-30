package nl.coinsweb.sdk.integration.validation;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.Derivation;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ValidityReport;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import nl.coinsweb.sdk.ModelFactory;
import nl.coinsweb.sdk.integration.IntegrationHelper;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import nl.coinsweb.sdk.jena.JenaModelFactory;
import nl.coinsweb.sdk.jena.JenaValidationExecutor;
import nl.coinsweb.sdk.validator.Validator;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class G1_InitValidator {

  protected static final Logger log = LoggerFactory.getLogger(G1_InitValidator.class);

  @Test
  public void init() {

    ModelFactory factory = new JenaModelFactory();
    JenaCoinsContainer model = new JenaCoinsContainer(factory, "http://playground.com/");
    model.load(IntegrationHelper.getResourceFile("F1", "WOAVoorbeeld.ccr").getAbsolutePath());

    JenaValidationExecutor executor = new JenaValidationExecutor();

    Validator validator = new Validator(model, executor, "COINS-2.0-Lite");
    Set<String> profiles = validator.listProfiles();

    assertTrue("Profiles should find one profile", profiles.size() == 1);
    assertTrue("Profiles should find this profile", profiles.contains("COINS-2.0-Lite"));
    validator.validate(Paths.get("/tmp/"));


    String reportHtml;
    try {
      reportHtml = new String(Files.readAllBytes(Paths.get("/tmp/report.html")), StandardCharsets.UTF_8);
      System.out.println(reportHtml);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }



  }

  @Test
  public void jenaValidator() {

    ModelFactory factory = new JenaModelFactory();
    JenaCoinsContainer model = new JenaCoinsContainer(factory, "http://playground.com/");

    model.load(IntegrationHelper.getResourceFile("F1", "WOAVoorbeeld.ccr").getAbsolutePath());

    String rules = "[rule1: (?a eg:p ?b) (?b eg:p ?c) -> (?a eg:p ?c)]";
    Reasoner reasoner = new GenericRuleReasoner(Rule.parseRules(rules));
    reasoner.setDerivationLogging(true);


    OntModel instanceModel = model.getCoinsGraphSet().getJenaOntModel(model.getCoinsGraphSet().getInstanceNamespace(), reasoner);
    ValidityReport report = instanceModel.validate();
    System.out.println(report.isValid());
    System.out.println(report.isClean());
    System.out.println(report);

    PrintWriter out = new PrintWriter(System.out);
    for (StmtIterator i = instanceModel.listStatements(); i.hasNext(); ) {
      Statement s = i.nextStatement();
      System.out.println("Statement is " + s);
      for (Iterator id = instanceModel.getDerivation(s); id.hasNext(); ) {
        Derivation deriv = (Derivation) id.next();
        deriv.printTrace(out, true);
      }
    }
    out.flush();
  }


}



