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

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;
import nl.coinsweb.sdk.*;
import nl.coinsweb.sdk.apolda.impl.XSDAnySimpleTypeLiteral;
import nl.coinsweb.sdk.apolda.iterator.SparqlPropertyDeclarationIterator;
import nl.coinsweb.sdk.apolda.language.Language;
import nl.coinsweb.sdk.apolda.ontology.PropertyDeclaration;
import nl.coinsweb.sdk.exceptions.*;
import nl.coinsweb.sdk.injectors.AttachmentInjector;
import nl.coinsweb.sdk.injectors.Injector;
import nl.coinsweb.sdk.injectors.WOAInjector;
import org.apache.jena.riot.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author Bastian Bijl
 */
public class JenaCoinsContainer implements CoinsContainer, CoinsModel, ExpertCoinsModel {

  private static final Logger log = LoggerFactory.getLogger(JenaCoinsContainer.class);


  private String internalRef;      // pointer for use in FileManager

  private HashMap<String, File> attachments = new HashMap<>();
  private HashMap<Namespace, File> availableLibraryFiles = new HashMap<>();

  // stuff from model


  private CoinsParty party;
  private File originalContainerFile;


  private File rdfFile;
  private String rdfFileName = "content.rdf";

  private File woaFile;
  private String woaFileName = "woa.rdf";


  private ArrayList<Injector> injectors;

  private String containerId;
  private String fileName;



  private CoinsGraphSet graphSet;





  /**
   * create an empty clean container
   * @param namespace
   */
  public JenaCoinsContainer(String namespace) {
    this(new CoinsParty("http://sandbox.coinsweb.nl/defaultUser"), namespace, true);
  }
  public JenaCoinsContainer(CoinsParty party, String namespace) {
    this(party, namespace, true);
  }
  public JenaCoinsContainer(String namespace, boolean loadCoreModels) {
    this(new CoinsParty("http://sandbox.coinsweb.nl/defaultUser"), namespace, loadCoreModels);
  }
  public JenaCoinsContainer(CoinsParty party, String namespace, boolean loadCoreModels) {
    this(party, new InMemGraphSet(namespace), loadCoreModels);
  }
  public JenaCoinsContainer(CoinsParty party, CoinsGraphSet graphSet, boolean loadCoreModels) {


    this.fileName = null;

    this.party = party;
    this.party.setModel(this);


    this.graphSet = graphSet;



    this.internalRef = FileManager.newCoinsContainer();
    this.containerId = UUID.randomUUID().toString();



    // Prepare an empty graphset
    if(this.graphSet.getInstanceNamespace() == null) {
      throw new InvalidNamespaceException("Please provide a namespace if an empty CoinsModel is constructed.");
    }
    try {
      new URI(this.graphSet.getInstanceNamespace());
    } catch (URISyntaxException e) {
      throw new InvalidNamespaceException("Please provide a valid namespace, problems with "+this.graphSet.getInstanceNamespace() +".", e);
    }

    // Create empty model
    graphSet.getInstanceModel().setNsPrefix("", this.graphSet.getInstanceNamespace());
    graphSet.getInstanceModel().setNsPrefix("coins2", "http://www.coinsweb.nl/cbim-2.0.rdf#");
    addOntologyHeader();
    log.info("Added instance model with name "+ this.graphSet.getInstanceNamespace());

    // Add core model
    Namespace coreModelNamespace = FileManager.copyAndRegisterLibrary(FileManager.getResourceFileAsStream("libraries/cbim-2.0.rdf"), "cbim-2.0.rdf", availableLibraryFiles);
    addImport(null, coreModelNamespace.toString(), loadCoreModels, loadCoreModels, false);

    // Add core model
    Namespace unitsNamespace = FileManager.copyAndRegisterLibrary(FileManager.getResourceFileAsStream("libraries/units-2.0.rdf"), "units-2.0.rdf", availableLibraryFiles);
    addImport(null, unitsNamespace.toString(), loadCoreModels, loadCoreModels, false);

    // Add core model
    Namespace woaNamespace = FileManager.copyAndRegisterLibrary(FileManager.getResourceFileAsStream("libraries/COINSWOA.rdf"), "COINSWOA.rdf", availableLibraryFiles);
    addImport(null, woaNamespace.toString(), loadCoreModels, loadCoreModels, false);

    // Add core model
    Namespace versioningNamespace = FileManager.copyAndRegisterLibrary(FileManager.getResourceFileAsStream("libraries/BranchVersioning.rdf"), "BranchVersioning.rdf", availableLibraryFiles);
    addImport(null, versioningNamespace.toString(), loadCoreModels, loadCoreModels, false);

    initInjectors();
  }

  /**
   * create a container from existing files
   * @param filePath       a container (ccr-file) or an rdf-file
   */
  public JenaCoinsContainer(String filePath, String namespace) {
    this(new CoinsParty("http://sandbox.rws.nl/defaultUser"), filePath, namespace);
  }
  public JenaCoinsContainer(CoinsParty party, String filePath, String namespace) {


    this.fileName = null;

    this.party = party;
    this.party.setModel(this);


    this.graphSet = new InMemGraphSet(namespace);

    // Load an existing
    this.load(filePath);



    initInjectors();
  }





  private void initInjectors() {
    this.injectors = new ArrayList<>();
    this.injectors.add(new AttachmentInjector());
    this.injectors.add(new WOAInjector(graphSet.getWoaModel(), graphSet.getInstanceOntModel()));
  }


  /**
   * CoinsContainer interface
   */

  @Override
  public String getContainerId() {
    return containerId;
  }

  @Override
  public String getFileName() {
    return fileName;
  }




