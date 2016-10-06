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

import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.riot.RDFFormat;

import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public interface CoinsGraphSet {

  Map<Namespace, Model> getLibraryModels();
  public void reset();





  // Namespaces

  /**
   * List the names of the models, namespaces of the instance model (the first one) and the libraries.
   *
   * @return  iterator of String namespaces
   */
  public Iterator<String> listModelNames();

  /**
   * Configuration option to specify which namespace will be used for the instance model.
   *
   * @param namespace  set default to "http://sandbox.coinsweb.nl/default"
   */
  public void setInstanceNamespace(String namespace);

  /**
   * Returns the namespace used for the instance model.
   *
   * @return  the namespace ("http://sandbox.coinsweb.nl/default")
   */
  public String getInstanceNamespace();
  public String getInstanceNamespaceWithoutHash();

  public void setWoaNamespace(String namespace);
  public String getWoaNamespace();




  // Models

  public Model getEmptyModel();

  /**
   * If implemented as Jena model, return a Jena Model object containing the instances.
   *
   * @return  a Jena Model containing all instances, no libraries
   *          null if not implemented
   */
  public Model getInstanceModel();

  public Model getWoaModel();
  public Model getSchemaModel();
  public Model getSchemaUnionModel();

  /**
   * If implemented as Jena model, return a Jena Model object containing all data (instance an library).
   *
   * For only the instance data, use asJenaOntModel().
   *
   * @return  a Jena OntModel containing the union over the instance model and all library models
   *          null if not implemented
   */
  public Model getFullUnionModel();

  /**
   * If implemented as Jena model, return a Jena OntModel of the model represented by the namespaces.
   *
   * @param namespace  an uri used to represent a model (e.g. 'http://www.coinsweb.nl/cbim-2.0.rdf#')
   * @return  a Jena Model containing all instances, no libraries
   *          null if no model by this name
   *          null if not implemented
   */
  public Model getModel(String namespace);



  // OntModels

  public OntModel asOntModel(Model model);
  public OntModel asOntModel(Model model, Reasoner reasoner);

  /**
   * If implemented as Jena model, return a Jena OntModel object containing the instances.
   *
   * @return  a Jena OntModel containing all instances, no libraries
   *          null if not implemented
   */
  public OntModel getInstanceOntModel();
  public OntModel getInstanceOntModel(Reasoner reasoner);

  /**
   * If implemented as Jena model, return a Jena OntModel of the model represented by the namespaces.
   *
   * (!) No libraries are included, see unionJenaOntModel().
   *
   * @param namespace  an uri used to represent a model (e.g. 'http://www.coinsweb.nl/cbim-2.0.rdf#')
   * @return  a Jena OntModel containing all instances, no libraries
   *          null if no model by this name
   *          null if not implemented
   */
  public OntModel getJenaOntModel(String namespace);
  public OntModel getJenaOntModel(String namespace, Reasoner reasoner);

  /**
   * If implemented as Jena model, return a Jena OntModel object containing all data (instance an library).
   *
   * For only the instance data, use asJenaOntModel().
   *
   * @return  a Jena OntModel containing the union over the instance model and all library models
   *          null if not implemented
   */
  public OntModel getUnionJenaOntModel();
  public OntModel getUnionJenaOntModel(Reasoner reasoner);


  // Datasets

  public Dataset getEmptyDataset();

  public Dataset getDataset();

  public Dataset getDatasetWithUnionGraphs();

  public void writeModelToFile(Model model, OutputStream output, RDFFormat format);

  public String writeModelToString(Model model, RDFFormat format);

  public void writeFullToFile(OutputStream output, RDFFormat format);
  public void writeFullToFile(Dataset dataset, OutputStream output, RDFFormat format);

}