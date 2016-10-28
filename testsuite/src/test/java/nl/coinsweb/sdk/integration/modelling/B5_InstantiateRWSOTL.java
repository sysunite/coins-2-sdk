package nl.coinsweb.sdk.integration.modelling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Usecase B5:
 *
 *    Instantiate data based on a generated set of classes.
 *
 *
 *
 *
 * @author Bastiaan Bijl, Sysunite 2016
 */

public class B5_InstantiateRWSOTL {

  protected static final Logger log = LoggerFactory.getLogger(B5_InstantiateRWSOTL.class);




//  @Test
//  public void instantiateSomething() {
//
//    // Init the coins api
//    CoinsParty defaultPerson = new CoinsParty("http://sandbox.rws.nl/defaultUser");
//    CoinsModel workspace = new InMemCoinsModel("http://www.example.com/", defaultPerson);
//
//
//
//
//    // Create object
//    NCS0025091 object = new NCS0025091Impl(workspace);
//    log.info("class: " + object.atom().getUri());
//    log.info("instance: " + object.getUri());
//
//
//    // Create property
//    NCR0149411 property = new NCR0149411Impl(workspace);
//    log.info("class: " + property.atom().getUri());
//    log.info("instance: " + property.getUri());
//
//    property.setDatatypeValue(199);
//
//    object.setHasProperty_NCR0149411(property);
//
//
//    // Check if the object can be found
//    String findUsingUri = object.getUri();
//    NCS0025091 foundObject = new NCS0025091Impl(workspace, findUsingUri);
//
//    log.info("Retrieved instance:");
//    log.info(foundObject.getUri());
//
//    // Check if the property is there
//    NCR0149411 foundProperty = foundObject.getHasProperty_NCR0149411(NCR0149411Impl.atom);
//
//    log.info("Retrieved property:");
//    log.info(foundProperty.toString());
//
//
//
//    log.info("Search results:");
//    Iterator<NCS0025091Impl> searchResult = workspace.findCoinsObjects(NCS0025091Impl.atom);
//    while(searchResult.hasNext()) {
//      log.info(searchResult.next().toString());
//    }
//
//    log.info("Statements listing:");
////    StmtIterator statements = ((JenaCoinsModel)workspace).getInstanceModel().listStatements();
////    while (statements.hasNext()) {
////      Statement statement = statements.nextStatement();
////      if(!statement.getSubject().isAnon()) {
////        if(statement.getSubject().getNameSpace().equals("http://default.namespace.org/instances#") || statement.getPredicate().getNameSpace().equals("http://default.namespace.org/instances#")) {
////          log.info(statement);
////        }
////      }
////    }
//
//    // Export instance to rdf file in coins container
//    CoinsContainer container = FileManager.existingCoinsContainer(new InMemCoinsModelCreator(), "http://www.example.com/");
//    container.zip(Paths.get("/tmp/propertyinstance.ccr").toFile());
//    container.close();
//
//
//  }
}