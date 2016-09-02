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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class ValidationQueryResult {

  private static final Logger log = LoggerFactory.getLogger(ValidationQueryResult.class);


  private String reference;
  private String description;
  private String sparqlQuery;
  private Iterator<Map<String,String>> resultSet;
  private List<String> formattedResults;
  private boolean passed;
  private String errorMessage;
  private long executionTime;

  public ValidationQueryResult(String reference,
                               String description,
                               String sparqlQuery,
                               Iterator<Map<String,String>> resultSet,
                               List<String> formattedResults,
                               boolean passed,
                               String errorMessage,
                               long executionTime) {

    // Set passed attributes
    this.reference = reference;
    this.description = description;
    this.sparqlQuery = sparqlQuery;
    this.resultSet = resultSet;
    this.formattedResults = formattedResults;
    this.passed = passed;
    this.errorMessage = errorMessage;
    this.executionTime = executionTime;
  }


  public String getReference() {
    return reference;
  }
  public String getDescription() {
    return description;
  }
  public String getSparqlQuery() {
    return sparqlQuery;
  }
  public Iterator<Map<String,String>> getResultSet() {
    return resultSet;
  }
  public List<String> getFormattedResults() {
    return formattedResults;
  }
  public boolean getPassed() {
    return passed;
  }
  public String getErrorMessage() {
    return errorMessage;
  }
  public long getExecutionTime() {
    return executionTime;
  }

}