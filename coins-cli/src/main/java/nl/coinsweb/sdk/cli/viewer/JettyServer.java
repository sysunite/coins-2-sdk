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

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import nl.coinsweb.sdk.ExpertCoinsModel;
import nl.coinsweb.sdk.RuntimeCoinsObject;
import nl.coinsweb.sdk.apolda.language.Language;
import nl.coinsweb.sdk.cli.viewer.servlet.ApiServlet;
import nl.coinsweb.sdk.cli.viewer.servlet.AttachmentServlet;
import nl.coinsweb.sdk.exceptions.CoinsObjectNotFoundException;
import nl.coinsweb.sdk.jena.JenaCoinsContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class JettyServer {

  private static final Logger log = LoggerFactory.getLogger(JettyServer.class);

  int port;
  String url;
  HashSet<ApiServlet> servlets = new HashSet<>();

  public JettyServer() {
      port = obtainPort(new int[]{80, 8080, 8090});
      url = (port==80)?"http://localhost":"http://localhost:"+port;
  }

  public String getUrl() {
    return url;
  }

  public void setContainer(JenaCoinsContainer container) {
    for(ApiServlet servlet : servlets) {
      servlet.setContainer(container);
    }
  }

  public class Thing {

    public Thing(String uri, ExpertCoinsModel model) {
      this.uri = uri;


      this.clazz = ((OntModel)model.getCoinsGraphSet().getUnionJenaOntModel()).getOntClass(uri);
      if (clazz != null) {

        if (Language.getLabel(clazz) != null) {
          label = Language.getLabel(clazz);
        }

        if (clazz.getComment("EN") != null) {
          comment = clazz.getComment("EN");
        }
      }

      try {
        String foundName = model.getLiteralValue(uri, "http://www.coinsweb.nl/cbim-2.0.rdf#name", String.class);
        if(foundName != null) {
          name = foundName;
        }
      } catch (RuntimeException e) {}

      try {
        Integer foundVersion = model.getLiteralValue(uri, "http://www.coinsweb.nl/cbim-2.0.rdf#versionID", Integer.class);
        if(foundVersion != null) {
          version = foundVersion.toString();
        }
      } catch (RuntimeException e) {}

      try {
        Iterator<RuntimeCoinsObject> iterator = model.listProperties(uri, "http://www.coinsweb.nl/cbim-2.0.rdf#creator", "http://www.coinsweb.nl/cbim-2.0.rdf#Party");
        if(iterator.hasNext()) {
          user = iterator.next().getUri();
        }
      } catch (RuntimeException e) {}
    }

    String label = "";
    String getLabel() { return label; }
    String name = "";
    String getName() { return name; }
    String user = "";
    String getUser() { return user; }
    String version = "";
    String getVersion() { return version; }
    String modifier = "";
    String getModifier() { return modifier; }
    String modificationDate = "";
    String getModificationDate() { return modificationDate; }
    String comment = "";
    String getComment() { return comment; }
    String uri;
    String getUri() { return uri; }
    OntClass clazz;
    OntClass getClazz() { return clazz; }
  }

  public void run() {

    try {

      if(port == 0) {
        return;
      }


      String webDir = JettyServer.class.getClassLoader().getResource("html").toExternalForm();

      Server server = new Server(port);
      ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

      FilterHolder cors = context.addFilter(CrossOriginFilter.class,"/*", EnumSet.of(DispatcherType.REQUEST));
      cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
      cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
      cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD");
      cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");


      server.setHandler(context);

      context.setWelcomeFiles(new String[]{"index.html"});
      context.setResourceBase(webDir);
      context.setContextPath("/");

      context.addServlet(new ServletHolder(new DefaultServlet()),"/*");
      AttachmentServlet attachmentServlet = new AttachmentServlet();
      servlets.add(attachmentServlet);
      context.addServlet(new ServletHolder(attachmentServlet), "/attachment/*");







      ApiServlet classesServlet = new ApiServlet() {
        @Override
        public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        String result = "[]";
        if(container != null) {

//          && request.getParameter("sort") != null
//              && request.getParameter("offset") != null
//              && request.getParameter("limit") != null
//              && request.getParameter("filter") != null
//
//          String sort = request.getParameter("sort");
//          String offset = request.getParameter("offset");
//          String limit = request.getParameter("limit");
//          String filter = request.getParameter("filter");

          result = "";

          Iterator<String> classes = container.listClasses();
          while(classes.hasNext()) {
            String classUri = classes.next();
            Thing thing = new Thing(classUri, container);

            result +=
                "{\n" +
                "\"uri\": \""+classUri+"\",\n" +
                "\"label\": \""+ thing.getLabel()+"\",\n" +
                "\"comment\": \""+thing.getComment() +"\"\n" +
                "},";

          }
          if(!result.isEmpty()) {
            result = "[" + result.substring(0, result.length() - 1) + "]";
          }
        }

        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().print(result);
        response.getWriter().close();
        }
      };
      servlets.add(classesServlet);
      context.addServlet(new ServletHolder(classesServlet), "/api/classes");






      ApiServlet singleClassServlet = new ApiServlet() {
        @Override
        public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        String result = "{}";
        if(container != null && request.getParameter("uri") != null) {

          String classUri = request.getParameter("uri");
          Thing thing = new Thing(classUri, container);

          OntClass clazz = thing.getClazz();
          if (clazz != null) {



            String superClasses = "";
            ExtendedIterator<OntClass> superClassIterator = clazz.listSuperClasses();
            while(superClassIterator.hasNext()) {
              OntClass superClass = superClassIterator.next();
              if(!superClass.isAnon()) {
                Thing indiv = new Thing(superClass.getURI(), container);
                superClasses += "{\"uri\":\"" + superClass.getURI() + "\",\"label\":\""+indiv.getLabel()+"\"},";
              }
            }
            if(!superClasses.isEmpty()) {
              superClasses = superClasses.substring(0, superClasses.length() - 1);
            }

            String subClasses = "";
            ExtendedIterator<OntClass> subClassIterator = clazz.listSubClasses();
            while(subClassIterator.hasNext()) {
              OntClass subClass = subClassIterator.next();
              if(!subClass.isAnon()) {
                Thing indiv = new Thing(subClass.getURI(), container);

                subClasses += "{\"uri\":\"" + subClass.getURI() + "\",\"label\":\""+indiv.getLabel()+"\"},";
              }
            }
            if(!subClasses.isEmpty()) {
              subClasses = subClasses.substring(0, subClasses.length() - 1);
            }

            String individuals = "";
            Iterator<String> individualIterator = container.listIndividuals(classUri);
            while(individualIterator.hasNext()) {
              String individualUri = individualIterator.next();

              Thing indiv = new Thing(individualUri, container);

              individuals+="{\"uri\":\""+individualUri+"\",\"" +
                              "name\":\""+indiv.getName()+"\",\"" +
                              "user\":\""+indiv.getUser()+"\",\"" +
                              "version\":\""+indiv.getVersion()+"\",\"" +
                              "modifier\":\""+indiv.getModifier()+"\",\"" +
                              "modificationDate\":\""+indiv.getModificationDate()+"\"},";
            }
            if(!individuals.isEmpty()) {
              individuals = individuals.substring(0, individuals.length() - 1);
            }

            try {

              result =
                  "{\n" +
                  "\"uri\": \"" + classUri + "\",\n" +
                  "\"label\": \""+thing.getLabel()+"\",\n" +
                  "\"comment\": \""+thing.getComment()+"\",\n" +
                  "\"superclasses\": ["+superClasses+"],\n" +
                  "\"subclasses\": ["+subClasses+"],\n" +
                  "\"individuals\": ["+individuals+"]\n" +
                  "}";


            } catch (CoinsObjectNotFoundException e) {
              // Could not be found
              log.info("Class " + classUri + " not found.");
            }

            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().print(result);
            response.getWriter().close();
          }
        }
        }
      };
      servlets.add(singleClassServlet);
      context.addServlet(new ServletHolder(singleClassServlet), "/api/class");






      ApiServlet individualsServlet = new ApiServlet() {
        @Override
        public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        String result = "[]";
        if(container != null) {
//            && request.getParameter("sort") != null
//            && request.getParameter("offset") != null
//            && request.getParameter("limit") != null
//            && request.getParameter("filter") != null
//
//          String sort = request.getParameter("sort");
//          String offset = request.getParameter("offset");
//          String limit = request.getParameter("limit");
//          String filter = request.getParameter("filter");

            result = "";

            Iterator<String> individuals = container.listIndividuals();
            while(individuals.hasNext()) {
              String individualUri = individuals.next();

              Thing indiv = new Thing(individualUri, container);

              result +=
                  "{\"uri\":\""+individualUri+"\",\"" +
                      "name\":\""+indiv.getName()+"\",\"" +
                      "user\":\""+indiv.getUser()+"\",\"" +
                      "version\":\""+indiv.getVersion()+"\",\"" +
                      "modifier\":\""+indiv.getModifier()+"\",\"" +
                      "modificationDate\":\""+indiv.getModificationDate()+"\"},";

            }
            if(!result.isEmpty()) {
              result = "[" + result.substring(0, result.length() - 1) + "]";
            }
          }

          response.setContentType("application/json;charset=utf-8");
          response.setStatus(HttpServletResponse.SC_OK);
          response.getWriter().print(result);
          response.getWriter().close();
        }
      };
      servlets.add(individualsServlet);
      context.addServlet(new ServletHolder(individualsServlet), "/api/individuals");






      ApiServlet singleIndividualServlet = new ApiServlet() {
        @Override
        public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        String result = "{}";
        if(container != null && request.getParameter("uri") != null) {

          String individualUri = request.getParameter("uri");
          try {

            RuntimeCoinsObject individual = container.getIndividual(individualUri);
            if(individual != null) {





              String types = "";
              Iterator<String> typeIterator = individual.listClassUris().iterator();
              while(typeIterator.hasNext()) {
                String typeUri = typeIterator.next();


                Thing indiv = new Thing(typeUri, container);
                types+="{\"uri\":\""+typeUri+"\",\"label\":\""+indiv.getLabel()+"\",\"comment\":\""+indiv.getComment()+"\"},";
              }
              if(!types.isEmpty()) {
                types = types.substring(0, types.length() - 1);
              }

              String properties = "";
              ExtendedIterator<Triple> propertyIterator = ((Model)container.getCoinsGraphSet().getInstanceModel()).getGraph().find(new ResourceImpl(individualUri).asNode(), Node.ANY, Node.ANY);
              while(propertyIterator.hasNext()) {
                Triple triple = propertyIterator.next();
                if(triple.getObject().isLiteral()) {
                  properties+="{\"key\":\"" + triple.getPredicate().getURI() + "\",\"datatype\":\""+triple.getObject().getLiteralDatatypeURI()+"\",\"value\":\""+triple.getObject().getLiteralLexicalForm()+"\"},";
                }
              }
              if(!properties.isEmpty()) {
                properties = properties.substring(0, properties.length() - 1);
              }



              String incoming = "";
              ExtendedIterator<Triple> incomingIterator = ((Model)container.getCoinsGraphSet().getInstanceModel()).getGraph().find(Node.ANY, Node.ANY, new ResourceImpl(individualUri).asNode());
              while(incomingIterator.hasNext()) {
                Triple triple = incomingIterator.next();
                Thing indiv = new Thing(triple.getSubject().getURI(), container);
                incoming+="{\"relation\":\""+triple.getPredicate().getURI()+"\",\"subjectUri\":\""+triple.getSubject().getURI()+"\",\"subjectName\":\""+indiv.getName()+"\"},";
              }
              if(!incoming.isEmpty()) {
                incoming = incoming.substring(0, incoming.length() - 1);
              }

              String outgoing = "";
              ExtendedIterator<Triple> outgoingIterator = ((Model)container.getCoinsGraphSet().getInstanceModel()).getGraph().find(new ResourceImpl(individualUri).asNode(), Node.ANY, Node.ANY);
              while(outgoingIterator.hasNext()) {
                Triple triple = outgoingIterator.next();
                if(triple.getObject().isURI()) {
                  Thing indiv = new Thing(triple.getObject().getURI(), container);
                  outgoing += "{\"relation\":\"" + triple.getPredicate().getURI() + "\",\"objectUri\":\"" + triple.getObject().getURI() + "\",\"objectName\":\""+indiv.getName()+"\"},";
                }
              }
              if(!outgoing.isEmpty()) {
                outgoing = outgoing.substring(0, outgoing.length() - 1);
              }

              Thing indiv = new Thing(individualUri, container);
              result =
                  "{\n" +
                  "\"uri\": \"" + individualUri + "\",\n" +
                  "\"name\": \""+indiv.getName()+"\",\n" +
                  "\"user\": \""+indiv.getUser()+"\",\n" +
                  "\"version\": \""+indiv.getVersion()+"\",\n" +
                  "\"modifier\": \""+indiv.getModifier()+"\",\n" +
                  "\"modificationDate\": \""+indiv.getModificationDate()+"\",\n" +
                  "\"types\" : ["+types+"],\n" +                         //{uri, label, comment}
                  "\"properties\": ["+properties+"],\n" +                //{key, datatype, value}
                  "\"incomingRelations\": ["+incoming+"],\n" +           //{relation, subjectUri, subjectName}
                  "\"outgoingRelations\": ["+outgoing+"]\n" +            //{relation, objectUri, objectName}
                  "}";


            }
          } catch(CoinsObjectNotFoundException e) {
            // Could not be found
            log.info("Individual "+individualUri+" not found.");
          }
        }

        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().print(result);
        response.getWriter().close();
        }
      };
      servlets.add(singleIndividualServlet);
      context.addServlet(new ServletHolder(singleIndividualServlet), "/api/individual");






      ApiServlet attachmentsServlet = new ApiServlet() {
        @Override
        public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        String result = "[]";
        if(container != null) {

          result = "";

          for(String attachment: container.getAttachments()) {

            String fileName = new File(attachment).getName();
            result += "{\"name\":\""+fileName+"\",\"location\":\""+url+"/attachment/"+fileName+"\"},";
          }
          if(!result.isEmpty()) {
            result = "["+result.substring(0, result.length()-1)+"]";
          }
        }

        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().print(result);
        response.getWriter().close();
        }
      };
      servlets.add(attachmentsServlet);
      context.addServlet(new ServletHolder(attachmentsServlet),"/api/attachments");








      server.start();

    } catch(Exception e) {
      log.error("",e);
    }
  }



  public int obtainPort(int[] ports) {
    for (int port : ports) {
      try {
        new ServerSocket(port).close();
        return port;
      } catch (IOException ex) {
        continue; // try next port
      }
    }

    return 0;
  }
}
