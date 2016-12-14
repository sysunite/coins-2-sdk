package nl.coinsweb.sdk.integration.d.owlgenerator;

import nl.coinsweb.sdk.cli.Run;
import nl.coinsweb.sdk.integration.IntegrationHelper;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */

public class D2_DynamicLoadElephant {

  protected static final Logger log = LoggerFactory.getLogger(D2_DynamicLoadElephant.class);







  @Test
  public void generateJarAndCheckContent() {

    String command =

        "coins-cli generate -q"+
            " -i "+ IntegrationHelper.getResourceFile("D4","olifant.rdf").getAbsolutePath() +
            " -o /tmp/coinstest/D2/java/ " +
            " -jarto /tmp/coinstest/D2/jar/";

    String result = Run.getCli(command);

    log.info(command);
    log.info(result);


    try {

      URL jarUrl = new URL("file:/tmp/coinstest/D2/jar/elephant.jar");
      URL[] jars = { jarUrl};
      URLClassLoader child = new URLClassLoader(jars, this.getClass().getClassLoader());

      Iterator<String> classes = listClasses(jarUrl).iterator();
      while(classes.hasNext()) {

        String canonicalClassName = classes.next();

        Class classToLoad = Class.forName(canonicalClassName, true, child);
        Iterator<Method> methods = Arrays.asList(classToLoad.getDeclaredMethods()).iterator();
        while(methods.hasNext()) {
          Method method = methods.next();
          log.info(
              canonicalClassName+" "+
              method.getReturnType()+" "+
              method.getName()+" "+
              method.getParameterTypes().length);
        }
      }




      FileUtils.deleteDirectory(new File("/tmp/coinstest/D2"));

    } catch (ClassNotFoundException e) {
      log.error(e.getMessage(), e);
    } catch (MalformedURLException e) {
      log.error(e.getMessage(), e);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }



  }


  private static ArrayList<String> listClasses(URL jarPath) {
    ArrayList<String> classNames = new ArrayList<>();
    try {
      JarInputStream crunchifyJarFile = new JarInputStream(new FileInputStream(jarPath.getFile()));
      JarEntry crunchifyJar;

      while (true) {
        crunchifyJar = crunchifyJarFile.getNextJarEntry();
        if (crunchifyJar == null) {
          break;
        }
        if ((crunchifyJar.getName().endsWith(".class"))) {
          String className = crunchifyJar.getName().replaceAll("/", "\\.");
          String myClass = className.substring(0, className.lastIndexOf('.'));
          classNames.add(myClass);
        }
      }
    } catch (Exception e) {
      log.error("problem listing classes", e);
    }
    return classNames;
  }



}