  @Override
  public void load(String sourceFile) {

    // Start with a clean sheet
    this.graphSet.reset();

    File file = new File(sourceFile);

    if(!file.exists()) {
      throw new CoinsFileNotFoundException("Supplied file does not exist.");
    }

    // See what file type it is
    if(file.getName().endsWith(".ccr") || file.getName().endsWith(".zip")) {

      this.fileName = file.getName();

      log.info("Reset current config");

      if(this.internalRef != null) {
        FileManager.destroy(this.internalRef);
      }



      log.info("Create CoinsContainer from ccr/zip file: "+file.getName());

      // Keep a pointer to the original .ccr/.zip file
      this.originalContainerFile = file;

      // Analyse the rdf-files
      HashMap<String, File> rdfFiles = new HashMap<>();
      HashMap<String, File> woaFiles = new HashMap<>();
      this.internalRef = FileManager.existingCoinsContainer(file, rdfFiles, woaFiles, attachments, availableLibraryFiles);

      if(rdfFiles.isEmpty()) {
        if(this.graphSet.getInstanceNamespace() == null) {
          throw new InvalidNamespaceException("No rdf file contained in coins container, please specify preferred namespace.");
        }
        this.rdfFile = null;
      } else {
        if(rdfFiles.size()>1) {
          log.warn("More than one rdf file found, picking a random first.");
        }
        this.rdfFile = rdfFiles.get(rdfFiles.keySet().iterator().next());
        log.info("Found file: " + this.rdfFile.toURI().toString());
        this.rdfFileName = this.rdfFile.getName();
      }

      if(woaFiles.isEmpty()) {
        this.woaFile = null;
      } else {
        if(woaFiles.size()>1) {
          log.warn("More than one woa file found, picking a random first.");
        }
        this.woaFile = woaFiles.get(woaFiles.keySet().iterator().next());
        log.info("Found file: " + this.woaFile.toURI().toString());
        this.woaFileName = this.woaFile.getName();
      }


    // Try to interpret as rdf-file
    } else {

      // Prepare models to be found
      FileManager.copyAndRegisterLibrary(FileManager.getResourceFileAsStream("libraries/cbim-2.0.rdf"), "cbim-2.0.rdf", availableLibraryFiles);
      FileManager.copyAndRegisterLibrary(FileManager.getResourceFileAsStream("libraries/units-2.0.rdf"), "units-2.0.rdf", availableLibraryFiles);
      FileManager.copyAndRegisterLibrary(FileManager.getResourceFileAsStream("libraries/COINSWOA.rdf"), "COINSWOA.rdf", availableLibraryFiles);
      FileManager.copyAndRegisterLibrary(FileManager.getResourceFileAsStream("libraries/BranchVersioning.rdf"), "BranchVersioning.rdf", availableLibraryFiles);

      log.info("Create CoinsContainer from rdf file: " + file.getName());

      this.originalContainerFile = null;
      this.internalRef = FileManager.newCoinsContainer();

      this.rdfFile = file;
      this.rdfFileName = file.getName();
    }


    // Create model and read the instance base model
    if(rdfFile != null) {
      graphSet.getInstanceModel().read(this.rdfFile.toURI().toString());
      graphSet.setInstanceNamespace(FileManager.getLeadingNamespace(this.rdfFile, graphSet.getInstanceModel()).toString());
    }
    graphSet.getInstanceModel().setNsPrefix("", graphSet.getInstanceNamespace());
    graphSet.getInstanceModel().setNsPrefix("coins2", "http://www.coinsweb.nl/cbim-2.0.rdf#");
    log.info("Added instance model with name "+ graphSet.getInstanceNamespace());

    Statement searchResult = graphSet.getInstanceModel().getProperty(new ResourceImpl(graphSet.getInstanceNamespaceWithoutHash()), new PropertyImpl("http://www.coinsweb.nl/cbim-2.0.rdf#containerId"));
    if(searchResult!=null && searchResult.getObject() != null) {
      this.containerId = searchResult.getObject().asLiteral().getLexicalForm();
    } else {
      this.containerId = UUID.randomUUID().toString();
      log.warn("No containerId found, setting it to: "+containerId+".");
      graphSet.getInstanceModel().add(new StatementImpl(
          new ResourceImpl(this.graphSet.getInstanceNamespaceWithoutHash()),
          new PropertyImpl("http://www.coinsweb.nl/cbim-2.0.rdf#containerId"),
          graphSet.getInstanceModel().createTypedLiteral(containerId)));
    }

    log.info("Found containerId "+this.containerId);
    addNamedModelForImports(graphSet.getInstanceModel());

    // Create woa model and read the woa base model
    if(woaFile != null) {
      graphSet.getWoaModel().read(this.woaFile.toURI().toString());
      graphSet.setWoaNamespace(FileManager.getLeadingNamespace(this.woaFile, graphSet.getWoaModel()).toString());
    }
    graphSet.getWoaModel().setNsPrefix("", graphSet.getWoaNamespace());
    log.info("Added woa model with name "+ graphSet.getWoaNamespace());

    initInjectors();
  }


  @Override
  public void export() {
    exportModel();
    if(originalContainerFile != null) {
      FileManager.zip(internalRef, originalContainerFile);
    } else {
      throw new CoinsFileNotFoundException("Can not export, no initial .ccr file was specified.");
    }
  }

  @Override
  public void export(String target) {
    exportModel();
    File targetFile = new File(target);
    targetFile.toPath().getParent().toFile().mkdirs();
    FileManager.zip(internalRef, targetFile);
  }


  public void exportModel() {
    File contentFile = FileManager.createRdfFile(internalRef, rdfFileName);
    exportModel(graphSet.getInstanceModel(), contentFile.getPath(), RDFFormat.RDFXML);
    File woaFile = FileManager.createWoaFile(internalRef, woaFileName);
    exportModel(graphSet.getWoaModel(), woaFile.getPath(), RDFFormat.RDFXML);
  }
  @Override
  public void exportModel(Model model, String target) {
    exportModel(model, target, RDFFormat.RDFXML);
  }
  public File exportModel(Model model, String toFileName, RDFFormat format) {

    try {

      File file = new File(toFileName);
      file.getParentFile().mkdirs();
      file.createNewFile(); // create if not already there


      OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
      getCoinsGraphSet().writeModelToFile(model, out, format);
      log.info("exported to " + file.getAbsolutePath());
      return file;


    } catch (IOException e) {
      throw new CoinsFileNotFoundException("Problem exporting to: "+toFileName, e);
    }
  }
  @Override
  public String exportAsString() {
    return exportAsString(graphSet.getInstanceModel());
  }
  @Override
  public String exportAsString(Model model) {
    return exportAsString(model, RDFFormat.RDFXML);
  }
  @Override
  public String exportAsString(RDFFormat format) {
    return exportAsString(graphSet.getInstanceModel(), format);
  }
  @Override
  public String exportAsString(Model model, RDFFormat format) {
    return getCoinsGraphSet().writeModelToString(graphSet.getInstanceModel(), format);
  }

  @Override
  public List<String> getLibraries() {
    ArrayList<String> list = new ArrayList<>();
    for(Namespace namespace : availableLibraryFiles.keySet()) {
      list.add(namespace.toString());
    }
    return list;
  }

  @Override
  public void exportLibrary(String libraryUri, String filePath) {
    try {
      Namespace libraryNs = new Namespace(libraryUri);
      if (libraryNs != null && availableLibraryFiles.containsKey(libraryNs)) {
        File tempFile = availableLibraryFiles.get(libraryNs);
        Files.copy(tempFile.toPath(), Paths.get(filePath));
      }
    } catch(IOException e) {
      throw new CoinsFileManagerException("Problem copying file to "+filePath, e);
    }
  }


  public HashMap<Namespace, File> getAvailableLibraryFiles() {
    return availableLibraryFiles;
  }

  @Override
  public List<String> getAttachments() {
    ArrayList<String> list = new ArrayList<>();
    list.addAll(attachments.keySet());
    return list;
  }

  @Override
  public File getAttachment(String fileName) {
    return FileManager.getAttachment(getInternalRef(), fileName).toFile();
  }

  @Override
  public void addAttachment(String filePath) {

    // Only continue if the attachment exists
    if (!new File(filePath).exists()) {
      return;
    }

    // Copy the file to the right directory
    Path absoluteTempPath = FileManager.placeAttachment(internalRef, new File(filePath).toPath());

    // Add to internal list of attachments
    attachments.put(absoluteTempPath.getFileName().toString(), absoluteTempPath.toFile());

    // Add an rdf element
    RuntimeCoinsObject documentReference = new RuntimeCoinsObject(this, "http://www.coinsweb.nl/cbim-2.0.rdf#InternalDocumentReference");
    RuntimeCoinsObject createdProperty = new RuntimeCoinsObject(this, "http://www.coinsweb.nl/cbim-2.0.rdf#StringProperty");
    createdProperty.setLiteralValue("http://www.coinsweb.nl/cbim-2.0.rdf#datatypeValue", absoluteTempPath.getFileName().toString());
    documentReference.setObject("http://www.coinsweb.nl/cbim-2.0.rdf#filePath", createdProperty);
  }









  /**
   * CoinsModel interface
   */


  @Override
  public void setLanguagePriority(String[] priority) {
    Language.languagePriority = priority;
  }

  @Override
  public void addInjector(Injector injector) {
    injectors.add(injector);
  }

  @Override
  public ArrayList<Injector> getInjectors() {
    return injectors;
  }

  @Override
  public void disableWOA() {
    // Remove all WOAInjectors from the injectors list
    for(int i =0; i<injectors.size(); i++) {
      if(injectors.get(i) instanceof WOAInjector) {
        injectors.remove(i);
        i--;
      }
    }
  }


  @Override
  public boolean hasImport(String namespace) {
    return hasImport(graphSet.getInstanceModel(), namespace);
  }
  @Override
  public boolean hasImport(Model model, String namespace) {
    if(!hasOntologyHeader(model)) {
      return false;
    }
    return model.contains(new ResourceImpl(this.graphSet.getInstanceNamespaceWithoutHash()), OWL.imports, new ResourceImpl(namespace));
  }


