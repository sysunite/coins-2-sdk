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

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;
import nl.coinsweb.sdk.CoinsGraphSet;
import nl.coinsweb.sdk.Namespace;
import nl.coinsweb.sdk.validator.InferenceExecution;
import nl.coinsweb.sdk.validator.InferenceQuery;
import nl.coinsweb.sdk.validator.InferenceQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Bastiaan Bijl
 */
public class FusekiGraphSet extends InMemGraphSet implements CoinsGraphSet {

  private static final Logger log = LoggerFactory.getLogger(FusekiGraphSet.class);

  public static String sparqlEndPointU;
  public static String sparqlEndPointQ;
  public static String sparqlEndPointD;

  private boolean initDataset = false;
  private boolean initValidationDataset = false;

  public FusekiGraphSet(String namespace, String host, String database) {
    super(namespace);

    sparqlEndPointU = host + "/" + database + "/update";
    sparqlEndPointQ = host + "/" + database + "/query";
    sparqlEndPointD = host + "/" + database + "/data";
  }

  @Override
  public boolean supportsMultiThreading() {
    return true;
  }

  public boolean checkIfDbAvailable() {
    boolean check;
    try {
      check = numTriples(getFullUnionNamespace()) > -1;
    } catch  (QueryExceptionHTTP e) {
      check = false;
    }
    return check;
  }

  private void wipe() {
    UpdateRequest request = UpdateFactory.create("DELETE {?s ?p ?o} WHERE {?s ?p ?o}");
    UpdateExecutionFactory.createRemote(request, sparqlEndPointU).execute();
  }

  @Override
  public String getFullUnionNamespace() {
    return "urn:x-arq:UnionGraph";
  }



  @Override
  public Dataset getEmptyDataset() {
    return null;
  }





  @Override
  public Dataset getDataset() {
    if(!initDataset) {
      rebuildDataset();
      initDataset = true;
    }
    return null;
  }
  @Override
  public Dataset rebuildDataset() {

    log.info("Wipe fuseki.");
    wipe();

    updateModel(null, instanceNamespace.toString(), instanceModel);
    updateModel(null, woaNamespace.toString(), woaModel);
    for(Namespace ns : libraryModels.keySet()) {
      updateModel(null, ns.toString(), libraryModels.get(ns));
    }
    return null;
  }


  @Override
  public Dataset getValidationDataset() {
    if(!initValidationDataset) {
      rebuildValidationDataset();
      initValidationDataset = true;
    }
    return null;
  }
  private Dataset rebuildValidationDataset() {

    log.info("Wipe fuseki.");
    wipe();

    log.info("Arrange dataset with union graphs.");

    updateModel(null, INSTANCE_GRAPH, instanceModel);
    updateModel(null, WOA_GRAPH, woaModel);
    updateModel(null, SCHEMA_UNION_GRAPH, getSchemaAggregationModel());
//    updateModel(dataset, FULL_UNION_GRAPH, getFullUnionModel());

    log.info("Done arranging.");

    return null;
  }

  @Override
  public void updateModel(Dataset dataset, String ns, Model model) {

    log.info("Update model "+ns);
    DatasetAccessor accessor = DatasetAccessorFactory.createHTTP(sparqlEndPointD);
    accessor.putModel(ns, model);
  }










  @Override
  public void insert(InferenceQuery query, InferenceQueryResult result) {

    long start = new Date().getTime();
    String queryString = query.getSparqlQuery();

//    log.info("will perform insert query:");
//    log.info(queryString);

    try {

      UpdateRequest request = UpdateFactory.create(queryString);
      UpdateExecutionFactory.createRemote(request, sparqlEndPointU).execute();

    } catch (QueryException e) {
      throw new RuntimeException("There is a problem with this query: " + queryString, e);
    }

    long executionTime = new Date().getTime() - start;
    result.addExecutionTime(executionTime);
  }

  @Override
  public ResultSet getResultSet(String queryString, Dataset dataset) {
    return QueryExecutionFactory.sparqlService(sparqlEndPointQ, queryString).execSelect();
  }

  @Override
  public long numTriples(String graph) {

    String query;
    if(graph == null || graph.isEmpty()) {
      query = "select (count (?a) as ?count)  where { ?a ?b ?c }";
    } else {
      query = "select (count (?a) as ?count)  where { graph <"+graph+ "> { ?a ?b ?c } }";
    }

    try {
      ResultSet rss = QueryExecutionFactory.sparqlService(sparqlEndPointQ, query).execSelect();
      long count = rss.next().getLiteral("count").getLong();
      return count;

    } catch (Exception e) {
      e.printStackTrace();
    }
    return -1l;
  }

  @Override
  public Map<String, Long> numTriples() {
    HashMap<String, Long> result = new HashMap<>();

    Long instanceTriples = new Long(this.numTriples(InMemGraphSet.INSTANCE_GRAPH));
    Long woaTriples = new Long(this.numTriples(InMemGraphSet.WOA_GRAPH));
    Long schemaTriples = new Long(this.numTriples(InMemGraphSet.SCHEMA_UNION_GRAPH));
    Long fullUnionTriples = new Long(this.numTriples(getFullUnionNamespace()));

    result.put(InMemGraphSet.INSTANCE_GRAPH, instanceTriples);
    result.put(InMemGraphSet.SCHEMA_UNION_GRAPH, schemaTriples);
    result.put(getFullUnionNamespace(), fullUnionTriples);
    result.put(InMemGraphSet.WOA_GRAPH, woaTriples);

    return result;
  }
}
