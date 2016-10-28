#------------------------------------------------------------------
# GRAPH TEST profile v0.1
# $Id: graphtest.profile, v 0.1 2016-10-17
#------------------------------------------------------------------
#
# A COINS 2.0 profile file

ProfileName GraphSet test
ProfileAuthor Bastiaan Bijl
#
# This profile is used to test any GraphSet implementation. A GraphSet
# typically host these graphs:
#
#  - INSTANCE_GRAPH
#  - SCHEMA_UNION_GRAPH (all schema graphs combined)
#  - WOA_GRAPH
#  - FULL_UNION_GRAPH
#
#  To any but the full union graph, data should be addable. This
#  should reflect to the original graph and the graphs that form
#  a union with the specific graph as member.
#
#
#
#
#
#
#
#
#
#
#
#
#
#
#
#

#------------------------------------------------------------------
# Checks if the container is empty
#------------------------------------------------------------------

<ProfileCheck>
  Reference empty-instance-graph
  Description "The instance graph should be empty."
  ResultFormat "The subject ${s} has predicate ${p} to object ${o}"
  <SparqlQuery>
  SELECT ?s ?p ?o
  WHERE {
    GRAPH ${INSTANCE_GRAPH} {
      ?s ?p ?o .
    }
  }
  </SparqlQuery>
</ProfileCheck>

<ProfileCheck>
  Reference empty-woa-graph
  Description "The woa graph should be empty."
  ResultFormat "The subject ${s} has predicate ${p} to object ${o}"
  <SparqlQuery>
  SELECT ?s ?p ?o
  WHERE {
    GRAPH ${WOA_GRAPH} {
      ?s ?p ?o .
    }
  }
  </SparqlQuery>
</ProfileCheck>

<ProfileCheck>
  Reference empty-schema-union-graph
  Description "The schema union graph should be empty."
  ResultFormat "The subject ${s} has predicate ${p} to object ${o}"
  <SparqlQuery>
  SELECT ?s ?p ?o
  WHERE {
    GRAPH ${SCHEMA_UNION_GRAPH} {
      ?s ?p ?o .
    }
  }
  </SparqlQuery>
</ProfileCheck>

<ProfileCheck>
  Reference empty-full-union-graph
  Description "The full union graph should be empty."
  ResultFormat "The subject ${s} has predicate ${p} to object ${o}"
  <SparqlQuery>
  SELECT ?s ?p ?o
  WHERE {
    GRAPH ${FULL_UNION_GRAPH} {
      ?s ?p ?o .
    }
  }
  </SparqlQuery>
</ProfileCheck>



#------------------------------------------------------------------
# Inferences performed on the schema union
#------------------------------------------------------------------

<SchemaInference>
  Reference add-schema-triple
  Description "Add one triple to schema graph"

  <SparqlQuery>
  INSERT DATA {
    GRAPH ${SCHEMA_UNION_GRAPH} {
      <http://d> <http://e> <http://f>
    }
  }
  </SparqlQuery>
</SchemaInference>



#------------------------------------------------------------------
# Inferences performed on the instance data
#------------------------------------------------------------------

<DataInference>
  Reference add-instance-triple
  Description "Add one triple to instance graph"

  <SparqlQuery>
  INSERT DATA {
    GRAPH ${INSTANCE_GRAPH} {
      <http://a> <http://b> <http://c>
    }
  }
  </SparqlQuery>
</DataInference>

#------------------------------------------------------------------
# Validation queries, the queries should not find any results
#------------------------------------------------------------------

<ValidationRule>
  Reference schema-graph-check
  Description "The schema graph should contained added stuff."
  ResultFormat "The subject ${s} has predicate ${p} to object ${o}"
  <SparqlQuery>
  SELECT ?s ?p ?o
  WHERE {
    GRAPH ${SCHEMA_UNION_GRAPH} {
      ?s ?p ?o .
    }
  }
  </SparqlQuery>
</ValidationRule>

<ValidationRule>
  Reference instance-graph-check
  Description "The instance graph should contained added stuff."
  ResultFormat "The subject ${s} has predicate ${p} to object ${o}"
  <SparqlQuery>
  SELECT ?s ?p ?o
  WHERE {
    GRAPH ${INSTANCE_GRAPH} {
      ?s ?p ?o .
    }
  }
  </SparqlQuery>
</ValidationRule>

<ValidationRule>
  Reference woa-graph-check
  Description "The woa graph should contained added stuff."
  ResultFormat "The subject ${s} has predicate ${p} to object ${o}"
  <SparqlQuery>
  SELECT ?s ?p ?o
  WHERE {
    GRAPH ${WOA_GRAPH} {
      ?s ?p ?o .
    }
  }
  </SparqlQuery>
</ValidationRule>

<ValidationRule>
  Reference full-union-graph-check
  Description "The full union graph should contained added stuff."
  ResultFormat "The subject ${s} has predicate ${p} to object ${o}"
  <SparqlQuery>
  SELECT ?s ?p ?o
  WHERE {
    GRAPH ${FULL_UNION_GRAPH} {
      ?s ?p ?o .
    }
  }
  </SparqlQuery>
</ValidationRule>
