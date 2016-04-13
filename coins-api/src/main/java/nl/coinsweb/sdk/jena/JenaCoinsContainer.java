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
import com.hp.hpl.jena.ontology.*;
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
import org.apache.jena.riot.RDFDataMgr;
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
public abstract class JenaCoinsContainer implements CoinsContainer, CoinsModel, ExpertCoinsModel {

  private static final Logger log = LoggerFactory.getLogger(JenaCoinsContainer.class);


  private String internalRef;      // pointer for use in FileManager

  private HashMap<String, File> attachments = new HashMap<>();
  private HashMap<Namespace, File> availableLibraryFiles = new HashMap<>();

  // Use the file name as key for all models
  protected HashMap<String, ExpertCoinsModel> coinsModels = new HashMap<>();

  // stuff from model

//  private OntModelSpec reasoner = OntModelSpec.OWL_MEM_MICRO_RULE_INF;
  private OntModelSpec reasoner = OntModelSpec.OWL_MEM_RDFS_INF;
//  private OntModelSpec reasoner = OntModelSpec.OWL_MEM;

  protected Dataset dataset;

  private CoinsParty party;
  private File originalContainerFile;


  private File rdfFile;
  private String rdfFileName = "content.rdf";
  private Namespace instanceNamespace;
  private Model instanceModel;

  private File woaFile;
  private String woaFileName = "woa.rdf";
  public Namespace woaNamespace = new Namespace("http://woa.coinsweb.nl/");
  private Model woaModel;


  private Map<Namespace, Model> libraryModels;


  private ArrayList<Injector> injectors;

  private String containerId;





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

    this.party = party;
    this.party.setModel(this);

    this.instanceNamespace = new Namespace(namespace);

    this.internalRef = FileManager.newCoinsContainer();
    this.containerId = UUID.randomUUID().toString();

    doModelPreparation();
    this.dataset = createDataset();
    this.libraryModels = new HashMap<>();

    // Prepare an empty dataset
    if(this.instanceNamespace == null) {
      throw new InvalidNamespaceException("Please provide a namespace if an empty CoinsModel is constructed.");
    }
    try {
      new URI(this.instanceNamespace.toString());
    } catch (URISyntaxException e) {
      throw new InvalidNamespaceException("Please provide a valid namespace, problems with "+this.instanceNamespace +".", e);
    }

    // Create empty model
    instanceModel = ModelFactory.createDefaultModel();
    instanceModel.setNsPrefix("", instanceNamespace.toString());
    instanceModel.setNsPrefix("coins2", "http://www.coinsweb.nl/cbim-2.0.rdf#");
    addOntologyHeader();

    dataset.addNamedModel(instanceNamespace.toString(), instanceModel);
    log.info("Added instance model with name "+ instanceNamespace);


    woaModel = ModelFactory.createDefaultModel();


    // Add core model
    InputStream fileStream = getClass().getResourceAsStream("/cbim-2.0.rdf");
    Namespace coreModelNamespace = FileManager.copyAndRegisterLibrary(fileStream, "cbim-2.0.rdf", availableLibraryFiles);
    addImport(null, coreModelNamespace.toString(), loadCoreModels, loadCoreModels, false);

    // Add core model
    fileStream = getClass().getResourceAsStream("/units-2.0.rdf");
    Namespace unitsNamespace = FileManager.copyAndRegisterLibrary(fileStream, "units-2.0.rdf", availableLibraryFiles);
    addImport(null, unitsNamespace.toString(), loadCoreModels, loadCoreModels, false);

    // Add core model
    fileStream = getClass().getResourceAsStream("/COINSWOA.rdf");
    Namespace woaNamespace = FileManager.copyAndRegisterLibrary(fileStream, "COINSWOA.rdf", availableLibraryFiles);
    addImport(null, woaNamespace.toString(), loadCoreModels, loadCoreModels, false);


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

    this.party = party;
    this.party.setModel(this);

    this.instanceNamespace = new Namespace(namespace);

    doModelPreparation();

    // Load an existing
    this.load(filePath);



