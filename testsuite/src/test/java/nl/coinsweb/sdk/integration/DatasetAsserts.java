package nl.coinsweb.sdk.integration;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class DatasetAsserts {

  private static final Logger log = LoggerFactory.getLogger(DatasetAsserts.class);

  public static boolean verifyCompleteContent(JenaCoinsContainer model, Iterator<File> nquadFiles) {
    return verifyCompleteContent(model, nquadFiles, false);
  }
  public static boolean verifyCompleteContent(JenaCoinsContainer model, Iterator<File> nquadFiles, boolean quick) {

    if(quick)
      log.trace("start quick verification");
    else
      log.trace("start thorough verification");

    boolean allLinesFoundInVerificationFile = true;

    try {

      // Read statements from nquadFile into map
      HashMap<String, Integer> verificationLines = new HashMap<>();
      while(nquadFiles.hasNext()) {

        File nquadFile = nquadFiles.next();
        log.trace("added verifylines from file: " + nquadFile.getName());

        BufferedReader br = new BufferedReader(new FileReader(nquadFile));
        String line = null;
        while ((line = normalizeBlankNode(br.readLine())) != null) {
          if (verificationLines.containsKey(line)) {
            verificationLines.put(line, verificationLines.get(line) + 1);
          } else {
            verificationLines.put(line, 1);
          }
        }
      }

      // Iterate over statements
      ByteArrayOutputStream boas = new ByteArrayOutputStream();
      model.getCoinsGraphSet().writeFullToFile(boas, RDFFormat.NQUADS);
      BufferedReader reader = new BufferedReader(new StringReader(boas.toString()));



      String line = null;
      log.error("lines from coinsmodel were not found in verification file:");
      while ((line = normalizeBlankNode(reader.readLine())) != null) {
        if(!verificationLines.containsKey(line)) {
          log.error(line);
          allLinesFoundInVerificationFile = false;
          if(quick) {
            return allLinesFoundInVerificationFile;
          }
        } else {
          if (verificationLines.get(line) == 1) {
            verificationLines.remove(line);
          } else {
            verificationLines.put(line, verificationLines.get(line) - 1);
          }
        }
      }

      if(verificationLines.isEmpty()) {
        return allLinesFoundInVerificationFile;
      }


      log.error("the following lines from the verification file where not in the coins model: ");
      for(String notFoundLine : verificationLines.keySet()) {
        log.error(notFoundLine);
      }

    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }

    return allLinesFoundInVerificationFile;
  }

  public static boolean containsNodes(Model model, Set<RDFNode> requiredNodes, boolean allowOtherNodes) {

    Set<RDFNode> packingList = new HashSet(requiredNodes);


    // Create a new query
    String queryString =
        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
            "PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
            "SELECT ?subject ?predicate  ?object  " +
            "WHERE { " +
            "      ?subject ?predicate ?object . " +
            "}";

    Query query = QueryFactory.create(queryString);

    // Execute the query and obtain results
    QueryExecution qe = QueryExecutionFactory.create(query, model);
    ResultSet results = qe.execSelect();


    // Output query results
    while(results.hasNext()) {

      QuerySolution row = results.next();

      RDFNode subject = row.get("subject");
      RDFNode predicate = row.get("predicate");
      RDFNode object = row.get("object");

      // Check if these nodes was contained in the requiredNodes list
      if(subject.isResource() && requiredNodes.contains(subject)) {
        packingList.remove(subject);
      } else if(subject.isResource() && !allowOtherNodes) {
        return false;
      }
      if(predicate.isResource() && requiredNodes.contains(predicate)) {
        packingList.remove(predicate);
      } else if(predicate.isResource() && !allowOtherNodes) {
        return false;
      }
      if(object.isResource() && requiredNodes.contains(object)) {
        packingList.remove(object);
      } else if(object.isResource() && !allowOtherNodes) {
        return false;
      }

    }

    // Important - free up resources used running the query
    qe.close();

    // If all the files from the fileNames where in the zip archive, this list is now supposed to be emtpy
    return packingList.isEmpty();
  }

  private static String normalizeBlankNode(String line) {

    String result = line;
    while(result != null && result.length() > 0 && result.contains("_:")) {
      int begin = result.indexOf("_:");
      int end = result.indexOf(" ", begin);
      result = (result.substring(0, begin) + "$SOME_BN$" + result.substring(end)).trim();
    }

    return result;
  }

  public static int countTriples(Model model) {
    int i = 0;
    StmtIterator iterator = model.listStatements();
    while(iterator.hasNext()) {
      iterator.next();
      i++;
    }
    return i;
  }

  public static void logTriples(Model model) {
    StmtIterator iterator = model.listStatements();
    while(iterator.hasNext()) {
      log.info(iterator.next().toString());
    }
  }

  public static void logTriples(Dataset dataset) {
    ByteArrayOutputStream boas = new ByteArrayOutputStream();
    RDFDataMgr.write(boas, dataset, RDFFormat.NQUADS);

    try {
      BufferedReader reader = new BufferedReader(new StringReader(boas.toString()));

      String line = null;
      while ((line = reader.readLine()) != null) {
        log.info(line);
      }
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }
}
