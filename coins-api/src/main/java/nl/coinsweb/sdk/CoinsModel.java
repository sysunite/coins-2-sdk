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

import org.apache.jena.rdf.model.Model;
import nl.coinsweb.sdk.injectors.Injector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public interface CoinsModel {


  /**
   * Configuration section
   */


  /**
   * Configuration option to specify which language versions of string values should be read. Languages
   * that are not specified will not be returned.
   *
   * @param priority  set default to ["EN","NL"]
   */
  public void setLanguagePriority(String[] priority);

  /**
   * Add an injector to make the CoinsModel to execute additional operations for some triples.
   *
   * @param injector
   */
  public void addInjector(Injector injector);

  /**
   * Return the currently active list of injectors.
   *
   * @return  a list of injectors
   */
  public ArrayList<Injector> getInjectors();

  /**
   * Disable checking for woa access rights. Needs to be disabled after loading a new model.
   *
   * @return  a list of injectors
   */
  public void disableWOA();

  /**
   * Check if the current model imports the library represented by the specified namespace.
   *
   * @param namespace  the namespace representing the import ("http://otl.prorail.nl/2016/02/otl-1.owl")
   * @return  true if the ontology header imports this namespace
   *          false if there is no ontology header or if there is no import of this namespace
   *
   */
  public boolean hasImport(String namespace);
  public boolean hasImport(Model model, String namespace);

  /**
   * Add an import statement to the ontology header. If the model should actually try to load it, set
   * tryToLoad to true.
   *
   * Copy a .ttl, .rdf or .owl file to the doc folder and register it as a candidate to load in the model when
   * the related namespace is added as import dependency.
   *
   * By default the most recent version of the Coins Core model is loaded as "http://www.coinsweb.nl/cbim-2.0.rdf".
   *
   *
   * @param filePath  a file path ('/tmp/reference_model.rdf' or 'C:\tmp\otl.ttl')
   * @param namespace  the namespace representing the import ("http://otl.prorail.nl/2016/02/otl-1.owl")
   * @param addAsImport  add the attachment to the ontology header as import
   * @param tryToLoad  if set to true try to load the model as well
   * @param addToDoc  add to the doc folder
   */
  public void addImport(String filePath, String namespace, boolean addAsImport, boolean tryToLoad, boolean addToDoc);
  public void addImport(Model model, String filePath, String namespace, boolean addAsImport, boolean tryToLoad, boolean addToDoc);


  /**
   * Return the active party. The object is not a CoinsObject, but can be cast to it using CoinsParty.as(...).
   *
   * @return  the active party
   */
  public CoinsParty getActiveParty();

  /**
   * Register the party that is used to check and record the authority.
   *
   * @return  the active party
   */
  public void setActiveParty(CoinsParty party);




  /**
   * Data modification section
   */


  /**
   * List all owl:Class objects in all the loaded libraries.
   *
   * @return  a list of uri's
   */
  public Iterator<String> listClasses();

  /**
   * List all owl:Class objects in the specified library namespace.
   *
   * @return  a list of uri's
   */
  public Iterator<String> listClassesInLibrary(String namespace);

  /**
   * List all instances of any owl:Class from the instance model.
   *
   * @return  a list of uri's
   */
  public Iterator<String> listIndividuals();
  public Iterator<String> listIndividuals(Model model);

  /**
   * List all instances of the owl:Class subclass represented by the specified CoinsObject class.
   *
   * @param objectClass  the CoinsObject to find instances of
   * @return  a list of Java instances of the specified CoinsObject class
   */
  public <T extends CoinsObject> Iterator<T> listIndividuals(Class<T> objectClass);
  public <T extends CoinsObject> Iterator<T> listIndividuals(Model model, Class<T> objectClass);

  /**
   * List all instances of the owl:Class subclass represented by the specified uri.
   *
   * @param classUri  the uri to find instances of
   * @return  a list of Java instances of the specified CoinsObject class
   */
  public Iterator<String> listIndividuals(String classUri);
  public Iterator<String> listIndividuals(Model model, String classUri);

  /**
   * List all the individuals the specified uri is a class of. Reverse of listClassUris().
   *
   * @param objectClass  the uri of the class
   * @return  a set of class uris
   */
  public <T extends CoinsObject> Set<String> listIndividualUris(Class<T> objectClass);

  /**
   * List all the individuals the specified uri is a class of. Reverse of listClassUris().
   *
   * @param classUri  the uri of the class
   * @return  a set of class uris
   */
  public Set<String> listIndividualUris(String classUri);

  /**
   * Instantiate an Individual based on its uri.
   *
   * @param individualUri an uri ('http://sandbox.coinsweb.nl/default#123')
   * @return  a RuntimeCoinsObject based on the supplied uri
   */
  public RuntimeCoinsObject getIndividual(String individualUri);
  public RuntimeCoinsObject getIndividual(Model model, String individualUri);

  /**
   * Perform a sparql query on the union model. Graph names can be used to specify to look in any
   * library or the instance model.
   *
   * @param sparqlQuery the query, SPARQL 1.1 is used
   * @return  an iterator returning rows of the result set
   *          as a map with binding -> value ("comment" -> "Color used for ...")
   */
  public Iterator<Map<String,String>> query(String sparqlQuery);

  /**
   * Set the creator.
   *
   * @param instanceUri the uri to supply with the extra type definition
   * @param party the party object carrying the uri of the creator
   */
  public void addCreator(String instanceUri, CoinsParty party);
  public void addCreator(Model model, String instanceUri, CoinsParty party);

  /**
   * Set the creator on all individuals in the instance model that do not have a creator yet
   * (internally uses listIndividuals()).
   *
   * @param party the party object carrying the uri of the creator
   */
//  public void batchAddCreator(String instanceUri, CoinsParty party);

  /**
   * Set the creation date to now.
   *
   * @param instanceUri the uri to supply with the extra type definition
   */
  public void addCreatedNow(String instanceUri);
  public void addCreatedNow(Model model, String instanceUri);

  /**
   * Set the creation date to now on all individuals in the instance model that do not have a date yet
   * (internally uses listIndividuals()).
   *
   */
//  public void batchAddCreatedNow();

  /**
   * Add a type definition to the coins2:CoinsContainerObject object from the supplied uri.
   *
   * @param instanceUri the uri to supply with the extra type definition
   */
  public void addCoinsContainerObjectType(String instanceUri);

  /**
   * Add a type definition to the coins2:CoinsContainerObject to all the individuals in the instance model.
   * This function uses listIndividuals() internally.
   */
  public void batchAddCoinsContainerObjectType();



  /**
   * Transformations section
   */


  /**
   * Return the associated CoinsContainer object.
   *
   * @return  the CoinsContainer related to this model
   */
  public CoinsContainer getCoinsContainer();

  /**
   * Transform the CoinsModel into an object with advanced options.
   *
   * @return  a version of the CoinsModel with advances options
   */
  public ExpertCoinsModel asExpertCoinsModel();



  public CoinsGraphSet getCoinsGraphSet();

}
