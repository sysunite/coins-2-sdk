#------------------------------------------------------------------
# LITE profile v0.1
# $Id: lite.profile, v 0.1 2016-08-22
#------------------------------------------------------------------
#
# A COINS 2.0 profile file
#
# Any lines starting with a hash will be ignored.
#
# The profile consists of any number of these blocks:
#
# <ProfileCheck>          - must evaluate before this profile can be
# </ProfileCheck>           applied to a container
#
# <SchemaInference>       - inferences added to the schema before
# </SchemaInference>        validating
#
# <DataInference>         - inferences added to the data before
# </DataInference>          validating
#
# <ValidationRule>        - rules to use in the validation process,
# </ValidationRule>         the query should have not any results
#
# Each block should contain the following configuration
#
#     <ProfileCheck>              <ProfileCheck>
#       Reference ...               Reference ...
#       Description ...             Description ...
#     [ ResultFormat ... ]        [ ResultFormat ..  ]
#       <SparqlQuery>               <JenaRule>
#         ...               or        ...
#       </SparqlQuery>              </JenaRule>
#     </ProfileCheck>             </ProfileCheck>

#------------------------------------------------------------------
# Checks if the container meets the requirements for the profile
#------------------------------------------------------------------

<ProfileCheck>
  Reference lite-b
  Description "Een beschrijving mag ook in het Nederlands. Behalve een reference en een description is er mi niet zoiets nodig als een name."
  ResultFormat "The subject ${a} has predicate ${b} to object ${c}"
  <SparqlQuery>

  # these can be used:
  #  ${INSTANCE_GRAPH}, ${WOA_GRAPH}, ${CORE_GRAPH}, ${SCHEMA_UNION_GRAPH}, ${FULL_UNION_GRAPH}
  #
  #

  PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
  PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
  PREFIX owl: <http://www.w3.org/2002/07/owl#>
  PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

  select distinct ?a ?b ?c

  WHERE {
    Graph ${SCHEMA_UNION_GRAPH} {
      ?a ?b ?c .
      filter (STRSTARTS(STR(?b),"http://www.w3.org/2002/07/owl#")) .
      filter (?b not in (owl:allValuesFrom,owl:cardinality,owl:disjointWith,owl:distinctMembers,owl:hasValue,owl:imports,owl:intersectionOf,owl:inverseOf,owl:maxCardinality,owl:maxQualifiedCardinality,owl:members,owl:minCardinality,owl:minQualifiedCardinality,owl:onClass,owl:oneOf,owl:onProperty,owl:qualifiedCardinality,owl:someValuesFrom,owl:unionOf,owl:equivalentClass,owl:annotatedProperty,owl:annotatedSource,owl:annotatedTarget,owl:backwardCompatibleWith,owl:deprecated,owl:incompatibleWith,owl:priorVersion,owl:versionInfo,owl:versionIRI))
    }
  }
  </SparqlQuery>
</ProfileCheck>



#------------------------------------------------------------------
# Inferences performed on the schema union
#------------------------------------------------------------------

<SchemaInference>
  Reference prp-inv
  Description "Entailment of inverseOf values part 1, InverseOf value entailment part 2"

  <SparqlQuery>
  PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
  PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
  PREFIX owl: <http://www.w3.org/2002/07/owl#>
  PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

  INSERT {
    GRAPH ${INSTANCE_GRAPH} {
      ?y ?p2 ?x
    }
  }

  WHERE {
    GRAPH ${SCHEMA_UNION_GRAPH} {
      ?p1 owl:inverseOf ?p2 .
    }
    GRAPH ${INSTANCE_GRAPH} {
      ?x ?p1 ?y .
    }
  }
  </SparqlQuery>
</SchemaInference>

<SchemaInference>
  Reference scm-avf2
  Description "subclassing allvaluesfrom restriction part 2, Entailment of restriction subclasses via the allvaluesfrom construct part 2"

  <SparqlQuery>
  PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
  PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
  PREFIX owl: <http://www.w3.org/2002/07/owl#>
  PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

  insert {Graph <schemaGraph>{
  ?c2 rdfs:subClassOf ?c1
  }}

   WHERE {Graph <schemaGraph>{
    ?c1 owl:allValuesFrom ?y .     ?c1 owl:onProperty ?p1 .    ?c2 owl:allValuesFrom ?y .    ?c2 owl:onProperty ?p2 .    ?p1 rdfs:subPropertyOf ?p2
  }

  }
  </SparqlQuery>
</SchemaInference>

#------------------------------------------------------------------
# Inferences performed on the instance data
#------------------------------------------------------------------

<DataInference>
</DataInference>

#------------------------------------------------------------------
# Validation queries, the queries should not find any results
#------------------------------------------------------------------

<ValidationRule>
</ValidationRule>

<ValidationRule>
</ValidationRule>
