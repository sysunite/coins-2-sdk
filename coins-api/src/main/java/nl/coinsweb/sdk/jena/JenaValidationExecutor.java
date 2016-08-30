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
package nl.coinsweb.sdk.jena;


import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ValidityReport;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import nl.coinsweb.sdk.CoinsModel;
import nl.coinsweb.sdk.validator.Profile;
import nl.coinsweb.sdk.validator.ValidationExecutor;
import nl.coinsweb.sdk.validator.ValidationQuery;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

/**
 * @author Bastiaan Bijl
 */
public class JenaValidationExecutor implements ValidationExecutor {

  CoinsModel model;
  Dataset dataset;
  Profile profile;

  public JenaValidationExecutor() {
  }

  @Override
  public void execute(CoinsModel model, Profile profile) {

    JenaModelFactory factory = new JenaModelFactory();
    factory.setOntModelSpec(OntModelSpec.OWL_MEM);
    this.model = model;
    this.dataset = factory.getDatasetWithUnionGraphs(model);
    this.profile = profile;

    // See if the model applies to the profile
    boolean allChecksPassed = true;
    for(ValidationQuery query : profile.getProfileChecks()) {
      if(query.hasSparqlQuery()) {

        selectQuery(query);
      }
    }
    if(!allChecksPassed) {
      throw new RuntimeException("The content of the Coins container does not meet the requirements for this profile: " + profile.getName());
    }

    // Calculate all schema inferences
    for(ValidationQuery query : profile.getSchemaInferences()) {
      if(query.hasSparqlQuery()) {

        constructQuery(query);
      }
    }

    // Calculate all data inferences
    for(ValidationQuery query : profile.getDataInferences()) {
      if(query.hasSparqlQuery()) {

        constructQuery(query);
      }
    }

    // Perform all validation queries
    boolean allPassed = true;
    for(ValidationQuery query : profile.getValidationRules()) {
      if(query.hasSparqlQuery()) {

        allPassed &= selectQuery(query);
      }
    }
  }




  public boolean selectQuery(ValidationQuery query) {
    boolean passed;
    try {
      long start = new Date().getTime();
      Iterator<Map<String, String>> resultSet = model.query(query.getSparqlQuery());
      query.setResults(resultSet);
      query.setExecutionTime(new Date().getTime() - start);
      passed = !resultSet.hasNext();
    } catch (QueryParseException e) {

      String errorMessage = "Problem executing query: "+query;
      errorMessage += escapeHtml4("\n" + query + "\n" + e.getMessage());
      query.setErrorMessage(errorMessage);

      passed = false;
    }
    return passed;
  }



  public void constructQuery(ValidationQuery validationQuery) {

    Query query = QueryFactory.create(validationQuery.getSparqlQuery());
    QueryExecution qe = QueryExecutionFactory.create(query, dataset);
    Model updated = qe.execConstruct();
  }

  private ValidityReport jenaReasoning(CoinsModel model) {

    String someSchemaName = null;
    Iterator<String> names = model.getCoinsGraphSet().listModelNames();
    while(names.hasNext()) {
      String name = names.next();
      if(name.equals(model.getCoinsGraphSet().getInstanceNamespace())) {
        continue;
      }
      // System.out.println(name);
//      http://www.coinsweb.nl/voorbeeld#
//      http://www.coinsweb.nl/COINSWOA.rdf#
//      http://www.coinsweb.nl/cbim-2.0.rdf#
//      http://www.coinsweb.nl/units-2.0.rdf#
      if(someSchemaName == null) {
        someSchemaName = name;
      }
    }

    if(someSchemaName == null) {
      throw new RuntimeException("Couldn't find a schema namespace");
    }

    OntModel schemaModel = (OntModel) model.getCoinsGraphSet().getJenaOntModel(someSchemaName);      // todo: find a way to get the relevant schema

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

    OntModel instanceModel = (OntModel) model.getCoinsGraphSet().getJenaOntModel(model.getCoinsGraphSet().getInstanceNamespace(), reasoner);
    return instanceModel.validate();
  }
}
