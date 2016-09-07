ProfileName Test Profile
ProfileAuthor Bastiaan



<DataInference>

Reference reverse instance relations
Description Baaa
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

insert {Graph ${INSTANCE_GRAPH}{
?o ?p ?s
}}

 WHERE {Graph ${INSTANCE_GRAPH}{
  ?s ?p ?o
}
}
</SparqlQuery>

</DataInference>
<SchemaInference>

Reference reverse schema relations
Description Baaa
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

insert {Graph ${SCHEMA_UNION_GRAPH}{
?o ?p ?s
}}

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
  ?s ?p ?o
}
}
</SparqlQuery>

</SchemaInference>
