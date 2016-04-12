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
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import nl.coinsweb.sdk.exceptions.*;
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
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class FileManager {

  private static final Logger log = LoggerFactory.getLogger(FileManager.class);

  public static Path TEMP_ZIP_PATH;
  public static Path TEMP_LIB_PATH;


  private static final String[] INDEX_AS_RDF =  {".rdf", ".owl", ".ttl"};

  private static final String DEFAULT_TEMP_FOLDER = "/tmp/";
  private static final String ZIP_FOLDER = "coinscontainers/";
  private static final String LIB_FOLDER = "coinsgloballibraries/";


  private static String RDF_PATH = "bim/";
  private static String ONTOLOGIES_PATH = "bim/repository/";
  private static String ATTACHMENT_PATH = "doc/";
  private static String WOA_PATH = "woa/";





  public static String newCoinsContainer() {

    String internalRef = RandomStringUtils.random(8, true, true);
    Path homePath = getTempZipPath().resolve(internalRef);
    initContainer(homePath);

    return internalRef;
  }

  public static String existingCoinsContainer(File sourceFile, HashMap<String, File> rdfFiles, HashMap<String, File> attachments, HashMap<Namespace, File> libraryFiles) {

    if (!sourceFile.exists()) {
      throw new CoinsFileNotFoundException("Supplied .ccr-file could not be found.");
    }

    String internalRef = RandomStringUtils.random(8, true, true);
    Path homePath = getTempZipPath().resolve(internalRef);
    unzipTo(sourceFile, homePath);
    initContainer(homePath);



    FileManager.indexZipFile(sourceFile, internalRef, rdfFiles, attachments, libraryFiles);


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







  private static void initContainer(Path homePath) {

    // Create output directory is not exists
    File homePathFile = homePath.toFile();
    if(!homePathFile.exists()){
      if(!homePathFile.mkdirs()) {
        throw new CoinsFileNotFoundException("Not able to create temp path "+homePathFile+".");
      }
    }
    Path rdfPath = homePath.resolve(RDF_PATH);
    File rdfPathFile = rdfPath.toFile();
    if(!rdfPathFile.exists()){
      if(!rdfPathFile.mkdirs()) {
        throw new CoinsFileNotFoundException("Not able to create temp path "+rdfPathFile+".");
      }
    }
    Path ontologiesPath = homePath.resolve(ONTOLOGIES_PATH);
    File ontologiesPathFile = ontologiesPath.toFile();
    if(!ontologiesPathFile.exists()){
      if(!ontologiesPathFile.mkdirs()) {
        throw new CoinsFileNotFoundException("Not able to create temp path "+ontologiesPathFile+".");
      }
    }
    Path attachmentPath = homePath.resolve(ATTACHMENT_PATH);
    File attachmentPathFile = attachmentPath.toFile();
    if(!attachmentPathFile.exists()){
      if(!attachmentPathFile.mkdirs()) {
        throw new CoinsFileNotFoundException("Not able to create temp path "+attachmentPathFile+".");
      }
    }
    Path woaPath = homePath.resolve(WOA_PATH);
    File woaPathFile = woaPath.toFile();
    if(!woaPathFile.exists()){
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


  /**
   * Extracts all the content of the .ccr-file specified in the constructor to [TEMP_ZIP_PATH] / [internalRef].
   *
   */
  public static void unzipTo(File sourceFile, Path destinationPath) {

    byte[] buffer = new byte[1024];
    Path startFolder = null;

    try {

      // Get the zip file content
      ZipInputStream zis = new ZipInputStream(new FileInputStream(sourceFile));
      ZipEntry ze = zis.getNextEntry();

      while(ze != null) {

        if(ze.isDirectory()) {
          ze = zis.getNextEntry();
          continue;
        }

        String fileName = ze.getName();

        // If the first folder is a somename/bim/file.ref skip it
        Path filePath = Paths.get(fileName);
        Path pathPath = filePath.getParent();

        if(pathPath.endsWith("bim") ||
           pathPath.endsWith("bim/repository") ||
           pathPath.endsWith("doc") ||
           pathPath.endsWith("woa")) {


          Path pathRoot = pathPath.endsWith("repository") ? pathPath.getParent().getParent() : pathPath.getParent();

          Path newStartFolder = Paths.get("");
          if (pathRoot != null) {
            newStartFolder = pathRoot;
          }

          if(startFolder == null) {
            startFolder = newStartFolder;
            log.debug("File root set to: "+startFolder);

          } else if(startFolder != null && !newStartFolder.equals(startFolder)) {
            throw new InvalidContainerFileException("The container file has an inconsistent file root, was "+startFolder+", now dealing with "+newStartFolder+".");
          }
        } else {
          log.debug("Skipping file: "+filePath.toString());
          continue;
        }


        Path insideStartFolder = startFolder.relativize(filePath);
        File newFile = new File(destinationPath + File.separator + insideStartFolder);
        log.info("Extract "+newFile+".");

        // Create all non exists folders
        // else you will hit FileNotFoundException for compressed folder
        new File(newFile.getParent()).mkdirs();

        FileOutputStream fos = new FileOutputStream(newFile);

        int len;
        while ((len = zis.read(buffer)) > 0) {
          fos.write(buffer, 0, len);
        }

        fos.close();
        ze = zis.getNextEntry();
      }

      zis.closeEntry();
      zis.close();

    } catch(IOException ex) {
      ex.printStackTrace();
    }
  }

  public static void indexZipFile(File sourceFile,
                                  String internalRef,
                                  HashMap<String, File> rdfFiles,
                                  HashMap<String, File> attachments,
                                  HashMap<Namespace, File> libraryFiles) {

    Path homePath = getTempZipPath().resolve(internalRef);

    try {

      // Get the zip file content
      ZipInputStream zis = new ZipInputStream(new FileInputStream(sourceFile));
      ZipEntry ze = zis.getNextEntry();

      while(ze!=null){

        if(ze.isDirectory()) {
          ze = zis.getNextEntry();
          continue;
        }

        String fileName = ze.getName();
        File newFile = new File(homePath + File.separator + fileName);

        log.info("index file from ccr "+newFile.getAbsolutePath());

        ze = zis.getNextEntry();

        // If this did not throw exceptions add it to the indexes
        if(newFile.toPath().startsWith(homePath.resolve(RDF_PATH)) &&
           !newFile.toPath().startsWith(homePath.resolve(ONTOLOGIES_PATH))) {
          rdfFiles.put(fileName, newFile);
        }

        if(newFile.toPath().startsWith(homePath.resolve(ATTACHMENT_PATH))) {
          attachments.put(fileName, newFile);

          if(isRdfFile(newFile)) {
            Model libraryModel = ModelFactory.createDefaultModel();
            libraryModel.read(newFile.toURI().toString());
            Namespace ns = getLeadingNamespace(newFile, libraryModel);
            log.info("found leading namespace " + ns + " for file " + fileName);

            libraryFiles.put(ns, newFile);
          }

        }
      }

      zis.closeEntry();
      zis.close();

    } catch(IOException ex) {
      ex.printStackTrace();
    }
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
      final byte[] buffer = new byte[1024];

      Files.walkFileTree(homePath, EnumSet.noneOf(FileVisitOption.class), 3, new SimpleFileVisitor<Path>() {


        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {

          // Don't add the root itself
          if (dir.equals(homePath)) {
            return FileVisitResult.CONTINUE;
          }

          // Register entry
          ZipEntry ze = new ZipEntry(homePath.relativize(dir).toString() + "/");
          zos.putNextEntry(ze);
          log.trace("adding to zip: "+ze);

          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path dir, BasicFileAttributes attrs) throws IOException {

          // Register entry


          if(dir.toFile().isFile()) {

            ZipEntry ze = new ZipEntry(homePath.relativize(dir).toString());
            zos.putNextEntry(ze);
            log.trace("adding to zip: "+ze);

            // Write file content
            FileInputStream in = new FileInputStream(dir.toFile());

            int len;
            while ((len = in.read(buffer)) > 0) {
              zos.write(buffer, 0, len);
            }

            in.close();

          } else if(dir.toFile().isDirectory()) {

            ZipEntry ze = new ZipEntry(homePath.relativize(dir).toString() + "/");
            zos.putNextEntry(ze);
            log.trace("adding to zip: "+ze);

          }

          return FileVisitResult.CONTINUE;
        }
      });

      zos.closeEntry();
      zos.close();

    } catch(IOException ex) {
      ex.printStackTrace();
    }
  }




  public static void destroyAll() {
    List<String> internalRefs = Arrays.asList(getTempZipPath().toFile().list());
    for(String internalRef : internalRefs) {
      destroy(internalRef);
    }
  }

  public static void destroy(String internalRef) {
    try {
      log.info("destroying cache folder for "+internalRef);
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

    log.trace("get library "+resource+" in "+internalRef);

    try {
      File file = new File(resource);
      if (file.exists()) {
        log.trace("found file as local path " + resource);
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
          log.trace("found file as previously registered " + matchingFile.getAbsolutePath());
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
        log.info("found active link online: "+resource);
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
        log.info("xmlns="+namespace.toString()+" found while importing file " + file.getName());
      } catch(InvalidNamespaceException e) {
        log.info("xmlns not found while importing file " + file.getName());}
    }

    // If still null, base the model name on the subject of the owl:Ontology object
    if(namespace == null) {
      ResIterator subjects = model.listSubjectsWithProperty(RDF.type, OWL.Ontology);
      if(subjects.hasNext()) {
        namespace = new Namespace(subjects.next().asResource().getNameSpace());
        log.info("rdf:type found for owl:Ontology to "+namespace.toString()+", using as namespace while importing file " + file.getName());
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
      log.error("problem reading xml file",e);
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
      log.warn("failed saving file "+fileName+", stream is null");
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
        log.warn("problem saving file "+fileName+" to temp folder "+copyTo, ex);
      } finally {
        stream.close();
        resStreamOut.close();
      }
    } catch (IOException ex) {
      log.warn("problem saving file "+fileName+" to temp folder "+copyTo, ex);
    }


    Model libraryModel = ModelFactory.createDefaultModel();
    libraryModel.read(fullPathUri.toString());
    Namespace ns = getLeadingNamespace(fullPathFile, libraryModel);
    if(!availableLibraryFiles.containsKey(ns)) {
      log.info("registering for namespace " + ns + " for file " + fileName);
      availableLibraryFiles.put(ns,fullPathFile);
    } else {
      log.info("skipping, already a file registered for namespace " + ns);
      // Remove file and folder too
      fullPathFile.delete();
      copyTo.toFile().delete();
    }
    return ns;
  }
  public static Namespace registerLibrary(URI fileUri, Namespace fallbackNs, HashMap<Namespace, File> availableLibraryFiles) {
    File newFile = new File(fileUri);
    Model libraryModel = ModelFactory.createDefaultModel();
    libraryModel.read(fileUri.toString());
    Namespace ns = fallbackNs;
    try {
      ns = getLeadingNamespace(newFile, libraryModel);
    } catch(CoinsNamespaceNotFoundException e) {
      if(fallbackNs==null) {
        return null;
      }
    }
    log.info("registering for namespace "+ns+" for file "+newFile.getName());

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
}
