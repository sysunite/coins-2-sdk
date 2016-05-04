package nl.coinsweb.sdk.cli.generate;

import com.hp.hpl.jena.ontology.OntModelSpec;
import nl.coinsweb.sdk.CoinsParty;
import nl.coinsweb.sdk.ModelFactory;
import nl.coinsweb.sdk.cli.CliOptions;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import nl.coinsweb.sdk.jena.JenaModelFactory;
import nl.coinsweb.sdk.owlgenerator.ClassGenerateEngine;
import org.codehaus.plexus.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class RunGenerate {

  private static final Logger log = LoggerFactory.getLogger(RunGenerate.class);




  public void go(CliOptions options) {



    // Check tools javac, jar en ikvmc

    boolean doCompile     = options.hasJarOption() || options.hasJarPathOption() || options.hasDllOption() || options.hasDllPathOption();
    boolean doJar         = options.hasJarOption() || options.hasJarPathOption() || options.hasDllOption() || options.hasDllPathOption();
    boolean doInclSources = doJar && options.hasIncludeSourcesOption();
    boolean doDll         = options.hasDllOption() || options.hasDllPathOption();

    if(doCompile && !(getCli("javac -version").startsWith("javac 1.7") || getCli("javac -version").startsWith("javac 1.8"))) {
      System.out.println("Please make the javac command from the Java SDK (version 7 or 8) available on the path.");
      return;
    }
    if(doJar && !getCli("jar").startsWith("Usage: jar {ctxui}")) {
      System.out.println("Please make the jar command available on the path.");
      return;
    }

    if(doDll && !options.hasApiPathOption()) {
      System.out.println("Please specify a pointer to the coins-api.dll using -a.");
      return;
    }

    if(doDll && !(getCli("ikvmc -help").startsWith("IKVM.NET Compiler version "))) {
      System.out.println("Please make the ikvmc command (version 7 or 8) available on the path.");
      return;
    }



    // Pre visit output folders

    String coinsCliJarPath;
    String coinsApiDllPath;
    Path generatedFolder;
    Path javaFolder;
    Path generatedPerJarFolder;
    Path jarFolder;
    Path dllFolder;

    try {

      coinsCliJarPath = (System.getProperty("java.class.path"));
      coinsApiDllPath = doDll?(options.getApiPathOption().toFile().getCanonicalPath()):null;

      generatedFolder = Paths.get(options.getOutputOption().toFile().getCanonicalPath()).resolve("generated");
      generatedFolder.toFile().mkdirs();
      FileUtils.cleanDirectory(generatedFolder.toFile());
      System.out.println("Will write java to: " + generatedFolder);

      javaFolder = Paths.get(options.getOutputOption().toFile().getCanonicalPath()).resolve("java");
      if (doCompile) {
        javaFolder.toFile().mkdirs();
        FileUtils.cleanDirectory(javaFolder.toFile());
        System.out.println("Will write compiled java to: " + javaFolder);
      }

      generatedPerJarFolder = Paths.get(options.getOutputOption().toFile().getCanonicalPath()).resolve("java-per-jar");
      if (doJar) {
        generatedPerJarFolder.toFile().mkdirs();
        FileUtils.cleanDirectory(generatedPerJarFolder.toFile());
        System.out.println("Will split compiled java in: " + generatedPerJarFolder);
      }

      if (doInclSources) {
        System.out.println("Will include sources in the jar");
      }

      jarFolder =
          options.hasJarPathOption() ?
              Paths.get(options.getJarPathOption().toFile().getCanonicalPath()) :
              Paths.get(options.getOutputOption().toFile().getCanonicalPath()).resolve("jar");
      if (doJar) {
        jarFolder.toFile().mkdirs();
        System.out.println("Will put jars in: " + jarFolder);
      }

      dllFolder =
          options.hasDllPathOption() ?
              Paths.get(options.getDllPathOption().toFile().getCanonicalPath()) :
              Paths.get(options.getOutputOption().toFile().getCanonicalPath()).resolve("win-dist");
      if (doDll) {
        dllFolder.toFile().mkdirs();
        System.out.println("Will put dlls in: " + dllFolder);
      }

    } catch (IOException e) {
      System.out.println("Something went wrong preparing the folders.");
      log.error("Something went wrong preparing the folders.", e);
      return;
    }




    // Generate java code in one place

    ClassGenerateEngine engine = new ClassGenerateEngine();

    // Load the data file
    OntModelSpec reasoner = null;
    if(options.hasReasonerOption()) {
      reasoner = options.getReasonerOption();
    }
    if(reasoner != null) {
      log.info("Using custom reasoner: "+options.getReasonerString());
    } else {
      log.info("Using default reasoner: OWL_MEM_RDFS_INF");
      reasoner = OntModelSpec.OWL_MEM_RDFS_INF;
    }


    ModelFactory factory = new JenaModelFactory();
    factory.setReasoner(reasoner);
    JenaCoinsContainer model = new JenaCoinsContainer(factory, new CoinsParty("http://sandbox.coinsweb.nl/defaultUser"), "http://empty.com/", false);





    ArrayList<String> sourceFileNames = new ArrayList<>();
    for(Path modelPath : options.getInputOptions()) {
      String filePath = null;
      try {
        filePath = modelPath.toFile().getCanonicalPath().toString();
      } catch (IOException e) {
        log.error(e.getMessage(), e);
      }
      model.addImport(filePath, null, true, true, true);
      log.info("adding "+filePath);
      sourceFileNames.add(modelPath.getFileName().toString());
    }




    engine.setTargetFolder(generatedFolder.toString());
    Map<String, String> mapping = engine.process(model, sourceFileNames);

    if(!doCompile) {
      return;
    }


    // Compile the java code

    String fileList = "";
    for(String namespace : mapping.keySet()) {
      String packageFolder = mapping.get(namespace).replace(".", "//");
      fileList += files(generatedFolder.resolve(packageFolder), ".java", " ");
    }

    log.info("javac -target 1.7  -source 1.7 -classpath " + coinsCliJarPath + " -d " + javaFolder.toString() + " " + fileList);
    runCli("javac -target 1.7  -source 1.7 -classpath " + coinsCliJarPath + " -d " + javaFolder.toString() + " " + fileList);

    if(!doJar) {
      return;
    }



    List<String> producedDllFiles = new ArrayList<>();
    for(String namespace : mapping.keySet()) {

      String packageFolder = mapping.get(namespace).replace(".", "//");
      Path packageFolderPath = Paths.get(packageFolder);
      String libraryName = packageFolderPath.getName(packageFolderPath.getNameCount()-1).toString();

      Path absoluteSubFolder = generatedPerJarFolder.resolve(libraryName);
      absoluteSubFolder.toFile().mkdirs();


      log.info("start with with "+libraryName);

      // Copy to different folders
      try {

        FileUtils.copyDirectory(
            javaFolder.resolve(packageFolder).toFile(),
            generatedPerJarFolder.resolve(libraryName).resolve(packageFolder).toFile());

      } catch(IOException e) {
        log.error("",e);
        continue;
      }

      // Copy source files
      if(doInclSources) {
        List<String> sourceFiles = Arrays.asList(files(generatedFolder.resolve(packageFolder), ".java", " ").split(" "));
        for (String sourceFile : sourceFiles) {
          File source = new File(sourceFile);
          String fileName = source.getName();
          File destination = generatedPerJarFolder.resolve(libraryName).resolve(packageFolder).resolve(fileName).toFile();
          try {
            FileUtils.copyFile(source, destination);
          } catch (IOException e) {
            log.error(e.getMessage(), e);
          }
        }
      }



      // Copy the rdf file
      for(Path inputFile : options.getInputOptions()) {
        try {
          FileUtils.copyFileToDirectory(inputFile.toFile(), generatedPerJarFolder.resolve(libraryName).toFile());
        } catch (IOException e) {
          log.error("error while copying source rdf file to namespace folder", e);
        }
      }




      // Create jar
      String filesToPackageInJar = files(absoluteSubFolder, "", " ", absoluteSubFolder);
      log.info("jar cfM " + jarFolder.resolve(libraryName + ".jar") + " -C " + absoluteSubFolder + "/ " + filesToPackageInJar, absoluteSubFolder);
      runCli("jar cfM " + jarFolder.resolve(libraryName + ".jar") + " -C " + absoluteSubFolder + "/ " + filesToPackageInJar, absoluteSubFolder);


      if(doDll) {

        String dllFile = dllFolder.resolve(libraryName + ".dll").toString();

        String references = "-reference:"+coinsApiDllPath+" " ;
        for(String reference : producedDllFiles) {
          references += "-reference:"+reference+" " ;
        }

        // Create dlls
        String command = "ikvmc -nologo "+references+" -target:library " + jarFolder.resolve(libraryName + ".jar") + " -out:" + dllFile;

        log.info(command);
        runCli(command);

        producedDllFiles.add(dllFile);
      }

      log.info("done with "+libraryName);
    }
  }


  public String getCli(String command) {

    Runtime rt = Runtime.getRuntime();

    try {
      Process pr = rt.exec(command);

      String output = "";

      BufferedReader stdInput = new BufferedReader(new
          InputStreamReader(pr.getInputStream()));

      BufferedReader stdError = new BufferedReader(new
          InputStreamReader(pr.getErrorStream()));

      String line = null;
      while ((line = stdInput.readLine()) != null) {
        output += line + "\n";
      }
      while ((line = stdError.readLine()) != null) {
        output += line + "\n";
      }

      return output.trim();

    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }

    return "";
  }
  public void runCli(String command) {
    runCli(command, null);
  }
  public void runCli(String command, Path path) {

    Runtime rt = Runtime.getRuntime();

    try {
      String[] env = new String[]{};
      Process pr;
      if(path != null) {
        pr = rt.exec(command, env, path.toFile());
      } else {
        pr = rt.exec(command);
      }


      StreamGobbler errorGobbler = new StreamGobbler(pr.getErrorStream());
      StreamGobbler outputGobbler = new StreamGobbler(pr.getInputStream());
      errorGobbler.start();
      outputGobbler.start();
      pr.waitFor();

    } catch (InterruptedException e) {
      log.error(e.getMessage(), e);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }

  private static String files(Path path, String extension, String delimiter) {
    return files(path, extension, delimiter, null);
  }
  private static String files(Path path, String extension, String delimiter, Path relativeTo) {

    String result = "";
    File[] directoryListing = path.toFile().listFiles();
    if (directoryListing != null) {
      for (File child : directoryListing) {
        if("".equals(extension) || child.getName().endsWith(extension)) {

          if(relativeTo != null) {
            result += relativeTo.relativize(Paths.get(child.getAbsolutePath())) + delimiter;
          } else {
            result += child.getAbsolutePath() + delimiter;
          }
        }
      }
    }
    return result;
  }

  // Thanks to jitter@stackoverflow
  public class StreamGobbler extends Thread {
    InputStream is;

    // reads everything from is until empty
    StreamGobbler(InputStream is) {
      this.is = is;
    }

    public void run() {
      try {
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line = null;
        while ( (line = br.readLine()) != null)
          log.info(line);
      } catch (IOException ioe) {
        ioe.printStackTrace();
      }
    }
  }
}
