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
package nl.coinsweb.sdk.apolda.iterator;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.impl.ResourceImpl;
import org.apache.jena.util.iterator.ClosableIterator;
import nl.coinsweb.sdk.apolda.language.Language;
import nl.coinsweb.sdk.apolda.ontology.PropertyDeclaration;
import nl.coinsweb.sdk.apolda.ontology.impl.PropertyDeclarationImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class SparqlPropertyDeclarationIterator implements ClosableIterator {

  private static final Logger log = LoggerFactory.getLogger(SparqlPropertyDeclarationIterator.class);

  ResultSet resultSet;
  QueryExecution queryExecution;
  OntModel ontModel;


  HashMap<String, PropertyDeclaration> resultCollection = new HashMap<>();
  Iterator<PropertyDeclaration> resultIterator;


  public SparqlPropertyDeclarationIterator(String clazz, OntModel model) {
    this(clazz, model, null);
  }
  public SparqlPropertyDeclarationIterator(String clazz, OntModel model, String rangeSuper) {

    String queryString =

        " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>           " +
        " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>                " +
        " PREFIX owl: <http://www.w3.org/2002/07/owl#>                        " +
        " SELECT ?property ?range ?card ?propertyLabel ?property_owner        " +
        " WHERE                                                               " +
        " {                                                                   " +
        "  {                                                                  " +
        "   {                                                                 " +
        "    {                                                                " +
        "     <"+clazz+">           rdfs:subClassOf*    ?restriction .        " +
        "     ?restriction          owl:onProperty      ?property .           " +
        "     ?restriction          owl:allValuesFrom   ?range .              " +
        "    }                                                                " +
        "    UNION                                                            " +
        "    {                                                                " +
        "     {                                                               " +
        "      <"+clazz+">           rdfs:subClassOf*    ?property_owner .    " +
        "      ?property             rdfs:domain         ?property_owner .    " +
        "      ?property             rdfs:range          ?range .             " +
        "     }                                                               " +
        "     UNION                                                           " +
        "     {                                                               " +
        "      <"+clazz+">           rdfs:subClassOf*    ?property_owner .    " +
        "      ?collection           owl:unionOf         ?union .             " +
        "      ?union                rdf:rest*           ?subUnion .          " +
        "      ?subUnion             rdf:first           ?property_owner .    " +
        "      ?property             rdfs:domain         ?collection .        " +
        "      ?property             rdfs:range          ?range .             " +
        "     }                                                               " +
        "    }                                                                " +
        "   }                                                                 " +


        ((rangeSuper != null)?

        "   ?range                  rdfs:subClassOf*    <"+rangeSuper+">      ":"") +


        "   MINUS                                                             " +
        "   {                                                                 " +
        "    <"+clazz+">            rdfs:subClassOf*    ?other_restr .        " +
        "    ?other_restr           owl:onProperty      ?property .           " +
        "    ?other_restr           owl:allValuesFrom   ?other_range .        " +
        "    FILTER( ?other_range   != ?range ) .                             " +
        "    ?other_range           rdfs:subClassOf+    ?range .              " +
        "   }                                                                 " +

        "   MINUS                                                             " +
        "   {                                                                 " +
        "    <"+clazz+">            rdfs:subClassOf*    ?other_prop_owner .   " +
        "    ?property              rdfs:domain         ?other_prop_owner .   " +
        "    ?property              rdfs:range          ?other_range .        " +
        "    FILTER( ?other_range   != ?range ) .                             " +
        "    ?other_range           rdfs:subClassOf+    ?range .              " +
        "   }                                                                 " +
        "  }                                                                  " +
        "  UNION                                                              " +
        "  {                                                                  " +
        "   {                                                                 " +
        "    {                                                                " +
        "     <"+clazz+">           rdfs:subClassOf*    ?restriction .        " +
        "     ?restriction          owl:onProperty      ?property .           " +
        "    }                                                                " +
        "    UNION                                                            " +
        "    {                                                                " +
        "     <"+clazz+">           rdfs:subClassOf*    ?property_owner .     " +
        "     ?property             rdfs:domain         ?property_owner .     " +
        "    }                                                                " +
        "   }                                                                 " +
        "   MINUS                                                             " +
        "   {                                                                 " +
        "    <"+clazz+">          rdfs:subClassOf*    ?other_restr .          " +
        "    ?other_restr         owl:onProperty      ?property .             " +
        "    ?other_restr         owl:allValuesFrom   ?other_range .          " +
        "   }                                                                 " +
        "   MINUS                                                             " +
        "   {                                                                 " +
        "    <"+clazz+">          rdfs:subClassOf*    ?other_prop_owner .     " +
        "    ?property            rdfs:domain         ?other_prop_owner .     " +
        "    ?property            rdfs:range          ?other_range .          " +
        "   }                                                                 " +
        "   MINUS                                                             " +
        "   {                                                                 " +
        "    <"+clazz+">          rdfs:subClassOf*     ?other_prop_owner .    " +
        "    ?collection          owl:unionOf          ?union .               " +
        "    ?union               rdf:rest*            ?subUnion .            " +
        "    ?subUnion            rdf:first            ?other_prop_owner .    " +
        "    ?property            rdfs:domain          ?collection .          " +
        "    ?property            rdfs:range           ?other_range .         " +
        "   }                                                                 " +
        "  }                                                                  " +

        "  OPTIONAL                                                           " +
        "  {                                                                  " +
        "   ?cardrestriction       owl:onProperty      ?property .            " +
        "   ?cardrestriction       owl:cardinality     ?card .                " +
        "  }                                                                  " +
        " }                                                                   ";


    // Execute the query and obtain results
    QueryExecution qe = QueryExecutionFactory.create(queryString, Syntax.syntaxSPARQL_11, model);
//    QueryExecution qe = QueryExecutionFactory.create(queryString, Syntax.syntaxSPARQL_11, dataset);

    this.queryExecution = qe;
    this.ontModel = model;
    this.resultSet = qe.execSelect();


    // Prepare the next
    while (resultSet.hasNext()) {

      QuerySolution row = resultSet.next();


      PropertyDeclaration result = new PropertyDeclarationImpl();

      RDFNode property = row.get("property");
      if (property == null || !property.isResource() || property.asResource().getLocalName() == null) {
        log.error("found unparsable property " + property + " for querying property declaration");
        continue;
      }
      result.setPropertyUri(property.asResource().getURI());
      result.setPropertyName(property.asResource().getLocalName());




      int cardinality = row.contains("card") ? row.get("card").asLiteral().getInt() : -1;
      result.setCardinality(cardinality);

      OntProperty ontProperty = ontModel.getOntProperty(property.asResource().getURI());
      if(ontProperty == null) {
        log.warn("could not interpret " + property.asResource().getURI() + " as ont property, skipping this one");
        continue;
      }
      String label = Language.getLabel(ontProperty);
      if(label != null) {
        result.setPropertyLabel(label);
      }
      result.setOwlFunctionalProperty(ontProperty.isFunctionalProperty());

      RDFNode range = row.get("range");
      if (range != null && range.isResource() && range.asResource().getLocalName() != null) {
        result.setRangeUri(range.asResource().getURI());
        result.setRangeName(range.asResource().getLocalName());
      }


      RDFNode propertyOwner = row.get("property_owner");
      if (propertyOwner != null && propertyOwner.isResource()) {
        result.setPropertyOwner(propertyOwner.asResource().getURI());
      }

      if(!resultCollection.containsKey(property.asResource().getURI())) {
        resultCollection.put(property.asResource().getURI(), result);
      } else {
        // Choose which of the two to pick

        // Prefer the same namespace between domain and range
        Resource domain = new ResourceImpl(clazz);
        if(range == null || !resultCollection.get(property.asResource().getURI()).hasRange()) {
          log.warn("No way to choose from two property definitions for "+property.asResource().getURI()+", skipping one.");
          continue;
        }
        boolean newDeclarationHasEqualNs = domain.getNameSpace().equals(range.asResource().getNameSpace());
        boolean oldDeclarationHasEqualNs = domain.getNameSpace().equals(new ResourceImpl(resultCollection.get(property.asResource().getURI()).getRangeUri()).getNameSpace());

        if(!oldDeclarationHasEqualNs && newDeclarationHasEqualNs) {
          resultCollection.put(property.asResource().getURI(), result);
        } else if(oldDeclarationHasEqualNs && !newDeclarationHasEqualNs) {
          // keep the old one
        } else {
          log.warn("No way to choose from two property definitions for "+property.asResource().getURI()+", skipping one.");
        }
      }
    }
    queryExecution.close();
    resultIterator = resultCollection.values().iterator();
  }





  @Override
  public void close() {
  }

  @Override
  public boolean hasNext() {
    return resultIterator.hasNext();
  }

  @Override
  public PropertyDeclaration next() {
    return resultIterator.next();
  }

  @Override
  public void remove() {
    resultIterator.remove();
  }
}
