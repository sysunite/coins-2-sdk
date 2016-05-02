package nl.coinsweb.sdk;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;

import java.util.Map;

/**
 * @author Bastiaan Bijl
 */
public interface ModelFactory {

  public void setReasoner(OntModelSpec modelSpec);
  public Model getEmptyModel();
  public OntModel asOntModel(Model model);
  public Dataset getDataset(Namespace instanceNamespace, Model instanceModel,
                            Namespace woaNamespace, Model woaModel,
                            Map<Namespace, Model> libraryModels);
  public void close();
}
