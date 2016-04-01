package nl.coinsweb.sdk.cli.map;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.shared.NoWriterForLangException;
import com.hp.hpl.jena.util.FileManager;
import nl.coinsweb.sdk.cli.CliOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class RunMap {

  private static final Logger log = LoggerFactory.getLogger(RunMap.class);



  public static void go( CliOptions options ) {


    if(!options.hasInputOption() || options.getInputOptions().isEmpty()) {
      System.out.println("no input file specified");
      return;
    }

    if(options.hasInputOption() && options.getInputOptions().size() > 1) {
      System.out.println("too many input files specified");
      return;
    }

    if(!options.hasOutputOption()) {
      System.out.println("no output file specified");
      return;
    }

    if(!options.hasEncodingOption()) {
      System.out.println("no encoding specified");
      return;
    }

    try {
      Model turtleModel = FileManager.get().loadModel(options.getInputOptions().get(0).toAbsolutePath().toString());
      turtleModel.write(new FileWriter(options.getOutputOption().toAbsolutePath().toString()), options.getEncodingOption());
    } catch (IOException e) {
    } catch (NoWriterForLangException e) {
      System.out.println("the specified encoding is not supported, try one of these: "+CliOptions.JENA_ENCODINGS);
    }


  }


}
