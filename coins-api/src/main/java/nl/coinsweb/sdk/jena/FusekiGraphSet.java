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

import nl.coinsweb.sdk.CoinsGraphSet;
import nl.coinsweb.sdk.Namespace;
import nl.coinsweb.sdk.validator.*;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

/**
 * @author Bastiaan Bijl
 */
public class FusekiGraphSet extends TDBGraphSet implements CoinsGraphSet {

  private static final Logger log = LoggerFactory.getLogger(FusekiGraphSet.class);

  private String host;
  private String database;


  public static String sparqlEndPointU;
  public static String sparqlEndPointQ;
  public static String sparqlEndPointD;





  public FusekiGraphSet(String namespace, String host, String database) {
    super(namespace);
    this.host = host;
    this.database = database;

    sparqlEndPointU = host + "/" + database + "/update";
    sparqlEndPointQ = host + "/" + database + "/query";
    sparqlEndPointD = host + "/" + database + "/data";

  }



  @Override
  public Dataset getEmptyDataset() {
    return TDBFactory.createDataset();
  }





  @Override
  public Dataset rebuildDataset() {


    dataset = getEmptyDataset();

    updateModel(dataset, instanceNamespace.toString(), instanceModel);
    updateModel(dataset, woaNamespace.toString(), woaModel);
    for(Namespace ns : libraryModels.keySet()) {
      updateModel(dataset, ns.toString(), libraryModels.get(ns));
    }
    return dataset;
  }


  @Override
  public Dataset rebuildValidationDataset() {

    log.info("arrange dataset with union graphs");


    validationDataset = getEmptyDataset();

    updateModel(validationDataset, INSTANCE_GRAPH, instanceModel);
    updateModel(validationDataset, WOA_GRAPH, woaModel);
//    updateModel(dataset, SCHEMA_GRAPH, getSchemaModel());
    updateModel(validationDataset, SCHEMA_UNION_GRAPH, getSchemaUnionModel());
//    updateModel(dataset, FULL_UNION_GRAPH, getFullUnionModel());

    log.info("done arranging");

    return validationDataset;
  }

  @Override
  public void updateModel(Dataset dataset, String ns, Model model) {

    log.info("update model "+ns);
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
  public ValidationQueryResult select(ValidationQuery validationQuery) {

    String errorMessage = null;
    boolean passed;
    long start = new Date().getTime();
    String queryString = validationQuery.getSparqlQuery();
    Iterator<Map<String, String>> resultSet = null;
    ArrayList<String> formattedResults = new ArrayList<>();

    try {


      List<Map<String, String>> result = new ArrayList<>();



      // Execute the query and obtain results
      ResultSet results = QueryExecutionFactory.sparqlService(sparqlEndPointQ, queryString).execSelect();

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

  @Override
  public int numTriples(String graph) {

    String query;
    if(graph == null || graph.isEmpty()) {
      query = "select (count (?a) as ?count)  where { ?a ?b ?c }";
    } else {
      query = "select (count (?a) as ?count)  where { graph <"+graph+ "> { ?a ?b ?c } }";
    }

    try {
      ResultSet rss = QueryExecutionFactory.sparqlService(sparqlEndPointQ, query).execSelect();
      int count = rss.next().getLiteral("count").getInt();
      return count;

    } catch (Exception e) {
      e.printStackTrace();
    }
    return -1;
  }

  @Override
  public Map<String, Long> numTriples(Dataset dataset) {
    HashMap<String, Long> result = new HashMap<>();
    result.put(InferenceExecution.TOTAL_NUM, 0l);

    Long instanceTriples = new Long(this.numTriples("http://coinsweb.nl/INSTANCE_GRAPH"));
    Long schemaTriples = new Long(this.numTriples("http://coinsweb.nl/SCHEMA_UNION_GRAPH"));
    result.put("http://coinsweb.nl/INSTANCE_GRAPH", instanceTriples);
    result.put("http://coinsweb.nl/SCHEMA_UNION_GRAPH", schemaTriples);
    result.put(InferenceExecution.TOTAL_NUM, instanceTriples + schemaTriples);

    return result;
  }
}
