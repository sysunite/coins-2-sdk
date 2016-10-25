package nl.coinsweb.sdk.integration.modelnames;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;
import com.hp.hpl.jena.update.UpdateAction;
import com.hp.hpl.jena.update.UpdateRequest;
import nl.coinsweb.sdk.CoinsGraphSet;
import nl.coinsweb.sdk.integration.DatasetAsserts;
import nl.coinsweb.sdk.jena.InMemGraphSet;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class H3_Unions {

  protected static final Logger log = LoggerFactory.getLogger(H3_Unions.class);

  @Test
  public void mickyMouseInMemExample() {

    CoinsGraphSet graphSet = new InMemGraphSet("http://dfg");


    Model a = graphSet.getEmptyModel();
    Model b = graphSet.getEmptyModel();
    Model union = graphSet.getEmptyModel();

    a.add(new StatementImpl(new ResourceImpl("http://x"), new PropertyImpl("http://y"), new ResourceImpl("http://z")));
    b.add(new StatementImpl(new ResourceImpl("http://k"), new PropertyImpl("http://l"), new ResourceImpl("http://m")));

    log.info("union graph");
    DatasetAsserts.logTriples(union);

    union.add(new StatementImpl(new ResourceImpl("http://new"), new PropertyImpl("http://new"), new ResourceImpl("http://new")));
    log.info("union graph");
    DatasetAsserts.logTriples(union);
    log.info("a");
    DatasetAsserts.logTriples(a);
    log.info("b");
    DatasetAsserts.logTriples(b);



    Dataset dataset = graphSet.getEmptyDataset();


    dataset.addNamedModel("http://a", a);
    dataset.addNamedModel("http://b", b);
    dataset.addNamedModel("http://union", union);

    log.info("dataset before adding");
    DatasetAsserts.logTriples(dataset);

    UpdateRequest request = new UpdateRequest();
    request.add("INSERT DATA { GRAPH <http://union> { <http://insert> <http://insert> <http://insert> } }");            // urn:x-arq:UnionGraph
    UpdateAction.execute(request, dataset);




    log.info("dataset after adding");
    DatasetAsserts.logTriples(dataset);

    log.info("union graph");
    DatasetAsserts.logTriples(union);
    log.info("a");
    DatasetAsserts.logTriples(a);
    log.info("b");
    DatasetAsserts.logTriples(b);





    log.info("union graph");
    DatasetAsserts.logTriples(dataset.getNamedModel("http://union"));
    log.info("a");
    DatasetAsserts.logTriples(dataset.getNamedModel("http://a"));
    log.info("b");
    DatasetAsserts.logTriples(dataset.getNamedModel("http://b"));


  }

}