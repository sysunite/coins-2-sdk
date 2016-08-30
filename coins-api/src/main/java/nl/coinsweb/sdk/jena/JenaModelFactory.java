package nl.coinsweb.sdk.jena;

import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.tdb.TDBFactory;
import nl.coinsweb.sdk.CoinsModel;
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

    refreshModel(instanceNamespace.toString(), instanceModel);
    refreshModel(woaNamespace.toString(), woaModel);
    for(Namespace ns : libraryModels.keySet()) {
      refreshModel(ns.toString(), libraryModels.get(ns));
    }
    return dataset;
  }

  public Dataset getDatasetWithUnionGraphs(CoinsModel model) {

    Model instanceModel = (Model) model.getCoinsGraphSet().getInstanceModel();
    Model woaModel = (Model) model.getCoinsGraphSet().getWoaModel();
    Model schemaModel = (Model) model.getCoinsGraphSet().getJenaModel("http://www.coinsweb.nl/cbim-2.0.rdf");

    Model schemaUnionModel = getEmptyModel();
    schemaUnionModel.add(schemaModel);
    for(Namespace key : model.getCoinsGraphSet().getLibraryModels().keySet()) {
      schemaUnionModel.add(model.getCoinsGraphSet().getLibraryModels().get(key));
    }

    Model fullUnionModel = getEmptyModel();
    fullUnionModel.add(instanceModel);
    fullUnionModel.add(woaModel);
    fullUnionModel.add(schemaUnionModel);

    refreshModel("INSTANCE_GRAPH", instanceModel);
    refreshModel("WOA_GRAPH", woaModel);
    refreshModel("SCHEMA_GRAPH", schemaModel);
    refreshModel("SCHEMA_UNION_GRAPH", schemaUnionModel);
    refreshModel("FULL_UNION_GRAPH", fullUnionModel);

    return dataset;
  }

  void refreshModel(String ns, Model model) {
    if(dataset.containsNamedModel(ns)) {
      dataset.replaceNamedModel(ns, model);
    } else {
      dataset.addNamedModel(ns, model);
    }
  }

  @Override
  public void close() {
    dataset.close();
  }
}
