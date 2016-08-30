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


import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class ValidationQuery {

  private static final Logger log = LoggerFactory.getLogger(ValidationQuery.class);

  private StringTemplateLoader templateLoader;
  private Configuration cfg;

  private String reference = null;
  private String description = null;
  private String resultFormat = null;
  private String sparqlQuery = null;
//  private String jenaRule = null;

  private Iterator<Map<String,String>> resultSet;
  private boolean passed;
  private String errorMessage;
  private long executionTime;

  public ValidationQuery(String reference, String description, String resultFormat, String sparqlQuery, String jenaRule) {

    // Init template
    templateLoader = new StringTemplateLoader();
    cfg = new Configuration();
    cfg.setTemplateLoader(templateLoader);

    // Set passed attributes
    this.reference = reference;
    this.description = description;
    this.resultFormat = resultFormat;
    this.sparqlQuery = sparqlQuery;
//    this.jenaRule = jenaRule;



    if(sparqlQuery != null) {
      templateLoader.putTemplate("sparqlQuery", sparqlQuery);
    }

//    if(jenaRule != null) {
//      templateLoader.putTemplate("jenaRule", jenaRule);
//    }

    if(resultFormat != null) {
      templateLoader.putTemplate("resultFormat", resultFormat);
    }

    // Check completeness
    System.out.println(toString());
  }


  public void setResults(Iterator<Map<String,String>> resultSet) {
    this.resultSet = resultSet;
  }
  public void setPassed(boolean passed) {
    this.passed = passed;
  }
  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }
  public void setExecutionTime(long executionTime) {
    this.executionTime = executionTime;
  }








  public boolean hasSparqlQuery() {
    return sparqlQuery != null;
  }
//  public boolean hasJenaRule() {
//    return jenaRule != null;
//  }





  public String getDescription() {
    return description;
  }

  public String getReference() {
    return reference;
  }

  public String getSparqlQuery() {

    if(resultFormat == null) {
      throw new RuntimeException("Please set a <SparqlQuery>...</SparqlQuery> before the query can be returned.");
    }

    try {
      Template template = cfg.getTemplate("sparqlQuery");

      Map<String, String> data = new HashMap<>();
      data.put("INSTANCE_GRAPH", "instance...");
      data.put("WOA_GRAPH", "woa...");
      data.put("CORE_GRAPH", "core...");
      data.put("SCHEMA_UNION_GRAPH", "schema...");
      data.put("FULL_UNION_GRAPH", "full...");

      Writer writer = new StringWriter();
      template.process(data, writer);
      return writer.toString();


    } catch (IOException e) {
      log.error(e.getMessage(), e);
    } catch (TemplateException e) {
      log.error(e.getMessage(), e);
    }

    throw new RuntimeException("Something went wrong building query.");
  }

//  public String getJenaRule() {
//    return jenaRule;
//  }

  public boolean getPassed() {
    return passed;
  }
  public String getErrorMessage() {
    return errorMessage;
  }
  public long getExecutionTime() {
    return executionTime;
  }

  public String toString() {
    return getReference();
  }

  public List<String> getFormattedResults() {

    if(resultFormat == null) {
      throw new RuntimeException("Please set a ResultFormat before the results can be returned in a formatted form.");
    }

    if(resultSet == null) {
      throw new RuntimeException("Please first execute this ValidationQuery");
    }

    ArrayList<String> results = new ArrayList<>();
    try {

      Template template = cfg.getTemplate("resultFormat");

      while(resultSet.hasNext()) {
        Map<String,String> data = resultSet.next();
        Writer writer = new StringWriter();
        template.process(data, writer);
        results.add(writer.toString());
      }

    } catch (IOException e) {
      log.error(e.getMessage(), e);
    } catch (TemplateException e) {
      log.error(e.getMessage(), e);
    }
    return results;
  }

}