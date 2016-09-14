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


import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.update.UpdateAction;
import com.hp.hpl.jena.update.UpdateRequest;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import nl.coinsweb.sdk.CoinsModel;
import nl.coinsweb.sdk.Namespace;
import nl.coinsweb.sdk.validator.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

/**
 * @author Bastiaan Bijl
 */
public class JenaValidator implements Validator {

  private static final Logger log = LoggerFactory.getLogger(JenaValidator.class);


  JenaCoinsGraphSet graphSet;
  Dataset dataset;
  Profile profile;


  private CoinsModel model;


  public JenaValidator(CoinsModel model, String profileName) {
    this.model = model;
    this.profile = Profile.loadProfile(profileName);
  }

  /**
   * Perform the validation process
   *
   * @param reportLocation  the location where the report.html file will be placed
   * @return true if no problems were found, false otherwise (see the generated report for reasons)
   */
  public boolean validate(Path reportLocation) {

    ProfileExecution execution = execute(model, profile);

    List<String> libraries = new ArrayList<>();
    List<String> graphs = new ArrayList<>();
    for(Namespace ns : ((JenaCoinsContainer) model.getCoinsContainer()).getAvailableLibraryFiles().keySet()) {
      graphs.add(ns.toString());
      libraries.add(((JenaCoinsContainer) model.getCoinsContainer()).getAvailableLibraryFiles().get(ns).getName());
    }

    // Prepare data to transfer to the template
    Map<String, Object> data = new HashMap<>();
    data.put("filename", model.getCoinsContainer().getFileName());
    data.put("libraries", libraries);
    data.put("graphs", graphs);
    data.put("attachments", model.getCoinsContainer().getAttachments());
    data.put("date", new Date().toString());
    data.put("profileName", this.profile.getName());
    data.put("profileChecksPassed", execution.profileChecksPassed());
    data.put("validationPassed", execution.validationPassed());
    data.put("profileChecks", execution.getProfileCheckResults());
    data.put("schemaInferences", execution.getSchemaInferenceResults());
    data.put("dataInferences", execution.getDataInferenceResults());
    data.put("validationRules", execution.getValidationRuleResults());

    writeReport(reportLocation, data);
    return execution.profileChecksPassed() && execution.validationPassed();
  }

