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
package nl.coinsweb.sdk;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import nl.coinsweb.sdk.apolda.ontology.PropertyDeclaration;
import org.apache.jena.riot.RDFFormat;

import java.util.Iterator;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public interface ExpertCoinsModel extends BindingCoinsModel {


  /**
   * Checks whether an owl:Ontology instance is present in the instance Model.
   *
   * @return  true if the owl:Ontology is present
   */
  public boolean hasOntologyHeader();


  /**
   * Create an owl:Ontology instance ins the instance Modal. Meta data is added
   * to the object.
   */
  public void addOntologyHeader();


  /**
   * Used to cleanly close the model provider. Important to use when a persistent store provider is connected.
   */
  public void close();


  /**
   * Iterate over all available classes in the connected models (instance and libraries) as Jena OntClass.
   *
   * @return  a Jena OntClass iterator
   */
  public ExtendedIterator<OntClass> listOntClasses();


  /**
   * Iterate over all property declarations found for the specified classUri.
   *
   * Used by the coins-owlgenerator to generate the Java library wrappers (e.g. cbim.jar / cbim.dll).
   *
   * @param classUri  the uri of a coins class
   * @return  an iterator with PropertyDeclaration objects
   */
  public Iterator<PropertyDeclaration> listPropertyDeclarations(String classUri);


  /**
   * Low level access to add statements to the instance Model.
   *
   * @param subject  uri
   * @param predicate  uri
   * @param object  uri (!) to add literals use the method with RDFNode
   */
  public void addStatement(String subject, String predicate, String object);


  /**
   * Low level access to add statements to the instance Model.
   *
   * @param subject  uri
   * @param predicate  uri
   * @param object  RDFNode, for example containing a literal
   */
  public void addStatement(String subject, String predicate, RDFNode object);


  /**
   * Low level access to remove statements from the instance Model.
   *
   * @param subject  uri
   * @param predicate  uri
   * @param object  uri (!) to add literals use the method with RDFNode
   */
  public void removeStatement(String subject, String predicate, String object);


  /**
   * Low level access to remove statements from the instance Model.
   *
   * @param subject  uri
   * @param predicate  uri
   * @param object  RDFNode, for example containing a literal
   */
  public void removeStatement(String subject, String predicate, RDFNode object);


  /**
   * Export the instance model as a string.
   *
   * @return  string consisting rdf/xml format export of the instance model
   */
  public String exportAsString();

  /**
   * Export the instance model as a string.
   *
   * @param format  jena export format (e.g. RDFFormat.TTL)
   * @return  string consisting export of the instance model in specified format
   */
  public String exportAsString(RDFFormat format);


  /**
   * Transform this Expert CoinsModel model to a simpler CoinsModel. It is the same object,
   * but it is 'cast down' to the CoinsModel interface.
   *
   * @return  the same object as coins model
   */
  public CoinsModel asCoinsModel();
}
