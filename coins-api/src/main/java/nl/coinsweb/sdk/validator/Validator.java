/**
 * MIT License
 *
 * Copyright (c) 2016 Bouw Informatie Raad
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 *
 **/
package nl.coinsweb.sdk.validator;


import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ValidityReport;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import nl.coinsweb.sdk.CoinsModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class Validator {

  private static final Logger log = LoggerFactory.getLogger(Validator.class);

  private CoinsModel model;
  private HashMap<String, ValidationQuery> validationQueries;

  public Validator(CoinsModel model) {
    this.model = model;
  }

  /**
   * Read all the vq-files from the jar //todo: add a custom place to look for queries
   */
  public void init() {
    ResourceScanner scanner = new ResourceScanner();
    this.validationQueries = scanner.getResourceFiles("validator/queries/validation/");
  }

  /**
   * Perform the validation process
   *
   * @param reportLocation  the location where the report.html file will be placed
   * @return true if no problems were found, false otherwise (see the generated report for reasons)
   */
  public boolean validate(Path reportLocation) {

    boolean allPassed = true;

    Map<String, Object> data = new HashMap<>();

    // Perform the inferences and check jena validity
    ValidityReport jenaReport = jenaReasoning();
    ArrayList<ValidityReport.Report> jenaReports = new ArrayList<>();
    for (Iterator i = jenaReport.getReports(); i.hasNext(); ) {
      ValidityReport.Report report = (ValidityReport.Report)i.next();
      jenaReports.add(report);
    }


    allPassed &= jenaReport.isValid();

    // Execute all queries
    Collection<ValidationQuery> allQueries = getValidationQueries().values();
    for(ValidationQuery query : allQueries) {
      allPassed &= query.executeOn(model);
    }

    // Prepare input for report
    data.put("filename", model.getCoinsContainer().getContainerId());
    data.put("date", new Date().toString());
    data.put("validation", allPassed);
    data.put("jenaReport", jenaReport);
    data.put("jenaReports", jenaReports);
    data.put("queries", allQueries);

    writeReport(reportLocation, data);
    return allPassed;
  }

  private ValidityReport jenaReasoning() {

    OntModel schemaModel = (OntModel) model.getJenaOntModel("http://custom.library.com#");      // todo: find a way to get the relevant schema

    String rules =

        // Rdfs rules (copied from rdfs.rules in jena-core:etc)
        "[rdf1and4: (?x ?p ?y) -> (?p rdf:type rdf:Property), (?x rdf:type rdfs:Resource), (?y rdf:type rdfs:Resource)]\n" +
            "[rdfs7b:   (?a rdf:type rdfs:Class) -> (?a rdfs:subClassOf rdfs:Resource)] \n" +
            "[rdfs2:    (?x ?p ?y), (?p rdfs:domain ?c) -> (?x rdf:type ?c)] \n" +
            "[rdfs3:    (?x ?p ?y), (?p rdfs:range ?c) -> (?y rdf:type ?c)] \n" +
            "[rdfs5a:   (?a rdfs:subPropertyOf ?b), (?b rdfs:subPropertyOf ?c) -> (?a rdfs:subPropertyOf ?c)] \n" +
            "[rdfs5b:   (?a rdf:type rdf:Property) -> (?a rdfs:subPropertyOf ?a)] \n" +
            "[rdfs6:    (?a ?p ?b), (?p rdfs:subPropertyOf ?q) -> (?a ?q ?b)] \n" +
            "[rdfs7:    (?a rdf:type rdfs:Class) -> (?a rdfs:subClassOf ?a)]\n" +
            "[rdfs8:    (?a rdfs:subClassOf ?b), (?b rdfs:subClassOf ?c) -> (?a rdfs:subClassOf ?c)] \n" +
            "[rdfs9:    (?x rdfs:subClassOf ?y), (?a rdf:type ?x) -> (?a rdf:type ?y)] \n" +
            "[rdfs10:   (?x rdf:type rdfs:ContainerMembershipProperty) -> (?x rdfs:subPropertyOf rdfs:member)] \n" +
            "\n" +

            // Inference rules (based on queries in COINS 2.0_Semantiek_ConceptVersieV5.docx)
            "[caxsco:   (?x a ?c1), (?c1 rdfs:subClassOf ?c2) -> (?x a ?c2)]\n" +
            "[cls-hv1:  (?x owl:hasValue ?y), (?x owl:onProperty ?p), (?u a ?x) -> (?u ?p ?y)]\n" +
            "[cls-uni:  (?y a ?ci), (?c owl:unionOf ?x), listContains(?x, ?ci)  -> (?y a ?c)]\n" +              // (?x rdf:rest*/rdf:first ?ci)
            "[prp-inv:  (?p1 owl:inverseOf ?p2),(?x ?p1 ?y) -> (?y ?p2 ?x)]\n" +
            "[prp-inv2: (?p1 owl:inverseOf ?p2), (?x ?p2 ?y) -> (?y ?p1 ?x)]\n" +
            "[prp-spo1: (?p1 rdfs:subPropertyOf ?p2), (?x ?p1 ?y) -> (?x ?p2 ?y)]\n" +
            "[prp-trp:  (?p a owl:TransitiveProperty), (?x ?p ?y), (?y ?p ?z) -> (?x ?p ?z)]\n" +
            "[scm-avf1: (?c1 owl:allValuesFrom ?y1), (?y1 rdfs:subClassOf ?y2), (?c2 owl:allValuesFrom ?y2), (?c1 owl:onProperty ?p), (?c2 owl:onProperty ?p) -> (?c1 rdfs:subClassOf ?c2)]\n" +
            "[scm-avf2: (?c1 owl:allValuesFrom ?y), (?c1 owl:onProperty ?p1), (?c2 owl:allValuesFrom ?y), (?c2 owl:onProperty ?p2), (?p1 rdfs:subPropertyOf ?p2) -> (?c2 rdfs:subClassOf ?c1)]\n" +
            "[scm-dom2: (?p2 rdfs:domain ?c), (?p1 rdfs:subPropertyOf ?p2) -> (?p1 rdfs:domain ?c)]\n" +
            "[scm-hv:   (?c1 owl:hasValue ?i), (?c1 owl:onProperty ?p1), (?c2 owl:hasValue ?i), (?c2 owl:onProperty ?p2), (?p1 rdfs:subPropertyOf ?p2) -> (?c1 rdfs:subClassOf ?c2)]\n" +
            "[scm-int:  (?c owl:intersectionOf ?l), listContains(?l, ?cl)  -> (?c rdfs:subClassOf ?cl )]\n" +                            // /rdf:rest*/rdf:first ?cl
            "[scm-rng1: (?p rdfs:range ?c1), (?c1 rdfs:subClassOf ?c2) -> (?p rdfs:range ?c2)]\n" +
            "[scm-rng2: (?p2 rdfs:range ?c), (?p1 rdfs:subPropertyOf ?p2) -> (?p1 rdfs:range ?c)]\n" +
            "[scm-sco:  (?c1 rdfs:subClassOf ?c2), (?c2 rdfs:subClassOf ?c3) -> (?c1 rdfs:subClassOf ?c3)]\n" +
            "[scm-spo:  (?p1 rdfs:subPropertyOf ?p2), (?p2 rdfs:subPropertyOf ?p3) -> (?p1 rdfs:subPropertyOf ?p3)]\n" +
            "[scm-svf1: (?c1 owl:someValuesFrom ?y1), (?c1 owl:onProperty ?p), (?c2 owl:someValuesFrom ?y2), (?c2 owl:onProperty ?p), (?y1 rdfs:subClassOf ?y2) -> (?c1 rdfs:subClassOf ?c2)]\n" +
            "[scm-svf2: (?c1 owl:someValuesFrom ?y), (?c1 owl:onProperty ?p1), (?c2 owl:someValuesFrom ?y), (?c2 owl:onProperty ?p2), (?p1 rdfs:subPropertyOf ?p2) -> (?c1 rdfs:subClassOf ?c2)]\n" +
            "\n" +

            // Validation rules (improvised)
            "[validationFP: (?v rb:validation on()) -> [fpb: (?X rb:violation warning('nonsensical warning', 'If any triple exists, be warned (for no reason)')) <- (?X ?P ?V) ] ]\n" +
            "[validationFP: (?v rb:validation on()) -> [fpb: (?X rb:violation error('nonsensical error', 'If any triple exists, be warned (for no reason)')) <- (?X ?P ?V) ] ]";


    Reasoner reasoner = new GenericRuleReasoner(Rule.parseRules(rules));
    reasoner.bindSchema(schemaModel);
//    reasoner.setDerivationLogging(true);

    OntModel instanceModel = (OntModel) model.getJenaOntModel(model.getInstanceNamespace(), reasoner);
    return instanceModel.validate();
  }


  private void writeReport(Path reportLocation, Map<String, Object> data) {

    try {

      Configuration cfg = new Configuration();
      cfg.setClassForTemplateLoading(Validator.class, "/validator/");
      Template template = cfg.getTemplate("report.html");

      File out = reportLocation.resolve("report.html").toFile();
      PrintStream printStream = new PrintStream( new FileOutputStream( out ) );
      Writer file = new PrintWriter(new OutputStreamWriter(printStream, "UTF-8"));

      template.process(data, file);
      file.flush();
      file.close();


    } catch (IOException e) {
      e.printStackTrace();
    } catch (TemplateException e) {
      log.error(e.getMessage(), e);
    }

  }

  public HashMap<String, ValidationQuery> getValidationQueries() {
    return validationQueries;
  }

  private class ResourceScanner {
    private HashMap<String, ValidationQuery> getResourceFiles(String path) {
      HashMap<String, ValidationQuery> files = new HashMap<>();

      InputStream in = getResourceAsStream( path );
      BufferedReader br = new BufferedReader( new InputStreamReader( in ) );
      String resource;

      try {
        while( (resource = br.readLine()) != null ) {
          if(resource.endsWith(".vq")) {
            files.put(resource, new ValidationQuery(getResourceAsStream("validator/queries/validation/"+resource)));
          }
        }
      } catch (IOException e) {
        log.error(e.getMessage(), e);
      }

      return files;
    }

    private InputStream getResourceAsStream( String resource ) {
      final InputStream in = getContextClassLoader().getResourceAsStream( resource );
      return in == null ? getClass().getResourceAsStream( resource ) : in;
    }

    private ClassLoader getContextClassLoader() {
      return Thread.currentThread().getContextClassLoader();
    }
  }

}