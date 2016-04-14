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
package nl.coinsweb.sdk.injectors;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import nl.coinsweb.sdk.CoinsModel;
import nl.coinsweb.sdk.exceptions.WOAAccessDeniedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class WOAInjector implements Injector {

  protected static final Logger log = LoggerFactory.getLogger(WOAInjector.class);

  private static final String NO_ACCESS = "http://www.coinsweb.nl/COINSWOA.rdf#NoAccess";
  private static final String READ_ACCESS = "http://www.coinsweb.nl/COINSWOA.rdf#ReadAccess";
  private static final String WRITE_ACCESS = "http://www.coinsweb.nl/COINSWOA.rdf#WriteAccess";

  Model woaModel;
  OntModel instanceModel;
  HashMap<String, Clearance> cache = new HashMap<>();

  public WOAInjector(Model woaModel, OntModel instanceModel) {
    this.woaModel = woaModel;
    this.instanceModel = instanceModel;

    initCache();
  }

  /*

      http://www.coinsweb.nl/wiki/index.php/Window_of_Authorization

      Finally, all (non-function fulfiller) information objects that cannot be reached
      using above procedure will receive by default write-access rights.

   */

  private void initCache() {

    String queryString =

        " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
        " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>      \n" +
        " PREFIX owl: <http://www.w3.org/2002/07/owl#>              \n" +
        " PREFIX woa: <http://www.coinsweb.nl/COINSWOA.rdf#>        \n" +
        " SELECT ?uri ?level ?depth                                 \n" +
        " WHERE                                                     \n" +
        " {                                                         \n" +
        "    ?uri rdf:type ?level .                                 \n" +
        "    OPTIONAL {                                             \n" +
        "      ?uri woa:layerdepth ?depth .                         \n" +
        "    }                                                      \n" +
        " }                                                           ";

    // Execute the query and obtain results
    QueryExecution queryExecution = QueryExecutionFactory.create(queryString, Syntax.syntaxSPARQL_11, woaModel);
    ResultSet result = queryExecution.execSelect();

    // Output query results
    while (result.hasNext()) {

      QuerySolution row = result.next();

      RDFNode uri = row.get("uri");
      RDFNode level = row.get("level");
      RDFNode depth = row.get("depth");

      if(!uri.isResource() || !level.isResource()) {
        continue;
      }

      int depthInt = 0;
      if(depth!=null && depth.isLiteral()) {
        depthInt = depth.asLiteral().getInt();
      }

      // Check if the level is valid
      if(!NO_ACCESS.equals(level.toString()) && !READ_ACCESS.equals(level.toString()) && !WRITE_ACCESS.equals(level.toString())) {
        continue;
      }

      setCacheValue(new Clearance(uri.toString(), level.toString(), 0));
      List<Clearance> subParts = getParts(new ArrayList<Clearance>(), uri.asNode(), 1, depthInt, level.toString());
      for(Clearance part : subParts) {
        setCacheValue(part);
      }
    }

    queryExecution.close();
  }

  private void setCacheValue(Clearance clearance) {

    // Is the level was not set already, easy
    if(!cache.containsKey(clearance.uri)) {
      log.info("set level "+clearance.level+" for "+clearance.uri);
      cache.put(clearance.uri, clearance);
      return;
    }

    // Else, see which is the more precise
    Clearance current = cache.get(clearance.uri);

    // Keep the old one
    if(current.distance < clearance.distance) {
      return;
    }

    // Take the new one
    if(clearance.distance < current.distance) {
      log.info("set level "+clearance.level+" for "+clearance.uri);
      cache.put(clearance.uri, clearance);
      return;
    }

    // If the distance is equal, pick the more restrictive
    if(NO_ACCESS.equals(clearance.level) || NO_ACCESS.equals(current.level)) {
      log.info("set level "+NO_ACCESS+" for "+clearance.uri);
      cache.put(clearance.uri, new Clearance(clearance.uri, NO_ACCESS, clearance.distance));
      return;
    }

    if(READ_ACCESS.equals(clearance.level) || READ_ACCESS.equals(current.level)) {
      log.info("set level "+READ_ACCESS+" for "+clearance.uri);
      cache.put(clearance.uri, new Clearance(clearance.uri, READ_ACCESS, clearance.distance));
      return;
    }

    if(WRITE_ACCESS.equals(clearance.level) || WRITE_ACCESS.equals(current.level)) {
      log.info("set level "+WRITE_ACCESS+" for "+clearance.uri);
      cache.put(clearance.uri, new Clearance(clearance.uri, WRITE_ACCESS, clearance.distance));
      return;
    }

  }

  private boolean hasClearance(String uri, String level) {

    // Grant by default
    if(!cache.containsKey(uri)) {
      return true;
    }

    // If requesting read access only no_access can block
    if(READ_ACCESS.equals(level) && cache.get(uri).level.equals(NO_ACCESS)) {
      return false;
    }

    // If requesting write access both no_access and read_access can block
    if(WRITE_ACCESS.equals(level) && cache.get(uri).level.equals(NO_ACCESS) ||
       WRITE_ACCESS.equals(level) && cache.get(uri).level.equals(READ_ACCESS)) {
      return false;
    }

    return true;
  }

  private class Clearance {
    public String uri;
    public String level;
    public int distance;
    public Clearance(String uri, String level, int distance) {
      this.uri = uri;
      this.level = level;
      this.distance = distance;
    }
    @Override
    public int hashCode() {
      return level.hashCode();
    }
    @Override
    public boolean equals(Object o) {
      return level.equals(o);
    }
  }




  @Override
  public boolean proposeRead(CoinsModel model, String subject) {



    if(!hasClearance(subject, READ_ACCESS)) {
      log.trace("Received 🛂 propose read, deny.");
      throw new WOAAccessDeniedException("WOA restriction blocked operation.");
    }

    log.trace("Received 🛂 propose read, grant." );
    return true;
  }

  @Override
  public boolean proposeRead(CoinsModel model, String subject, String predicate) {

    if(!hasClearance(subject, READ_ACCESS)) {
      log.trace("Received 🛂 propose read, deny.");
      throw new WOAAccessDeniedException("WOA restriction blocked operation.");
    }

    log.trace("Received 🛂 propose read, grant." );
    return true;
  }

  @Override
  public boolean proposeWrite(CoinsModel model, String subject, String predicate, String object) {

    if(!hasClearance(subject, WRITE_ACCESS)) {
      log.trace("Received 🛂 propose write, deny.");
      throw new WOAAccessDeniedException("WOA restriction blocked operation.");
    }

    log.trace("Received 🛂 propose write, grant." );
    return true;
  }


  /**
   *
   * @param parts
   * @param uri
   * @param currentDepth  starts with 1 and when bigger than totalDepth stop
   * @param totalDepth
   * @return
   */
  private List<Clearance> getParts(ArrayList<Clearance> parts, Node uri, int currentDepth, int totalDepth, String level) {

    if(currentDepth > totalDepth) {
      return parts;
    }

    HashSet<Node> containsRelations = new HashSet<>();
    ExtendedIterator<Triple> iterator;

    // Assembly --hasContainsRelation--> ContainsRelation
    iterator = instanceModel.getGraph().find(uri, new PropertyImpl("http://www.coinsweb.nl/cbim-2.0.rdf#hasContainsRelation").asNode(), Node.ANY);
    while (iterator.hasNext()) {

      Triple triple = iterator.next();
      Node containsRelation = triple.getObject();
      if (containsRelation.isURI()) {

        // Add it
        containsRelations.add(containsRelation);
      }
    }

    // ContainsRelation --hasAssembly--> Assembly
    iterator = instanceModel.getGraph().find(Node.ANY, new PropertyImpl("http://www.coinsweb.nl/cbim-2.0.rdf#hasAssembly").asNode(), uri);
    while (iterator.hasNext()) {

      Triple triple = iterator.next();
      Node containsRelation = triple.getSubject();
      if (containsRelation.isURI()) {

        // Add it
        containsRelations.add(containsRelation);
      }
    }

    Iterator<Node> relIterator = containsRelations.iterator();
    while(relIterator.hasNext()) {
      Node containsRelation = relIterator.next();



      // ContainsRelation --hasPart--> Part
      iterator = instanceModel.getGraph().find(containsRelation, new PropertyImpl("http://www.coinsweb.nl/cbim-2.0.rdf#hasPart").asNode(), Node.ANY);
      while (iterator.hasNext()) {

        Triple triple = iterator.next();
        Node part = triple.getObject();
        if (part.isURI()) {

          // Add it
          parts.add(new Clearance(part.getURI(), level, currentDepth));

          // Add all sub parts
          getParts(parts, part, currentDepth+1, totalDepth, level);
        }
      }

      // Part --partOf--> ContainsRelation
      iterator = instanceModel.getGraph().find(Node.ANY, new PropertyImpl("http://www.coinsweb.nl/cbim-2.0.rdf#partOf").asNode(), containsRelation);
      while (iterator.hasNext()) {

        Triple triple = iterator.next();
        Node part = triple.getSubject();
        if (part.isURI()) {

          // Add it
          parts.add(new Clearance(part.getURI(), level, currentDepth));

          // Add all sub parts
          getParts(parts, part, currentDepth - 1, totalDepth, level);
        }
      }
    }

    return parts;
  }
}
