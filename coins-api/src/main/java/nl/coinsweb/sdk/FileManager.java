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
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import nl.coinsweb.sdk.exceptions.*;
import nl.coinsweb.sdk.jena.InMemGraphSet;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import org.apache.commons.lang3.RandomStringUtils;
import org.codehaus.plexus.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class FileManager {

  private static final Logger log = LoggerFactory.getLogger(FileManager.class);

  private static Path TEMP_ZIP_PATH;
  private static Path TEMP_LIB_PATH;


  private static final String[] INDEX_AS_RDF =  {".rdf", ".owl", ".ttl"};

  public static String DEFAULT_TEMP_FOLDER = "/tmp/";
  private static final String ZIP_FOLDER = "coinscontainers/";
  private static final String LIB_FOLDER = "coinsgloballibraries/";


  private static String RDF_PATH = "bim";
  private static String ONTOLOGIES_PATH = "bim/repository";
  private static String ATTACHMENT_PATH = "doc";
  private static String WOA_PATH = "woa";

  private static CoinsGraphSet graphSet = new InMemGraphSet("http://sandbox/");


  private static ArrayList<File> foldersToCleanup;
  public static ArrayList<File> getFoldersToCleanup() {
    if(foldersToCleanup == null) {
      foldersToCleanup = new ArrayList<>();
      foldersToCleanup.add(getTempZipPath().toFile());
      foldersToCleanup.add(getTempLibPath().toFile());
    }
    return foldersToCleanup;
  }


  public static String newCoinsContainer() {

    String internalRef = RandomStringUtils.random(8, true, true);
    Path homePath = getTempZipPath().resolve(internalRef);
    initContainer(homePath, false);

    return internalRef;
  }

  public static String existingCoinsContainer(String internalRef) {

    Path homePath = getTempZipPath().resolve(internalRef);
    initContainer(homePath, false);

    return internalRef;
  }

  public static String existingCoinsContainer(File sourceFile,
                                              HashMap<String, File> rdfFiles,
                                              HashMap<String, File> woaFiles,
                                              HashMap<String, File> attachments,
                                              HashMap<Namespace, File> libraryFiles,
                                              boolean strict) {

    if (!sourceFile.exists()) {
      throw new CoinsFileNotFoundException("Supplied .ccr-file could not be found.");
    }

    String internalRef = RandomStringUtils.random(8, true, true);
    Path homePath = getTempZipPath().resolve(internalRef);
    unzipTo(sourceFile, homePath);
    initContainer(homePath, strict);



    FileManager.indexZipFile(internalRef, rdfFiles, woaFiles, attachments, libraryFiles, strict);


    return internalRef;
  }







  private static Path getTempZipPath() {
    if(TEMP_ZIP_PATH != null) {
      return TEMP_ZIP_PATH;
    }
    TEMP_ZIP_PATH = Paths.get(DEFAULT_TEMP_FOLDER).resolve(ZIP_FOLDER);
    return TEMP_ZIP_PATH;
  }
  private static Path getTempLibPath() {
    if(TEMP_LIB_PATH != null) {
      return TEMP_LIB_PATH;
    }
    TEMP_LIB_PATH = Paths.get(DEFAULT_TEMP_FOLDER).resolve(LIB_FOLDER);
    return TEMP_LIB_PATH;
  }






  private static void initContainer(Path homePath, boolean strict) {

    // Create output directory is not exists
    File homePathFile = homePath.toFile();
    if(!homePathFile.exists()){
      if(!homePathFile.mkdirs()) {
        throw new CoinsFileNotFoundException("Not able to create temp path "+homePathFile+".");
      }
    }
    Path rdfPath = homePath.resolve(RDF_PATH);
    File rdfPathFile = rdfPath.toFile();
    if(!rdfPathFile.exists()) {
      if(strict) {
        throw new InvalidContainerFileException("Folder "+RDF_PATH+" does not exist inside the container file.");
      }
      log.warn("Missing folder in container, will create it: " + rdfPathFile);
      if(!rdfPathFile.mkdirs()) {
        throw new CoinsFileNotFoundException("Not able to create temp path "+rdfPathFile+".");
      }
    }
    Path ontologiesPath = homePath.resolve(ONTOLOGIES_PATH);
    File ontologiesPathFile = ontologiesPath.toFile();
    if(!ontologiesPathFile.exists()) {
      log.warn("Missing folder in container, will create it: " + ontologiesPathFile);
      if(!ontologiesPathFile.mkdirs()) {
        throw new CoinsFileNotFoundException("Not able to create temp path "+ontologiesPathFile+".");
      }
    }
    Path attachmentPath = homePath.resolve(ATTACHMENT_PATH);
    File attachmentPathFile = attachmentPath.toFile();
    if(!attachmentPathFile.exists()) {
      log.warn("Missing folder in container, will create it: " + attachmentPath);
      if(!attachmentPathFile.mkdirs()) {
        throw new CoinsFileNotFoundException("Not able to create temp path "+attachmentPathFile+".");
      }
    }
    Path woaPath = homePath.resolve(WOA_PATH);
    File woaPathFile = woaPath.toFile();
    if(!woaPathFile.exists()) {
      log.warn("Missing folder in container, will create it: " + woaPathFile);
      if(!woaPathFile.mkdirs()) {
        throw new CoinsFileNotFoundException("Not able to create temp path "+woaPathFile+".");
      }
    }
  }



  public static File createRdfFile(String internalRef, String fileName) {
    if(internalRef == null) {
      throw new CoinsFileNotFoundException("Creating rdf file in non existing coins container.");
    }
    Path homePath = getTempZipPath().resolve(internalRef);
    Path rdfPath = homePath.resolve(RDF_PATH);
    File candidate = rdfPath.resolve(fileName).toFile();
    if(candidate.exists()) {
      log.debug("File in container already exists, will be overriding.");
    } else {
      candidate.getParentFile().mkdirs();
    }
    return candidate;
  }

  public static File createWoaFile(String internalRef, String fileName) {
    if(internalRef == null) {
      throw new CoinsFileNotFoundException("Creating rdf file in non existing coins container.");
    }
    Path homePath = getTempZipPath().resolve(internalRef);
    Path rdfPath = homePath.resolve(WOA_PATH);
    File candidate = rdfPath.resolve(fileName).toFile();
    if(candidate.exists()) {
      log.debug("File in container already exists, will be overriding.");
    } else {
      candidate.getParentFile().mkdirs();
    }
    return candidate;
  }


  /**
   * Extracts all the content of the .ccr-file specified in the constructor to [TEMP_ZIP_PATH] / [internalRef].
   *
   */
  public static void unzipTo(File sourceFile, Path destinationPath) {
    unzipTo(sourceFile, destinationPath, false);
  }
  public static void unzipTo(File sourceFile, Path destinationPath, boolean strict) {

    byte[] buffer = new byte[1024];
    String startFolder = null;

    try {

      // Get the zip file content
      ZipInputStream zis = new ZipInputStream(new FileInputStream(sourceFile));
      ZipEntry ze = zis.getNextEntry();

      int fileCounter = 0;
      while(ze != null) {

        if(ze.isDirectory()) {
          ze = zis.getNextEntry();
          continue;
        }

        String fileName = ze.getName();

        // If the first folder is a somename/bim/file.ref skip it
        Path filePath = Paths.get(fileName);
        Path pathPath = filePath.getParent();
        String pathPathString = (pathPath!=null) ? pathPath.toString().toLowerCase() : "";

        if(pathPathString.endsWith("bim") ||
           pathPathString.endsWith("bim/repository") || pathPathString.endsWith("bim\\repository") ||
           pathPathString.endsWith("doc") ||
           pathPathString.endsWith("woa")) {

          Path pathRoot = pathPathString.endsWith("repository") ? pathPath.getParent().getParent() : pathPath.getParent();

          String prefix = "";
          if (pathRoot != null) {
            prefix = pathRoot.toString();
          }

          if(startFolder == null) {
            startFolder = prefix;
            log.debug("File root set to: "+startFolder);

          } else if(startFolder != null && !prefix.equals(startFolder)) {
            throw new InvalidContainerFileException("The container file has an inconsistent file root, was "+startFolder+", now dealing with "+prefix+".");
          }
        } else {
          if(strict) {
            throw new InvalidContainerFileException("File found in the container that was not in the correct folder: "+filePath.toString());
          } else {
            log.warn("Skipping illegal file: " + filePath.toString());
          }
          ze = zis.getNextEntry();
          continue;
        }


        String insideStartFolder = filePath.toString().substring(startFolder.length());
        File newFile = new File(destinationPath + "/" + insideStartFolder);
        fileCounter++;

        // Create all non exists folders
        // else you will hit FileNotFoundException for compressed folder
        File newFileParent = new File(newFile.getParent());
        if(!newFileParent.exists()) {
          log.warn("Unpacking for the first time to folder " + newFileParent.toString() + ".");
          newFileParent.mkdirs();
        }

        FileOutputStream fos = new FileOutputStream(newFile);

        int len;
        while ((len = zis.read(buffer)) > 0) {
          fos.write(buffer, 0, len);
        }

        fos.close();
        ze = zis.getNextEntry();
      }
      log.info("Extracted "+fileCounter+" files.");

      zis.closeEntry();
      zis.close();

    } catch(IOException ex) {
      ex.printStackTrace();
    }
  }

  public static void indexZipFile(String internalRef,
                                  HashMap<String, File> rdfFiles,
                                  HashMap<String, File> woaFiles,
                                  HashMap<String, File> attachments,
                                  HashMap<Namespace, File> libraryFiles,
                                  boolean strict) {

    Path homePath = getTempZipPath().resolve(internalRef);


    File folder;
    File[] listOfFiles;

    folder = new File(homePath.resolve(RDF_PATH).toString());
    log.info("Will index "+folder+" for rdf files.");
    listOfFiles = folder.listFiles();

    for (int i = 0; i < listOfFiles.length; i++) {
      if (listOfFiles[i].isFile()) {
        log.info("Index file as rdf: "+listOfFiles[i].getName());
        rdfFiles.put(listOfFiles[i].getName(), listOfFiles[i]);
      }
    }

    folder = new File(homePath.resolve(WOA_PATH).toString());
    log.info("Will index "+folder+" for woa files.");
    listOfFiles = folder.listFiles();

    for (int i = 0; i < listOfFiles.length; i++) {
      if (listOfFiles[i].isFile()) {
        log.info("Index file as woa: "+listOfFiles[i].getName());
        woaFiles.put(listOfFiles[i].getName(), listOfFiles[i]);
      }
    }


    folder = new File(homePath.resolve(ONTOLOGIES_PATH).toString());
    log.info("Will index "+folder+" for ontology files.");
    listOfFiles = folder.listFiles();

    for (int i = 0; i < listOfFiles.length; i++) {
      if (listOfFiles[i].isFile()) {
        if(isRdfFile(listOfFiles[i])) {

          log.info("Index file as ontology file: "+listOfFiles[i].getName());

          Model libraryModel = graphSet.readModel(listOfFiles[i].toURI().toString());
          Namespace ns = getLeadingNamespace(listOfFiles[i], libraryModel);
          log.info("Found leading namespace " + ns + " for file " + listOfFiles[i].getName()+".");

          libraryFiles.put(ns, listOfFiles[i]);
        } else {
          if(strict) {
            throw new InvalidContainerFileException("Failed to interpret file as ontology file: " + listOfFiles[i]);
          } else {
            log.warn("Failed to interpret file as ontology file: " + listOfFiles[i]);
          }
        }
      }
    }

    folder = new File(homePath.resolve(ATTACHMENT_PATH).toString());
    log.info("Will index "+folder+" for attachment files.");
    listOfFiles = folder.listFiles();

    int fileCount = 0;
    for (int i = 0; i < listOfFiles.length; i++) {
      if (listOfFiles[i].isFile()) {
        fileCount++;
        attachments.put(listOfFiles[i].getName(), listOfFiles[i]);
      }
    }

    log.info("Indexed "+fileCount+" files as attachment.");
  }

  /**
   * Zips all the content of the [TEMP_ZIP_PATH] / [internalRef] to the specified target .ccr-file.
   *
   * @param target  the .ccr-file containing all the content from this container
   */
  public static void zip(String internalRef, File target) {




    try {

      final Path homePath = getTempZipPath().resolve(internalRef);

      FileOutputStream fos = new FileOutputStream(target);
      final ZipOutputStream zos = new ZipOutputStream(fos);

      ZipEntry ze;
      File[] files;

      // Add bim
      ze = new ZipEntry(RDF_PATH+"/");
      zos.putNextEntry(ze);
      zos.closeEntry();
      files = homePath.resolve(RDF_PATH).toFile().listFiles();
      for (int i = 0; i < files.length; i++) {
        if (!files[i].isDirectory()) {
          addFileToZip(files[i].toString(), Paths.get(RDF_PATH).resolve(files[i].getName()), zos);
        }
      }

      // Add bim/repository
      ze = new ZipEntry(ONTOLOGIES_PATH+"/");
      zos.putNextEntry(ze);
      zos.closeEntry();
      files = homePath.resolve(ONTOLOGIES_PATH).toFile().listFiles();
      for (int i = 0; i < files.length; i++) {
        if (!files[i].isDirectory()) {
          addFileToZip(files[i].toString(), Paths.get(ONTOLOGIES_PATH).resolve(files[i].getName()), zos);
        }
      }

      // Add doc
      ze = new ZipEntry(ATTACHMENT_PATH+"/");
      zos.putNextEntry(ze);
      zos.closeEntry();
      files = homePath.resolve(ATTACHMENT_PATH).toFile().listFiles();
      for (int i = 0; i < files.length; i++) {
        if (!files[i].isDirectory()) {
          addFileToZip(files[i].toString(), Paths.get(ATTACHMENT_PATH).resolve(files[i].getName()), zos);
        }
      }

      // Add woa
      ze = new ZipEntry(WOA_PATH+"/");
      zos.putNextEntry(ze);
      zos.closeEntry();
      files = homePath.resolve(WOA_PATH).toFile().listFiles();
      for (int i = 0; i < files.length; i++) {
        if (!files[i].isDirectory()) {
          addFileToZip(files[i].toString(), Paths.get(WOA_PATH).resolve(files[i].getName()), zos);
        }
      }




      zos.close();

    } catch(IOException ex) {
      ex.printStackTrace();
    }
  }

  private static void addFileToZip(String fromPath, Path zipPath, ZipOutputStream zos) {

    // Do dark magic, needed to correct ikvm consequences
    String pathInZip = zipPath.toFile().getPath().replace("\\","/");

    try {
      final byte[] buffer = new byte[1024];
      ZipEntry ze = new ZipEntry(pathInZip);
      zos.putNextEntry(ze);
      log.trace("Adding to zip: "+pathInZip);

      // Write file content
      FileInputStream in = new FileInputStream(fromPath);

      int len;
      while ((len = in.read(buffer)) != -1) {
        zos.write(buffer, 0, len);
      }

      zos.closeEntry();
      in.close();
    } catch(IOException ex) {
      ex.printStackTrace();
    }
  }




  public static void destroyAll() {
    for(File folder : getFoldersToCleanup()) {
      if(folder.isDirectory()) {
        try {
          log.info("Destroying folder "+folder.toString());
          FileUtils.deleteDirectory(folder);
        } catch (IOException e) {
          log.error(e.getMessage(), e);
        }
      }
    }
  }

  public static void destroy(String internalRef) {
    try {
      log.info("Destroying cache folder for "+internalRef);
      File homePath = getTempZipPath().resolve(internalRef).toFile();
      if(homePath.isDirectory()) {
        FileUtils.deleteDirectory(homePath);
      }
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }

  public static Path placeAttachment(String internalRef, Path attachment) {
    Path fileName = attachment.getFileName();
    Path homePath = getTempZipPath().resolve(internalRef);
    Path attachmentPath = homePath.resolve(ATTACHMENT_PATH);
    Path absoluteTempPath = attachmentPath.resolve(fileName);
    try {
      Files.copy(attachment, absoluteTempPath, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
    return absoluteTempPath;
  }

  public static Path getAttachment(String internalRef, String fileName) {
    Path homePath = getTempZipPath().resolve(internalRef);
    Path attachmentPath = homePath.resolve(ATTACHMENT_PATH);
    Path absoluteTempPath = attachmentPath.resolve(fileName);
    return absoluteTempPath;
  }

  public static Path getAttachmentPath(String internalRef) {
    Path homePath = getTempZipPath().resolve(internalRef);
    Path attachmentPath = homePath.resolve(ATTACHMENT_PATH);
    return attachmentPath;
  }


  /**
   * Return either an url or a file:// url to find the resource
   * @param internalRef
   * @param resource
   * @return
   */
  public static URI getLibrary(JenaCoinsContainer container, String internalRef, URI resource) {

    log.trace("Get library "+resource+" in "+internalRef);

    try {
      File file = new File(resource);
      if (file.exists()) {
        log.trace("Found file as local path " + resource);
        copyAndLinkLibrary(internalRef, file);

        return file.toURI();
      }
    } catch(IllegalArgumentException e) {
      // try next option, the resource is not a file
    }


    Namespace resourceAsNs = null;
    try {
      resourceAsNs = new Namespace(resource.toString());
    } catch(InvalidNamespaceException e) {
    }

    if(resourceAsNs != null) {
      if (internalRef != null && container != null) {
        if (container.getAvailableLibraryFiles().containsKey(resourceAsNs)) {

          File matchingFile = container.getAvailableLibraryFiles().get(resourceAsNs);
          log.trace("Found file as previously registered " + matchingFile.getAbsolutePath());
          copyAndLinkLibrary(internalRef, matchingFile);
          return matchingFile.toURI();
        }
      }
    }


    // Alternatively try to resolve the resourceUri online
    try {
      URL resourceAsUrl = resource.toURL();
      HttpURLConnection connection = (HttpURLConnection) resourceAsUrl.openConnection();
      connection.setRequestMethod("HEAD");
      int responseCode = connection.getResponseCode();
      if (responseCode == 200) {
        log.info("Found active link online: "+resource);
        return resource;
      }
    } catch (MalformedURLException e) {
    } catch (ProtocolException e) {
    } catch (IOException e) {
    }

    log.trace("Import resource can not be found in the ccr and is not available online.");
    throw new CoinsResourceNotFoundException("Import resource can not be found in the ccr and is not available online.");
  }



  public static Namespace getLeadingNamespace(File file, Model model) {

    Namespace namespace = null;

    // Assume its xml, get the base uri
    if(file != null && (file.getName().endsWith(".rdf") || file.getName().endsWith(".xml"))) {
      try {
        namespace = new Namespace(FileManager.getXmlBaseOrxmlns(file));
      } catch (InvalidNamespaceException e) {
        log.info(e.getMessage());
      }
    }

    // Try to find the empty prefix if no base uri was found (maybe not xml)
    if(namespace == null) {
      try {
        namespace = new Namespace(model.getNsPrefixURI(""));
        log.info("The xmlns="+namespace.toString()+" found while importing file " + file.getName());
      } catch(InvalidNamespaceException e) {
        log.info("The xmlns not found while importing file " + file.getName());}
    }

    // If still null, base the model name on the subject of the owl:Ontology object
    if(namespace == null) {
      ResIterator subjects = model.listSubjectsWithProperty(RDF.type, OWL.Ontology);
      if(subjects.hasNext()) {
        namespace = new Namespace(subjects.next().asResource().getNameSpace());
        log.info("The rdf:type found for owl:Ontology to "+namespace.toString()+", using as namespace while importing file " + file.getName());
      }
    }

    if(namespace == null) {
      throw new CoinsNamespaceNotFoundException("No namespace could be found for " + file.getName()+".");
    }

    return namespace;
  }

  public static String getXmlBaseOrxmlns(File xmlFile) {
    final ArrayList<String> baseUriDropHere = new ArrayList<>();
    final ArrayList<String> xmlnsDropHere = new ArrayList<>();

    DefaultHandler handler = new DefaultHandler() {

      @Override
      public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        if("rdf:RDF".equals(qName)) {
          for (int i=0; i<attributes.getLength(); i++) {
            if("xml:base".equals(attributes.getQName(i))) {
              baseUriDropHere.add(attributes.getValue(i));
            }
            if("xmlns".equals(attributes.getQName(i))) {
              xmlnsDropHere.add(attributes.getValue(i));
            }
          }
          return;
        }
      }
    };


    try {

      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setValidating(false);
      SAXParser parser = factory.newSAXParser();
      parser.parse(xmlFile, handler);

    } catch (ParserConfigurationException e) {
      // do not print this, this is supposed to crash for non-xml files
    } catch (SAXException e) {
      // do not print this, this is supposed to crash for non-xml files
    } catch (IOException e) {
      log.error("Problem reading xml file.", e);
    }

    if(baseUriDropHere.isEmpty()) {
      if(xmlnsDropHere.isEmpty()) {
        return null;
      } else {
        return xmlnsDropHere.get(0);
      }
    } else {
      return baseUriDropHere.get(0);
    }
  }


  public static void copyAndLinkRdfFile(String internalRef, File source, String contentFileName) {
    if(internalRef == null) {
      throw new CoinsFileManagerException("Registering rdf file in non existing coins container.");
    }
    Path homePath = getTempZipPath().resolve(internalRef);
    Path rdfPath = homePath.resolve(ONTOLOGIES_PATH);
    Path absoluteTempPath = rdfPath.resolve(contentFileName);

    // check if the folder is empty (it should)
    if(rdfPath.toFile().list().length > 0) {
      throw new CoinsFileManagerException("The FileManager cache already contains an rdf-file.");
    }

    try {
      Files.copy(source.toPath(), absoluteTempPath, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }

  public static void copyAndLinkLibrary(String internalRef, File source) {
    if(internalRef == null) {
      throw new CoinsFileManagerException("Registering rdf file in non existing coins container.");
    }
    Path homePath = getTempZipPath().resolve(internalRef);
    Path rdfPath = homePath.resolve(ONTOLOGIES_PATH);
    Path absoluteTempPath = rdfPath.resolve(source.getName());

    if(absoluteTempPath.toFile().exists()) {
      log.info("File "+absoluteTempPath.toString()+" already exists, skipping.");
      return;
    }

    try {
      Files.copy(source.toPath(), absoluteTempPath, StandardCopyOption.REPLACE_EXISTING);
      log.info("Copied file "+source.getName()+" to "+internalRef+".");
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }
  public static Namespace copyAndRegisterLibrary(InputStream stream, String fileName, HashMap<Namespace, File> availableLibraryFiles) {

    if(stream == null) {
      log.warn("Failed saving file "+fileName+", stream is null.");
      return null;
    }

    Path copyTo = getTempLibPath().resolve(RandomStringUtils.random(4, true, true));
    copyTo.toFile().mkdirs();
    URI fullPathUri = copyTo.resolve(fileName).toUri();
    File fullPathFile = new File(fullPathUri);


    OutputStream resStreamOut = null;
    try {
      try {
        int readBytes;
        byte[] buffer = new byte[4096];
        resStreamOut = new FileOutputStream(fullPathFile);
        while ((readBytes = stream.read(buffer)) > 0) {
          resStreamOut.write(buffer, 0, readBytes);
        }
      } catch (IOException ex) {
        log.warn("Problem saving file "+fileName+" to temp folder "+copyTo, ex);
      } finally {
        stream.close();
        resStreamOut.close();
      }
    } catch (IOException ex) {
      log.warn("Problem saving file "+fileName+" to temp folder "+copyTo, ex);
    }

    Model libraryModel = graphSet.readModel(fullPathUri.toString());
    Namespace ns = getLeadingNamespace(fullPathFile, libraryModel);
    if(!availableLibraryFiles.containsKey(ns)) {
      log.info("Registering for namespace " + ns + " for file " + fileName);
      availableLibraryFiles.put(ns,fullPathFile);
    } else {
      log.info("Skipping, already a file registered for namespace " + ns);
      // Remove file and folder too
      fullPathFile.delete();
      copyTo.toFile().delete();
    }
    return ns;
  }
  public static Namespace registerLibrary(URI fileUri, Namespace fallbackNs, HashMap<Namespace, File> availableLibraryFiles) {
    File newFile = new File(fileUri);
    Model libraryModel = graphSet.readModel(fileUri.toString());
    Namespace ns = fallbackNs;
    try {
      ns = getLeadingNamespace(newFile, libraryModel);
    } catch(CoinsNamespaceNotFoundException e) {
      if(fallbackNs==null) {
        return null;
      }
    }
    log.info("Registering for namespace "+ns+" for file "+newFile.getName());

    availableLibraryFiles.put(ns, newFile);
    return ns;
  }

  private static boolean isRdfFile(File file) {
    for(String extension : Arrays.asList(INDEX_AS_RDF)) {
      if(file.getName().endsWith(extension)) {
        return true;
      }
    }
    return false;
  }


  // this should be able to be run from everywhere
  public static ArrayList<String> listResourceFiles(String path) {

    ArrayList<String> result = new ArrayList<>();

    while(path.startsWith("/")) {
      path = path.substring(1);
    }

    try {
      URI uri = FileManager.class.getResource("/"+path).toURI();

      File myPath;
      if (uri.getScheme().equals("jar")) {

        String jarPath = uri.toString().substring(9, uri.toString().indexOf("!"));
        String insideJar = uri.toString().substring(uri.toString().indexOf("!")+1);
        while(insideJar.startsWith("/")) {
          insideJar = insideJar.substring(1);
        }

        ZipInputStream zis = new ZipInputStream(new FileInputStream(new File(jarPath)));
        ZipEntry ze = zis.getNextEntry();

        while (ze != null) {

          if (!ze.isDirectory() && ze.getName().startsWith(insideJar)) {
            String relativePath = ze.getName().substring(insideJar.length());
            while (relativePath.startsWith("/")) {
              relativePath = relativePath.substring(1);
            }
            if (!relativePath.isEmpty()) {
              result.add(relativePath);
            }
          }

          ze = zis.getNextEntry();
        }

        zis.closeEntry();
        zis.close();

      } else {
        myPath = Paths.get(uri).toFile();
        File[] directoryListing = myPath.listFiles();
        if(directoryListing != null) {
          for (File folder : directoryListing ) {
            if (folder.isFile()) {
              String fullPath = folder.toString();
              String relativePath = fullPath.substring(uri.getRawPath().length());
              if (!relativePath.isEmpty()) {
                result.add(relativePath.substring(1));
              }
            }
          }
        }
      }


    } catch (IOException e) {
    } catch (URISyntaxException e) {
    }


    return result;
  }

  public static InputStream getResourceFileAsStream(String path) {

    while(path.startsWith("/")) {
      path = path.substring(1);
    }
    return FileManager.class.getResourceAsStream("/"+path);
  }
}