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
import nl.coinsweb.sdk.jena.JenaCoinsGraphSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class InferenceQuery {

  private static final Logger log = LoggerFactory.getLogger(InferenceQuery.class);

  private StringTemplateLoader templateLoader;
  private Configuration cfg;

  private String reference = null;
  private String description = null;
  private String sparqlQuery = null;


  public InferenceQuery(String reference, String description, String sparqlQuery) {

    // Init template
    templateLoader = new StringTemplateLoader();
    cfg = new Configuration();
    cfg.setTemplateLoader(templateLoader);

    // Set passed attributes
    this.reference = reference;
    this.description = description;
    this.sparqlQuery = sparqlQuery;




    if(sparqlQuery != null) {
      templateLoader.putTemplate("sparqlQuery", sparqlQuery);
    }



  }









  public boolean hasSparqlQuery() {
    return sparqlQuery != null;
  }





  public String getDescription() {
    return description;
  }

  public String getReference() {
    return reference;
  }

  public String getSparqlQuery() {

    if(sparqlQuery == null) {
      throw new RuntimeException("Please set a <SparqlQuery>...</SparqlQuery> before the query can be returned.");
    }

    try {
      Template template = cfg.getTemplate("sparqlQuery");

      Map<String, String> data = new HashMap<>();
      data.put("INSTANCE_GRAPH", "<"+ JenaCoinsGraphSet.INSTANCE_GRAPH +">");
      data.put("WOA_GRAPH", "<"+ JenaCoinsGraphSet.WOA_GRAPH +">");
      data.put("CORE_GRAPH", "<"+ JenaCoinsGraphSet.SCHEMA_GRAPH +">");
      data.put("SCHEMA_GRAPH", "<"+ JenaCoinsGraphSet.SCHEMA_GRAPH +">");
      data.put("SCHEMA_UNION_GRAPH", "<"+ JenaCoinsGraphSet.SCHEMA_UNION_GRAPH +">");
      data.put("FULL_UNION_GRAPH", "<"+ JenaCoinsGraphSet.FULL_UNION_GRAPH +">");

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





}