  private void writeReport(Path reportLocation, Map<String, Object> data) {

    try {

      Configuration cfg = new Configuration();
      cfg.setClassForTemplateLoading(nl.coinsweb.sdk.jena.JenaValidator.class, "/validator/");
      cfg.setDefaultEncoding("UTF-8");
      Template template = cfg.getTemplate("report.html");

      File out;
      if(reportLocation.toString().endsWith("html")) {
        out = reportLocation.toFile();
      } else {
        out = reportLocation.resolve("report.html").toFile();
      }
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

  @Override
  public ProfileExecution execute(CoinsModel model, Profile profile) {

    ProfileExecution resultCollection = new ProfileExecution();

    this.model = model;
    this.graphSet = (JenaCoinsGraphSet) model.getCoinsGraphSet();
    this.graphSet.setOntModelSpec(OntModelSpec.OWL_MEM);
    this.dataset = this.graphSet.getDatasetWithUnionGraphs();
    this.profile = profile;


    log.trace("\uD83D\uDC1A Will perform profile checks." );
    profileCheck(resultCollection);

    log.trace("\uD83D\uDC1A Will add schema inferences." );
    addSchemaInferences(resultCollection);

    log.trace("\uD83D\uDC1A Will add data inferences.");
    addDataInferences(resultCollection);

    log.trace("\uD83D\uDC1A Will perform validation checks.");
    performValidation(resultCollection);

    return resultCollection;
  }



  private boolean profileCheck(ProfileExecution resultCollection) {

    boolean allChecksPassed = true;
    for(ValidationQuery query : profile.getProfileChecks()) {
      if(query.hasSparqlQuery()) {

        ValidationQueryResult result = selectQuery(query);
        allChecksPassed &= result.getPassed();

        resultCollection.addProfileCheckResult(result);
      }
    }

    resultCollection.setProfileChecksPassed(allChecksPassed);
    return allChecksPassed;
  }

  private void addSchemaInferences(ProfileExecution resultCollection) {
    for(InferenceQuery query : profile.getSchemaInferences()) {
      if(query.hasSparqlQuery()) {

        resultCollection.addSchemaInferenceResult(constructQuery(query));
      }
    }
  }

  private void addDataInferences(ProfileExecution resultCollection) {
    for(InferenceQuery query : profile.getDataInferences()) {
      if(query.hasSparqlQuery()) {

        resultCollection.addDataInferenceResult(constructQuery(query));
      }
    }

  }

  private boolean performValidation(ProfileExecution resultCollection) {
    boolean allPassed = true;
    for(ValidationQuery query : profile.getValidationRules()) {
      if(query.hasSparqlQuery()) {

        ValidationQueryResult result = selectQuery(query);
        allPassed &= result.getPassed();

        resultCollection.addValidationRuleResult(result);
      }
    }
    resultCollection.setValidationPassed(allPassed);
    return allPassed;
  }




  public InferenceQueryResult constructQuery(InferenceQuery query) {

    String errorMessage = null;
    long start = new Date().getTime();
    String queryString = query.getSparqlQuery();
    Map<String, Long> numTriplesBefore = graphSet.numTriples(dataset);

//    log.info("will perform insert query:");
//    log.info(queryString);

    try {
      UpdateRequest request = new UpdateRequest();
      request.add(queryString);
      UpdateAction.execute(request, dataset);
    } catch(QueryException e) {
      throw new RuntimeException("There is a problem with this query: "+queryString, e);
    }

    long executionTime = new Date().getTime() - start;
    Map<String, Long> numTriplesAfter = graphSet.numTriples(dataset, numTriplesBefore);
    return new InferenceQueryResult(query.getReference(), query.getDescription(), queryString, numTriplesAfter, errorMessage, executionTime);
  }


  public ValidationQueryResult selectQuery(ValidationQuery validationQuery) {

    String errorMessage = null;
    boolean passed;
    long start = new Date().getTime();
    String queryString = validationQuery.getSparqlQuery();
    Iterator<Map<String, String>> resultSet = null;
    ArrayList<String> formattedResults = new ArrayList<>();

    try {

      List<Map<String, String>> result = new ArrayList<>();

      Query query = QueryFactory.create(queryString);

      // Execute the query and obtain results
      QueryExecution qe = QueryExecutionFactory.create(query, dataset);
      ResultSet results = qe.execSelect();

      passed = !results.hasNext();

      // Output query results
      while (results.hasNext()) {

        HashMap<String, String> resultRow = new HashMap();

        QuerySolution row = results.next();

        Iterator columnNames = row.varNames();
        while(columnNames.hasNext()) {
          String columnName = (String) columnNames.next();
          RDFNode item = row.get(columnName);
          if(item.isAnon()) {
            resultRow.put(columnName, "BLANK");
          }
          if(item.isResource()) {
            String value = item.asResource().getURI();
            if(value == null) {
              value = "NULL";
            }
            resultRow.put(columnName, value);
          } else if(item.isLiteral()) {
            String value = item.asLiteral().getLexicalForm();
            if(value == null) {
              value = "NULL";
            }
            resultRow.put(columnName, value);
          } else {
            resultRow.put(columnName, "NOT INTERPRETED");
            log.warn("Skipping a result from the query.");
          }
        }

        formattedResults.add(validationQuery.formatResult(resultRow));

        result.add(resultRow);
      }

      resultSet = result.iterator();

      // Important - free up resources used running the query
      qe.close();

      if(passed) {
        log.trace("query found no results, passed");
      } else {
        log.trace("! results where found, not passing");

      }

    } catch (QueryParseException e) {

      errorMessage = "Problem executing query: ";
      errorMessage += escapeHtml4("\n" + queryString + "\n" + e.getMessage());
      passed = false;
    }

    long executionTime = new Date().getTime() - start;
    return new ValidationQueryResult(validationQuery.getReference(), validationQuery.getDescription(), queryString, resultSet, formattedResults, passed, errorMessage, executionTime);
  }
}