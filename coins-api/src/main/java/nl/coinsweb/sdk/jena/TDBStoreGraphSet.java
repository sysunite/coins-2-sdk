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
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;
import nl.coinsweb.sdk.CoinsGraphSet;
import nl.coinsweb.sdk.FileManager;
import nl.coinsweb.sdk.validator.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.codehaus.plexus.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Bastiaan Bijl
 */
public class TDBStoreGraphSet extends InMemGraphSet implements CoinsGraphSet {

  private static final Logger log = LoggerFactory.getLogger(TDBStoreGraphSet.class);

  private static final String DEFAULT_TEMP_FOLDER = "/tmp/";
  private static final String TDB_FOLDER = "coinstdb/";

  private Path path;







  public TDBStoreGraphSet(String namespace) {
    super(namespace);
    log.info("Start new tdb for namespace given file.");
    Path tempPath = Paths.get(DEFAULT_TEMP_FOLDER);
    Path path = tempPath.resolve(TDB_FOLDER + RandomStringUtils.random(8, true, true) + "/");
    path.toFile().mkdirs();
    FileManager.foldersToCleanup.add(tempPath.resolve(TDB_FOLDER).toFile());
    this.path = path;
  }

  @Override
  public boolean supportsMultiThreading() {
    return true;
  }

  @Override
  public String getFullUnionNamespace() {
    return "urn:x-arq:UnionGraph";
  }

  @Override
  public Dataset getEmptyDataset() {
    return TDBFactory.createDataset(path.toString());
  }



  @Override
  public Iterator<Map<String, String>> query(String sparqlQuery) {

    Iterator<Map<String, String>> result = null;
    log.trace("Start read transaction.");
    getDataset().begin(ReadWrite.READ) ;
    try {
      result = super.query(sparqlQuery);
    } finally {
      log.trace("Stop read transaction.");
      getDataset().end() ;
    }
    return result;

  }


  @Override
  public void insert(InferenceQuery query, InferenceQueryResult result) {

    log.trace("Start write transaction.");
    getValidationDataset().begin(ReadWrite.WRITE);
    try {
      super.insert(query, result);
      getValidationDataset().commit();
    } finally {
      log.trace("Stop write transaction.");
      getValidationDataset().end() ;
    }
  }


  public ResultSet getResultSet(String queryString, Dataset dataset) {
    Query query = QueryFactory.create(queryString);

    // Execute the query and obtain results
    QueryExecution qe = QueryExecutionFactory.create(query, dataset);
    ResultSet result = qe.execSelect();
    return result;
  }
  @Override
  public ValidationQueryResult select(ValidationQuery validationQuery) {

    ValidationQueryResult result = null;
    log.trace("Start read transaction.");
    getValidationDataset().begin(ReadWrite.READ) ;
    try {
      result = super.select(validationQuery);
    } finally {
      log.trace("Stop read transaction.");
      getValidationDataset().end() ;
    }
    return result;
  }




  @Override
  public Map<String, Long> numTriples() {

    Map<String, Long> result = new HashMap<>();
    log.trace("Start read transaction.");
    getValidationDataset().begin(ReadWrite.READ) ;
    try {


      result.put(InferenceExecution.TOTAL_NUM, 0l);

      long instanceTriples = this.numTriples(InMemGraphSet.INSTANCE_GRAPH);
      long woaTriples = this.numTriples(InMemGraphSet.WOA_GRAPH);
      long schemaTriples = this.numTriples(InMemGraphSet.SCHEMA_UNION_GRAPH);
      long fullUnionTriples = this.numTriples(getFullUnionNamespace());

      result.put(InMemGraphSet.INSTANCE_GRAPH, instanceTriples);
      result.put(InMemGraphSet.SCHEMA_UNION_GRAPH, schemaTriples);
      result.put(getFullUnionNamespace(), fullUnionTriples);
      result.put(InferenceExecution.TOTAL_NUM, instanceTriples + schemaTriples);
      result.put(InMemGraphSet.WOA_GRAPH, woaTriples);

    } finally {
      log.trace("Stop read transaction.");
      getValidationDataset().end() ;
    }
    return result;
  }

  public void close(Dataset dataset) {

    TDB.sync(dataset);
    dataset.close();
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      log.error(e.getMessage(), e);
    }

    // destroy
    try {
      FileUtils.deleteDirectory(path.toFile());
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }
}