  @Override
  public void addImport(String filePath, String namespace, boolean addAsImport, boolean tryToLoad, boolean addToDoc) {
    addImport(graphSet.getInstanceModel(), filePath, namespace, addAsImport, tryToLoad, addToDoc);
  }
  public void addImport(Model model, String filePath, String namespace, boolean addAsImport, boolean tryToLoad, boolean addToDoc) {

    // Check the file
    boolean validFile = false;
    File checkFile;
    if(filePath != null) {
      checkFile = new File(filePath);
      if(checkFile.exists()) {
        validFile = true;
      }
    }

    // Add the file to the doc folder if requested
    Namespace actualNamespace = null;
    if(addToDoc) {
      if(!validFile) {
        throw new CoinsFileNotFoundException("Requested to add file "+filePath+" to the doc folder, but the file cannot be found.");
      }
      if(namespace != null) {
        actualNamespace = FileManager.registerLibrary(new File(filePath).toURI(), new Namespace(namespace), availableLibraryFiles);
      } else {
        actualNamespace = FileManager.registerLibrary(new File(filePath).toURI(), null, availableLibraryFiles);
      }
    }

    // Check if the supplied namespace is valid
    Namespace namespaceImpl;

    if(namespace != null) {
      namespaceImpl = new Namespace(namespace);
    } else {
      namespaceImpl = actualNamespace;
    }
    if(namespaceImpl == null || namespaceImpl.toURI()==null) {
      throw new InvalidNamespaceException("The supplied namespace "+namespace+" is not valid.");
    }

    // Add the import statement
    if(addAsImport && !hasImport(namespaceImpl.toString())) {

      if (!hasOntologyHeader()) {
        addOntologyHeader();
      }
      Statement statement = new StatementImpl(new ResourceImpl(this.graphSet.getInstanceNamespaceWithoutHash()), OWL.imports, new ResourceImpl(namespaceImpl.toString()));
      model.add(statement);
    }

    // Load the content of the library
    if(tryToLoad) {
      loadLibraryContent(namespaceImpl);
    }
  }
  @Override
  public CoinsParty getActiveParty() {
    return party;
  }
  @Override
  public void setActiveParty(CoinsParty party) {
    this.party = party;
    graphSet.getInstanceModel().getGraph().remove(
        new ResourceImpl(this.graphSet.getInstanceNamespaceWithoutHash()).asNode(),
        new PropertyImpl("http://www.coinsweb.nl/cbim-2.0.rdf#creator").asNode(),
        Node.ANY);
    graphSet.getInstanceModel().add(new StatementImpl(
        new ResourceImpl(this.graphSet.getInstanceNamespaceWithoutHash()),
        new PropertyImpl("http://www.coinsweb.nl/cbim-2.0.rdf#creator"),
        new ResourceImpl(party.getUri())));
  }

  @Override
  public Iterator<String> listClasses() {
    ArrayList<String> buffer = new ArrayList<>();
    ExtendedIterator<OntClass> iterator = graphSet.getUnionJenaOntModel().listClasses();
    while(iterator.hasNext()) {
      OntClass ontClass = iterator.next();
      if(!ontClass.isAnon()) {
        buffer.add(ontClass.getURI());
      }
    }
    return buffer.iterator();
  }

  @Override
  public Iterator<String> listClassesInLibrary(String namespace) {
    ArrayList<String> buffer = new ArrayList<>();
    Namespace key = new Namespace(namespace);
    if(graphSet.getLibraryModels().containsKey(key)) {

      ExtendedIterator<OntClass> iterator = graphSet.asOntModel(graphSet.getLibraryModels().get(key)).listClasses();
      while(iterator.hasNext()) {
        OntClass ontClass = iterator.next();
        if(!ontClass.isAnon()) {
          buffer.add(ontClass.getURI());
        }
      }
    }
    return buffer.iterator();
  }

  @Override
  public Iterator<String> listIndividuals() {
    return listIndividuals(graphSet.getInstanceModel());
  }
  @Override
  public Iterator<String> listIndividuals(Model model) {
    ArrayList<String> buffer = new ArrayList<>();
    ResIterator iterator = model.listSubjectsWithProperty(RDF.type);
    while(iterator.hasNext()) {
      String instanceUri = iterator.next().getURI();
      buffer.add(instanceUri);
    }
    return buffer.iterator();
  }

  @Override
  public <T extends CoinsObject> Iterator<T> listIndividuals(Class<T> objectClass) {
    return listIndividuals(graphSet.getInstanceModel(), objectClass);
  }
  @Override
  public <T extends CoinsObject> Iterator<T> listIndividuals(Model model, Class<T> objectClass) {

    ArrayList<T> buffer = new ArrayList<>();
    try {

      String classUri = (String) objectClass.getField("classUri").get(String.class);

      for(Triple triple : listInstances(model, classUri).toList()) {

        String individualUri = triple.getSubject().getURI();

        Constructor constructor = objectClass.getConstructor(ExpertCoinsModel.class, String.class, boolean.class);
        buffer.add((T) constructor.newInstance(this, individualUri, true));
      }
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      log.error("", e);
    } catch (InvocationTargetException e) {
      log.error("", e);
    } catch (InstantiationException e) {
      log.error("",e);
    } catch (IllegalAccessException e) {
      log.error("",e);
    }

    return buffer.iterator();
  }

  @Override
  public Iterator<String> listIndividuals(String classUri) {
    return listIndividuals(graphSet.getInstanceModel(), classUri);
  }
  @Override
  public Iterator<String> listIndividuals(Model model, String classUri) {
    ArrayList<String> buffer = new ArrayList<>();
    for(Triple triple : listInstances(model, classUri).toList()) {

      String individualUri = triple.getSubject().getURI();
      buffer.add(individualUri);
    }
    return buffer.iterator();
  }

  @Override
  public <T extends CoinsObject> Set<String> listIndividualUris(Class<T> objectClass) {

    HashSet<String> buffer = new HashSet<>();


    try {
      String classUri = (String) objectClass.getField("classUri").get(String.class);


      log.info("Try to find individuals for uri "+classUri);

      ExtendedIterator<Individual> individuals = graphSet.getUnionJenaOntModel().listIndividuals(new ResourceImpl(classUri));
      while(individuals.hasNext()) {
        Individual individual = individuals.next();
        buffer.add(individual.getURI());

      }
    } catch (IllegalAccessException e) {
      log.error(e.getMessage(), e);
    } catch (NoSuchFieldException e) {
      log.error(e.getMessage(), e);
    }


    return buffer;
  }

  @Override
  public Set<String> listIndividualUris(String classUri) {

    HashSet<String> buffer = new HashSet<>();
    log.info("Try to find individuals for uri "+classUri);

    ExtendedIterator<Individual> individuals = graphSet.getUnionJenaOntModel().listIndividuals(new ResourceImpl(classUri));
    while(individuals.hasNext()) {
      Individual individual = individuals.next();
      buffer.add(individual.getURI());

    }
    return buffer;
  }

  @Override
  public RuntimeCoinsObject getIndividual(String individualUri) {
    return getIndividual(graphSet.getInstanceModel(), individualUri);
  }

  @Override
  public RuntimeCoinsObject getIndividual(Model model, String individualUri) {
    Individual individual = graphSet.asOntModel(model).getIndividual(individualUri);
    if(individual!=null) {
      ExtendedIterator<OntClass> classIterator = individual.listOntClasses(true);
      while(classIterator.hasNext()) {
        OntClass clazz = classIterator.next();
        if(!clazz.isAnon()) {
          return new RuntimeCoinsObject(this, clazz.getURI(), individualUri);
        }
      }
    }
    return null;
  }

