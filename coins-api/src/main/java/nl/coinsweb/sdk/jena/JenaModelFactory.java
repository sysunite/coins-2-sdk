package nl.coinsweb.sdk.jena;

import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.tdb.TDBFactory;
import nl.coinsweb.sdk.ModelFactory;
import nl.coinsweb.sdk.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author Bastiaan Bijl
 */
public class JenaModelFactory implements ModelFactory {

  private static final Logger log = LoggerFactory.getLogger(JenaModelFactory.class);

  Dataset dataset;



  //  OntModelSpec ontModelSpec = OntModelSpec.OWL_MEM_MICRO_RULE_INF;
  OntModelSpec ontModelSpec = OntModelSpec.OWL_MEM_RDFS_INF;
  //  OntModelSpec ontModelSpec = OntModelSpec.OWL_MEM;

  public JenaModelFactory() {
    dataset = TDBFactory.createDataset();
  }

  @Override
  public void setOntModelSpec(OntModelSpec modelSpec) {
    ontModelSpec = modelSpec;
  }

  @Override
  public Model getEmptyModel() {
    return com.hp.hpl.jena.rdf.model.ModelFactory.createDefaultModel();
  }

  @Override
  public OntModel asOntModel(Model model) {

    // Set document manager policy file
    OntDocumentManager dm = new OntDocumentManager();
    dm.setProcessImports(false);

    OntModelSpec modelSpec = ontModelSpec;
    modelSpec.setDocumentManager(dm);

    OntModel result = com.hp.hpl.jena.rdf.model.ModelFactory.createOntologyModel(modelSpec, model);

    return result;
  }

  @Override
  public OntModel asOntModel(Model model, Reasoner reasoner) {

    // Set document manager policy file
    OntDocumentManager dm = new OntDocumentManager();
    dm.setProcessImports(false);

    OntModelSpec modelSpec = ontModelSpec;
    modelSpec.setDocumentManager(dm);
    modelSpec.setReasoner(reasoner);

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

  void refreshModel(Namespace ns, Model model) {
    if(dataset.containsNamedModel(ns.toString())) {
      dataset.replaceNamedModel(ns.toString(), model);
    } else {
      dataset.addNamedModel(ns.toString(), model);
    }
  }

  @Override
  public void close() {
    dataset.close();
  }
}
