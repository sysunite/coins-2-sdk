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