package nl.coinsweb.sdk.integration.owlgenerator;

import nl.coinsweb.sdk.cli.Run;
import nl.coinsweb.sdk.integration.IntegrationHelper;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import nl.coinsweb.sdk.owlgenerator.ClassGenerateEngine;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */

public class D3_IterateRestrictions {

  protected static final Logger log = LoggerFactory.getLogger(D3_IterateRestrictions.class);







  @Test
  public void iterateOverRequirementSpecification() {

    Path path = IntegrationHelper.getResourceFile("D3", "SE.rdf").toPath();



    JenaCoinsContainer model = new JenaCoinsContainer(true);


    ArrayList<String> sourceFileNames = new ArrayList<>();
    model.addImport(path.toFile().toString(), null, true, true, true);
    sourceFileNames.add(path.getFileName().toString());

    ClassGenerateEngine engine = new ClassGenerateEngine();

    engine.initTemplateFields();
    engine.processPropertyDeclarationsSparql(model, "http://www.coinsweb.nl/SE.rdf#RequirementSpecification");
  }



}