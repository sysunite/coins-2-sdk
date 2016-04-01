package nl.coinsweb.sdk.cli.viewer.servlet;

import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class ApiServlet extends HttpServlet {

  protected static final Logger log = LoggerFactory.getLogger(ApiServlet.class);

  protected JenaCoinsContainer container;

  public ApiServlet() {

  }


  public ApiServlet setContainer(JenaCoinsContainer container) {
    this.container = container;
    return this;
  }




}