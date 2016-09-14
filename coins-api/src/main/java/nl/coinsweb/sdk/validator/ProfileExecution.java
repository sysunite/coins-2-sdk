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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class ProfileExecution {

  private static final Logger log = LoggerFactory.getLogger(ProfileExecution.class);

  private boolean profileChecksPassed = false;
  private boolean validationPassed = false;
  private long executionTime;


  private List<ValidationQueryResult> profileChecks = new ArrayList<>();
  private List<InferenceQueryResult> schemaInferences = new ArrayList<>();
  private List<InferenceQueryResult> dataInferences = new ArrayList<>();
  private List<ValidationQueryResult> validationRules = new ArrayList<>();

  public ProfileExecution() {

  }

  public void setProfileChecksPassed(boolean passed) {
    this.profileChecksPassed = passed;
  }
  public boolean profileChecksPassed() {
    return this.profileChecksPassed;
  }
  public void setValidationPassed(boolean passed) {
    this.validationPassed = passed;
  }
  public boolean validationPassed() {
    return this.validationPassed;
  }
  public void setExecutionTime(long executionTime) {
    this.executionTime = executionTime;
  }
  public long getExecutionTime() {
    return executionTime;
  }

  public void addProfileCheckResult(ValidationQueryResult queryResult) {
    profileChecks.add(queryResult);
  }
  public List<ValidationQueryResult> getProfileCheckResults() {
    return profileChecks;
  }

  public void addSchemaInferenceResult(InferenceQueryResult queryResult) {
    schemaInferences.add(queryResult);
  }
  public List<InferenceQueryResult> getSchemaInferenceResults() {
    return schemaInferences;
  }

  public void addDataInferenceResult(InferenceQueryResult queryResult) {
    dataInferences.add(queryResult);
  }
  public List<InferenceQueryResult> getDataInferenceResults() {
    return dataInferences;
  }

  public void addValidationRuleResult(ValidationQueryResult queryResult) {
    validationRules.add(queryResult);
  }
  public List<ValidationQueryResult> getValidationRuleResults() {
    return validationRules;
  }
}