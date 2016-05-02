package nl.coinsweb.sdk.jena;

import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;
import nl.coinsweb.sdk.ModelFactory;
import nl.coinsweb.sdk.Namespace;
import org.apache.commons.lang3.RandomStringUtils;
import org.codehaus.plexus.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * @author Bastiaan Bijl
 */
public class TDBModelFactory implements ModelFactory {

  private static final Logger log = LoggerFactory.getLogger(TDBModelFactory.class);

  private static final String DEFAULT_TEMP_FOLDER = "/tmp/";
  private static final String TDB_FOLDER = "coinstdb/";

  private Path path;

  Dataset dataset;



  //  private OntModelSpec reasoner = OntModelSpec.OWL_MEM_MICRO_RULE_INF;
  private OntModelSpec reasoner = OntModelSpec.OWL_MEM_RDFS_INF;
  //  private OntModelSpec reasoner = OntModelSpec.OWL_MEM;

  public TDBModelFactory() {
    log.info("start new tdb for namespace given file");
    Path tempPath = Paths.get(DEFAULT_TEMP_FOLDER);
    Path path = tempPath.resolve(TDB_FOLDER + RandomStringUtils.random(8, true, true) + "/");
    path.toFile().mkdirs();
    this.path = path;
    dataset = TDBFactory.createDataset(path.toString());
  }

  @Override
  public void setReasoner(OntModelSpec modelSpec) {
    reasoner = modelSpec;
  }

  @Override
  public Model getEmptyModel() {
    return com.hp.hpl.jena.rdf.model.ModelFactory.createDefaultModel();
  }

  @Override
  public OntModel asOntModel(Model model) {

    // Set document manager policy file
    OntDocumentManager dm = new OntDocumentManager();
    OntModelSpec modelSpec = reasoner;
    modelSpec.setDocumentManager(dm);
    dm.setProcessImports(false);
    OntModel result = com.hp.hpl.jena.rdf.model.ModelFactory.createOntologyModel(modelSpec, model);

    return result;
  }

  @Override
  public Dataset getDataset(Namespace instanceNamespace, Model instanceModel,
                            Namespace woaNamespace, Model woaModel,
                            Map<Namespace, Model> libraryModels) {

    refreshModel(instanceNamespace, instanceModel);
    refreshModel(woaNamespace, woaModel);
    for(Namespace ns : libraryModels.keySet()) {
      refreshModel(ns, libraryModels.get(ns));
    }
    return dataset;
  }

  private void refreshModel(Namespace ns, Model model) {
    if(dataset.containsNamedModel(ns.toString())) {
      dataset.replaceNamedModel(ns.toString(), model);
    } else {
      dataset.addNamedModel(ns.toString(), model);
    }
  }





  @Override
  public void close() {

    TDB.sync(dataset);
    dataset.close();
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      log.error(e.getMessage(), e);
    }

    // destroy
    try {
      FileUtils.deleteDirectory(path.toFile());
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }
}
