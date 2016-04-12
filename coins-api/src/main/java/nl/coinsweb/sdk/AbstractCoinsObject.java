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

import com.hp.hpl.jena.ontology.OntModel;
import nl.coinsweb.sdk.exceptions.CoinsObjectCastNotAllowedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

/**
 * Represents an instance of an owl:Class.
 * All generated classes extend this base class. It has no dependency to Jena.
 *
 * @author Bastiaan Bijl, Sysunite 2016
 */
public abstract class AbstractCoinsObject implements CoinsObject {

  protected static final Logger log = LoggerFactory.getLogger(AbstractCoinsObject.class);

  protected ExpertCoinsModel model;




  // Instance fields

  protected String uri;


  /**
   * For RuntimeCoinsObject to override the constructor
   */
  protected AbstractCoinsObject() {}

  /**
   * Constructor for new Individual.
   */
  public AbstractCoinsObject(ExpertCoinsModel model) {
    this.model = model;


    // Verify if the rdf files that where the origin for this class have been presented to the current CoinsModel
    registerSourceFiles();



    // Create fields for this new instance
    this.uri = model.generateUri();
    model.addType(getUri(), getClassUri());
    model.addCreator(getUri(), model.getActiveParty());
    model.addCreatedNow(getUri());
  }

  /**
   * Constructor for Individual with specified uri. Check if the uri already exists and if not create new.
   */
  public AbstractCoinsObject(ExpertCoinsModel model, String uri) {
    this(model, uri, false);
  }
  public AbstractCoinsObject(ExpertCoinsModel model, String uri, boolean dontCheck) {
    this.model = model;

    // Verify if the rdf files that where the origin for this class have been presented to the current CoinsModel
    registerSourceFiles();


    this.uri = uri;
    log.info("set uri to "+this.uri);

    // Create fields for this new instance
    if(((OntModel)model.getUnionJenaOntModel()).getIndividual(uri)==null) {
      log.info("Uri "+uri+" not found, creating new individual with this uri.");

      // Save this new instance to model
      model.addType(getUri(), getClassUri());
      model.addCreator(getUri(), model.getActiveParty());
      model.addCreatedNow(getUri());
    }

    // Check if the object with the specified uri may be cast to this type
    if(!dontCheck) {
      boolean foundClassDef = false;
      Iterator<String> classes = model.listClassUris(uri).iterator();
      while (classes.hasNext()) {
        String candidateClassUri = classes.next();
        log.trace("try if " + candidateClassUri + " as candidate equals where we want to cast to " + getClassUri());
        if (candidateClassUri.equals(getClassUri())) {
          foundClassDef = true;
          break;
        }
      }
      if (!foundClassDef) {
        throw new CoinsObjectCastNotAllowedException("Can not cast to " + this.getClass().getCanonicalName() + " because not rdf class for this type was found.");
      }
    }

  }


  @Override
  public String getUri() {
    return this.uri;
  }

  public String getClassUri() {
    try {
      return (String) getClass().getField("classUri").get(String.class);
    } catch (IllegalAccessException e) {
      log.error(e.getMessage(), e);
    } catch (NoSuchFieldException e) {
      log.error(e.getMessage(), e);
    }
    return null;
  }
  @Override
  public String getClassLabel() {
    try {
      return (String) getClass().getField("label").get(String.class);
    } catch (IllegalAccessException e) {
      log.error(e.getMessage(), e);
    } catch (NoSuchFieldException e) {
      log.error(e.getMessage(), e);
    }
    return null;
  }
  @Override
  public String getClassComment() {
    try {
      return (String) getClass().getField("comment").get(String.class);
    } catch (IllegalAccessException e) {
      log.error(e.getMessage(), e);
    } catch (NoSuchFieldException e) {
      log.error(e.getMessage(), e);
    }
    return null;
  }


  /**
   * Delegate to BindingCoinsModel
   */


  @Override
  public Set<String> listClassUris() {
    return model.listClassUris(getUri());
  }
  @Override
  public boolean hasAsClass(String classUri) {
    return model.hasAsClass(getUri(), classUri);
  }
  @Override
  public <T extends CoinsObject> void addType(Class<T> clazz) {
    try {
      String classUri = (String) clazz.getField("classUri").get(String.class);
      addType(classUri);
    } catch (IllegalAccessException e) {
      log.error(e.getMessage(), e);
    } catch (NoSuchFieldException e) {
      log.error(e.getMessage(), e);
    }
  }
  @Override
  public void addType(String classUri) {
    model.addType(getUri(), classUri);
  }
  @Override
  public <T extends CoinsObject> void removeType(Class<T> clazz) {
    try {
      String classUri = (String) clazz.getField("classUri").get(String.class);
      removeType(classUri);
    } catch (IllegalAccessException e) {
      log.error(e.getMessage(), e);
    } catch (NoSuchFieldException e) {
      log.error(e.getMessage(), e);
    }
  }
  @Override
  public void removeType(String classUri) {
    model.removeType(getUri(), classUri);
  }
  @Override
  public void addCoinsContainerObjectType() {
    model.addCoinsContainerObjectType(getUri());
  }
  @Override
  public Iterator<String> findSubClasses(String key) {
    return model.findSubClasses(getClassUri(), key);
  }
  @Override
  public <T extends CoinsObject> boolean canAs(Class<T> clazz) {
    return model.canAs(getUri(), clazz);
  }
  @Override
  public <T extends CoinsObject> T as(Class<T> clazz) {
    return model.as(getUri(), clazz);
  }