  @Override
  public Iterator<Map<String, String>> query(String sparqlQuery) {

    List<Map<String, String>> result = new ArrayList<>();

    // Execute the query and obtain results
    ResultSet results = graphSet.getResultSet(sparqlQuery);

    // Output query results
    while (results.hasNext()) {

      HashMap<String, String> resultRow = new HashMap();

      QuerySolution row = results.next();

      Iterator columnNames = row.varNames();
      while(columnNames.hasNext()) {
        String columnName = (String) columnNames.next();
        RDFNode item = row.get(columnName);
        if(item.isAnon()) {
          continue;
        }
        if(item.isResource()) {
          resultRow.put(columnName, item.asResource().getURI());
        } else if(item.isLiteral()) {
          resultRow.put(columnName, item.asLiteral().getLexicalForm());
        } else {
          log.warn("Skipping a result from the query.");
        }
      }

      result.add(resultRow);
    }

    // If all the files from the fileNames where in the zip archive, this list is now supposed to be emtpy
    return result.iterator();
  }


  /**
   * ExpertCoinsModel
   */

  @Override
  public Set<String> listClassUris(String instanceUri) {

    for(Injector injector : injectors) {
      injector.proposeRead(this, instanceUri);
    }

    HashSet<String> buffer = new HashSet<>();
    log.info("Try to find classes for uri "+instanceUri);

    ExtendedIterator<OntClass> classes = graphSet.getUnionJenaOntModel().getIndividual(instanceUri).listOntClasses(false);
    while(classes.hasNext()) {
      OntClass clazz = classes.next();
      if(!clazz.isAnon()) {
        buffer.add(clazz.getURI());
      }
    }
    return buffer;
  }


  @Override
  public boolean hasAsClass(String instanceUri, String classUri) {
    return listClassUris(instanceUri).contains(classUri);
  }


  @Override
  public void addType(String instanceUri, String classUri) {
    addStatement(instanceUri, RDF.type.getURI(), classUri);
  }
  @Override
  public void addType(Model model, String instanceUri, String classUri) {
    addStatement(model, instanceUri, RDF.type.getURI(), classUri);
  }
  @Override
  public void removeType(String instanceUri, String classUri) {
    removeStatement(instanceUri, RDF.type.getURI(), classUri);
  }
  @Override
  public void removeType(Model model, String instanceUri, String classUri) {
    removeStatement(model, instanceUri, RDF.type.getURI(), classUri);
  }

  @Override
  public void addCreator(String instanceUri, CoinsParty party) {
    addStatement(instanceUri, "http://www.coinsweb.nl/cbim-2.0.rdf#creator", party.getUri());
  }
  @Override
  public void addCreator(Model model, String instanceUri, CoinsParty party) {
    addStatement(model, instanceUri, "http://www.coinsweb.nl/cbim-2.0.rdf#creator", party.getUri());
  }
  @Override
  public void addCreatedNow(String instanceUri) {
    setLiteralValue(instanceUri, "http://www.coinsweb.nl/cbim-2.0.rdf#creationDate", new Date());
  }
  @Override
  public void addCreatedNow(Model model, String instanceUri) {
    setLiteralValue(model, instanceUri, "http://www.coinsweb.nl/cbim-2.0.rdf#creationDate", new Date());
  }
  @Override
  public void addCoinsContainerObjectType(String instanceUri) {
    addStatement(instanceUri, RDF.type.getURI(), "http://www.coinsweb.nl/cbim-2.0.rdf#CoinsContainerObject");
  }
  @Override
  public void batchAddCoinsContainerObjectType() {
    Iterator<String> iterator = listIndividuals();
    while(iterator.hasNext()) {
      addCoinsContainerObjectType(iterator.next());
    }
  }
  @Override
  public Iterator<String> findSubClasses(String classUri, String key) {

    ArrayList<String> buffer = new ArrayList<>();

    String queryString =

        " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>        " +
        " PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>             " +
        " PREFIX owl: <http://www.w3.org/2002/07/owl#>                     " +
        " SELECT ?result                                                   " +
        " WHERE                                                            " +
        " {                                                                " +
        "    ?result           rdfs:subClassOf*    <"+classUri+"> .        " +
        "    ?result           rdf:label           ?label .                " +
        "    FILTER (CONTAINS(?label, \""+key+"\"))                        " +
        " }                                                                ";


    // Execute the query and obtain results
    QueryExecution queryExecution = QueryExecutionFactory.create(queryString, Syntax.syntaxSPARQL_11, graphSet.getFullUnionModel());
    ResultSet resultSet = queryExecution.execSelect();

    while (resultSet.hasNext()) {

      QuerySolution row = resultSet.next();

      RDFNode result = row.get("result");
      if(result.isResource() && !result.isAnon()) {
        buffer.add(result.asResource().getURI());
      }
    }
    queryExecution.close();
    return buffer.iterator();
  }

  @Override
  public <T extends CoinsObject> boolean canAs(String instanceUri, Class<T> clazz) {
    try {
      T constructed = (T) as(instanceUri, clazz);
      return true;
    } catch (RuntimeException e) {
    }
    return false;
  }
  @Override
  public <T extends CoinsObject> boolean canAs(Model model, String instanceUri, Class<T> clazz) {
    try {
      T constructed = (T) as(model, instanceUri, clazz);
      return true;
    } catch (RuntimeException e) {
    }
    return false;
  }
  @Override
  public <T extends CoinsObject> T as(String instanceUri, Class<T> clazz) {
    log.info("try to cast to "+clazz.getCanonicalName() + " with uri "+instanceUri);
    try {
      Constructor constructor = clazz.getConstructor(ExpertCoinsModel.class, String.class);
      T constructed = (T) constructor.newInstance(getCoinsModel(), instanceUri);
      return constructed;
    } catch (NoSuchMethodException e) {
    } catch (InvocationTargetException e) {
    } catch (InstantiationException e) {
    } catch (IllegalAccessException e) {
    } catch (RuntimeException e) {
    }

    throw new CoinsObjectCastNotAllowedException("Could not cast to "+clazz.getCanonicalName()+".");
  }
  @Override
  public <T extends CoinsObject> T as(Model model, String instanceUri, Class<T> clazz) {
    log.info("try to cast to "+clazz.getCanonicalName() + " with uri "+instanceUri);
    try {
      Constructor constructor = clazz.getConstructor(ExpertCoinsModel.class, Model.class, String.class);
      T constructed = (T) constructor.newInstance(getCoinsModel(), model, instanceUri);
      return constructed;
    } catch (NoSuchMethodException e) {
    } catch (InvocationTargetException e) {
    } catch (InstantiationException e) {
    } catch (IllegalAccessException e) {
    } catch (RuntimeException e) {
    }

    throw new CoinsObjectCastNotAllowedException("Could not cast to "+clazz.getCanonicalName()+".");
  }

