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
package nl.coinsweb.sdk.jena;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.reasoner.Reasoner;
import nl.coinsweb.sdk.CoinsGraphSet;
import nl.coinsweb.sdk.ModelFactory;
import nl.coinsweb.sdk.Namespace;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * @author Bastian Bijl
 */
public class JenaCoinsGraphSet implements CoinsGraphSet {

  private static final Logger log = LoggerFactory.getLogger(JenaCoinsGraphSet.class);

  ModelFactory factory;

  private Namespace instanceNamespace;
  private Model instanceModel;


  public Namespace woaNamespace = new Namespace("http://woa.coinsweb.nl/");
  private Model woaModel;


  private Map<Namespace, Model> libraryModels;


  public JenaCoinsGraphSet(Namespace instanceNamespace, ModelFactory factory) {
    this.factory = factory;
    this.instanceNamespace = instanceNamespace;
    this.libraryModels = new HashMap<>();
    this.instanceModel = factory.getEmptyModel();
    this.woaModel = factory.getEmptyModel();
  }

  public void reset() {
    this.libraryModels = new HashMap<>();
  }

  @Override
  public void setInstanceNamespace(String namespace) {
    this.instanceNamespace = new Namespace(namespace);
  }

  @Override
  public String getInstanceNamespace() {
    return this.instanceNamespace.toString();
  }

  @Override
  public String getInstanceNamespaceWithoutHash() {
    return this.instanceNamespace.withoutHash();
  }

  @Override
  public String getWoaNamespace() {
    return this.woaNamespace.toString();
  }
  public void setWoaNamespace(String namespace) {
    this.woaNamespace = new Namespace(namespace);
  }


  @Override
  public Model getWoaModel() {
    return woaModel;
  }

  @Override
  public Model getInstanceModel() {
    return this.instanceModel;
  }

  @Override
  public Iterator<String> listModelNames() {
    List<String> buffer = new ArrayList<>();
    buffer.add(instanceNamespace.toString());
    buffer.add(woaNamespace.toString());
    Set<Namespace> namespaces = libraryModels.keySet();
    for(Namespace namespace : namespaces) {
      buffer.add(namespace.toString());
    }
    return buffer.iterator();
  }

  public Map<Namespace, Model> getLibraryModels() {
    return libraryModels;
  }

  public Model getJenaModel() {
    return this.instanceModel;
  }

  @Override
  public Model getJenaModel(String namespace) {
    Namespace ns = new Namespace(namespace);

    if(this.instanceNamespace.equals(ns)) {
      log.info("InstanceModel requested.");
      return instanceModel;
    }
    if(this.woaNamespace.equals(ns)) {
      log.info("WoaModel requested.");
      return woaModel;
    }
    if(libraryModels.containsKey(ns)) {
      log.info("Some library model requested.");
      return libraryModels.get(ns);
    }

    log.warn("Requested model could not be found: "+ns.toString()+", pick from:");
    log.warn("InstanceModel: "+instanceNamespace.toString());
    log.warn("WoaModel: "+woaNamespace.toString());
    for(Namespace candidate : libraryModels.keySet()) {
      log.warn("libraries: "+candidate.toString());
    }
    return null;
  }

  @Override
  public OntModel getJenaOntModel() {
    return factory.asOntModel(this.instanceModel);
  }
  public OntModel getJenaOntModel(Reasoner reasoner) {
    return factory.asOntModel(this.instanceModel, reasoner);
  }

  @Override
  public OntModel getJenaOntModel(String namespace) {
    Model model = getJenaModel(namespace);
    if(model != null) {
      return factory.asOntModel(model);
    }
    return null;
  }
  public OntModel getJenaOntModel(String namespace, Reasoner reasoner) {
    Model model = getJenaModel(namespace);
    if(model != null) {
      return factory.asOntModel(model, reasoner);
    }
    return null;
  }


  @Override
  public Model getUnionJenaModel() {
    return getUnionModel();
  }


  @Override
  public OntModel getUnionJenaOntModel() {
    return factory.asOntModel(getUnionModel());
  }
  public OntModel getUnionJenaOntModel(Reasoner reasoner) {
    return factory.asOntModel(getUnionModel(), reasoner);
  }


  public Model getUnionModel() {

    Model unionModel = factory.getEmptyModel();
    unionModel.add(instanceModel);
    unionModel.add(woaModel);
    for(Namespace key : libraryModels.keySet()) {
      unionModel.add(libraryModels.get(key));
    }


    return unionModel;
  }







  @Override
  public void writeModelToFile(Model model, OutputStream output, RDFFormat format) {

    if(format == RDFFormat.RDFXML || format == RDFFormat.RDFXML_ABBREV ||
        format == RDFFormat.RDFXML_PLAIN || format == RDFFormat.RDFXML_PRETTY) {

      RDFWriter writer;
      if(format == RDFFormat.RDFXML_ABBREV) {
        writer = model.getWriter( "RDF/XML-ABBREV" );
      } else {
        writer = model.getWriter( "RDF/XML" );
      }
      writer.setProperty("xmlbase", getInstanceNamespace() );
      writer.write(model, output, null);

    } else {
      Dataset dataset = factory.getDataset(instanceNamespace, instanceModel, woaNamespace, woaModel, libraryModels);
      dataset.getNamedModel(instanceNamespace.toString());
      RDFDataMgr.write(output, model, format);
    }
  }

  @Override
  public String writeModelToString(Model model, RDFFormat format) {

    log.trace("starting to export");


    ByteArrayOutputStream boas = new ByteArrayOutputStream();


    if(format == RDFFormat.RDFXML || format == RDFFormat.RDFXML_ABBREV ||
        format == RDFFormat.RDFXML_PLAIN || format == RDFFormat.RDFXML_PRETTY) {

      RDFWriter writer;
      if(format == RDFFormat.RDFXML_ABBREV) {
        writer = model.getWriter( "RDF/XML-ABBREV" );
      } else {
        writer = model.getWriter( "RDF/XML" );
      }
      writer.setProperty("xmlbase", getInstanceNamespace() );
      writer.write(model, boas, null);

    } else {
      Dataset dataset = factory.getDataset(instanceNamespace, instanceModel, woaNamespace, woaModel, libraryModels);
      dataset.getNamedModel(instanceNamespace.toString());
      RDFDataMgr.write(boas, model, format);
    }


    String result = "";

    try {
      BufferedReader reader = new BufferedReader(new StringReader(boas.toString()));

      String line = null;
      while ((line = reader.readLine()) != null) {
        result += line+"\n";
      }
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }

    return result;
  }

  @Override
  public void writeFullToFile(OutputStream output, RDFFormat format) {
    Dataset dataset = factory.getDataset(instanceNamespace, instanceModel, woaNamespace, woaModel, libraryModels);
    RDFDataMgr.write(output, dataset, format);
  }


  @Override
  public Dataset getDataset() {
    return factory.getDataset(instanceNamespace, instanceModel, woaNamespace, woaModel, libraryModels);
  }

}