  @Override
  public Iterator<String> listPropertyDefinitions(Class<CoinsObject> propertyTypeClass) {
    return model.listPropertyDefinitions(getClassUri(), propertyTypeClass);
  }
  @Override
  public Iterator<String> listPropertyDefinitions(String propertyTypeClassUri) {
    return model.listPropertyDefinitions(getClassUri(), propertyTypeClassUri);
  }

  @Override
  public Iterator<CoinsObject> listProperties() {
    return model.listProperties(getUri());
  }

  @Override
  public <T extends CoinsObject> Iterator<T> listProperties(Class<T> propertyTypeClass) {
    return model.listProperties(getUri(), propertyTypeClass);
  }
  @Override
  public Iterator<RuntimeCoinsObject> listProperties(String propertyTypeClassUri) {
    return model.listProperties(getUri(), propertyTypeClassUri);
  }
  @Override
  public <T extends CoinsObject> Iterator<T> listProperties(String predicate, Class<T> propertyTypeClass) {
    return model.listProperties(getUri(), predicate, propertyTypeClass);
  }
  @Override
  public Iterator<RuntimeCoinsObject> listProperties(String predicate, String propertyTypeClassUri) {
    return model.listProperties(getUri(), predicate, propertyTypeClassUri);
  }

  @Override
  public RuntimeCoinsObject createProperty(String predicateUri, String propertyTypeClassUri) {
    return model.createProperty(getUri(), predicateUri, propertyTypeClassUri);
  }
  @Override
  public <T extends AbstractCoinsObject> T createProperty(String predicateUri, Class<T> propertyTypeClass) {
    return model.createProperty(getUri(), predicateUri, propertyTypeClass);
  }
  @Override
  public void removeProperty(CoinsObject property) {
    model.removeProperty(getUri(), property);
  }

  @Override
  public <T> T getLiteralValue(String predicate, Class<T> clazz) {
    return model.getLiteralValue(getUri(), predicate, clazz);
  }
  @Override
  public <T> Iterator<T> getLiteralValues(String predicate, Class<T> clazz) {
    return model.getLiteralValues(getUri(), predicate, clazz);
  }
  @Override
  public <T> void setLiteralValue(String predicate, T object) {
    model.setLiteralValue(getUri(), predicate, object);
  }
  @Override
  public <T> void addLiteralValue(String predicate, T object) {
    model.addLiteralValue(getUri(), predicate, object);
  }

  @Override
  public <T extends CoinsObject> T getObject(String predicate, Class<T> clazz) {
    return model.getObject(getUri(), predicate, clazz);
  }
  @Override
  public <T extends CoinsObject> Iterator<T> getObjects(String predicate, Class<T> clazz) {
    return model.getObjects(getUri(), predicate, clazz);
  }
  @Override
  public void setObject(String predicate, AbstractCoinsObject object) {
    model.setObject(getUri(), predicate, object);
  }
  @Override
  public void addObject(String predicate, AbstractCoinsObject object) {
    model.addObject(getUri(), predicate, object);
  }

  @Override
  public void removeIndividualAndProperties() {
    model.removeIndividualAndProperties(getUri());
  }


















  public void registerSourceFiles() {



    try {
      for(String fileName : Arrays.asList((String[]) getClass().getField("sourceFiles").get(Array.class))) {

        log.trace("register "+fileName+" from jar");

        InputStream fileStream = getClass().getResourceAsStream("/"+fileName);
        Namespace ns = FileManager.copyAndRegisterLibrary(fileStream, fileName, model.getAvailableLibraryFiles());
        model.addImport(null, ns.toString(), true, true, false);

      }


    } catch (IllegalAccessException e) {
      log.error(e.getMessage(), e);
    } catch (NoSuchFieldException e) {
      log.error(e.getMessage(), e);
    }
  }




























  @Override
  public ExpertCoinsModel getCoinsModel() {
    return model;
  }


}