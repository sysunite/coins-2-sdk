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
package nl.coinsweb.sdk.cli.viewer;

import nl.coinsweb.sdk.cli.Run;
import nl.coinsweb.sdk.cli.validate.ValidateOptions;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class RunViewer {

  private static final Logger log = LoggerFactory.getLogger(RunViewer.class);



  public static void go(String[] args) {

    ViewerOptions options;
    try {
      options = new ViewerOptions(args);
    } catch (ParseException e) {
      Run.printHeader();
      System.out.println("(!)" + e.getMessage() + "\n");
      ValidateOptions.usage();
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
    log.info("Started viewer.");



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

    JettyServer server = new JettyServer();
    if(server.getUrl() != null) {

      File sourceFile = new File(options.getInputOptions().get(0).toString());
      JenaCoinsContainer container = new JenaCoinsContainer(sourceFile);

      if(container != null) {

        // Start the webserver
        if(!Run.QUIET) {
          System.out.println("running coins viewer on " + options.getInputOptions().get(0).getFileName().toString());
          System.out.println("use your web browser to navigate to " + server.getUrl() );
          System.out.println("(use Ctrl-C to stop service)\n");
        }
        server.run();


        // Create the web server
        server.setContainer(container);

      } else {

        if(!Run.QUIET) {
          System.out.println("(!) unable to open " + options.getInputOptions().get(0).getFileName().toString());
        }

      }
    }
  }
}