  @Override
  public Iterator<String> listPropertyDefinitions(String classUri, Class<CoinsObject> propertyTypeClass) {

    try {
      String propertyTypeClassUri =  (String) propertyTypeClass.getField("classUri").get(String.class);
      return listPropertyDefinitions(classUri, propertyTypeClassUri);
    } catch (IllegalAccessException e) {
      log.error(e.getMessage(), e);
    } catch (NoSuchFieldException e) {
      log.error(e.getMessage(), e);
    }

    ArrayList<String> buffer = new ArrayList<>();
    return buffer.iterator();
  }
  @Override
  public Iterator<String> listPropertyDefinitions(String classUri, String propertyTypeClassUri) {
    ArrayList<String> buffer = new ArrayList<>();
//    Iterator<ApoPropertyDeclaration> iterator =  new JenaPropertyDeclarationIterator(classUri, asOntModel(getFullUnionModel()), propertyTypeClassUri);
    Iterator<PropertyDeclaration> iterator =  new SparqlPropertyDeclarationIterator(classUri, graphSet.asOntModel(graphSet.getFullUnionModel()), propertyTypeClassUri);
    while(iterator.hasNext()) {
      buffer.add(iterator.next().getPropertyUri());
    }
    return buffer.iterator();
  }
  @Override
  public Iterator<CoinsObject> listProperties(String instanceUri) {
    return listProperties(graphSet.getInstanceModel(), instanceUri);
  }
  @Override
  public Iterator<CoinsObject> listProperties(Model model, String instanceUri) {

    boolean permission = true;
    for(Injector injector : injectors) {
      permission &= injector.proposeRead(this, instanceUri);
    }
    if(!permission) {
      throw new UnspecifiedInjectorRejectionException();
    }

    ArrayList<CoinsObject> buffer = new ArrayList<>();

    StmtIterator iterator = graphSet.asOntModel(model).getIndividual(instanceUri).listProperties();
    while(iterator.hasNext()) {

      Statement statement = iterator.nextStatement();

      RDFNode object = statement.getObject();

      if(object.isResource() && !object.isAnon()) {

        Individual property = graphSet.asOntModel(model).getIndividual(object.asResource().getURI());
        if(property != null) {

          ExtendedIterator<OntClass> classes = property.listOntClasses(true);

          while(classes.hasNext()) {
            String classUri = classes.next().getURI();
            if(classUri.startsWith(RDF.getURI())) continue;
            if(classUri.startsWith(RDFS.getURI())) continue;
            if(classUri.startsWith(OWL.getURI())) continue;
            if(classUri.startsWith(XSD.getURI())) continue;
            buffer.add(new RuntimeCoinsObject(this, classUri, object.asResource().getURI()));
            break;
          }


        }
      }
    }

    return buffer.iterator();
  }
  @Override
  public <T extends CoinsObject> Iterator<T> listProperties(String instanceUri, Class<T> propertyTypeClass) {
    return listProperties(graphSet.getInstanceModel(), instanceUri, propertyTypeClass);
  }
  @Override
  public <T extends CoinsObject> Iterator<T> listProperties(Model model, String instanceUri, Class<T> propertyTypeClass) {

    boolean permission = true;
    for(Injector injector : injectors) {
      permission &= injector.proposeRead(this, instanceUri);
    }
    if(!permission) {
      throw new UnspecifiedInjectorRejectionException();
    }

    ArrayList<T> buffer = new ArrayList<>();
    try {
      String propertyTypeClassUri =  (String) propertyTypeClass.getField("classUri").get(String.class);
      StmtIterator iterator = graphSet.asOntModel(model).getIndividual(instanceUri).listProperties();
      while(iterator.hasNext()) {
        Statement statement = iterator.nextStatement();
        RDFNode object = statement.getObject();
        if(object.isResource() && !object.isAnon()) {
          if(listClassUris(object.asResource().getURI()).contains(propertyTypeClassUri)) {
            buffer.add(as(object.asResource().getURI(), propertyTypeClass));
          }
        }
      }
    } catch (IllegalAccessException e) {
      log.error(e.getMessage(), e);
    } catch (NoSuchFieldException e) {
      log.error(e.getMessage(), e);
    }
    return buffer.iterator();
  }
  @Override
  public Iterator<RuntimeCoinsObject> listProperties(String instanceUri, String propertyClassUri) {
    return listProperties(graphSet.getInstanceModel(), instanceUri, propertyClassUri);
  }
  @Override
  public Iterator<RuntimeCoinsObject> listProperties(Model model, String instanceUri, String propertyClassUri) {

    boolean permission = true;
    for(Injector injector : injectors) {
      permission &= injector.proposeRead(this, instanceUri);
    }
    if(!permission) {
      throw new UnspecifiedInjectorRejectionException();
    }

    ArrayList<RuntimeCoinsObject> buffer = new ArrayList<>();
    StmtIterator iterator = graphSet.asOntModel(model).getIndividual(instanceUri).listProperties();
    while(iterator.hasNext()) {
      Statement statement = iterator.nextStatement();
      RDFNode object = statement.getObject();
      if(object.isResource() && !object.isAnon()) {
        if(listClassUris(object.asResource().getURI()).contains(propertyClassUri)) {
          buffer.add(new RuntimeCoinsObject(this, propertyClassUri, object.asResource().getURI()));
        }
      }
    }
    return buffer.iterator();
  }
  @Override
  public <T extends CoinsObject> Iterator<T> listProperties(String instanceUri, String predicate, Class<T> propertyTypeClass) {
    return listProperties(graphSet.getInstanceModel(), instanceUri, predicate, propertyTypeClass);
  }
  @Override
  public <T extends CoinsObject> Iterator<T> listProperties(Model model, String instanceUri, String predicate, Class<T> propertyTypeClass) {

    boolean permission = true;
    for(Injector injector : injectors) {
      permission &= injector.proposeRead(this, instanceUri);
    }
    if(!permission) {
      throw new UnspecifiedInjectorRejectionException();
    }

    ArrayList<T> buffer = new ArrayList<>();
    try {
      String propertyTypeClassUri =  (String) propertyTypeClass.getField("classUri").get(String.class);
      StmtIterator iterator = graphSet.asOntModel(model).getIndividual(instanceUri).listProperties(new PropertyImpl(predicate));
      while(iterator.hasNext()) {
        Statement statement = iterator.nextStatement();
        RDFNode object = statement.getObject();
        if(object.isResource() && !object.isAnon()) {
          if (listClassUris(object.asResource().getURI()).contains(propertyTypeClassUri)) {
            buffer.add(as(object.asResource().getURI(), propertyTypeClass));
          }
        }
      }
    } catch (IllegalAccessException e) {
      log.error(e.getMessage(), e);
    } catch (NoSuchFieldException e) {
      log.error(e.getMessage(), e);
    }
    return buffer.iterator();
  }
  @Override
  public Iterator<RuntimeCoinsObject> listProperties(String instanceUri, String predicate, String propertyClassUri) {
    return listProperties(graphSet.getInstanceModel(), instanceUri, predicate, propertyClassUri);
  }
  @Override
  public Iterator<RuntimeCoinsObject> listProperties(Model model, String instanceUri, String predicate, String propertyClassUri) {

    boolean permission = true;
    for(Injector injector : injectors) {
      permission &= injector.proposeRead(this, instanceUri);
    }
    if(!permission) {
      throw new UnspecifiedInjectorRejectionException();
    }

    ArrayList<RuntimeCoinsObject> buffer = new ArrayList<>();
    StmtIterator iterator = graphSet.asOntModel(model).getIndividual(instanceUri).listProperties(new PropertyImpl(predicate));
    while(iterator.hasNext()) {
      Statement statement = iterator.nextStatement();
      RDFNode object = statement.getObject();
      if(object.isResource() && !object.isAnon()) {
        if(listClassUris(object.asResource().getURI()).contains(propertyClassUri)) {
          buffer.add(new RuntimeCoinsObject(this, propertyClassUri, object.asResource().getURI()));
        }
      }
    }
    return buffer.iterator();
  }
  @Override
  public RuntimeCoinsObject createProperty(String instanceUri, String predicateUri, String propertyClassUri) {
    return createProperty(graphSet.getInstanceModel(), instanceUri, predicateUri, propertyClassUri);
  }
  @Override
  public RuntimeCoinsObject createProperty(Model model, String instanceUri, String predicateUri, String propertyClassUri) {

    RuntimeCoinsObject object = new RuntimeCoinsObject(this, propertyClassUri);
    addStatement(model, instanceUri, predicateUri, object.getUri());

    return object;
  }
  @Override
  public <T extends CoinsObject> T createProperty(String instanceUri, String predicateUri, Class<T> propertyClass) {
    return createProperty(graphSet.getInstanceModel(), instanceUri, predicateUri, propertyClass);
  }
  @Override
  public <T extends CoinsObject> T createProperty(Model model, String instanceUri, String predicateUri, Class<T> propertyClass) {

    try {
      Constructor constructor = propertyClass.getConstructor(ExpertCoinsModel.class);
      T constructed = (T) constructor.newInstance(this);

      addStatement(model, instanceUri, predicateUri, constructed.getUri());
      return constructed;
    } catch (NoSuchMethodException e) {
    } catch (InvocationTargetException e) {
    } catch (InstantiationException e) {
    } catch (IllegalAccessException e) {
    } catch (RuntimeException e) {
    }
    throw new CoinsPropertyCreationException("Something went wrong creating a property for class "+propertyClass.getCanonicalName());
  }
  @Override
  public void removeProperty(String instanceUri, CoinsObject property) {
    removeProperty(graphSet.getInstanceModel(), instanceUri, property);
  }
  @Override
  public void removeProperty(Model model, String instanceUri, CoinsObject property) {

    Resource subject = new ResourceImpl(instanceUri);
    Resource object = new ResourceImpl(property.getUri());

    // Find the predicate
    ExtendedIterator<Triple> predicateIterator = model.getGraph().find(subject.asNode(), Node.ANY, object.asNode());
    if(!predicateIterator.hasNext()) {
      throw new CoinsPropertyNotFoundException("Property not found, so not able to remove it.");
    }
    Property predicate = new PropertyImpl(predicateIterator.next().getPredicate().getURI());

    // Remove the link to this property
    Statement statement = new StatementImpl(subject, predicate, object);
    log.info("Removing link to property " + statement);
    removeStatement(model, subject.getURI(), predicate.getURI(), object.getURI());



    // Remove the property object and all its content
    removeIndividualAndProperties(model, property.getUri());
  }
  @Override
  public <T> T getLiteralValue(String subject, String predicate, Class<T> clazz) {
    return getLiteralValue(graphSet.getInstanceModel(), subject, predicate, clazz);
  }
  public <T> T getLiteralValue(Model model, String subject, String predicate, Class<T> clazz) {

    boolean permission = true;
    for(Injector injector : injectors) {
      permission &= injector.proposeRead(this, subject);
    }
    if(!permission) {
      throw new UnspecifiedInjectorRejectionException();
    }

    RDFDatatype datatype = TypeMapper.getInstance().getTypeByClass(clazz);

    NodeIterator result = listObjectsOfProperty(model, subject, predicate);
    while(result!=null && result.hasNext()) {

      RDFNode single = result.next();
      if(single.isLiteral()) {
        if(single.asLiteral().getDatatypeURI().equals("http://www.w3.org/2001/XMLSchema#anySimpleType")) {


          if(result.hasNext()) {
            log.warn(subject+" -"+predicate+"-> ... has more than one value");
          }

          if(clazz.equals(XSDAnySimpleTypeLiteral.class)) {

            return (T) new XSDAnySimpleTypeLiteral(single.asLiteral());
          } else if(clazz.equals(Boolean.class)) {
            return (T) new Boolean(Boolean.getBoolean(single.asLiteral().getLexicalForm()));
          } else {
            return (T) single.asLiteral().getLexicalForm();
          }

        } else if(clazz.equals(Date.class)) {

          OntProperty prop = graphSet.getUnionJenaOntModel().getOntProperty(predicate);
          Individual instance = graphSet.getInstanceOntModel().getIndividual(subject);
          if(prop == null || instance == null) {
            throw new CoinsPropertyNotFoundException("The predicate "+predicate+" could not be found as Property when requesting literal value.");
          }
          XSDDateTime date = (XSDDateTime) instance.getProperty(prop).getLiteral().getValue();
          return (T) new Date(date.asCalendar().getTimeInMillis());


        } else if(clazz.equals(URI.class)) {

          try {
            return (T) new URI(single.asLiteral().getLexicalForm());
          } catch(URISyntaxException e) {
            throw new CoinsPropertyNotFoundException("The found uri could not be parsed: "+single.asLiteral().getLexicalForm());
          }

        } else if(single.asLiteral().getDatatypeURI().equals(XSD.integer.getURI()) ||
            single.asLiteral().getDatatypeURI().equals(XSD.xint.getURI())) {

          return (T) Integer.valueOf(single.asLiteral().getLexicalForm());

        } else if(single.asLiteral().getDatatypeURI().equals(datatype.getURI())) {


          if(result.hasNext()) {
            log.warn(subject+" -"+predicate+"-> ... has more than one value");
          }

          return (T) datatype.parse(single.asLiteral().getLexicalForm());
        }
      }
    }
    return null;
  }
  @Override
  public <T> Iterator<T> getLiteralValues(String subject, String predicate, Class<T> clazz) {
    return getLiteralValues(graphSet.getInstanceModel(), subject, predicate, clazz);
  }
  public <T> Iterator<T> getLiteralValues(Model model, String subject, String predicate, Class<T> clazz) {

    boolean permission = true;
    for(Injector injector : injectors) {
      permission &= injector.proposeRead(this, subject);
    }
    if(!permission) {
      throw new UnspecifiedInjectorRejectionException();
    }

    ArrayList<T> buffer = new ArrayList<>();

    RDFDatatype datatype = TypeMapper.getInstance().getTypeByClass(clazz);

    NodeIterator result = listObjectsOfProperty(model, subject, predicate);
    while(result!=null && result.hasNext()) {

      RDFNode single = result.next();
      if(single.isLiteral()) {
        if(clazz.equals(XSDAnySimpleTypeLiteral.class) &&
            single.asLiteral().getDatatypeURI().equals("http://www.w3.org/2001/XMLSchema#anySimpleType")) {

          buffer.add((T) new XSDAnySimpleTypeLiteral(single.asLiteral()));

        } else if(clazz.equals(Date.class)) {

          OntProperty prop = graphSet.getInstanceOntModel().getOntProperty(predicate);
          Individual instance = graphSet.getInstanceOntModel().getIndividual(subject);
          XSDDateTime date = (XSDDateTime) instance.getProperty(prop).getLiteral().getValue();
          buffer.add((T) new Date(date.asCalendar().getTimeInMillis()));


        } else if(single.asLiteral().getDatatypeURI().equals(datatype.getURI())) {

          buffer.add((T) datatype.parse(single.asLiteral().getLexicalForm()));
        }
      }
    }

    return buffer.iterator();
  }
  @Override
  public <T> void setLiteralValue(String subject, String predicate, T object) {
    setLiteralValue(graphSet.getInstanceModel(), subject, predicate, object);
  }
  @Override
  public <T> void setLiteralValue(Model model, String subject, String predicate, T object) {

    removeAllStatements(model, subject, predicate);

    if(object instanceof Date) {

      GregorianCalendar calendar = new GregorianCalendar();
      calendar.setTime((Date)object);
      XSDDateTime dateTime = new XSDDateTime(calendar);
      Literal propValue = graphSet.asOntModel(model).createTypedLiteral(dateTime, XSDDatatype.XSDdateTime);

      boolean permission = true;
      for(Injector injector : injectors) {
        permission &= injector.proposeWrite(this, subject, predicate, propValue.getString());
      }
      if(permission) {
        OntProperty prop = graphSet.getUnionJenaOntModel().getOntProperty(predicate);
        Individual individual = graphSet.asOntModel(model).getIndividual(subject);
        individual.setPropertyValue(prop, propValue);
      } else {
        throw new UnspecifiedInjectorRejectionException();
      }

    } else {
      Literal literal = model.createTypedLiteral(object);
      addStatement(model, subject, predicate, literal);
    }
  }
  @Override
  public <T> void addLiteralValue(String subject, String predicate, T object) {
    addLiteralValue(graphSet.getInstanceModel(), subject, predicate, object);
  }
  @Override
  public <T> void addLiteralValue(Model model, String subject, String predicate, T object) {

    if(object instanceof Date) {

      GregorianCalendar calendar = new GregorianCalendar();
      calendar.setTime((Date)object);
      XSDDateTime dateTime = new XSDDateTime(calendar);
      Literal propValue = graphSet.getInstanceOntModel().createTypedLiteral(dateTime, XSDDatatype.XSDdateTime);

      boolean permission = true;
      for(Injector injector : injectors) {
        permission &= injector.proposeWrite(this, subject, predicate, propValue.getString());
      }
      if(permission) {
        OntProperty prop = graphSet.getUnionJenaOntModel().getOntProperty(predicate);
        Individual individual = graphSet.asOntModel(model).getIndividual(subject);
        individual.setPropertyValue(prop, propValue);
      } else {
        throw new UnspecifiedInjectorRejectionException();
      }


    } else {
      addStatement(model, subject, predicate, graphSet.getInstanceModel().createTypedLiteral(object));
    }
  }
  @Override
  public <T> void removeLiteralValue(String subject, String predicate, T object) {
    removeLiteralValue(graphSet.getInstanceModel(), subject, predicate, object);
  }
  @Override
  public <T> void removeLiteralValue(Model model, String subject, String predicate, T object) {

    if(object instanceof Date) {

      GregorianCalendar calendar = new GregorianCalendar();
      calendar.setTime((Date)object);
      XSDDateTime dateTime = new XSDDateTime(calendar);
      Literal propValue = graphSet.getInstanceOntModel().createTypedLiteral(dateTime, XSDDatatype.XSDdateTime);


      boolean permission = true;
      for(Injector injector : injectors) {
        permission &= injector.proposeWrite(this, subject, predicate, propValue.getString());
      }
      if(permission) {
        OntProperty prop = graphSet.getUnionJenaOntModel().getOntProperty(predicate);
        Individual individual = graphSet.asOntModel(model).getIndividual(subject);
        individual.removeProperty(prop, propValue);
      } else {
        throw new UnspecifiedInjectorRejectionException();
      }


    } else {
      removeStatement(model, subject, predicate, graphSet.getInstanceModel().createTypedLiteral(object));
    }
  }


