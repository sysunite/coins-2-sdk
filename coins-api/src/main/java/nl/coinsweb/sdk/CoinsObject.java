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

import com.hp.hpl.jena.rdf.model.Model;

import java.util.Iterator;
import java.util.Set;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public interface CoinsObject {

  /**
   * Static getters
   */

  /**
   * Give the uri of the rdf instance this CoinsObject (as Java instance) represents.
   * @return  an uri
   */
  public String getUri();

  /**
   * Give the uri of the rdf class this CoinsObject (as Java class) represents.
   * @return  an uri
   */
  public String getClassUri();

  /**
   * Give the label of the rdf class this CoinsObject (as Java class) represents.
   * @return  an uri
   */
  public String getClassLabel();

  /**
   * Give the comment of the rdf class this CoinsObject (as Java class) represents.
   * @return  an uri
   */
  public String getClassComment();







  /**
   * Membership of catalogue classes
   */



  /**
   * Add a type definition to the coins2:CoinsContainerObject object.
   *
   */
  public void addCoinsContainerObjectType();

  /**
   * List all the classes the rdf instance this CoinsObject (as Java instance) represents is an instance of.
   *
   * @return  a set of class uris
   */
  public Set<String> listClassUris();


  /**
   * Return if the instance has the specified class as class by rdf:type definition.
   *
   * @param classUri  the uri of the class
   *
   * @return  true if a rdf:type definition exists
   */
  public boolean hasAsClass(String classUri);


  /**
   * Add an instantiation statement with rdf:type to the model.
   *
   * @param <T>    type of class the argument can be an instance of
   * @param clazz  CoinsObject extension representing the class
   */
  public <T extends CoinsObject> void addType(Class<T> clazz);
  public <T extends CoinsObject> void addType(Model model, Class<T> clazz);


  /**
   * Add an instantiation statement with rdf:type to the model.
   *
   * @param classUri  the uri of the class
   */
  public void addType(String classUri);
  public void addType(Model model, String classUri);


  /**
   * Remove any instantiation statement with rdf:type from the model.
   *
   * @param <T>    type of class the argument can be an instance of
   * @param clazz  CoinsObject extension representing the class
   */
  public <T extends CoinsObject> void removeType(Class<T> clazz);
  public <T extends CoinsObject> void removeType(Model model, Class<T> clazz);


  /**
   * Remove any instantiation statement with rdf:type from the model.
   *
   * @param classUri  the uri of the class
   */
  public void removeType(String classUri);
  public void removeType(Model model, String classUri);

  /**
   * Find among the children of the rdf class this CoinsObject (as Java class) represents a class
   * using the specified search key.
   *
   * @param key  used to search for a sub class
   * @return  an iterator with classUris
   */
  public Iterator<String> findSubClasses(String key);









  /**
   * Casting
   */

  /**
   * Check if the instance can be cast to the specified CoinsObject class.
   *
   * @param <T>    type of class the argument can be an instance of
   * @param clazz  the Class to cast to
   *
   * @return  true if the cast can be done
   */
  public <T extends CoinsObject> boolean canAs(Class<T> clazz);
  public <T extends CoinsObject> boolean canAs(Model model, Class<T> clazz);


  /**
   * Instantiate a CoinsObject specified by the instance uri as a specific CoinsObject extension.
   *
   * @param <T>    type of class the argument can be an instance of
   * @param clazz  the Class (CoinsObject extension) to instantiate
   * @return  a cast of the specified instance to the specified CoinsObject type
   */
  public <T extends CoinsObject> T as(Class<T> clazz);
  public <T extends CoinsObject> T as(Model model, Class<T> clazz);







  /**
   * Listing and modifying properties
   */

  /**
   * Return the property uri (predicate) that can exist from instances of the class
   * to an instance of the specified property type class (uri).
   *
   * @param propertyTypeClass  CoinsObject representation of a property class (object)
   *
   * @return  iterator with predicate uri's
   */
  public Iterator<String> listPropertyDefinitions(Class<CoinsObject> propertyTypeClass);


  /**
   * Return the property uri (predicate) that can exist from instances of the class
   * to an instance of the specified property type class (uri).
   *
   * @param propertyTypeClassUri  the uri of the property class (object)
   *
   * @return  iterator with predicate uri's
   */
  public Iterator<String> listPropertyDefinitions(String propertyTypeClassUri);


  /**
   * Iterate over all the properties of the instance. All properties are currently returned as a
   * RuntimeCoinsObject that has as class uri the owl:Thing.
   *
   * @return  iterator with RuntimeCoinsObject's
   */
  public Iterator<CoinsObject> listProperties();
  public Iterator<CoinsObject> listProperties(Model model);

  /**
   * Iterate over all the properties of the instance of a specific property type. The returned properties are
   * instances of the specified property type.
   *
   * @param <T>    type of class the argument can be an instance of
   * @param propertyTypeClass  the class object (extension of CoinsObject) representing the property type
   *
   * @return  iterator with CoinsObjects (of the type specified)
   */
  public <T extends CoinsObject> Iterator<T> listProperties(Class<T> propertyTypeClass);
  public <T extends CoinsObject> Iterator<T> listProperties(Model model, Class<T> propertyTypeClass);


  /**
   * Iterate over all the properties of the instance of a specific property type. The returned properties are
   * instances of the RuntimeCoinsObject (there is currently no way to instantiate automatically to a CoinsObject
   * extension.
   *
   * @param propertyClassUri  uri representing the property type
   *
   * @return  iterator with CoinsObjects (of the type specified)
   */
  public Iterator<RuntimeCoinsObject> listProperties(String propertyClassUri);
  public Iterator<RuntimeCoinsObject> listProperties(Model model, String propertyClassUri);


  /**
   * Iterate over all the properties of the instance of a specific property type using a specified predicate.
   * The returned properties are instances of the specified property type.
   *
   * @param <T>    type of class the argument can be an instance of
   * @param predicate  uri used as predicate pointing to the property object
   * @param propertyTypeClass  the class object (extension of CoinsObject) representing the property type
   *
   * @return  iterator with CoinsObjects (of the type specified)
   */
  public <T extends CoinsObject> Iterator<T> listProperties(String predicate, Class<T> propertyTypeClass);
  public <T extends CoinsObject> Iterator<T> listProperties(Model model, String predicate, Class<T> propertyTypeClass);


  /**
   * Iterate over all the properties of the instance of a specific property type using a specified predicate.
   * The returned properties are instances of the RuntimeCoinsObject (there is currently no way to instantiate
   * automatically to a CoinsObject extension.
   *
   * @param predicate  uri used as predicate pointing to the property object
   * @param propertyClassUri  uri representing the property type
   *
   * @return  iterator with CoinsObjects (of the type specified)
   */
  public Iterator<RuntimeCoinsObject> listProperties(String predicate, String propertyClassUri);
  public Iterator<RuntimeCoinsObject> listProperties(Model model, String predicate, String propertyClassUri);


  /**
   * Create a new property object and link the supplied instance to this property using the specified predicate.
   *
   * @param <T>    type of class the argument can be an instance of
   * @param predicateUri  the uri of the predicate
   * @param propertyClass  a CoinsObject extension representing the property class
   *
   * @return  the constructed property object as RuntimeCoinsObject
   */
  public <T extends AbstractCoinsObject> T createProperty(String predicateUri, Class<T> propertyClass);
  public <T extends AbstractCoinsObject> T createProperty(Model model, String predicateUri, Class<T> propertyClass);


  /**
   * Create a new property object and link the supplied instance to this property using the specified predicate.
   *
   * @param predicateUri  the uri of the predicate
   * @param propertyClassUri  the uri of the property class
   *
   * @return  the constructed property object as RuntimeCoinsObject
   */
  public RuntimeCoinsObject createProperty(String predicateUri, String propertyClassUri);
  public RuntimeCoinsObject createProperty(Model model, String predicateUri, String propertyClassUri);


  /**
   * Remove the specified property from the instance model and remove the link from the instance to the property object.
   *
   * @param property  CoinsObject representation of the property instance
   */
  public void removeProperty(CoinsObject property);
  public void removeProperty(Model model, CoinsObject property);

  /**
   * Get the value of a property of the specified type (String, Integer, etc.). A predicate needs to be supplied
   * that is used to relate the property to the value.
   *
   * @param <T>    type of class the argument can be an instance of
   * @param predicate  uri used between property and value
   * @param clazz  the expected type
   *
   * @return  the value as String, Integer, etc.
   */
  public <T> T getLiteralValue(String predicate, Class<T> clazz);
  public <T> T getLiteralValue(Model model, String predicate, Class<T> clazz);


  /**
   * Get all the values of a property of the specified type (String, Integer, etc.). A predicate needs to be supplied
   * that is used to relate the property to the value.
   *
   * @param <T>    type of class the argument can be an instance of
   * @param predicate  uri used between property and value
   * @param clazz  the expected type
   *
   * @return  iterator with the values as String, Integer, etc.
   */
  public <T> Iterator<T> getLiteralValues(String predicate, Class<T> clazz);
  public <T> Iterator<T> getLiteralValues(Model model, String predicate, Class<T> clazz);


  /**
   * If the property can only have one value, set the value of a property. A predicate needs to be supplied
   * that is used to relate the property to the value.
   *
   * @param <T>    type of class the argument can be an instance of
   * @param predicate  uri used between property and value
   * @param object  the new value
   */
  public <T> void setLiteralValue(String predicate, T object);
  public <T> void setLiteralValue(Model model, String predicate, T object);


  /**
   * If the property can have more than one value, add a value to a property. A predicate needs to be supplied
   * that is used to relate the property to the value.
   *
   * @param <T>    type of class the argument can be an instance of
   * @param predicate  uri used between instance and object
   * @param object  the new value
   */
  public <T> void addLiteralValue(String predicate, T object);
  public <T> void addLiteralValue(Model model, String predicate, T object);


  /**
   * Get the object of a relation of the specified type. A predicate needs to be supplied
   * that is used to relate the instance to the object.
   *
   * @param <T>    type of class the argument can be an instance of
   * @param predicate  uri used between instance and object
   * @param clazz  the expected type
   *
   * @return  the object of the relation from the instance as specified CoinsObject extension
   */
  public <T extends CoinsObject> T getObject(String predicate, Class<T> clazz);
  public <T extends CoinsObject> T getObject(Model model, String predicate, Class<T> clazz);


  /**
   * Get all the objects of relations of the specified type. A predicate needs to be supplied
   * that is used to relate the instance to the object.
   *
   * @param <T>    type of class the argument can be an instance of
   * @param predicate  uri used between property and value
   * @param clazz  the expected type
   *
   * @return  iterator with the values as String, Integer, etc.
   */
  public <T extends CoinsObject> Iterator<T> getObjects(String predicate, Class<T> clazz);
  public <T extends CoinsObject> Iterator<T> getObjects(Model model, String predicate, Class<T> clazz);


  /**
   * If the instance can only have one relation to these type of objects, set a new object of the relation.
   * A predicate needs to be supplied that is used to point to the new object.
   *
   * @param predicate  uri used between instance and object
   * @param object  CoinsObject representation of the object
   */
  public void setObject(String predicate, AbstractCoinsObject object);
  public void setObject(Model model, String predicate, AbstractCoinsObject object);


  /**
   * If the instance can have more than one relation to these type of objects, add a relation. A predicate needs to be supplied
   * that is used to point to the new object.
   *
   * @param predicate  uri used between instance and object
   * @param object  CoinsObject representation of the object
   */
  public void addObject(String predicate, AbstractCoinsObject object);
  public void addObject(Model model, String predicate, AbstractCoinsObject object);


  /**
   * A complete wipe. Remove all the properties and links to those properties starting from the instances
   * and the instance itself.
   *
   */
  public void removeIndividualAndProperties();
  public void removeIndividualAndProperties(Model model);


  /**
   * Compare two coins objects, if they have the same uri, the are the same.
   *
   * @param that  the CoinsContainer to compare this to
   * @return  true if both uri's are the same
   */
  @Override
  public boolean equals(Object that);


  /**
   * Produce a hash that is unique for this instance.
   *
   * @return  int that is unique for the uri
   */
  @Override
  public int hashCode();


  /**
   * Return the associated CoinsModel object.
   *
   * @return  the CoinsModel related to this container
   */
  public CoinsModel getCoinsModel();

}