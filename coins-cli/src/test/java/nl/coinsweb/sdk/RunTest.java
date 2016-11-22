package nl.coinsweb.sdk;

import nl.coinsweb.sdk.cli.Run;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import nl.coinsweb.sdk.owlgenerator.ClassGenerateEngine;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class RunTest {

//  @Test
  public void runDescribeEngine() {
    String[] args = { };
    Run.main(args);
  }

  //  @Test
  public void runEngine() {

    Path path = Paths.get(getClass().getClassLoader().getResource("Cbim-2.0-RC17.rdf").getPath());



    JenaCoinsContainer model = new JenaCoinsContainer(true);


    ArrayList<String> sourceFileNames = new ArrayList<>();
    model.addImport(path.toFile().toString(), null, true, true, true);
    sourceFileNames.add(path.getFileName().toString());

    ClassGenerateEngine engine = new ClassGenerateEngine();
    engine.process(model, sourceFileNames);

  }

  //  @Test
  public void runOwlGeneratorCore() {

    File file = new File(getClass().getClassLoader().getResource("Cbim-2.0-RC17.rdf").getFile());

    String[] args = {
        "-i",        file.getAbsolutePath(),
    };
    Run generator = new Run();
    generator.go(args);

  }




}