  @Override
  public <T extends CoinsObject> T getObject(String subject, String predicate, Class<T> clazz) {
    return getObject(graphSet.getInstanceModel(), subject, predicate, clazz);
  }
  @Override
  public <T extends CoinsObject> T getObject(Model model, String subject, String predicate, Class<T> clazz) {

    boolean permission = true;
    for(Injector injector : injectors) {
      permission &= injector.proposeRead(this, subject);
    }
    if(!permission) {
      throw new UnspecifiedInjectorRejectionException();
    }

    NodeIterator result = listObjectsOfProperty(model, subject, predicate);

    while(result!=null && result.hasNext()) {
      RDFNode single = result.next();

      if(single.isResource()) {
        try {
          Constructor constructor = clazz.getConstructor(ExpertCoinsModel.class, Model.class, String.class);
          return (T) constructor.newInstance(this, model, single.asResource().getURI());
        } catch (NoSuchMethodException e) {
          e.printStackTrace();
        } catch (InvocationTargetException e) {
          e.printStackTrace();
        } catch (InstantiationException e) {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    }

    return null;
  }
  @Override
  public <T extends CoinsObject> Iterator<T> getObjects(String subject, String predicate, Class<T> clazz) {
    return getObjects(graphSet.getInstanceModel(), subject, predicate, clazz);
  }
  public <T extends CoinsObject> Iterator<T> getObjects(Model model, String subject, String predicate, Class<T> clazz) {

    boolean permission = true;
    for(Injector injector : injectors) {
      permission &= injector.proposeRead(this, subject);
    }
    if(!permission) {
      throw new UnspecifiedInjectorRejectionException();
    }

    ArrayList<T> buffer = new ArrayList<>();

    NodeIterator result = listObjectsOfProperty(model, subject, predicate);
    if(result!=null && result.hasNext()) {
      for(RDFNode node : result.toList()) {
        if(node.isResource()) {

          try {
            Constructor constructor = clazz.getConstructor(ExpertCoinsModel.class, Model.class, String.class);
            buffer.add((T) constructor.newInstance(this, model, node.asResource().getURI()));
          } catch (NoSuchMethodException e) {
            log.error("",e);
          } catch (InvocationTargetException e) {
            log.error("",e);
          } catch (InstantiationException e) {
            log.error("",e);
          } catch (IllegalAccessException e) {
            log.error("",e);
          }
        }
      }
    }



    return buffer.iterator();
  }
  @Override
  public void setObject(String subject, String predicate, CoinsObject object) {
    setObject(graphSet.getInstanceModel(), subject, predicate, object);
  }
  @Override
  public void setObject(Model model, String subject, String predicate, CoinsObject object) {
    removeAllStatements(model, subject, predicate);
    addStatement(model, subject, predicate, object.getUri());
  }
  @Override
  public void setObject(String subject, String predicate, String objectUri) {
    setObject(graphSet.getInstanceModel(), subject, predicate, objectUri);
  }
  @Override
  public void setObject(Model model, String subject, String predicate, String objectUri) {
    removeAllStatements(model, subject, predicate);
    addStatement(model, subject, predicate, objectUri);
  }
  @Override
  public void addObject(String subject, String predicate, CoinsObject object) {
    addStatement(graphSet.getInstanceModel(), subject, predicate, object.getUri());
  }
  @Override
  public void addObject(Model model, String subject, String predicate, CoinsObject object) {
    addStatement(model, subject, predicate, object.getUri());
  }
  @Override
  public void addObject(String subject, String predicate, String objectUri) {
    addStatement(graphSet.getInstanceModel(), subject, predicate, objectUri);
  }
  @Override
  public void addObject(Model model, String subject, String predicate, String objectUri) {
    addStatement(model, subject, predicate, objectUri);
  }
  @Override
  public void removeObject(String subject, String predicate, CoinsObject object) {
    removeObject(graphSet.getInstanceModel(), subject, predicate, object);
  }
  @Override
  public void removeObject(Model model, String subject, String predicate, CoinsObject object) {
    removeStatement(model, subject, predicate, object.getUri());
  }
  @Override
  public void removeObject(String subject, String predicate, String objectUri) {
    removeObject(graphSet.getInstanceModel(), subject, predicate, objectUri);
  }
  @Override
  public void removeObject(Model model, String subject, String predicate, String objectUri) {
    removeStatement(model, subject, predicate, objectUri);
  }

  @Override
  public void removeIndividualAndProperties(String instanceUri) {
    removeIndividualAndProperties(graphSet.getInstanceModel(), instanceUri);
  }
  @Override
  public void removeIndividualAndProperties(Model model, String instanceUri) {

    log.info("Removing individual and all it's properties for uri: "+instanceUri+".");

    // Iterate over all properties an delete them
    Iterator<RuntimeCoinsObject> properties = listProperties(model, instanceUri, "http://www.coinsweb.nl/cbim-2.0.rdf#EntityProperty");
    while(properties.hasNext()) {
      removeProperty(model, instanceUri, properties.next());
    }

    // Remove all remaining statements mentioning the instanceUri directly (including type definition)
    ExtendedIterator<Triple> incoming = model.getGraph().find(Node.ANY, Node.ANY, new ResourceImpl(instanceUri).asNode());
    Set<Triple> collectIncoming = new HashSet<>();
    while(incoming.hasNext()) {
      collectIncoming.add(incoming.next());
    }
    for(Triple triple : collectIncoming) {
      removeStatement(model, triple.getSubject().getURI(), triple.getPredicate().getURI(), new ResourceImpl(instanceUri));
    }

    ExtendedIterator<Triple> outgoing = model.getGraph().find(new ResourceImpl(instanceUri).asNode(), Node.ANY, Node.ANY);
    Set<Triple> collectOutgoing = new HashSet<>();
    while(outgoing.hasNext()) {
      collectOutgoing.add(outgoing.next());
    }
    for(Triple triple : collectOutgoing) {
      removeAllStatements(model, new ResourceImpl(instanceUri).getURI(), triple.getPredicate().getURI());
    }

  }












  @Override
  public ExtendedIterator<OntClass> listOntClasses() {
    return graphSet.asOntModel(graphSet.getFullUnionModel()).listClasses();
  }

  @Override
  public Iterator<PropertyDeclaration> listPropertyDeclarations(String classUri) {
    return new SparqlPropertyDeclarationIterator(classUri, graphSet.asOntModel(graphSet.getFullUnionModel()));
//    return new JenaPropertyDeclarationIterator(clazz, asOntModel(getFullUnionModel()));
  }

  @Override
  public boolean hasOntologyHeader() {
    return hasOntologyHeader(graphSet.getInstanceModel());
  }
  @Override
  public boolean hasOntologyHeader(Model model) {
    return model.contains(new StatementImpl(new ResourceImpl(this.graphSet.getInstanceNamespaceWithoutHash()), RDF.type, OWL.Ontology));
  }

  @Override
  public void addOntologyHeader() {

    // Add header itself
    log.info("use this as subject for ontology header "+graphSet.getInstanceNamespace());
    graphSet.getInstanceModel().add(new StatementImpl(new ResourceImpl(this.graphSet.getInstanceNamespaceWithoutHash()), RDF.type, OWL.Ontology));

    // Add creator and containerId
    graphSet.getInstanceModel().add(new StatementImpl(
        new ResourceImpl(this.graphSet.getInstanceNamespaceWithoutHash()),
        new PropertyImpl("http://www.coinsweb.nl/cbim-2.0.rdf#creator"),
        new ResourceImpl(getActiveParty().getUri())));
    graphSet.getInstanceModel().add(new StatementImpl(
        new ResourceImpl(this.graphSet.getInstanceNamespaceWithoutHash()),
        new PropertyImpl("http://www.coinsweb.nl/cbim-2.0.rdf#containerId"),
        graphSet.getInstanceModel().createTypedLiteral(containerId)));

    // Add import statements
    for(Namespace key : graphSet.getLibraryModels().keySet()) {
      log.info("add an imports statement to "+key.toString());
      graphSet.getInstanceModel().add(new StatementImpl(new ResourceImpl(this.graphSet.getInstanceNamespaceWithoutHash()), OWL.imports, new ResourceImpl(key.toString())));
    }
  }

  @Override
  public void addStatement(String subject, String predicate, String object) {
    addStatement(graphSet.getInstanceModel(), subject, predicate, object);
  }
  public void addStatement(Model model, String subject, String predicate, String object) {
    Statement statement = new StatementImpl(new ResourceImpl(subject), new PropertyImpl(predicate), new ResourceImpl(object));
    log.info("Adding statement " + statement);

    boolean permission = true;
    for(Injector injector : injectors) {
      permission &= injector.proposeWrite(this, subject, predicate, object);
    }
    if(permission) {
      model.add(statement);
    } else {
      throw new UnspecifiedInjectorRejectionException();
    }
  }
  @Override
  public void addStatement(String subject, String predicate, RDFNode object) {
    addStatement(graphSet.getInstanceModel(), subject, predicate, object);
  }
  public void addStatement(Model model, String subject, String predicate, RDFNode object) {
    Statement statement = new StatementImpl(new ResourceImpl(subject), new PropertyImpl(predicate), object);
    log.info("Adding statement "+statement);

    boolean permission = true;
    for(Injector injector : injectors) {
      permission &= injector.proposeWrite(this, subject, predicate, object.toString());
    }
    if(permission) {
      model.add(statement);
    } else {
      throw new UnspecifiedInjectorRejectionException();
    }
  }

  @Override
  public void removeStatement(String subject, String predicate, String object) {
    removeStatement(graphSet.getInstanceModel(), subject, predicate, object);
  }
  public void removeStatement(Model model, String subject, String predicate, String object) {
    Statement statement = new StatementImpl(new ResourceImpl(subject), new PropertyImpl(predicate), new ResourceImpl(object));
    log.info("Removing statement " + statement);

    boolean permission = true;
    for(Injector injector : injectors) {
      permission &= injector.proposeWrite(this, subject, predicate, object);
    }
    if(permission) {
      model.remove(statement);
    } else {
      throw new UnspecifiedInjectorRejectionException();
    }
  }
  @Override
  public void removeStatement(String subject, String predicate, RDFNode object) {
    removeStatement(graphSet.getInstanceModel(), subject, predicate, object);
  }
  @Override
  public void removeStatement(Model model, String subject, String predicate, RDFNode object) {
    Statement statement = new StatementImpl(new ResourceImpl(subject), new PropertyImpl(predicate), object);
    log.info("Removing statement " + statement);

    boolean permission = true;
    for(Injector injector : injectors) {
      permission &= injector.proposeWrite(this, subject, predicate, object.toString());
    }
    if(permission) {
      model.remove(statement);
    } else {
      throw new UnspecifiedInjectorRejectionException();
    }
  }
  @Override
  public void removeAllStatements(String subject, String predicate) {
    removeAllStatements(graphSet.getInstanceModel(), subject, predicate);
  }
  @Override
  public void removeAllStatements(Model model, String subject, String predicate) {
    log.info("Removing statement " + subject + " -> "+predicate+" -> any");

    boolean permission = true;
    for(Injector injector : injectors) {
      permission &= injector.proposeWrite(this, subject, predicate, null);
    }
    if(permission) {
      model.getGraph().remove(new ResourceImpl(subject).asNode(), new PropertyImpl(predicate).asNode(), Node.ANY);
    } else {
      throw new UnspecifiedInjectorRejectionException();
    }
  }

  @Override
  public String generateUri() {
    return graphSet.getInstanceNamespace() + UUID.randomUUID().toString();
  }









  /**
   *
   */



  public String getInternalRef() {
    return internalRef;
  }











  /**
   * Close all loaded models
   */
  public void close() {
    FileManager.destroy(this.internalRef);
  }









  public void addNamedModelForImports(Model model) {

    OntModel enrichedModel = graphSet.asOntModel(model);

    for(String imp : enrichedModel.listImportedOntologyURIs()) {
      log.trace("need to load "+imp);
      Namespace namespace = new Namespace(imp);
      loadLibraryContent(namespace);
    }
  }

  private void loadLibraryContent(Namespace namespace) {

    try {

      // Check if the model was already loaded
      if(graphSet.getLibraryModels().containsKey(namespace)) {
        return;
      }

      // Acquire an uri to a local file or online resolvable uri
      URI importUri = FileManager.getLibrary(this, internalRef, namespace.toURI());
      if(importUri == null) {
        log.info("\u274E Unfortunately not found for internalRef " + internalRef + " and " + namespace.toString());
        return;
      }

      // Load the model
      Model libraryModel = graphSet.getEmptyModel();
      libraryModel.read(importUri.toString());
      graphSet.getLibraryModels().put(namespace, libraryModel);
      log.info("\u2705 Adding model with name " + namespace.toString());

      // Recursively add everything this import depended on
      addNamedModelForImports(libraryModel);


    } catch (RuntimeException e) {
      log.info("failed importing file linked to namespace " + namespace.toString());
    }
  }















  private NodeIterator listObjectsOfProperty(Model model, String subject, String predicate) {

    boolean permission = true;
    for(Injector injector : injectors) {
      permission &= injector.proposeRead(this, subject, predicate);
    }
    if(!permission) {
      throw new UnspecifiedInjectorRejectionException();
    }

    Resource subj = new ResourceImpl(subject);
    Property pred = new PropertyImpl(predicate);

    if(model.contains(subj, pred)) {
      return model.listObjectsOfProperty(subj, pred);
    }
    return null;
  }
  private ExtendedIterator<Triple> listInstances(String classUri) {
    return listInstances(graphSet.getInstanceModel(), classUri);
  }
  private ExtendedIterator<Triple> listInstances(Model model, String classUri) {
    return model.getGraph().find(Node.ANY, RDF.type.asNode(), new ResourceImpl(classUri).asNode());
  }












  public CoinsGraphSet getCoinsGraphSet() {
    return this.graphSet;
  }




  @Override
  public CoinsContainer getCoinsContainer() {
    return (CoinsContainer) this;
  }

  @Override
  public CoinsModel asCoinsModel() {
    return (CoinsModel) this;
  }

  @Override
  public CoinsModel getCoinsModel() {
    return (CoinsModel) this;
  }

  @Override
  public ExpertCoinsModel asExpertCoinsModel() {
    return (ExpertCoinsModel) this;
  }
}