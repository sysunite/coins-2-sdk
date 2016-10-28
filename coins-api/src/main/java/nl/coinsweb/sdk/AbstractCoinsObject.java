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
import com.hp.hpl.jena.rdf.model.Model;
import nl.coinsweb.sdk.exceptions.CoinsObjectCastNotAllowedException;
import nl.coinsweb.sdk.injectors.Injector;
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

  public ExpertCoinsModel coinsModel;
  public Model model;




  // Instance fields

  protected String uri;


  /**
   * For RuntimeCoinsObject to override the constructor
   */
  protected AbstractCoinsObject() {}

  /**
   * Constructor for new Individual.
   */
  public AbstractCoinsObject(ExpertCoinsModel coinsModel) {
    this(coinsModel, (Model)coinsModel.getCoinsGraphSet().getInstanceModel());
  }
  public AbstractCoinsObject(ExpertCoinsModel coinsModel, Model model) {
    this.coinsModel = coinsModel;
    this.model = model;


    // Verify if the rdf files that where the origin for this class have been presented to the current CoinsModel
    registerSourceFiles();



    // Create fields for this new instance
    this.uri = coinsModel.generateUri();
    coinsModel.addType(getUri(), getClassUri());
    coinsModel.addCreator(getUri(), coinsModel.getActiveParty());
    coinsModel.addCreatedNow(getUri());
  }

  /**
   * Constructor for Individual with specified uri. Check if the uri already exists and if not create new.
   */
  public AbstractCoinsObject(ExpertCoinsModel model, String uri) {
    this(model, uri, false);
  }
  public AbstractCoinsObject(ExpertCoinsModel coinsModel, Model model, String uri) {
    this(coinsModel, model, uri, false);
  }
  public AbstractCoinsObject(ExpertCoinsModel coinsModel, String uri, boolean dontCheck) {
    this(coinsModel, (Model)coinsModel.getCoinsGraphSet().getInstanceModel(), uri, dontCheck);
  }
  public AbstractCoinsObject(ExpertCoinsModel coinsModel, Model model, String uri, boolean dontCheck) {
    this.coinsModel = coinsModel;
    this.model = model;

    // Verify if the rdf files that where the origin for this class have been presented to the current CoinsModel
    registerSourceFiles();


    this.uri = uri;
    log.info("Set uri to "+this.uri);

    // Create fields for this new instance
    if(((OntModel)coinsModel.getCoinsGraphSet().getUnionJenaOntModel()).getIndividual(uri) == null) {
      log.info("Uri "+uri+" not found, creating new individual with this uri.");

      // Save this new instance to model
      coinsModel.addType(model, getUri(), getClassUri());
      coinsModel.addCreator(model, getUri(), coinsModel.getActiveParty());
      coinsModel.addCreatedNow(model, getUri());
    }

    // Creating an instance based on an existing uri can be considered a read
    for(Injector injector : coinsModel.getInjectors()) {
      injector.proposeRead(coinsModel, uri);
    }

    // Check if the object with the specified uri may be cast to this type
    if(!dontCheck) {
      boolean foundClassDef = false;
      Iterator<String> classes = coinsModel.listClassUris(uri).iterator();
      while (classes.hasNext()) {
        String candidateClassUri = classes.next();
        log.trace("Try if " + candidateClassUri + " as candidate equals where we want to cast to " + getClassUri());
        if (candidateClassUri.equals(getClassUri())) {
          foundClassDef = true;
          break;
        }
      }
      if (!foundClassDef) {
        throw new CoinsObjectCastNotAllowedException("Can not cast to " + this.getClass().getCanonicalName() + " because no rdf class for this type was found.");
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
    return coinsModel.listClassUris(getUri());
  }
  @Override
  public boolean hasAsClass(String classUri) {
    return coinsModel.hasAsClass(getUri(), classUri);
  }
  @Override
  public <T extends CoinsObject> void addType(Class<T> clazz) {
    addType((Model)coinsModel.getCoinsGraphSet().getInstanceModel(), clazz);
  }
  @Override
  public <T extends CoinsObject> void addType(Model model, Class<T> clazz) {
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
    coinsModel.addType(getUri(), classUri);
  }
  @Override
  public void addType(Model model, String classUri) {
    coinsModel.addType(model, getUri(), classUri);
  }
  @Override
  public <T extends CoinsObject> void removeType(Class<T> clazz) {
    removeType((Model)coinsModel.getCoinsGraphSet().getInstanceModel(), clazz);
  }
  @Override
  public <T extends CoinsObject> void removeType(Model model, Class<T> clazz) {
    try {
      String classUri = (String) clazz.getField("classUri").get(String.class);
      removeType(model, classUri);
    } catch (IllegalAccessException e) {
      log.error(e.getMessage(), e);
    } catch (NoSuchFieldException e) {
      log.error(e.getMessage(), e);
    }
  }
  @Override
  public void removeType(String classUri) {
    coinsModel.removeType(getUri(), classUri);
  }
  @Override
  public void removeType(Model model, String classUri) {
    coinsModel.removeType(model, getUri(), classUri);
  }
  @Override
  public void addCoinsContainerObjectType() {
    coinsModel.addCoinsContainerObjectType(getUri());
  }
  @Override
  public Iterator<String> findSubClasses(String key) {
    return coinsModel.findSubClasses(getClassUri(), key);
  }
  @Override
  public <T extends CoinsObject> boolean canAs(Class<T> clazz) {
    return coinsModel.canAs(getUri(), clazz);
  }
  @Override
  public <T extends CoinsObject> boolean canAs(Model model, Class<T> clazz) {
    return coinsModel.canAs(model, getUri(), clazz);
  }
  @Override
  public <T extends CoinsObject> T as(Class<T> clazz) {
    return coinsModel.as(getUri(), clazz);
  }
  @Override
  public <T extends CoinsObject> T as(Model model, Class<T> clazz) {
    return coinsModel.as(model, getUri(), clazz);
  }



  @Override
  public Iterator<String> listPropertyDefinitions(Class<CoinsObject> propertyTypeClass) {
    return coinsModel.listPropertyDefinitions(getClassUri(), propertyTypeClass);
  }
  @Override
  public Iterator<String> listPropertyDefinitions(String propertyTypeClassUri) {
    return coinsModel.listPropertyDefinitions(getClassUri(), propertyTypeClassUri);
  }

  @Override
  public Iterator<CoinsObject> listProperties() {
    return coinsModel.listProperties(getUri());
  }
  @Override
  public Iterator<CoinsObject> listProperties(Model model) {
    return coinsModel.listProperties(model, getUri());
  }

  @Override
  public <T extends CoinsObject> Iterator<T> listProperties(Class<T> propertyTypeClass) {
    return listProperties(getUri(), propertyTypeClass);
  }
  @Override
  public <T extends CoinsObject> Iterator<T> listProperties(Model model, Class<T> propertyTypeClass) {
    return coinsModel.listProperties(model, getUri(), propertyTypeClass);
  }
  @Override
  public Iterator<RuntimeCoinsObject> listProperties(String propertyTypeClassUri) {
    return listProperties(getUri(), propertyTypeClassUri);
  }
  @Override
  public Iterator<RuntimeCoinsObject> listProperties(Model model, String propertyTypeClassUri) {
    return coinsModel.listProperties(model, getUri(), propertyTypeClassUri);
  }
  @Override
  public <T extends CoinsObject> Iterator<T> listProperties(String predicate, Class<T> propertyTypeClass) {
    return coinsModel.listProperties(getUri(), predicate, propertyTypeClass);
  }
  @Override
  public <T extends CoinsObject> Iterator<T> listProperties(Model model, String predicate, Class<T> propertyTypeClass) {
    return coinsModel.listProperties(model, getUri(), predicate, propertyTypeClass);
  }
  @Override
  public Iterator<RuntimeCoinsObject> listProperties(String predicate, String propertyTypeClassUri) {
    return coinsModel.listProperties(getUri(), predicate, propertyTypeClassUri);
  }
  @Override
  public Iterator<RuntimeCoinsObject> listProperties(Model model, String predicate, String propertyTypeClassUri) {
    return coinsModel.listProperties(model, getUri(), predicate, propertyTypeClassUri);
  }

  @Override
  public RuntimeCoinsObject createProperty(String predicateUri, String propertyTypeClassUri) {
    return coinsModel.createProperty(getUri(), predicateUri, propertyTypeClassUri);
  }
  @Override
  public RuntimeCoinsObject createProperty(Model model, String predicateUri, String propertyTypeClassUri) {
    return coinsModel.createProperty(model, getUri(), predicateUri, propertyTypeClassUri);
  }
  @Override
  public <T extends AbstractCoinsObject> T createProperty(String predicateUri, Class<T> propertyTypeClass) {
    return coinsModel.createProperty(getUri(), predicateUri, propertyTypeClass);
  }
  @Override
  public <T extends AbstractCoinsObject> T createProperty(Model model, String predicateUri, Class<T> propertyTypeClass) {
    return coinsModel.createProperty(model, getUri(), predicateUri, propertyTypeClass);
  }
  @Override
  public void removeProperty(CoinsObject property) {
    coinsModel.removeProperty(getUri(), property);
  }
  @Override
  public void removeProperty(Model model, CoinsObject property) {
    coinsModel.removeProperty(model, getUri(), property);
  }

  @Override
  public <T> T getLiteralValue(String predicate, Class<T> clazz) {
    return coinsModel.getLiteralValue(getUri(), predicate, clazz);
  }
  @Override
  public <T> T getLiteralValue(Model model, String predicate, Class<T> clazz) {
    return coinsModel.getLiteralValue(model, getUri(), predicate, clazz);
  }
  @Override
  public <T> Iterator<T> getLiteralValues(String predicate, Class<T> clazz) {
    return coinsModel.getLiteralValues(getUri(), predicate, clazz);
  }
  @Override
  public <T> Iterator<T> getLiteralValues(Model model, String predicate, Class<T> clazz) {
    return coinsModel.getLiteralValues(model, getUri(), predicate, clazz);
  }
  @Override
  public <T> void setLiteralValue(String predicate, T object) {
    coinsModel.setLiteralValue(getUri(), predicate, object);
  }
  @Override
  public <T> void setLiteralValue(Model model, String predicate, T object) {
    coinsModel.setLiteralValue(model, getUri(), predicate, object);
  }
  @Override
  public <T> void addLiteralValue(String predicate, T object) {
    coinsModel.addLiteralValue(getUri(), predicate, object);
  }
  @Override
  public <T> void addLiteralValue(Model model, String predicate, T object) {
    coinsModel.addLiteralValue(model, getUri(), predicate, object);
  }

  @Override
  public <T extends CoinsObject> T getObject(String predicate, Class<T> clazz) {
    return coinsModel.getObject(getUri(), predicate, clazz);
  }
  @Override
  public <T extends CoinsObject> T getObject(Model model, String predicate, Class<T> clazz) {
    return coinsModel.getObject(model, getUri(), predicate, clazz);
  }
  @Override
  public <T extends CoinsObject> Iterator<T> getObjects(String predicate, Class<T> clazz) {
    return coinsModel.getObjects(getUri(), predicate, clazz);
  }
  @Override
  public <T extends CoinsObject> Iterator<T> getObjects(Model model, String predicate, Class<T> clazz) {
    return coinsModel.getObjects(model, getUri(), predicate, clazz);
  }
  @Override
  public void setObject(String predicate, AbstractCoinsObject object) {
    coinsModel.setObject(getUri(), predicate, object);
  }
  @Override
  public void setObject(Model model, String predicate, AbstractCoinsObject object) {
    coinsModel.setObject(model, getUri(), predicate, object);
  }
  @Override
  public void addObject(String predicate, AbstractCoinsObject object) {
    coinsModel.addObject(getUri(), predicate, object);
  }
  @Override
  public void addObject(Model model, String predicate, AbstractCoinsObject object) {
    coinsModel.addObject(model, getUri(), predicate, object);
  }

  @Override
  public void removeIndividualAndProperties() {
    coinsModel.removeIndividualAndProperties(getUri());
  }
  @Override
  public void removeIndividualAndProperties(Model model) {
    coinsModel.removeIndividualAndProperties(model, getUri());
  }




  @Override
  public boolean equals(Object that) {
    if(!(that instanceof CoinsObject)) {
      return false;
    }
    return getUri().equals(((CoinsObject)that).getUri());
  }
  @Override
  public int hashCode() {
    return getUri().hashCode();
  }













  public void registerSourceFiles() {



    try {
      for(String fileName : Arrays.asList((String[]) getClass().getField("sourceFiles").get(Array.class))) {

        log.trace("Register "+fileName+" from jar.");

        InputStream fileStream = getClass().getResourceAsStream("/"+fileName);
        Namespace ns = FileManager.copyAndRegisterLibrary(fileStream, fileName, coinsModel.getAvailableLibraryFiles());
        coinsModel.addImport(null, ns.toString(), true, true, false);

      }


    } catch (IllegalAccessException e) {
      log.error(e.getMessage(), e);
    } catch (NoSuchFieldException e) {
      log.error(e.getMessage(), e);
    }
  }




























  @Override
  public ExpertCoinsModel getCoinsModel() {
    return coinsModel;
  }


}