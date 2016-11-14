package nl.coinsweb.sdk.owlgenerator;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import nl.coinsweb.sdk.ExpertCoinsModel;
import nl.coinsweb.sdk.Namespace;
import nl.coinsweb.sdk.apolda.language.Language;
import nl.coinsweb.sdk.apolda.ontology.PropertyDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class ClassGenerateEngine {

  protected static final Logger log = LoggerFactory.getLogger(ClassGenerateEngine.class);

  /*
   * Configuration
   */
  private String targetFolder;
  private String classTemplateFilePath = "Class.clazz";
  private String interfaceTemplateFilePath = "Interface.clazz";
  private String classNamePostFix = "";
  private String interfaceNamePostFix = "Class";
  private Template classTemplate;
  private Template interfaceTemplate;


  private List<String> subjectIgnoreList = Arrays.asList(new String[]{RDF.getURI(), RDFS.getURI(), OWL.getURI(), XSD.getURI()});
  private List<String> predicateIgnoreList = Arrays.asList(new String[]{RDF.getURI(), RDFS.getURI(), OWL.getURI(), XSD.getURI()});


  public static final boolean useDosNL = false;
  public static String m_nl = "\n";
  public void setTargetFolder(String folder) {
    this.targetFolder = folder;
  }

  HashMap<String, String> preRunClassList = new HashMap<>();

  Map<String, String> namespaceToPackage = new HashMap<>();

  Set<String> fullyQualifiedJavaClasses = new HashSet<>();


  Map<String, String> simpleTypeMap = new HashMap<>();
  private String simplify(String objectKey) {
    if(simpleTypeMap.containsKey(objectKey)) {
      return simpleTypeMap.get(objectKey);
    }
    return objectKey;
  }

  public void addNamespace(String rdfNamespace, String javaPackageName) {
    log.info("Adding to namespaceToPackage map "+rdfNamespace+" / "+javaPackageName);
    if(!subjectIgnoreList.contains(rdfNamespace)) {
      namespaceToPackage.put(rdfNamespace, javaPackageName);
    }
  }


  Map<String, Object> currentClass;
  String currentClassName;
  String currentPackage;
  Set<PropBlock> literalProperties;
  Set<PropBlock> objectProperties;
  Set<String> imports;
  Set<String> parents;
  HashMap<String, String> individualsUris;     // uri, CAPITALISED_NAME


  Configuration cfg = new Configuration();

  private List<String> sourceFileNames = new ArrayList<>();
  public  List<String> getSourceFileNames() {
    return sourceFileNames;
  }

  public ClassGenerateEngine() {

    simpleTypeMap.put("Float", "float");
    simpleTypeMap.put("Integer", "int");
    simpleTypeMap.put("BigInteger", "int");
    simpleTypeMap.put("Double", "double");

    cfg.setClassForTemplateLoading(ClassGenerateEngine.class, "/");

    try {
      classTemplate = cfg.getTemplate(classTemplateFilePath);
      interfaceTemplate = cfg.getTemplate(interfaceTemplateFilePath);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }





  // The main function that calls all the above
  public Map<String, String> process(ExpertCoinsModel model, List<String> sourceFileNames) {


    this.sourceFileNames = sourceFileNames;


    int preCount = 0;
    ExtendedIterator<OntClass> clazzIterator = model.listOntClasses();
    while (clazzIterator.hasNext()) {
      OntClass clazz = clazzIterator.next();
      if (clazz.isAnon() || subjectIgnoreList.contains(clazz.asResource().getNameSpace())) {
        continue;
      }

      preRunClassList.put(clazz.getURI(), Language.getLabel(clazz));

      if(!namespaceToPackage.containsKey(clazz.asResource().getNameSpace())) {
        String javaPackageName = Utils.namespaceUriToPackage(clazz.asResource().getNameSpace());
        if(javaPackageName != null) {
          addNamespace(clazz.asResource().getNameSpace(), javaPackageName);
        } else {
          log.warn("Failed to process class "+clazz.getURI());
        }
      }

      try {
        System.out.write(("\rpre-visit all classes " + (++preCount)).getBytes());
      } catch (IOException e) {
        log.error(e.getMessage(), e);
      }
    }
    System.out.print("\n");



    // Iterate over all classes
    int realCount = 0;
    clazzIterator = model.listOntClasses();
    while (clazzIterator.hasNext()) {
      OntClass clazz = clazzIterator.next();
      if (clazz.isAnon() || subjectIgnoreList.contains(clazz.asResource().getNameSpace())) {
        continue;
      }

      if(!processClass(model, clazz)) {
        log.warn("Skipping processing class "+clazz.asResource().getURI());
        continue;
      }


      currentClass.put("imports", imports);
      currentClass.put("literalProperties", literalProperties);
      currentClass.put("objectProperties", objectProperties);
      currentClass.put("parents", parents);
      currentClass.put("individuals", individualsUris);


      writeClassFile(classTemplate, currentPackage, currentClass, targetFolder, currentClassName, classNamePostFix);
      writeClassFile(interfaceTemplate, currentPackage, currentClass, targetFolder, currentClassName, interfaceNamePostFix);



      try {
        System.out.write(("\rprocess classes " + (++realCount)).getBytes());
      } catch (IOException e) {
        log.error(e.getMessage(), e);
      }
    }
    System.out.print("\n");






    return namespaceToPackage;
  }












  public boolean processClass(ExpertCoinsModel model, OntClass clazz) {


    if(clazz.isAnon()) {
      return false;
    }

    if(!namespaceToPackage.containsKey(clazz.asResource().getNameSpace())) {

      log.warn("Class should have been in namespaceToPackage list: "+clazz.getURI());
      return false;
    }


    currentClass = new HashMap<>();

    currentPackage = namespaceToPackage.get(clazz.asResource().getNameSpace());

    literalProperties = new HashSet();
    objectProperties = new HashSet();
    imports = new HashSet<>();
    parents = new HashSet<>();
    individualsUris = new HashMap<>();

    ExtendedIterator<? extends OntResource> individuals = clazz.listInstances(false);
    while(individuals.hasNext()) {
      OntResource individual = individuals.next();
      if(individual.isResource())  {

        String individualUri = individual.asResource().getURI();
        String capitalisedName = Utils.capitalise(individual.asResource().getLocalName());
        individualsUris.put(individualUri, capitalisedName);
      }
    }



    currentClass.put("classUri", clazz.asResource().getURI());
    currentClass.put("sourceFileNames", getSourceFileNames());

    String label = Utils.cleanUp(preRunClassList.get(clazz.asResource().getURI()));
    currentClass.put("label", label);
    String comment = Utils.cleanUp(clazz.getComment(null));
    currentClass.put("comment", comment);

    String className = Utils.asLegalJavaID( clazz.asResource().getLocalName(), true );
    String classLabelName = Utils.asLegalJavaID( label, true );
    if(label != null && !label.isEmpty() && !classLabelName.isEmpty()) {
      className = classLabelName;
    }

    parents.add(className + interfaceNamePostFix);
    ExtendedIterator<OntClass> superClasses = clazz.listSuperClasses(false);
    while(superClasses.hasNext()) {
      OntClass superClass = superClasses.next();
      if(superClass.isResource() && preRunClassList.containsKey(superClass.asResource().getURI())) {


        String parentClassName = Utils.asLegalJavaID( superClass.asResource().getLocalName(), true );
        String parentClassLabel = Utils.cleanUp(preRunClassList.get(superClass.asResource().getURI()));
        String parentClassLabelName = Utils.asLegalJavaID( parentClassLabel, true );
        if(parentClassLabel != null && !parentClassLabel.isEmpty() && !parentClassLabelName.isEmpty()) {
          parentClassName = parentClassLabelName;
        }





        parents.add(parentClassName+interfaceNamePostFix);
        Namespace parentNamespace = new Namespace(superClass.asResource().getURI());
        if(!namespaceToPackage.get(parentNamespace).equals(currentPackage)) {
          imports.add(namespaceToPackage.get(parentNamespace) + "." + parentClassName+interfaceNamePostFix);
          imports.add(namespaceToPackage.get(parentNamespace) + "." + parentClassName + classNamePostFix);
        }
      }
    }

    String fullyQualifiedName = currentPackage+"."+className;
    if(fullyQualifiedJavaClasses.contains(fullyQualifiedName.toLowerCase())) {
      log.warn("Already mapped to "+fullyQualifiedName.toLowerCase()+" (this time: "+clazz.asResource().getURI()+"), skipping");
      return false;
    }
    fullyQualifiedJavaClasses.add(fullyQualifiedName.toLowerCase());

    // Build the data-model
    currentClass.put("date", new SimpleDateFormat("dd MMM yyyy HH:mm").format(new Date()));
    currentClass.put("package", "package " + currentPackage);
    currentClass.put("nl", m_nl);


    // Push the local bindings for the substitution onto the stack
    currentClass.put("className", className);
    currentClassName = className;




    processPropertyDeclarationsSparql(model, clazz.getURI());

    return true;
  }








  public void processPropertyDeclarationsSparql(ExpertCoinsModel model, String clazz) {


    Iterator<PropertyDeclaration> results = model.listPropertyDeclarations(clazz);


    // Output query results
    while(results.hasNext()) {


      PropertyDeclaration declaration = results.next();
      if(declaration==null || predicateIgnoreList.contains(new Namespace(declaration.getPropertyUri()))) {
        continue;
      }

      String propertyClassName = Utils.asLegalJavaID(declaration.getPropertyName(), true);

      if(declaration.getPropertyLabel() != null) {
        String propertyLabelName = Utils.asLegalJavaID(declaration.getPropertyLabel(), true);
        if (propertyLabelName != null && !propertyLabelName.isEmpty()) {
          propertyClassName = propertyLabelName;
        }
      }


      if(!declaration.hasRange()) {


        literalProperties.add(new PropBlock(declaration.isSingle(), declaration.getPropertyUri(), "String", propertyClassName));
      } else {
        String rangeClassName = Utils.asLegalJavaID(declaration.getRangeName(), true);
        if (preRunClassList.containsKey(declaration.getRangeUri())) {
          String rangeLabelName = Utils.asLegalJavaID(preRunClassList.get(declaration.getRangeUri()), true);
          if (rangeLabelName != null && !rangeLabelName.isEmpty()) {
            rangeClassName = rangeLabelName;
          }
        }

        RDFDatatype datatype = TypeMapper.getInstance().getTypeByName(declaration.getRangeUri());


        if(datatype != null) {

          if("http://www.w3.org/2001/XMLSchema#anySimpleType".equals(datatype.getURI())) {

            literalProperties.add(new PropBlock(declaration.isSingle(), declaration.getPropertyUri(), "XSDAnySimpleTypeLiteral", propertyClassName));
            imports.add("XSDAnySimpleTypeLiteral");

          } else if(XSD.integer.getURI().equals(datatype.getURI())) {

            literalProperties.add(new PropBlock(declaration.isSingle(), declaration.getPropertyUri(), "int", "Integer", propertyClassName));
            imports.add("java.lang.Integer");

//          } else if("http://www.w3.org/2001/XMLSchema#string".equals(datatype.getURI())) {
//
//            literalProperties.add(new PropBlock(declaration.isSingle(), declaration.getPropertyUri(), "String", propertyClassName));

          } else if("http://www.w3.org/2001/XMLSchema#dateTime".equals(datatype.getURI())) {

            literalProperties.add(new PropBlock(declaration.isSingle(), declaration.getPropertyUri(), "Date", propertyClassName));
            imports.add("java.util.Date");

//          } else if("http://www.w3.org/2001/XMLSchema#string".equals(datatype.getURI())) {
//
//            literalProperties.add(new PropBlock(declaration.isSingle(), declaration.getPropertyUri(), "String", propertyClassName));

          } else {

            Class javaClass = datatype.getJavaClass();
            if (javaClass != null) {

              literalProperties.add(new PropBlock(declaration.isSingle(), declaration.getPropertyUri(), simplify(javaClass.getSimpleName()), javaClass.getSimpleName(), propertyClassName));
              imports.add(javaClass.getCanonicalName());
            } else {
              log.debug("No java class found by TypeMapper/RDFDatatype for range uri " + declaration.getRangeUri() + ", using String.");
              literalProperties.add(new PropBlock(declaration.isSingle(), declaration.getPropertyUri(), "String", propertyClassName));
            }
          }



        } else if (namespaceToPackage.containsKey(new Namespace(declaration.getRangeUri())) &&
            preRunClassList.containsKey(declaration.getRangeUri())) {




          if(rangeClassName.equals(currentClassName)) {
            rangeClassName = namespaceToPackage.get(new Namespace(declaration.getRangeUri())) + "." + rangeClassName;
          } else if(!namespaceToPackage.get(new Namespace(declaration.getRangeUri())).equals(currentPackage)) {
            imports.add(namespaceToPackage.get(new Namespace(declaration.getRangeUri())) + "." + rangeClassName+interfaceNamePostFix);
            imports.add(namespaceToPackage.get(new Namespace(declaration.getRangeUri())) + "." + rangeClassName+classNamePostFix);
          }

          objectProperties.add(new PropBlock(declaration.isSingle(), declaration.getPropertyUri(), rangeClassName, propertyClassName));

        } else {
          log.info("Not mapping " + declaration.getPropertyUri() + " " + declaration.getRangeUri());

          if(!namespaceToPackage.containsKey(new Namespace(declaration.getRangeUri()))) {
            log.info("(reason :) namespace " + new Namespace(declaration.getRangeUri()) + " not contained in namespaceToPackage");
          }
          if(!preRunClassList.containsKey(declaration.getRangeUri())) {
            log.info("(reason :) range uri " + declaration.getRangeUri() + " not contained in preRunClassList");
          }

        }

      }



    }
  }



  public void writeClassFile(Template template, String packageName, Map<String, Object> data, String outputFolder, String className, String classNamePostFix) {

    // Freemarker configuration object

    try {

      // Load template from source folder

      // Check for DOS line endings
      if (useDosNL) {
        m_nl = "\r\n";
      }


      // Check for package name
      if (packageName != null) {
        String packagePath = "";

        // Build the package path (e.g. com.foo.bar -> /com/foo/bar)
        for (String p: packageName.split( "\\." )) {
          packagePath = packagePath + File.separator + p;
        }

        if (!outputFolder.endsWith( packagePath )) {
          outputFolder = outputFolder + packagePath;
        }
      }

      File out = new File( outputFolder );

      // Create the directory if needed
      if (!out.exists() && !outputFolder.endsWith( ".java" )) {
        out.mkdirs();
      }

      // Create a file in this directory named classname.java
      if (out.isDirectory()) {
        String fileName = outputFolder + File.separator + className + classNamePostFix + ".java";
        out = new File( fileName );
      }

      // File output
      PrintStream printStream = new PrintStream( new FileOutputStream( out ) );
      Writer file = new PrintWriter(new OutputStreamWriter(printStream, "UTF-8"));
      template.process(data, file);
      file.flush();
      file.close();





    } catch (IOException e) {
      e.printStackTrace();
    } catch (TemplateException e) {
      e.printStackTrace();
    }
  }


  // Freemarker will access these getters
  public class PropBlock {
    public Boolean single;
    public String relationUri;
    public String simpleReturnType;
    public String returnType;
    public String relationName;

    public PropBlock(Boolean single, String uri, String type, String name) {
      this.single = single;
      this.relationUri = uri;
      this.simpleReturnType = type;
      this.returnType = type;
      this.relationName = name;
    }

    public PropBlock(Boolean single, String uri, String simpleType, String type, String name) {
      this.single = single;
      this.relationUri = uri;
      this.simpleReturnType = simpleType;
      this.returnType = type;
      this.relationName = name;
    }


    public Boolean getSingle() {
      return single;
    }

    public String getRelationUri() {
      return relationUri;
    }

    public String getSimpleReturnType() {
      return simpleReturnType;
    }
    public String getReturnType() {
      return returnType;
    }

    public String getRelationName() {
      return relationName;
    }

    @Override
    public int hashCode() {
      return (relationUri+returnType).hashCode();
    }

    @Override
    public boolean equals(Object obj) {

      if(!(obj instanceof PropBlock)) {
        return false;
      }

      PropBlock castObj = (PropBlock) obj;
      if(castObj.getSingle() != getSingle()) {
        return false;
      }
      if(!castObj.getRelationUri().equals(getRelationUri())) {
        return false;
      }
      if(!castObj.getReturnType().equals(getReturnType())) {
        return false;
      }
      if(!castObj.getRelationName().equals(getRelationName())) {
        return false;
      }
      return true;
    }
  }
}
