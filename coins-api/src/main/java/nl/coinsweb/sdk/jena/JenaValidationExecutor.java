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
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryException;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.update.UpdateAction;
import nl.coinsweb.sdk.CoinsModel;
import nl.coinsweb.sdk.validator.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

/**
 * @author Bastiaan Bijl
 */
public class JenaValidationExecutor implements ValidationExecutor {

  CoinsModel model;
  JenaCoinsGraphSet graphSet;
  Dataset dataset;
  Profile profile;

  public JenaValidationExecutor() {
  }

  @Override
  public ProfileExecution execute(CoinsModel model, Profile profile) {

    ProfileExecution resultCollection = new ProfileExecution();

    this.model = model;
    this.graphSet = (JenaCoinsGraphSet) model.getCoinsGraphSet();
    this.graphSet.setOntModelSpec(OntModelSpec.OWL_MEM);
    this.dataset = this.graphSet.getDatasetWithUnionGraphs();
    this.profile = profile;

    if(!profileCheck(resultCollection)) {
      throw new RuntimeException("The content of the Coins container does not meet the requirements for this profile: " + profile.getName());
    }
    addSchemaInferences(resultCollection);
    addDataInferences(resultCollection);
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

    return allChecksPassed;
  }

  private void addSchemaInferences(ProfileExecution resultCollection) {
    for(ValidationQuery query : profile.getSchemaInferences()) {
      if(query.hasSparqlQuery()) {

        resultCollection.addProfileCheckResult(constructQuery(query));
      }
    }
  }

  private void addDataInferences(ProfileExecution resultCollection) {
    for(ValidationQuery query : profile.getDataInferences()) {
      if(query.hasSparqlQuery()) {

        resultCollection.addProfileCheckResult(constructQuery(query));
      }
    }

  }

  private boolean performValidation(ProfileExecution resultCollection) {
    boolean allPassed = true;
    for(ValidationQuery query : profile.getValidationRules()) {
      if(query.hasSparqlQuery()) {

        ValidationQueryResult result = selectQuery(query);
        allPassed &= result.getPassed();

        resultCollection.addProfileCheckResult(result);
      }
    }
    return allPassed;
  }




  public ValidationQueryResult constructQuery(ValidationQuery query) {

    String errorMessage = null;
    boolean passed = true;
    long start = new Date().getTime();
    String queryString = query.getSparqlQuery();
    Iterator<Map<String, String>> resultSet = null;
    ArrayList<String> formattedResults = new ArrayList<>();

    try {
      UpdateAction.parseExecute(queryString, dataset);
    } catch(QueryException e) {
      throw new RuntimeException("There is a problem with this query: "+queryString, e);
    }

    long executionTime = new Date().getTime() - start;
    return new ValidationQueryResult(query.getReference(), query.getDescription(), queryString, resultSet, formattedResults, passed, errorMessage, executionTime);
  }
  public ValidationQueryResult selectQuery(ValidationQuery query) {

    String errorMessage = null;
    boolean passed;
    long start = new Date().getTime();
    String queryString = query.getSparqlQuery();
    Iterator<Map<String, String>> resultSet = null;
    ArrayList<String> formattedResults = new ArrayList<>();

    try {

      resultSet = model.query(queryString);
      passed = !resultSet.hasNext();

    } catch (QueryParseException e) {

      errorMessage = "Problem executing query: ";
      errorMessage += escapeHtml4("\n" + query + "\n" + e.getMessage());
      passed = false;
    }

    long executionTime = new Date().getTime() - start;
    return new ValidationQueryResult(query.getReference(), query.getDescription(), queryString, resultSet, formattedResults, passed, errorMessage, executionTime);
  }
}