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
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import nl.coinsweb.sdk.apolda.ontology.PropertyDeclaration;
import org.apache.jena.riot.RDFFormat;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public interface ExpertCoinsModel extends CoinsModel {

  /**
   * Generate an uri as identifier for a new CoinsObject.
   *
   * @return  uri
   */
  public String generateUri();


  /**
   * List all the classes the specified uri is an instance of.
   *
   * @param instanceUri  the uri of the instance
   *
   * @return  a set of class uris
   */
  public Set<String> listClassUris(String instanceUri);

  /**
   * Return if the specified instance has the specified class as class by rdf:type definition.
   *
   * @param instanceUri  the uri of the instance
   * @param classUri  the uri of the class
   *
   * @return  true if a rdf:type definition exists
   */
  public boolean hasAsClass(String instanceUri, String classUri);


  /**
   * Add an instantiation statement with rdf:type to the model.
   *
   * @param instanceUri  the uri of the instance
   * @param classUri  the uri of the class
   */
  public void addType(String instanceUri, String classUri);
  public void addType(Model model, String instanceUri, String classUri);


  /**
   * Remove any instantiation statement with rdf:type from the model.
   *
   * @param instanceUri  the uri of the instance
   * @param classUri  the uri of the class
   */
  public void removeType(String instanceUri, String classUri);
  public void removeType(Model model, String instanceUri, String classUri);


  /**
   * Look within the subclasses of the specified class (uri) for the use of the supplied key
   * in the rdf:label relations.
   *
   * @param classUri  the uri of the class
   * @param key  the uri of the instance
   *
   * @return  iterator of uri's
   */
  public Iterator<String> findSubClasses(String classUri, String key);


  /**
   * Check if the instance can be cast to the specified CoinsObject class.
   *
   * @param instanceUri  the uri of the instance
   * @param clazz  the Class to cast to
   *
   * @return  true if the cast can be done
   */
  public <T extends CoinsObject> boolean canAs(String instanceUri, Class<T> clazz);
  public <T extends CoinsObject> boolean canAs(Model model, String instanceUri, Class<T> clazz);


  /**
   * Instantiate a CoinsObject specified by the instance uri as a specific CoinsObject extension.
   *
   * @param instanceUri  the uri of the instance
   * @param clazz  the Class (CoinsObject extension) to instantiate
   * @return  a cast of the specified instance to the specified CoinsObject type
   */
  public <T extends CoinsObject> T as(String instanceUri, Class<T> clazz);
  public <T extends CoinsObject> T as(Model model, String instanceUri, Class<T> clazz);


  /**
   * Return the property uri (predicate) that can exist from instances of the specified class (uri)
   * to an instance of the specified property type class (uri).
   *
   * @param classUri  the uri of the subject class
   * @param propertyTypeClass  CoinsObject representation of a property class (object)
   *
   * @return  iterator with predicate uri's
   */
  public Iterator<String> listPropertyDefinitions(String classUri, Class<CoinsObject> propertyTypeClass);


  /**
   * Return the property uri (predicate) that can exist from instances of the specified class (uri)
   * to an instance of the specified property type class (uri).
   *
   * @param classUri  the uri of the subject class
   * @param propertyTypeClassUri  the uri of the property class (object)
   *
   * @return  iterator with predicate uri's
   */
  public Iterator<String> listPropertyDefinitions(String classUri, String propertyTypeClassUri);


  /**
   * Iterate over all the properties of an instance. All properties are currently returned as a
   * RuntimeCoinsObject. If no classUri is detected it has as class uri owl:Thing.
   *
   * @param instanceUri  the uri of the instance
   *
   * @return  iterator with RuntimeCoinsObject's
   */
  public Iterator<CoinsObject> listProperties(String instanceUri);
  public Iterator<CoinsObject> listProperties(Model model, String instanceUri);


  /**
   * Iterate over all the properties of an instance of a specific property type. The returned properties are
   * instances of the specified property type.
   *
   * @param instanceUri  the uri of the instance
   * @param propertyTypeClass  the class object (extension of CoinsObject) representing the property type
   *
   * @return  iterator with CoinsObjects (of the type specified)
   */
  public <T extends CoinsObject> Iterator<T> listProperties(String instanceUri, Class<T> propertyTypeClass);
  public <T extends CoinsObject> Iterator<T> listProperties(Model model, String instanceUri, Class<T> propertyTypeClass);


  /**
   * Iterate over all the properties of an instance of a specific property type. The returned properties are
   * instances of the RuntimeCoinsObject (there is currently no way to instantiate automatically to a CoinsObject
   * extension.
   *
   * @param instanceUri  the uri of the instance
   * @param propertyClassUri  uri representing the property type
   *
   * @return  iterator with CoinsObjects (of the type specified)
   */
  public Iterator<RuntimeCoinsObject> listProperties(String instanceUri, String propertyClassUri);
  public Iterator<RuntimeCoinsObject> listProperties(Model model, String instanceUri, String propertyClassUri);


  /**
   * Iterate over all the properties of an instance of a specific property type using a specified predicate.
   * The returned properties are instances of the specified property type.
   *
   * @param instanceUri  the uri of the instance
   * @param predicate  uri used as predicate pointing to the property object
   * @param propertyTypeClass  the class object (extension of CoinsObject) representing the property type
   *
   * @return  iterator with CoinsObjects (of the type specified)
   */
  public <T extends CoinsObject> Iterator<T> listProperties(String instanceUri, String predicate, Class<T> propertyTypeClass);
  public <T extends CoinsObject> Iterator<T> listProperties(Model model, String instanceUri, String predicate, Class<T> propertyTypeClass);


  /**
   * Iterate over all the properties of an instance of a specific property type using a specified predicate.
   * The returned properties are instances of the RuntimeCoinsObject (there is currently no way to instantiate
   * automatically to a CoinsObject extension.
   *
   * @param instanceUri  the uri of the instance
   * @param predicate  uri used as predicate pointing to the property object
   * @param propertyClassUri  uri representing the property type
   *
   * @return  iterator with CoinsObjects (of the type specified)
   */
  public Iterator<RuntimeCoinsObject> listProperties(String instanceUri, String predicate, String propertyClassUri);
  public Iterator<RuntimeCoinsObject> listProperties(Model model, String instanceUri, String predicate, String propertyClassUri);


  /**
   * Create a new property object and link the supplied instance to this property using the specified predicate.
   *
   * @param instanceUri  the uri of the instance
   * @param predicateUri  the uri of the predicate
   * @param propertyClass  a CoinsObject extension representing the property class
   *
   * @return  the constructed property object as RuntimeCoinsObject
   */
  public <T extends CoinsObject> T createProperty(String instanceUri, String predicateUri, Class<T> propertyClass);


  /**
   * Create a new property object and link the supplied instance to this property using the specified predicate.
   *
   * @param instanceUri  the uri of the instance
   * @param predicateUri  the uri of the predicate
   * @param propertyClassUri  the uri of the property class
   *
   * @return  the constructed property object as RuntimeCoinsObject
   */
  public RuntimeCoinsObject createProperty(String instanceUri, String predicateUri, String propertyClassUri);
  public RuntimeCoinsObject createProperty(Model model, String instanceUri, String predicateUri, String propertyClassUri);


  /**
   * Remove the specified property from the instance model and remove the link from the instance to the property object.
   *
   * @param instanceUri  the uri of the instance
   * @param property  CoinsObject representation of the property instance
   */
  public void removeProperty(String instanceUri, CoinsObject property);
  public void removeProperty(Model model, String instanceUri, CoinsObject property);


  /**
   * Get the value of a property of the specified type (String, Integer, etc.). A predicate needs to be supplied
   * that is used to relate the property to the value.
   *
   * @param instanceUri  the uri of the instance
   * @param predicate  uri used between property and value
   * @param clazz  the expected type
   *
   * @return  the value as String, Integer, etc.
   */
  public <T> T getLiteralValue(String instanceUri, String predicate, Class<T> clazz);
  public <T> T getLiteralValue(Model model, String instanceUri, String predicate, Class<T> clazz);


  /**
   * Get all the values of a property of the specified type (String, Integer, etc.). A predicate needs to be supplied
   * that is used to relate the property to the value.
   *
   * @param instanceUri  the uri of the instance
   * @param predicate  uri used between property and value
   * @param clazz  the expected type
   *
   * @return  iterator with the values as String, Integer, etc.
   */
  public <T> Iterator<T> getLiteralValues(String instanceUri, String predicate, Class<T> clazz);
  public <T> Iterator<T> getLiteralValues(Model model, String instanceUri, String predicate, Class<T> clazz);


  /**
   * If the property can only have one value, set the value of a property. A predicate needs to be supplied
   * that is used to relate the property to the value.
   *
   * First removes any predicate-relationship starting from the instanceUri.
   *
   * @param instanceUri  the uri of the instance
   * @param predicate  uri used between property and value
   * @param object  the new value
   *
   * @return  iterator with the values as String, Integer, etc.
   */
  public <T> void setLiteralValue(String instanceUri, String predicate, T object);
  public <T> void setLiteralValue(Model model, String instanceUri, String predicate, T object);


  /**
   * If the property can have more than one value, add a value to a property. A predicate needs to be supplied
   * that is used to relate the property to the value.
   *
   * @param instanceUri  the uri of the instance
   * @param predicate  uri used between instance and object
   * @param object  the new value
   *
   * @return  iterator with the values as String, Integer, etc.
   */
  public <T> void addLiteralValue(String instanceUri, String predicate, T object);
  public <T> void addLiteralValue(Model model, String instanceUri, String predicate, T object);


  /**
   * Remove the relation containing the value.
   *
   * @param instanceUri  the uri of the instance
   * @param predicate  uri used between instance and object
   * @param object  the new value
   *
   * @return  iterator with the values as String, Integer, etc.
   */
  public <T> void removeLiteralValue(String instanceUri, String predicate, T object);
  public <T> void removeLiteralValue(Model model, String instanceUri, String predicate, T object);


  /**
   * Get the object of a relation of the specified type. A predicate needs to be supplied
   * that is used to relate the instance to the object.
   *
   * @param instanceUri  the uri of the instance
   * @param predicate  uri used between instance and object
   * @param clazz  the expected type
   *
   * @return  the object of the relation from the instance as specified CoinsObject extension
   */
  public <T extends CoinsObject> T getObject(String instanceUri, String predicate, Class<T> clazz);
  public <T extends CoinsObject> T getObject(Model model, String instanceUri, String predicate, Class<T> clazz);


  /**
   * Get all the objects of relations of the specified type. A predicate needs to be supplied
   * that is used to relate the instance to the object.
   *
   * @param instanceUri  the uri of the instance
   * @param predicate  uri used between property and value
   * @param clazz  the expected type
   *
   * @return  iterator with the values as String, Integer, etc.
   */
  public <T extends CoinsObject> Iterator<T> getObjects(String instanceUri, String predicate, Class<T> clazz);
  public <T extends CoinsObject> Iterator<T> getObjects(Model model, String instanceUri, String predicate, Class<T> clazz);


  /**
   * If the instance can only have one relation to these type of objects, set a new object of the relation.
   * A predicate needs to be supplied that is used to point to the new object.
   *
   * First removes any predicate-relationship starting from the instanceUri.
   *
   * @param instanceUri  the uri of the instance
   * @param predicate  uri used between instance and object
   * @param object  CoinsObject representation of the object
   */
  public void setObject(String instanceUri, String predicate, CoinsObject object);
  public void setObject(Model model, String instanceUri, String predicate, CoinsObject object);


  /**
   * If the instance can only have one relation to these type of objects, set a new object of the relation.
   * A predicate needs to be supplied that is used to point to the new object.
   *
   * First removes any predicate-relationship starting from the instanceUri.
   *
   * @param instanceUri  the uri of the instance
   * @param predicate  uri used between instance and object
   * @param objectUri  uri of the object
   */
  public void setObject(String instanceUri, String predicate, String objectUri);
  public void setObject(Model model, String instanceUri, String predicate, String objectUri);


  /**
   * If the instance can have more than one relation to these type of objects, add a relation. A predicate needs to be supplied
   * that is used to point to the new object.
   *
   * @param instanceUri  the uri of the instance
   * @param predicate  uri used between instance and object
   * @param object  CoinsObject representation of the object
   */
  public void addObject(String instanceUri, String predicate, CoinsObject object);
  public void addObject(Model model, String instanceUri, String predicate, CoinsObject object);


  /**
   * If the instance can have more than one relation to these type of objects, add a relation. A predicate needs to be supplied
   * that is used to point to the new object.
   *
   * @param instanceUri  the uri of the instance
   * @param predicate  uri used between instance and object
   * @param objectUri  uri of the object
   */
  public void addObject(String instanceUri, String predicate, String objectUri);
  public void addObject(Model model, String instanceUri, String predicate, String objectUri);


  /**
   * Remove the relationship between the instance and the object.
   *
   * @param instanceUri  the uri of the instance
   * @param predicate  uri used between instance and object
   * @param object  CoinsObject representation of the object
   */
  public void removeObject(String instanceUri, String predicate, CoinsObject object);
  public void removeObject(Model model, String instanceUri, String predicate, CoinsObject object);


  /**
   * Remove the relationship between the instance and the object.
   *
   * @param instanceUri  the uri of the instance
   * @param predicate  uri used between instance and object
   * @param objectUri  uri of the object
   */
  public void removeObject(String instanceUri, String predicate, String objectUri);
  public void removeObject(Model model, String instanceUri, String predicate, String objectUri);


  /**
   * A complete wipe. Remove all the properties and links to those properties starting from the instances
   * and the instance itself.
   *
   * @param instanceUri  the uri of the instance
   */
  public void removeIndividualAndProperties(String instanceUri);
  public void removeIndividualAndProperties(Model model, String instanceUri);

  /**
   * Return the archive of ontology files available to load to the container.
   *
   * @return  a map containing namespaces and a file containing an ontology listed under this namespace
   */
  public HashMap<Namespace, File> getAvailableLibraryFiles();



  /**
   * Checks whether an owl:Ontology instance is present in the instance Model.
   *
   * @return  true if the owl:Ontology is present
   */
  public boolean hasOntologyHeader();
  public boolean hasOntologyHeader(Model model);


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
  public void addStatement(Model model, String subject, String predicate, String object);


  /**
   * Low level access to add statements to the instance Model.
   *
   * @param subject  uri
   * @param predicate  uri
   * @param object  RDFNode, for example containing a literal
   */
  public void addStatement(String subject, String predicate, RDFNode object);
  public void addStatement(Model model, String subject, String predicate, RDFNode object);


  /**
   * Low level access to remove statements from the instance Model.
   *
   * @param subject  uri
   * @param predicate  uri
   * @param object  uri (!) to add literals use the method with RDFNode
   */
  public void removeStatement(String subject, String predicate, String object);
  public void removeStatement(Model model, String subject, String predicate, String object);


  /**
   * Low level access to remove statements from the instance Model.
   *
   * @param subject  uri
   * @param predicate  uri
   * @param object  RDFNode, for example containing a literal
   */
  public void removeStatement(String subject, String predicate, RDFNode object);
  public void removeStatement(Model model, String subject, String predicate, RDFNode object);


  /**
   * Low level access to remove statements from the instance Model. Removes all triples that start with
   * the specified subject and predicate.
   *
   * @param subject  uri
   * @param predicate  uri
   */
  public void removeAllStatements(String subject, String predicate);
  public void removeAllStatements(Model model, String subject, String predicate);


  /**
   * Export the instance model as a string.
   *
   * @return  string consisting rdf/xml format export of the instance model
   */
  public String exportAsString();
  public String exportAsString(Model model);

  /**
   * Export the instance model as a string.
   *
   * @param format  jena export format (e.g. RDFFormat.TTL)
   * @return  string consisting export of the instance model in specified format
   */
  public String exportAsString(RDFFormat format);
  public String exportAsString(Model model, RDFFormat format);


  /**
   * Transform this Expert CoinsModel model to a simpler CoinsModel. It is the same object,
   * but it is 'cast down' to the CoinsModel interface.
   *
   * @return  the same object as coins model
   */
  public CoinsModel asCoinsModel();
}