    initInjectors();
  }


  protected abstract void doModelPreparation();
  protected abstract Dataset createDataset();

  private void initInjectors() {
    this.injectors = new ArrayList<>();
    this.injectors.add(new AttachmentInjector());
    this.injectors.add(new WOAInjector(woaModel));
  }


  /**
   * CoinsContainer interface
   */

  @Override
  public String getContainerId() {
    return containerId;
  }

  @Override
  public void load(String sourceFile) {

    // Start with a clean sheet
    this.dataset = createDataset();
    this.libraryModels = new HashMap<>();

    File file = new File(sourceFile);

    if(!file.exists()) {
      throw new CoinsFileNotFoundException("Supplied file does not exist.");
    }

    // See what file type it is
    if(file.getName().endsWith(".ccr") || file.getName().endsWith(".zip")) {

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
        if(this.instanceNamespace == null) {
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
      InputStream fileStream = getClass().getResourceAsStream("/cbim-2.0.rdf");
      FileManager.copyAndRegisterLibrary(fileStream, "cbim-2.0.rdf", availableLibraryFiles);
      fileStream = getClass().getResourceAsStream("/units-2.0.rdf");
      FileManager.copyAndRegisterLibrary(fileStream, "units-2.0.rdf", availableLibraryFiles);
      fileStream = getClass().getResourceAsStream("/COINSWOA.rdf");
      FileManager.copyAndRegisterLibrary(fileStream, "COINSWOA.rdf", availableLibraryFiles);


      log.info("Create CoinsContainer from rdf file: " + file.getName());

      this.originalContainerFile = null;
      this.internalRef = FileManager.newCoinsContainer();

      this.rdfFile = file;
      this.rdfFileName = file.getName();
    }


    // Create model and read the instance base model
    instanceModel = ModelFactory.createDefaultModel();
    if(rdfFile != null) {
      instanceModel.read(this.rdfFile.toURI().toString());
      instanceNamespace = FileManager.getLeadingNamespace(this.rdfFile, instanceModel);
    }
    instanceModel.setNsPrefix("", instanceNamespace.toString());
    instanceModel.setNsPrefix("coins2", "http://www.coinsweb.nl/cbim-2.0.rdf#");
    dataset.addNamedModel(instanceNamespace.toString(), instanceModel);
    log.info("Added instance model with name "+ instanceNamespace);

    Statement searchResult = instanceModel.getProperty(new ResourceImpl(this.instanceNamespace.withoutHash()), new PropertyImpl("http://www.coinsweb.nl/cbim-2.0.rdf#containerId"));
    if(searchResult!=null && searchResult.getObject() != null) {
      this.containerId = searchResult.getObject().asLiteral().getLexicalForm();
    } else {
      this.containerId = UUID.randomUUID().toString();
      log.warn("No containerId found, setting it to: "+containerId+".");
      instanceModel.add(new StatementImpl(
          new ResourceImpl(this.instanceNamespace.withoutHash()),
          new PropertyImpl("http://www.coinsweb.nl/cbim-2.0.rdf#containerId"),
          instanceModel.createTypedLiteral(containerId)));
    }

    log.info("Found containerId "+this.containerId);
    addNamedModelForImports(instanceModel);

    // Create woa model and read the woa base model
    woaModel = ModelFactory.createDefaultModel();
    if(woaFile != null) {
      woaModel.read(this.woaFile.toURI().toString());
      woaNamespace = FileManager.getLeadingNamespace(this.woaFile, woaModel);
    }
    woaModel.setNsPrefix("", woaNamespace.toString());
    dataset.addNamedModel(woaNamespace.toString(), woaModel);
    log.info("Added woa model with name "+ woaNamespace);

    initInjectors();
  }


  @Override
  public void export() {
    exportOwlModel();
    if(originalContainerFile != null) {
      FileManager.zip(internalRef, originalContainerFile);
    } else {
      throw new CoinsFileNotFoundException("Can not export, no initial .ccr file was specified.");
    }
  }

  @Override
  public void export(String target) {
    exportOwlModel();
    File targetFile = new File(target);
    FileManager.zip(internalRef, targetFile);
  }


  public void exportOwlModel() {
    File file = FileManager.createRdfFile(internalRef, rdfFileName);
    exportOwlModel(file.getPath(), RDFFormat.RDFXML);
  }
  @Override
  public void exportOwlModel(String target) {
    exportOwlModel(target, RDFFormat.RDFXML);
  }
  public File exportOwlModel(String toFileName, RDFFormat format) {

    try {

      File file = new File(toFileName);
      file.getParentFile().mkdirs();
      file.createNewFile(); // create if not already there


      OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
      writeInstanceModelToFile(out, format);
      log.info("exported to " + file.getAbsolutePath());
      return file;


    } catch (IOException e) {
      throw new CoinsFileNotFoundException("Problem exporting to: "+toFileName, e);
    }
  }
  public String exportAsString() {
    return exportAsString(RDFFormat.RDFXML);
  }
  public String exportAsString(RDFFormat format) {
    return writeInstanceModelToString(format);
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
  public Set<String> getAttachments() {
    return attachments.keySet();
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
    RuntimeCoinsObject createdProperty = documentReference.createProperty("http://www.coinsweb.nl/cbim-2.0.rdf#filePath", "http://www.coinsweb.nl/cbim-2.0.rdf#StringProperty");
    createdProperty.setLiteralValue("http://www.coinsweb.nl/cbim-2.0.rdf#datatypeValue", absoluteTempPath.getFileName().toString());
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
  public void setInstanceNamespace(String namespace) {
    this.instanceNamespace = new Namespace(namespace);
  }

  @Override
  public String getInstanceNamespace() {
    return this.instanceNamespace.toString();
  }

  @Override
  public boolean hasImport(String namespace) {
    if(!hasOntologyHeader()) {
      return false;
    }
    return instanceModel.contains(new ResourceImpl(this.instanceNamespace.withoutHash()), OWL.imports, new ResourceImpl(namespace));
  }


  @Override
  public void addImport(String filePath, String namespace, boolean addAsImport, boolean tryToLoad, boolean addToDoc) {

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
      Statement statement = new StatementImpl(new ResourceImpl(this.instanceNamespace.withoutHash()), OWL.imports, new ResourceImpl(namespaceImpl.toString()));
      instanceModel.add(statement);
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
    instanceModel.getGraph().remove(
        new ResourceImpl(this.instanceNamespace.withoutHash()).asNode(),
        new PropertyImpl("http://www.coinsweb.nl/cbim-2.0.rdf#creator").asNode(),
        Node.ANY);
    instanceModel.add(new StatementImpl(
        new ResourceImpl(this.instanceNamespace.withoutHash()),
        new PropertyImpl("http://www.coinsweb.nl/cbim-2.0.rdf#creator"),
        new ResourceImpl(party.getUri())));
  }

  @Override
  public Iterator<String> listClasses() {
    ArrayList<String> buffer = new ArrayList<>();
    ExtendedIterator<OntClass> iterator = getUnionJenaOntModel().listClasses();
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
    if(libraryModels.containsKey(key)) {

      ExtendedIterator<OntClass> iterator = asOntModel(libraryModels.get(key)).listClasses();
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
    ArrayList<String> buffer = new ArrayList<>();
    ResIterator iterator = instanceModel.listSubjectsWithProperty(RDF.type);
    while(iterator.hasNext()) {
      String instanceUri = iterator.next().getURI();
      buffer.add(instanceUri);
    }
    return buffer.iterator();
  }

  @Override
  public <T extends CoinsObject> Iterator<T> listIndividuals(Class<T> objectClass) {

    ArrayList<T> buffer = new ArrayList<>();
    try {

      String classUri = (String) objectClass.getField("classUri").get(String.class);

      for(Triple triple : listInstances(classUri).toList()) {

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
    ArrayList<String> buffer = new ArrayList<>();
    for(Triple triple : listInstances(classUri).toList()) {

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

      ExtendedIterator<Individual> individuals = getUnionJenaOntModel().listIndividuals(new ResourceImpl(classUri));
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

    ExtendedIterator<Individual> individuals = getUnionJenaOntModel().listIndividuals(new ResourceImpl(classUri));
    while(individuals.hasNext()) {
      Individual individual = individuals.next();
      buffer.add(individual.getURI());

    }
    return buffer;
  }

  @Override
  public RuntimeCoinsObject getIndividual(String individualUri) {
    Individual individual = getJenaOntModel().getIndividual(individualUri);
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

    Query query = QueryFactory.create(sparqlQuery);

    // Execute the query and obtain results
    QueryExecution qe = QueryExecutionFactory.create(query, dataset);
    ResultSet results = qe.execSelect();

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

    // Important - free up resources used running the query
    qe.close();

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

    ExtendedIterator<OntClass> classes = getUnionJenaOntModel().getIndividual(instanceUri).listOntClasses(false);
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
  public void removeType(String instanceUri, String classUri) {
    removeStatement(instanceUri, RDF.type.getURI(), classUri);
  }

  @Override
  public void addCreator(String instanceUri, CoinsParty party) {
    addStatement(instanceUri, "http://www.coinsweb.nl/cbim-2.0.rdf#creator", party.getUri());
  }
  @Override
  public void addCreatedNow(String instanceUri) {
    setLiteralValue(instanceUri, "http://www.coinsweb.nl/cbim-2.0.rdf#creationDate", new Date());
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
    QueryExecution queryExecution = QueryExecutionFactory.create(queryString, Syntax.syntaxSPARQL_11, getUnionModel());
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
//    Iterator<ApoPropertyDeclaration> iterator =  new JenaPropertyDeclarationIterator(classUri, asOntModel(getUnionModel()), propertyTypeClassUri);
    Iterator<PropertyDeclaration> iterator =  new SparqlPropertyDeclarationIterator(classUri, asOntModel(getUnionModel()), propertyTypeClassUri);
    while(iterator.hasNext()) {
      buffer.add(iterator.next().getPropertyUri());
    }
    return buffer.iterator();
  }
  @Override
  public Iterator<CoinsObject> listProperties(String instanceUri) {

    ArrayList<CoinsObject> buffer = new ArrayList<>();

    StmtIterator iterator = asOntModel(instanceModel).getIndividual(instanceUri).listProperties();
    while(iterator.hasNext()) {

      Statement statement = iterator.nextStatement();

      RDFNode object = statement.getObject();

      if(object.isResource() && !object.isAnon()) {

        Individual property = asOntModel(instanceModel).getIndividual(object.asResource().getURI());
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

    ArrayList<T> buffer = new ArrayList<>();
    try {
      String propertyTypeClassUri =  (String) propertyTypeClass.getField("classUri").get(String.class);
      StmtIterator iterator = asOntModel(instanceModel).getIndividual(instanceUri).listProperties();
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

    ArrayList<RuntimeCoinsObject> buffer = new ArrayList<>();
    StmtIterator iterator = asOntModel(instanceModel).getIndividual(instanceUri).listProperties();
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

    ArrayList<T> buffer = new ArrayList<>();
    try {
      String propertyTypeClassUri =  (String) propertyTypeClass.getField("classUri").get(String.class);
      StmtIterator iterator = asOntModel(instanceModel).getIndividual(instanceUri).listProperties(new PropertyImpl(predicate));
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

    ArrayList<RuntimeCoinsObject> buffer = new ArrayList<>();
    StmtIterator iterator = asOntModel(instanceModel).getIndividual(instanceUri).listProperties(new PropertyImpl(predicate));
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

    RuntimeCoinsObject object = new RuntimeCoinsObject(this, propertyClassUri);
    addStatement(instanceUri, predicateUri, object.getUri());

    return object;
  }
  @Override
  public <T extends CoinsObject> T createProperty(String instanceUri, String predicateUri, Class<T> propertyClass) {

    try {
      Constructor constructor = propertyClass.getConstructor(ExpertCoinsModel.class);
      T constructed = (T) constructor.newInstance(this);

      addStatement(instanceUri, predicateUri, constructed.getUri());
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

    Resource subject = new ResourceImpl(instanceUri);
    Resource object = new ResourceImpl(property.getUri());

    // Find the predicate
    ExtendedIterator<Triple> predicateIterator = instanceModel.getGraph().find(subject.asNode(), Node.ANY, object.asNode());
    if(!predicateIterator.hasNext()) {
      throw new CoinsPropertyNotFoundException("Property not found, so not able to remove it.");
    }
    Property predicate = new PropertyImpl(predicateIterator.next().getPredicate().getURI());

    // Remove the link to this property
    Statement statement = new StatementImpl(subject, predicate, object);
    log.info("Removing link to property " + statement);
    removeStatement(subject.getURI(), predicate.getURI(), object.getURI());



    // Remove the property object and all its content
    removeIndividualAndProperties(property.getUri());
  }
  @Override
  public <T> T getLiteralValue(String subject, String predicate, Class<T> clazz) {

    RDFDatatype datatype = TypeMapper.getInstance().getTypeByClass(clazz);

    NodeIterator result = listObjectsOfProperty(subject, predicate);
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

          OntProperty prop = getUnionJenaOntModel().getOntProperty(predicate);
          Individual instance = getJenaOntModel().getIndividual(subject);
          if(prop == null || instance == null) {
            throw new CoinsPropertyNotFoundException("The predicate "+predicate+" could not be found as Property when requesting literal value.");
          }
          XSDDateTime date = (XSDDateTime) instance.getProperty(prop).getLiteral().getValue();
          return (T) new Date(date.asCalendar().getTimeInMillis());


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

    ArrayList<T> buffer = new ArrayList<>();

    RDFDatatype datatype = TypeMapper.getInstance().getTypeByClass(clazz);

    NodeIterator result = listObjectsOfProperty(subject, predicate);
    while(result!=null && result.hasNext()) {

      RDFNode single = result.next();
      if(single.isLiteral()) {
        if(clazz.equals(XSDAnySimpleTypeLiteral.class) &&
            single.asLiteral().getDatatypeURI().equals("http://www.w3.org/2001/XMLSchema#anySimpleType")) {

          buffer.add((T) new XSDAnySimpleTypeLiteral(single.asLiteral()));

        } else if(clazz.equals(Date.class)) {

          OntProperty prop = getJenaOntModel().getOntProperty(predicate);
          Individual instance = getJenaOntModel().getIndividual(subject);
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

    removeStatement(subject, predicate);

    if(object instanceof Date) {

      GregorianCalendar calendar = new GregorianCalendar();
      calendar.setTime((Date)object);
      XSDDateTime dateTime = new XSDDateTime(calendar);
      Literal propValue = getJenaOntModel().createTypedLiteral(dateTime, XSDDatatype.XSDdateTime);
      OntProperty prop = getUnionJenaOntModel().getOntProperty(predicate);
      Individual individual = getJenaOntModel().getIndividual(subject);
      individual.setPropertyValue(prop, propValue);

    } else {
      addStatement(subject, predicate, instanceModel.createTypedLiteral(object));
    }
  }
  @Override
  public <T> void addLiteralValue(String subject, String predicate, T object) {

    if(object instanceof Date) {

      GregorianCalendar calendar = new GregorianCalendar();
      calendar.setTime((Date)object);
      XSDDateTime dateTime = new XSDDateTime(calendar);
      Literal propValue = getJenaOntModel().createTypedLiteral(dateTime, XSDDatatype.XSDdateTime);
      OntProperty prop = getUnionJenaOntModel().getOntProperty(predicate);
      Individual individual = getJenaOntModel().getIndividual(subject);
      individual.setPropertyValue(prop, propValue);

    } else {
      addStatement(subject, predicate, instanceModel.createTypedLiteral(object));
    }
  }


  @Override
  public <T extends CoinsObject> T getObject(String subject, String predicate, Class<T> clazz) {

    NodeIterator result = listObjectsOfProperty(subject, predicate);

    while(result!=null && result.hasNext()) {
      RDFNode single = result.next();

      if(single.isResource()) {
        try {
          Constructor constructor = clazz.getConstructor(ExpertCoinsModel.class, String.class);
          return (T) constructor.newInstance(this, single.asResource().getURI());
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

    ArrayList<T> buffer = new ArrayList<>();

    NodeIterator result = listObjectsOfProperty(subject, predicate);
    if(result!=null && result.hasNext()) {
      for(RDFNode node : result.toList()) {
        if(node.isResource()) {

          try {
            Constructor constructor = clazz.getConstructor(ExpertCoinsModel.class, String.class);
            buffer.add((T) constructor.newInstance(this, node.asResource().getURI()));
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
    removeStatement(subject, predicate);
    addStatement(subject, predicate, object.getUri());
  }
  @Override
  public void setObject(String subject, String predicate, String objectUri) {
    removeStatement(subject, predicate);
    addStatement(subject, predicate, objectUri);
  }
  @Override
  public void addObject(String subject, String predicate, CoinsObject object) {
    addStatement(subject, predicate, object.getUri());
  }
  @Override
  public void addObject(String subject, String predicate, String objectUri) {
    addStatement(subject, predicate, objectUri);
  }

  @Override
  public void removeIndividualAndProperties(String instanceUri) {

    log.info("Removing individual and all it's properties for uri: "+instanceUri+".");

    // Iterate over all properties an delete them
    Iterator<RuntimeCoinsObject> properties = listProperties(instanceUri, "http://www.coinsweb.nl/cbim-2.0.rdf#EntityProperty");
    while(properties.hasNext()) {
      removeProperty(instanceUri, properties.next());
    }

    // Remove all remaining statements mentioning the instanceUri directly (including type definition)
    ExtendedIterator<Triple> incoming = instanceModel.getGraph().find(Node.ANY, Node.ANY, new ResourceImpl(instanceUri).asNode());
    Set<Triple> collectIncoming = new HashSet<>();
    while(incoming.hasNext()) {
      collectIncoming.add(incoming.next());
    }
    for(Triple triple : collectIncoming) {
      removeStatement(triple.getSubject().getURI(), triple.getPredicate().getURI(), new ResourceImpl(instanceUri));
    }

    ExtendedIterator<Triple> outgoing = instanceModel.getGraph().find(new ResourceImpl(instanceUri).asNode(), Node.ANY, Node.ANY);
    Set<Triple> collectOutgoing = new HashSet<>();
    while(outgoing.hasNext()) {
      collectOutgoing.add(outgoing.next());
    }
    for(Triple triple : collectOutgoing) {
      removeStatement(new ResourceImpl(instanceUri).getURI(), triple.getPredicate().getURI());
    }

  }












  @Override
  public ExtendedIterator<OntClass> listOntClasses() {
    return asOntModel(getUnionModel()).listClasses();
  }

  @Override
  public Iterator<PropertyDeclaration> listPropertyDeclarations(String classUri) {
    return new SparqlPropertyDeclarationIterator(classUri, asOntModel(getUnionModel()));
//    return new JenaPropertyDeclarationIterator(clazz, asOntModel(getUnionModel()));
  }

  @Override
  public boolean hasOntologyHeader() {
    return instanceModel.contains(new StatementImpl(new ResourceImpl(this.instanceNamespace.withoutHash()), RDF.type, OWL.Ontology));
  }

  @Override
  public void addOntologyHeader() {

    // Add header itself
    log.info("use this as subject for ontology header "+this.instanceNamespace);
    instanceModel.add(new StatementImpl(new ResourceImpl(this.instanceNamespace.withoutHash()), RDF.type, OWL.Ontology));

    // Add creator and containerId
    instanceModel.add(new StatementImpl(
        new ResourceImpl(this.instanceNamespace.withoutHash()),
        new PropertyImpl("http://www.coinsweb.nl/cbim-2.0.rdf#creator"),
        new ResourceImpl(getActiveParty().getUri())));
    instanceModel.add(new StatementImpl(
        new ResourceImpl(this.instanceNamespace.withoutHash()),
        new PropertyImpl("http://www.coinsweb.nl/cbim-2.0.rdf#containerId"),
        instanceModel.createTypedLiteral(containerId)));

    // Add import statements
    for(Namespace key : libraryModels.keySet()) {
      log.info("add an imports statement to "+key.toString());
      instanceModel.add(new StatementImpl(new ResourceImpl(this.instanceNamespace.withoutHash()), OWL.imports, new ResourceImpl(key.toString())));
    }
  }

  @Override
  public void addStatement(String subject, String predicate, String object) {
    Statement statement = new StatementImpl(new ResourceImpl(subject), new PropertyImpl(predicate), new ResourceImpl(object));
    log.info("Adding statement " + statement);

    boolean permission = true;
    for(Injector injector : injectors) {
      permission &= injector.proposeWrite(this, subject, predicate, object);
    }
    if(permission) {
      instanceModel.add(statement);
    }
  }
  @Override
  public void addStatement(String subject, String predicate, RDFNode object) {
    Statement statement = new StatementImpl(new ResourceImpl(subject), new PropertyImpl(predicate), object);
    log.info("Adding statement "+statement);

    boolean permission = true;
    for(Injector injector : injectors) {
      permission &= injector.proposeWrite(this, subject, predicate, object.toString());
    }
    if(permission) {
      instanceModel.add(statement);
    }
  }

  @Override
  public void removeStatement(String subject, String predicate, String object) {
    Statement statement = new StatementImpl(new ResourceImpl(subject), new PropertyImpl(predicate), new ResourceImpl(object));
    log.info("Removing statement " + statement);

    boolean permission = true;
    for(Injector injector : injectors) {
      permission &= injector.proposeWrite(this, subject, predicate, object);
    }
    if(permission) {
      instanceModel.remove(statement);
    }
  }
  @Override
  public void removeStatement(String subject, String predicate, RDFNode object) {
    Statement statement = new StatementImpl(new ResourceImpl(subject), new PropertyImpl(predicate), object);
    log.info("Removing statement " + statement);

    boolean permission = true;
    for(Injector injector : injectors) {
      permission &= injector.proposeWrite(this, subject, predicate, object.toString());
    }
    if(permission) {
      instanceModel.remove(statement);
    }
  }
  public void removeStatement(String subject, String predicate) {
    log.info("Removing statement " + subject + " -> "+predicate+" -> any");

    boolean permission = true;
    for(Injector injector : injectors) {
      permission &= injector.proposeWrite(this, subject, predicate, null);
    }
    if(permission) {
      instanceModel.getGraph().remove(new ResourceImpl(subject).asNode(), new PropertyImpl(predicate).asNode(), Node.ANY);
    }
  }

  @Override
  public String generateUri() {
    return instanceNamespace + UUID.randomUUID().toString();
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
    log.info("distribute close");
    for (ExpertCoinsModel model : coinsModels.values()) {
      log.info("send close to " + model.getInstanceNamespace());
      model.close();
    }
    FileManager.destroy(this.internalRef);
  }









  public void setReasoner(OntModelSpec modelSpec) {
    reasoner = modelSpec;
  }

  private OntModel asOntModel(Model model) {

    // Set document manager policy file
    OntDocumentManager dm = new OntDocumentManager();
    OntModelSpec modelSpec = reasoner;
    modelSpec.setDocumentManager(dm);
    dm.setProcessImports(false);
    OntModel result = ModelFactory.createOntologyModel(modelSpec, model);

    return result;
  }

  public void addNamedModelForImports(Model model) {

    OntModel enrichedModel = asOntModel(model);

    for(String imp : enrichedModel.listImportedOntologyURIs()) {
      log.trace("need to load "+imp);
      Namespace namespace = new Namespace(imp);
      loadLibraryContent(namespace);
    }
  }

  private void loadLibraryContent(Namespace namespace) {

    try {

      // Check if the model was already loaded
      if(libraryModels.containsKey(namespace)) {
        return;
      }

      // Acquire an uri to a local file or online resolvable uri
      URI importUri = FileManager.getLibrary(this, internalRef, namespace.toURI());
      if(importUri == null) {
        log.info("❎ Unfortunately not found for internalRef " + internalRef + " and " + namespace.toString());
        return;
      }

      // Load the model
      Model libraryModel = ModelFactory.createDefaultModel();
      libraryModel.read(importUri.toString());
      libraryModels.put(namespace, libraryModel);
      dataset.addNamedModel(namespace.toString(), libraryModel);
      log.info("✅ Adding model with name " + namespace.toString());


      // Recursively add everything this import depended on
      addNamedModelForImports(libraryModel);


    } catch (RuntimeException e) {
      log.info("failed importing file linked to namespace " + namespace.toString());
    }
  }





  private Model getUnionModel() {

    Model unionModel = ModelFactory.createDefaultModel();
    unionModel.add(instanceModel);
    for(Namespace key : libraryModels.keySet()) {
      unionModel.add(libraryModels.get(key));
    }


    return unionModel;
  }









  private NodeIterator listObjectsOfProperty(String subject, String predicate) {

    Resource subj = new ResourceImpl(subject);
    Property pred = new PropertyImpl(predicate);

    if(instanceModel.contains(subj, pred)) {
      return instanceModel.listObjectsOfProperty(subj, pred);
    }
    return null;
  }
  private ExtendedIterator<Triple> listInstances(String classUri) {
    return instanceModel.getGraph().find(Node.ANY, RDF.type.asNode(), new ResourceImpl(classUri).asNode());
  }











  public void writeInstanceModelToFile(OutputStream output, RDFFormat format) {

    if(format == RDFFormat.RDFXML || format == RDFFormat.RDFXML_ABBREV ||
        format == RDFFormat.RDFXML_PLAIN || format == RDFFormat.RDFXML_PRETTY) {

      RDFWriter writer;
      if(format == RDFFormat.RDFXML_ABBREV) {
        writer = instanceModel.getWriter( "RDF/XML-ABBREV" );
      } else {
        writer = instanceModel.getWriter( "RDF/XML" );
      }
      writer.setProperty("xmlbase", getInstanceNamespace() );
      writer.write(instanceModel, output, null);

    } else {
      dataset.getNamedModel(instanceNamespace.toString());
      RDFDataMgr.write(output, instanceModel, format);
    }
  }

  public String writeInstanceModelToString(RDFFormat format) {

    log.trace("starting to export");


    ByteArrayOutputStream boas = new ByteArrayOutputStream();


    if(format == RDFFormat.RDFXML || format == RDFFormat.RDFXML_ABBREV ||
        format == RDFFormat.RDFXML_PLAIN || format == RDFFormat.RDFXML_PRETTY) {

      RDFWriter writer;
      if(format == RDFFormat.RDFXML_ABBREV) {
        writer = instanceModel.getWriter( "RDF/XML-ABBREV" );
      } else {
        writer = instanceModel.getWriter( "RDF/XML" );
      }
      writer.setProperty("xmlbase", getInstanceNamespace() );
      writer.write(instanceModel, boas, null);

    } else {
      dataset.getNamedModel(instanceNamespace.toString());
      RDFDataMgr.write(boas, instanceModel, format);
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

  public void writeFullToFile(OutputStream output, RDFFormat format) {
    RDFDataMgr.write(output, dataset, format);
  }












  public Model getWoaModel() {
    return woaModel;
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

  @Override
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
    return asOntModel(this.instanceModel);
  }

  @Override
  public OntModel getJenaOntModel(String namespace) {
    Model model = getJenaModel(namespace);
    if(model != null) {
      return asOntModel(model);
    }
    return null;
  }

  @Override
  public Model getUnionJenaModel() {
    return getUnionModel();
  }

  @Override
  public OntModel getUnionJenaOntModel() {
    return asOntModel(getUnionModel());
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