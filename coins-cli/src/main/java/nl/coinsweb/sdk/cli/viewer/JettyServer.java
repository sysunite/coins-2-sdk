package nl.coinsweb.sdk.cli.viewer;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
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
            OntClass clazz = container.getUnionJenaOntModel().getOntClass(classUri);

            String label = "";
            if(Language.getLabel(clazz)!= null) {
              label = Language.getLabel(clazz);
            }

            String comment = "";
            if(clazz.getComment("EN")!=null) {
              comment = clazz.getComment("EN");
            }

            result +=
                "{\n" +
                "\"uri\": \""+classUri+"\",\n" +
                "\"label\": \""+ label+"\",\n" +
                "\"comment\": \""+comment +"\"\n" +
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

          OntClass clazz = container.getUnionJenaOntModel().getOntClass(classUri);
          if (clazz != null) {

            String label = "";
            if(Language.getLabel(clazz)!= null) {
              label = Language.getLabel(clazz);
            }

            String comment = "";
            if(clazz.getComment("EN")!=null) {
              comment = clazz.getComment("EN");
            }

            String superClasses = "";
            ExtendedIterator<OntClass> superClassIterator = clazz.listSuperClasses();
            while(superClassIterator.hasNext()) {
              OntClass superClass = superClassIterator.next();
              if(!superClass.isAnon()) {
                superClasses += "{\"uri\":\"" + superClass.getURI() + "\",\"label\":\"label\"},";
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
                subClasses += "{\"uri\":\"" + subClass.getURI() + "\",\"label\":\"label\"},";
              }
            }
            if(!subClasses.isEmpty()) {
              subClasses = subClasses.substring(0, subClasses.length() - 1);
            }

            String individuals = "";
            Iterator<String> individualIterator = container.listIndividuals(classUri);
            while(individualIterator.hasNext()) {
              String individualUri = individualIterator.next();
              individuals+="{\"uri\":\""+individualUri+"\",\"name\":\"name\",\"user\":\"user\",\"version\":\"version\",\"modifier\":\"modifier\",\"modificationDate\":\"modificationDate\"},";
            }
            if(!individuals.isEmpty()) {
              individuals = individuals.substring(0, individuals.length() - 1);
            }

            try {

              result =
                  "{\n" +
                  "\"uri\": \"" + classUri + "\",\n" +
                  "\"label\": \""+label+"\",\n" +
                  "\"comment\": \""+comment+"\",\n" +
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

              result +=
                  "{\n" +
                      "\"uri\": \""+individualUri+"\",\n" +
                      "\"name\": \"name\",\n" +
                      "\"user\": \"user\",\n" +
                      "\"version\": \"version\",\n" +
                      "\"modifier\": \"modifier\",\n" +
                      "\"modificationDate\": \"modificationDate\"\n" +
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
                types+="{\"uri\":\""+typeUri+"\",\"label\":\"label\",\"comment\":\"comment\"},";
              }
              if(!types.isEmpty()) {
                types = types.substring(0, types.length() - 1);
              }

              String properties = "";
              ExtendedIterator<Triple> propertyIterator = container.getJenaModel().getGraph().find(new ResourceImpl(individualUri).asNode(), Node.ANY, Node.ANY);
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
              ExtendedIterator<Triple> incomingIterator = container.getJenaModel().getGraph().find(Node.ANY, Node.ANY, new ResourceImpl(individualUri).asNode());
              while(incomingIterator.hasNext()) {
                Triple triple = incomingIterator.next();
                incoming+="{\"relation\":\""+triple.getPredicate().getURI()+"\",\"subjectUri\":\""+triple.getSubject().getURI()+"\",\"subjectName\":\"name\"},";
              }
              if(!incoming.isEmpty()) {
                incoming = incoming.substring(0, incoming.length() - 1);
              }

              String outgoing = "";
              ExtendedIterator<Triple> outgoingIterator = container.getJenaModel().getGraph().find(new ResourceImpl(individualUri).asNode(), Node.ANY, Node.ANY);
              while(outgoingIterator.hasNext()) {
                Triple triple = outgoingIterator.next();
                if(triple.getObject().isURI()) {
                  outgoing += "{\"relation\":\"" + triple.getPredicate().getURI() + "\",\"objectUri\":\"" + triple.getObject().getURI() + "\",\"objectName\":\"name\"},";
                }
              }
              if(!outgoing.isEmpty()) {
                outgoing = outgoing.substring(0, outgoing.length() - 1);
              }

              result =
                  "{\n" +
                  "\"uri\": \"" + individualUri + "\",\n" +
                  "\"name\": \"name\",\n" +
                  "\"user\": \"user\",\n" +
                  "\"version\": \"version\",\n" +
                  "\"modifier\": \"modifier\",\n" +
                  "\"modificationDate\": \"modificationDate\",\n" +
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
