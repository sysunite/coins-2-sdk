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
package nl.coinsweb.sdk.cli.map;

import nl.coinsweb.sdk.cli.Run;
import nl.coinsweb.sdk.cli.validate.ValidateOptions;
import org.apache.commons.cli.ParseException;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.shared.NoWriterForLangException;
import com.hp.hpl.jena.util.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class RunMap {

  private static final Logger log = LoggerFactory.getLogger(RunMap.class);



  public static void go(String[] args) {

    MapOptions options;
    try {
      options = new MapOptions(args);
    } catch (ParseException e) {
      System.out.println(e.getMessage() + "\n");
      Run.printHeader();
      MapOptions.usage();
      System.exit(1);
      return;
    }

    // Print header
    Run.QUIET = options.quietMode();
    Run.printHeader();

    // Asked for help
    if(options.printHelpOption()) {
      ValidateOptions.usage();
      System.exit(1);
      return;
    }

    Run.startLoggingToFile();
    log.info("Started mapping.");


    if(!options.hasInputOption() || options.getInputOptions().isEmpty()) {
      if(!Run.QUIET) {
        System.out.println("(!) no input file specified");
      }
      return;
    }

    if(options.hasInputOption() && options.getInputOptions().size() > 1) {
      if(!Run.QUIET) {
        System.out.println("(!) too many input files specified");
      }
      return;
    }

    if(!options.hasOutputOption()) {
      if(!Run.QUIET) {
        System.out.println("(!) no output file specified");
      }
      return;
    }

    if(!options.hasEncodingOption()) {
      if(!Run.QUIET) {
        System.out.println("(!) no encoding specified");
      }
      return;
    }

    try {
      Model turtleModel = FileManager.get().loadModel(options.getInputOptions().get(0).toAbsolutePath().toString());
      turtleModel.write(new FileWriter(options.getOutputOption().toAbsolutePath().toString()), options.getEncodingOption());
    } catch (IOException e) {
    } catch (NoWriterForLangException e) {
      if(!Run.QUIET) {
        System.out.println("the specified encoding is not supported, try one of these: "+MapOptions.JENA_ENCODINGS);
      }
    }




  }
}
