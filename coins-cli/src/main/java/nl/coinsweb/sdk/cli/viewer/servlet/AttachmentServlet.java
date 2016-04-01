package nl.coinsweb.sdk.cli.viewer.servlet;

import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class AttachmentServlet extends ApiServlet {

  protected static final Logger log = LoggerFactory.getLogger(AttachmentServlet.class);

  protected JenaCoinsContainer container;

  public AttachmentServlet() {

  }


  public AttachmentServlet setContainer(JenaCoinsContainer container) {
    this.container = container;
    return this;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

    if(container == null) {
      return;
    }

    FileInputStream fis = null;
    OutputStream out = null;

    try {

      String fileName = new File(new URI(request.getRequestURI()).getPath()).getName();



      fis = new FileInputStream(container.getAttachment(fileName));
      response.setContentType("application/octet-stream");

      out = response.getOutputStream();
      IOUtils.copy(fis, out);

    } catch (URISyntaxException e) {

    } finally {

      IOUtils.closeQuietly(out);  // this is using apache-commons,
      IOUtils.closeQuietly(fis);  // make sure you provide required JARs

    }
  }



}