package nl.coinsweb.sdk.cli.viewer;

import nl.coinsweb.sdk.cli.CliOptions;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class RunViewer {

  private static final Logger log = LoggerFactory.getLogger(RunViewer.class);



  public static void go(CliOptions options) {


    if(!options.hasInputOption() || options.getInputOptions().isEmpty()) {
      System.out.println("no input file specified");
      return;
    }

    if(options.hasInputOption() && options.getInputOptions().size() > 1) {
      System.out.println("too many input files specified");
      return;
    }

    JettyServer server = new JettyServer();
    if(server.getUrl() != null) {


      JenaCoinsContainer container = new JenaCoinsContainer(options.getInputOptions().get(0).toString(), "http://www.example.com/");

      if(container != null) {

        // Start the webserver
        System.out.println("running coins viewer on " + options.getInputOptions().get(0).getFileName().toString());
        System.out.println("use your web browser to navigate to " + CliOptions.ANSI_BOLD + server.getUrl() + CliOptions.ANSI_RESET);
        System.out.println("(use Ctrl-C to stop service)\n");
        server.run();


        // Create the web server
        server.setContainer(container);

      } else {

        System.out.println("unable to open " + options.getInputOptions().get(0).getFileName().toString());

      }
    }
  }
}