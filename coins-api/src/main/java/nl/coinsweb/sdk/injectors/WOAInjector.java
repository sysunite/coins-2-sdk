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

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import nl.coinsweb.sdk.CoinsModel;
import nl.coinsweb.sdk.exceptions.WOAAccessDeniedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class WOAInjector implements Injector {

  protected static final Logger log = LoggerFactory.getLogger(WOAInjector.class);

  private static final String NO_ACCESS = "http://www.coinsweb.nl/COINSWOA.rdf#NoAccess";
  private static final String READ_ACCESS = "http://www.coinsweb.nl/COINSWOA.rdf#ReadAccess";
  private static final String WRITE_ACCESS = "http://www.coinsweb.nl/COINSWOA.rdf#WriteAccess";

  Model woaModel;

  public WOAInjector(Model woaModel) {
    this.woaModel = woaModel;
  }

  /*

      http://www.coinsweb.nl/wiki/index.php/Window_of_Authorization

      Finally, all (non-function fulfiller) information objects that cannot be reached
      using above procedure will receive by default write-access rights.

   */

  private boolean hasClearance(String uri, String level) {

    String queryString =

        " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
        " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>      " +
        " PREFIX owl: <http://www.w3.org/2002/07/owl#>              " +
        " SELECT ?level                                             " +
        " WHERE                                                     " +
        " {                                                         " +
        "    <"+uri+">  rdf:type    ?level .                        " +
        " }                                                         ";

    log.trace(queryString);

    // Execute the query and obtain results
    QueryExecution queryExecution = QueryExecutionFactory.create(queryString, Syntax.syntaxSPARQL_11, woaModel);
    ResultSet result = queryExecution.execSelect();

    // Output query results
    while (result.hasNext()) {

      RDFNode foundLevel = result.next().get("level");
      if(!foundLevel.isResource()) {
        continue;
      }

      if(READ_ACCESS.equals(level) && foundLevel.toString().equals(NO_ACCESS)) {
        return false;
      }

      if(WRITE_ACCESS.equals(level) && foundLevel.toString().equals(NO_ACCESS) ||
         WRITE_ACCESS.equals(level) && foundLevel.toString().equals(READ_ACCESS)) {
        return false;
      }
    }
    return true;
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
}
