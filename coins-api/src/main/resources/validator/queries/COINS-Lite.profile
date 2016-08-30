ProfileName COINS-2.0-Lite
ProfileAuthor Hans Schevers

<ProfileCheck>
Reference COINS 2.0 Lite
Description "COINS 2.0 Lite profile check on predicate (subject predicate object) usage "
ResultFormat "The subject ${a} uses an unsupported predicate ${b} to object ${c}"

<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
 select distinct ?a ?b ?c 
 WHERE {Graph ${SCHEMA_UNION_GRAPH} {
    ?a ?b ?c 
filter (STRSTARTS(STR(?b),"http://www.w3.org/2002/07/owl#")). 
filter (?b not in (owl:allValuesFrom,owl:cardinality,owl:disjointWith,owl:distinctMembers,owl:hasValue,owl:imports,owl:intersectionOf,owl:inverseOf,owl:maxCardinality,owl:maxQualifiedCardinality,owl:members,owl:minCardinality,owl:minQualifiedCardinality,owl:onClass,owl:oneOf,owl:onProperty,owl:qualifiedCardinality,owl:someValuesFrom,owl:unionOf,owl:equivalentClass,owl:annotatedProperty,owl:annotatedSource,owl:annotatedTarget,owl:backwardCompatibleWith,owl:deprecated,owl:incompatibleWith,owl:priorVersion,owl:versionInfo,owl:versionIRI))}}
</SparqlQuery>

</ProfileCheck>
<ProfileCheck>

<SparqlQuery>
Description "COINS 2.0 Lite profile check on object (subject predicate object) usage"
ResultFormat "The subject ${a} has a predicate ${b} to an unsupported object ${c}"
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> 
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
select distinct ?a ?b ?c 
 WHERE {Graph ${SCHEMA_UNION_GRAPH}{ 
 ?a ?b ?c   filter (STRSTARTS(STR(?c),"http://www.w3.org/2002/07/owl#")).
filter (?c not in (owl:AllDisjointClasses,owl:Annotation,owl:AnnotationProperty,owl:Class,owl:DatatypeProperty,owl:FunctionalProperty,owl:NamedIndividual,owl:ObjectProperty,owl:Ontology,owl:Restriction,owl:TransitiveProperty,owl:Thing)) }}
</SparqlQuery>

</ProfileCheck>

<SchemaInference>
<SparqlQuery>
(#name			:subclassing allvaluesfrom restrictions part 1 (49)#description		:Entailment of restriction subclasses via the allvaluesfrom construct part 1#reference		:scm-avf1 (49)#profile			:COINS 2.0 LitePREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

insert {Graph ${SCHEMA_UNION_GRAPH}{
?c1 rdfs:subClassOf ?c2
}}

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
  ?c1 owl:allValuesFrom ?y1 . 
  ?y1 rdfs:subClassOf ?y2.  ?c2 owl:allValuesFrom ?y2 . 
   ?c1 owl:onProperty ?p . 
  ?c2 owl:onProperty ?p      
}

}


</SparqlQuery>
</SchemaInference>

<SchemaInference>
<SparqlQuery>
(#name			:subclassing allvaluesfrom restriction part 2#description		:Entailment of restriction subclasses via the allvaluesfrom construct part 2#reference		:scm-avf2 (50)#profile			:COINS 2.0 LitePREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

insert {Graph ${SCHEMA_UNION_GRAPH}{
?c2 rdfs:subClassOf ?c1 
}}

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
  ?c1 owl:allValuesFrom ?y .     ?c1 owl:onProperty ?p1 .    ?c2 owl:allValuesFrom ?y .    ?c2 owl:onProperty ?p2 .    ?p1 rdfs:subPropertyOf ?p2 
}

}


</SparqlQuery>
</SchemaInference>

<SchemaInference>
<SparqlQuery>
(#name			:Domain additions for subproperties#description		:entailment of domains for subproperties#reference		:scm-dom2 (53)#profile			:COINS 2.0 LitePREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

insert {Graph ${SCHEMA_UNION_GRAPH}{
?p1 rdfs:domain ?c  
}}

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
  ?p2 rdfs:domain ?c .    ?p1 rdfs:subPropertyOf ?p2 
}

}


</SparqlQuery>
</SchemaInference>

<SchemaInference>
<SparqlQuery>
(#name			:Restriction subclassing for hasValue restrictions#description		:Entailment of restriction subclasses via the hasValue construct#reference		:scm-hv (59)#profile			:COINS 2.0 LitePREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

insert {Graph ${SCHEMA_UNION_GRAPH}{
?c1 rdfs:subClassOf ?c2
}}

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
  ?c1 owl:hasValue ?i .    ?c1 owl:onProperty ?p1 .    ?c2 owl:hasValue ?i .    ?c2 owl:onProperty ?p2 .    ?p1 rdfs:subPropertyOf ?p2 
}

}


</SparqlQuery>
</SchemaInference>

<SchemaInference>
<SparqlQuery>
(#name			:Subclass based upon intersection classes#description		:Restriction subclassing for hasValue restrictions. dependency on list:member removed #reference		:scm-int (60) (member:list replaced)#profile			:COINS 2.0 LitePREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

insert {Graph ${SCHEMA_UNION_GRAPH}{
?c rdfs:subClassOf ?cl 
}}

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
  ?c owl:intersectionOf /rdf:rest*/rdf:first ?cl 
}

}


</SparqlQuery>
</SchemaInference>

<SchemaInference>
<SparqlQuery>
(#name			:Scm-rng1 (62)#description		:entailment of ranges for subclasses#reference		:Scm-rng1 (62)#profile			:COINS 2.0 LitePREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

insert {Graph ${SCHEMA_UNION_GRAPH}{
?p rdfs:range ?c2
}}

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
  ?p rdfs:range ?c1 .    ?c1 rdfs:subClassOf ?c2 
}

}


</SparqlQuery>
</SchemaInference>

<SchemaInference>
<SparqlQuery>
(#name			:Scm-rng2 (63)#description		:entailment of ranges for subproperties#reference		:Scm-rng2 (63)#profile			:COINS 2.0 LitePREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

insert {Graph ${SCHEMA_UNION_GRAPH}{
?p1 rdfs:range ?c
}}

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
    ?p2 rdfs:range ?c .	    ?p1 rdfs:subPropertyOf ?p2 
}

}


</SparqlQuery>
</SchemaInference>

<SchemaInference>
<SparqlQuery>
(#name			:Scm-sco (64)#description		:entailment of the subclass hierarchy#reference		:Scm-sco (64)#profile			:COINS 2.0 LitePREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

insert {Graph ${SCHEMA_UNION_GRAPH}{
?c1 rdfs:subClassOf ?c3
}}

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
  ?c1 rdfs:subClassOf ?c2 .    ?c2 rdfs:subClassOf ?c3 
}

}


</SparqlQuery>
</SchemaInference>

<SchemaInference>
<SparqlQuery>
(#name			:Scm-spo (65)#description		:entailment of the subproperty hierarchy#reference		:Scm-spo (65)#profile			:COINS 2.0 LitePREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

insert {Graph ${SCHEMA_UNION_GRAPH}{
?p1 rdfs:subPropertyOf ?p3
}}

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
  ?p1 rdfs:subPropertyOf ?p2 .   ?p2 rdfs:subPropertyOf ?p3 
}

}


</SparqlQuery>
</SchemaInference>

<SchemaInference>
<SparqlQuery>
(#name			:scm-svf1 (66)#description		:Restriction subclassing based upon someValuesFrom#reference		:scm-svf1 (66)#profile			:COINS 2.0 LitePREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

insert {Graph ${SCHEMA_UNION_GRAPH}{
?c1 rdfs:subClassOf ?c2 
}}

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
  ?c1 owl:someValuesFrom ?y1 .    ?c1 owl:onProperty ?p .    ?c2 owl:someValuesFrom ?y2 .    ?c2 owl:onProperty ?p .    ?y1 rdfs:subClassOf ?y2 
}

}


</SparqlQuery>
</SchemaInference>

<SchemaInference>
<SparqlQuery>
(#name			:scm-svf2 (67)#description		:Restriction subclassing based upon someValuesFrom#reference		:scm-svf2 (67)#profile			:COINS 2.0 LitePREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

insert {Graph ${SCHEMA_UNION_GRAPH}{
?c1 rdfs:subClassOf ?c2  
}}

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
  ?c1 owl:someValuesFrom ?y .    ?c1 owl:onProperty ?p1 .    ?c2 owl:someValuesFrom ?y .    ?c2 owl:onProperty ?p2 .    ?p1 rdfs:subPropertyOf ?p2  
}

}


</SparqlQuery>
</SchemaInference>

<SchemaInference>
<SparqlQuery>
(#name			:equivalent class subtyping#description		:subtyping of equivalentclasses#reference		:scm-eqc1 (55)#profile			:COINS 2.0 Lite+EqPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

insert {Graph ${SCHEMA_UNION_GRAPH}{
?c1 rdfs:subClassOf ?c2 .   ?c2 rdfs:subClassOf ?c1 .
}}

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
  ?c1 owl:equivalentClass ?c2  
}

}


</SparqlQuery>
</SchemaInference>

<SchemaInference>
<SparqlQuery>
(#name			:equivalent class deduction#description		:Equivalentclasses deduction from subtype constructs#reference		:scm-eqc2 (56)#profile			:COINS 2.0 Lite+EqPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

insert {Graph ${SCHEMA_UNION_GRAPH}{
?c1 owl:equivalentClass ?c2 
}}

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
  ?c1 rdfs:subClassOf ?c2 .   ?c2 rdfs:subClassOf ?c1  
}

}


</SparqlQuery>
</SchemaInference>

<SchemaInference>
<SparqlQuery>
(#name			:equivalent property subtyping#description		:Equivalent property subproperty entailment#reference		:scm-eqp1 (57)#profile			:COINS 2.0 Lite+EqPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

insert {Graph ${SCHEMA_UNION_GRAPH}{
?p1 rdfs:subPropertyOf ?p2 .   ?p2 rdfs:subPropertyOf ?p1  
}}

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
  ?p1 owl:equivalentProperty ?p2   
}

}


</SparqlQuery>
</SchemaInference>

<SchemaInference>
<SparqlQuery>
(#name			:Equivalent property deduction#description		:Equivalent property deduction from subproperty constructs#reference		:scm-eqp1 (58)#profile			:COINS 2.0 Lite+EqPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

insert {Graph ${SCHEMA_UNION_GRAPH}{
?p1 owl:equivalentProperty ?p2  
}}

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
  ?p1 rdfs:subPropertyOf ?p2 .   ?p2 rdfs:subPropertyOf ?p1 
}

}


</SparqlQuery>
</SchemaInference>

<SchemaInference>
<SparqlQuery>
(#name			:oneOf typing #description		:Entailment owl:oneOf typing (list:member dependency)#reference		:cls-oo (27)#profile			:COINS 2.0 Lite+EqPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

insert {Graph ${SCHEMA_UNION_GRAPH}{
 ?yi a ?c  
}}

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
 ?c owl:oneOf ?x . ?x rdf:rest*/rdf:first ?yi 
}

}


</SparqlQuery>
</SchemaInference>

<DataInference>
<SparqlQuery>
(#name			:Subclass typing#description		:Entailment of superclass typing#reference		:CAXSCO (18)#profile			:COINS 2.0 LitePREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

insert {Graph ${INSTANCE_GRAPH}{
?x a ?c2
}}

 WHERE {Graph ${INSTANCE_GRAPH}{
 ?x a ?c1  
}
Graph ${SCHEMA_UNION_GRAPH}{
 ?c1 rdfs:subClassOf ?c2 
}

}


</SparqlQuery>
</DataInference>

<DataInference>
<SparqlQuery>
(#name			:Subproperty value entailment#description		:Entailment of subproperty values#reference		:Prp-spo1 (45)#profile			:COINS 2.0 LitePREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

insert {Graph ${INSTANCE_GRAPH}{
?x ?p2 ?y 
}}

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
 ?p1 rdfs:subPropertyOf ?p2 
}
Graph ${INSTANCE_GRAPH}{
  ?x ?p1 ?y .
}

}


</SparqlQuery>
</DataInference>

<DataInference>
<SparqlQuery>
(#name			:Transitive property values#description		:Entailment of transitive property values#reference		:Prp-trp (48)#profile			:COINS 2.0 LitePREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

insert {Graph ${INSTANCE_GRAPH}{
?x ?p ?z  
}}

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
   ?p a owl:TransitiveProperty 
}
Graph ${INSTANCE_GRAPH}{
    ?x ?p ?y .	    ?y ?p ?z  
}

}


</SparqlQuery>
</DataInference>

<DataInference>
<SparqlQuery>
(#name			:Default values via owl:hasValue#description		:Default values entailment via the hasValue constraint#reference		:cls-hv1 (20)#profile			:COINS 2.0 LitePREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

insert {Graph ${INSTANCE_GRAPH}{
?u ?p ?y
}}

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
  ?x owl:hasValue ?y .    ?x owl:onProperty ?p .
}
Graph ${INSTANCE_GRAPH}{
   ?u a ?x  .
}

}


</SparqlQuery>
</DataInference>

<DataInference>
<SparqlQuery>
(#name			:InverseOf value entailment part 2#description		:Entailment of inverseOf values part 1#reference		:Prp-inv (41)#profile			:COINS 2.0 LitePREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

insert {Graph ${INSTANCE_GRAPH}{
?y ?p2 ?x
}}

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
      ?p1 owl:inverseOf ?p2 .  
}
Graph ${INSTANCE_GRAPH}{
     ?x ?p1 ?y .
}

}


</SparqlQuery>
</DataInference>

<DataInference>
<SparqlQuery>
(#name			:InverseOf value entailment part 2#description		:Entailment of inverse values part 2#reference		:Prp-inv2 (42)#profile			:COINS 2.0 LitePREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

insert {Graph ${INSTANCE_GRAPH}{
?y ?p1 ?x 
}}

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
       ?p1 owl:inverseOf ?p2 
}
Graph ${INSTANCE_GRAPH}{
    ?x ?p2 ?y .
}

}


</SparqlQuery>
</DataInference>

<DataInference>
<SparqlQuery>
(#name			:UnionOf typing#description		:Typing to Union Class entailment#reference		:Cls-uni (30)#profile			:COINS 2.0 LitePREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

insert {Graph ${INSTANCE_GRAPH}{
 ?y a ?c 
}}

 WHERE {Graph ${INSTANCE_GRAPH}{
?y a ?ci 
}
Graph ${SCHEMA_UNION_GRAPH}{
    ?c owl:unionOf ?x .     ?x rdf:rest*/rdf:first ?ci 
}

}


</SparqlQuery>
</DataInference>

<DataInference>
<SparqlQuery>
(#name			:EquivalentClass typing part 1#description		:Entailment of equivalent typing#reference		:Cax-eqc1 (16)#profile			:COINS 2.0 Lite+EqPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

insert {Graph ${INSTANCE_GRAPH}{
  ?x a ?c2 
}}

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
 ?c1 owl:equivalentClass ?c2  
}
Graph ${INSTANCE_GRAPH}{
 ?x a ?c1  
}

}


</SparqlQuery>
</DataInference>

<DataInference>
<SparqlQuery>
(#name			:EquivalentClass typing part 2#description		:Entailment of equivalent typing#reference		:Cax-eqc2 (17)#profile			:COINS 2.0 Lite+EqPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

insert {Graph ${INSTANCE_GRAPH}{
  ?x a ?c1 
}}

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
 ?c1 owl:equivalentClass ?c2  
}
Graph ${INSTANCE_GRAPH}{
 ?x a ?c2  
}

}


</SparqlQuery>
</DataInference>

<DataInference>
<SparqlQuery>
(#name			:allValuesFrom typing#description		:Entailment for allValuesFrom Typing#reference		:AllvaluesFrom Typing#profile			:COINS 2.0 Lite+EqPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

insert {Graph ${INSTANCE_GRAPH}{
  ?x a ?res 
}}

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
  ?res owl:allValuesFrom ?type .    ?res owl:onProperty ?prop .  
}
Graph ${INSTANCE_GRAPH}{
  ?x ?prop ?value.   
{?value a ?type} 
union
 {bind(datatype(?value) as ?type) }  
}

}


</SparqlQuery>
</DataInference>

<DataInference>
<SparqlQuery>
(#name			:hasValue Typing#description		:Restriction typing via the hasValue constraint#reference		:cls-hv2 (21)#profile			:COINS 2.0 Lite+EqPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

insert {Graph ${INSTANCE_GRAPH}{
    ?u a ?x 
}}

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
  ?x owl:hasValue ?y .    ?x owl:onProperty ?p 
}
Graph ${INSTANCE_GRAPH}{
 ?u ?p ?y 
}

}


</SparqlQuery>
</DataInference>

<DataInference>
<SparqlQuery>
(#name			:Intersection typing entailment#description		:Intersection typing entailment#reference		:TMG)#profile			:COINS 2.0 Lite+EqPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

insert {Graph ${INSTANCE_GRAPH}{
?y a ?c
}}

 WHERE {{select (count(distinct ?type) as ?totaal) where {
Graph ${SCHEMA_UNION_GRAPH}{
   ?c owl:intersectionOf ?in. ?in rdf:rest*/rdf:first ?type . filter (?type!=rdf:nil) 
}
 }group by ?c}
 {select distinct ?c ?y (count(distinct ?type) as ?aantal)		  WHERE { 
Graph ${SCHEMA_UNION_GRAPH}{
   ?c owl:intersectionOf ?in. ?in rdf:rest*/rdf:first ?type .filter (?type!=rdf:nil) 
}
Graph ${INSTANCE_GRAPH}{
?y a ?type  
}
 	}	  group by ?y ?c }
 filter (?aantal=?totaal)

}


</SparqlQuery>
</DataInference>

<DataInference>
<SparqlQuery>
(#name			:someValuesFrom typing#description		:Entailment of someValueFrom typing#reference		:cls-svf1 (28)#profile			:COINS 2.0 Lite+EqPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

insert {Graph ${INSTANCE_GRAPH}{
 ?u a ?x 
}}

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
 ?x owl:someValuesFrom ?y .    ?x owl:onProperty ?p . 
}
Graph ${INSTANCE_GRAPH}{
 ?u ?p ?v .    ?v a ?y 
}

}


</SparqlQuery>
</DataInference>

<DataInference>
<SparqlQuery>
(#name			:someValuesFrom typing owl:Thing#description		:Entailment of someValueFrom typing of owl:Thing#reference		:cls-svf2 (29)#profile			:COINS 2.0 Lite+EqPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

insert {Graph ${INSTANCE_GRAPH}{
 ?u a ?x 
}}

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
 ?x owl:someValuesFrom owl:Thing .    ?x owl:onProperty ?p . 
}
Graph ${INSTANCE_GRAPH}{
 ?u ?p ?v 
}

}


</SparqlQuery>
</DataInference>

<ValidationRule>
<SparqlQuery>
(#name			:Minimal Cardinality validatie#description		:minimal cardinality constraint validation#reference		:COINS-minCar#profile			:COINS 2.0 LitePREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?this  ?min ?aantal ?property 

 WHERE {{
 select distinct ?this ?class ?min (count(distinct ?value) as ?aantal) ?property 

 WHERE {Graph ${FULL_UNION_GRAPH}{
   ?class a owl:Restriction .     ?class owl:minCardinality ?min .    ?class owl:onProperty ?property  
}
Graph ${FULL_UNION_GRAPH}{
 {?this a ?class . ?this ?property ?value. filter not exists {?value a <http://www.coinsweb.nl/cbim-2.0.rdf#ExpiredEntity>} } union {?this a ?class . filter not exists {?this ?property ?value} }	
}

}group by ?this ?class ?min ?property
}
filter(?aantal<?min)

}


</SparqlQuery>
</ValidationRule>
<ValidationRule>
<SparqlQuery>
(#name			:Cardinality 'exactly' validatie#description		:Exactly cardinality validatie#reference		:COINS-CAREX#profile			:COINS 2.0 LitePREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?this ?min ?aantal ?property 

 WHERE {{
 select distinct ?this ?min (count(distinct ?value) as ?aantal) ?property 

 WHERE {Graph ${FULL_UNION_GRAPH}{
  ?class a owl:Restriction .    ?class owl:cardinality ?min .    ?class owl:onProperty ?property .
}
Graph ${FULL_UNION_GRAPH}{
 {?this a ?class . ?this ?property ?value. filter not exists {?value a <http://www.coinsweb.nl/cbim-2.0.rdf#ExpiredEntity>} } union {?this a ?class . filter not exists {?this ?property ?value} }	
}

}  group by ?class ?this ?min ?property  
}
filter((?aantal<?min) || (?aantal>?min) )

}


</SparqlQuery>
</ValidationRule>
<ValidationRule>
<SparqlQuery>
(#name			:Maximum Cardinality validatie#description		:maximum cardinality constraint validation#reference		:COINS-maxCar#profile			:COINS 2.0 LitePREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?this ?class ?min ?aantal ?property 

 WHERE {{
 select distinct ?this  ?min (count(distinct ?value) as ?aantal) ?property 

 WHERE {Graph ${FULL_UNION_GRAPH}{
   ?class a owl:Restriction .     ?class owl:maxCardinality ?min .    ?class owl:onProperty ?property   
}
Graph ${FULL_UNION_GRAPH}{
 ?this a ?class . ?this ?property ?value. filter not exists {?value a <http://www.coinsweb.nl/cbim-2.0.rdf#ExpiredEntity>}	
}

}group by ?this ?class ?min  ?property
}
filter(?aantal>?min)

}


</SparqlQuery>
</ValidationRule>
<ValidationRule>
<SparqlQuery>
(#name			:Qualitative Cardinality Restriction 'exactly' validatie#description		:Exactly Qualitative Cardinality restriction validatie#reference		:COINS-QCREx#profile			:COINS 2.0 LitePREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?this  ?min ?aantal ?property 

 WHERE {{
 select distinct ?this ?class ?min (count(distinct ?value) as ?aantal) ?property 

 WHERE {Graph ${FULL_UNION_GRAPH}{
  ?class a owl:Restriction .    ?class owl:onClass ?onClass .  ?class owl:qualifiedCardinality ?min .    ?class owl:onProperty ?property .
}
Graph ${FULL_UNION_GRAPH}{
    ?this a ?class .			optional{   ?this ?property ?value.	filter not exists {?value a <http://www.coinsweb.nl/cbim-2.0.rdf#ExpiredEntity>}. ?value a ?onClass }
}

}  group by ?this ?class ?min  ?property  
}
filter((?aantal<?min) || (?aantal>?min) )

}


</SparqlQuery>
</ValidationRule>
<ValidationRule>
<SparqlQuery>
(#name			:Qualitative Cardinality Restriction maximum validatie#description		:Maximum Qualitative Cardinality restriction validatie#reference		:COINS-QCRMax#profile			:COINS 2.0 LitePREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?this ?class ?min ?aantal ?property 

 WHERE {{
 select distinct ?this ?class ?min  (count(distinct ?value) as ?aantal)	 ?property 

 WHERE {Graph ${FULL_UNION_GRAPH}{
    ?class a owl:Restriction .   ?class owl:onClass ?onClass.   ?class owl:maxQualifiedCardinality ?min .    ?class owl:onProperty ?property .
}
Graph ${FULL_UNION_GRAPH}{
    ?this a ?class .	 optional{ ?this ?property ?value. filter not exists {?value a <http://www.coinsweb.nl/cbim-2.0.rdf#ExpiredEntity>}.?value a ?onClass}		
}

}  group by  ?this ?class ?min  ?property  
}
 filter((?aantal>?min) )  

}


</SparqlQuery>
</ValidationRule>
<ValidationRule>
<SparqlQuery>
(#name			:Qualitative Cardinality Restriction Minimum validatie #description		:Minimal Qualitative Cardinality restriction validatie#reference		:COINS-QCRMin#profile			:COINS 2.0 LitePREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?this  ?min ?aantal ?property 

 WHERE {{
 select distinct ?this ?class ?min (count(distinct ?value) as ?aantal)  ?property 

 WHERE {Graph ${FULL_UNION_GRAPH}{
  ?class a owl:Restriction .   ?class owl:onClass ?onClass.   ?class owl:minQualifiedCardinality ?min .    ?class owl:onProperty ?property .
}
Graph ${FULL_UNION_GRAPH}{
    ?this a ?class .			optional{   ?this ?property ?value.	filter not exists {?value a <http://www.coinsweb.nl/cbim-2.0.rdf#ExpiredEntity>}.?value a ?onClass}	  	
}

}  group by ?this ?class ?min  ?property  
}
filter((?aantal<?min) )  

}


</SparqlQuery>
</ValidationRule>
<ValidationRule>
<SparqlQuery>
(#name			:FunctionalProperty aantal validatie#description		:functional property as a restriction validation#reference		:COINS-FUP#profile			:COINS 2.0 LitePREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?this  ?property ?aantal

 WHERE {{
 select distinct ?this ?property  (count(distinct ?value) as ?aantal) 

 WHERE {Graph ${FULL_UNION_GRAPH}{
   ?property a owl:FunctionalProperty
}
Graph ${FULL_UNION_GRAPH}{
 ?this ?property ?value. filter not exists {?value a <http://www.coinsweb.nl/cbim-2.0.rdf#ExpiredEntity>}  
}

} group by  ?this ?property  
}
filter(?aantal>1) 

}


</SparqlQuery>
</ValidationRule>
<ValidationRule>
<SparqlQuery>
(#name			:Simple datatyping check #description		:Datatype check#reference		:COINS-DTVC#profile			:COINS 2.0 LitePREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?prop ?range  (Datatype(?value) as ?dat)

 WHERE {Graph ${FULL_UNION_GRAPH}{
  ?prop  a owl:DatatypeProperty . 			   ?prop rdfs:range ?range. 
}
Graph ${FULL_UNION_GRAPH}{
  ?a ?prop ?value
}
filter (Datatype(?value) !=?range)

}


</SparqlQuery>
</ValidationRule>
<ValidationRule>
<SparqlQuery>
(#name			:Literal check for datatypes#description		:Datatype waarde check#reference		:COINS-DPVL#profile			:COINS 2.0 LitePREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?a ?prop ?value

 WHERE {Graph ${FULL_UNION_GRAPH}{
  ?prop  a owl:DatatypeProperty 
}
Graph ${FULL_UNION_GRAPH}{
  ?a ?prop ?value.	filter(!isLiteral(?value)) 
}

}


</SparqlQuery>
</ValidationRule>
<ValidationRule>
<SparqlQuery>
(#name			:ObjectProperty value type check #description		:ObjectProperty waarde check#reference		:COINS-OPVU#profile			:COINS 2.0 LitePREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?a ?prop ?value

 WHERE {Graph ${FULL_UNION_GRAPH}{
  ?prop  a owl:ObjectProperty 
}
Graph ${FULL_UNION_GRAPH}{
  ?a ?prop ?value.	filter(!isIRI(?value)) 
}

}


</SparqlQuery>
</ValidationRule>
<ValidationRule>
<SparqlQuery>
(#name			:domain als een restrictie validatie #description		:Domain als een restrictie#reference		:COINS-dom#profile			:COINS 2.0 LitePREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct * 

 WHERE {Graph ${FULL_UNION_GRAPH}{
     ?prop a owl:ObjectProperty. ?prop rdfs:domain  ?domain.
}
Graph ${FULL_UNION_GRAPH}{
    ?a ?prop ?b
}
filter not exists {{Graph ${FULL_UNION_GRAPH}{
    ?a a ?domain
}}  union { Graph ${FULL_UNION_GRAPH}{
 ?a a ?domain}
}}

}


</SparqlQuery>
</ValidationRule>
<ValidationRule>
<SparqlQuery>
(#name			:Range als restrictie validatie#description		:Range als een restrictie#reference		:COINS-rng#profile			:COINS 2.0 LitePREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct * 

 WHERE {Graph ${FULL_UNION_GRAPH}{
     ?prop a owl:ObjectProperty. ?prop rdfs:range  ?range.
}
Graph ${FULL_UNION_GRAPH}{
 ?a ?prop ?b
}
filter not exists {{Graph ${FULL_UNION_GRAPH}{
    ?b a ?range 
}}  union { Graph ${FULL_UNION_GRAPH}{
 ?b a ?range}
}}

}


</SparqlQuery>
</ValidationRule>
<ValidationRule>
<SparqlQuery>
(#name			:UnionOf als een restrictie validatie#description		:UnionOf as a restriction validation#reference		:COINS-UO#profile			:COINS 2.0 LitePREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?restriction ?a ?first

 WHERE {Graph ${FULL_UNION_GRAPH}{
     ?restriction owl:unionOf  ?list.
}
Graph ${FULL_UNION_GRAPH}{
    ?a a ?restriction. 
}
filter not exists{Graph ${FULL_UNION_GRAPH}{
 ?a a ?first . 
}Graph ${FULL_UNION_GRAPH}{
 ?list rdf:rest*/rdf:first ?first
}}

}


</SparqlQuery>
</ValidationRule>
<ValidationRule>
<SparqlQuery>
(#name			:IntersectionOf #description		:Intersection as a restriction validation#reference		:COINS-IS#profile			:COINS 2.0 LitePREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?restriction ?a 

 WHERE {Graph ${FULL_UNION_GRAPH}{
     ?restriction owl:intersectionOf  ?list.
}
Graph ${FULL_UNION_GRAPH}{
    ?a a ?restriction. 
}
filter not exists{Graph ${FULL_UNION_GRAPH}{
 ?a a ?first  
}Graph ${FULL_UNION_GRAPH}{
 ?list rdf:rest*/rdf:first ?first
}}

}


</SparqlQuery>
</ValidationRule>
<ValidationRule>
<SparqlQuery>
(#name			:validatie van alle objecten of ze minimaal een COINS Entity zijn#description		:Only COINS concept instances check#reference		:COINS-CCO#profile			:COINS 2.0 LitePREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?x ?type

 WHERE {Graph ${FULL_UNION_GRAPH}{
  ?x a ?type. Filter not exists {?x a <http://www.coinsweb.nl/cbim-2.0.rdf#Concept>}.  filter not exists{ ?x a owl:Ontology}
}

}


</SparqlQuery>
</ValidationRule>
<ValidationRule>
<SparqlQuery>
(#name			:Geen illegale RDF/RDFS/OWL objecten in een COINSContainer#description		:only instances in a semantic BIM model#reference		:COINS-NSWO#profile			:COINS 2.0 LitePREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?a ?b ?c

 WHERE {Graph ${FULL_UNION_GRAPH}{
    ?a ?b ?c.   filter (?c!=owl:Thing).  filter (?c!=owl:Ontology). filter (?c!=owl:NamedIndividual). filter (?c!=owl:Nothing) . filter (STRSTARTS(STR(?c),"http://www.w3.org/1999/02/22-rdf-syntax-ns#")||(STRSTARTS(STR(?c),"http://www.w3.org/2000/01/rdf-schema#"))||(STRSTARTS(STR(?c),"http://www.w3.org/2002/07/owl#")))
}

}


</SparqlQuery>
</ValidationRule>
<ValidationRule>
<SparqlQuery>
(#name			:geen Rdfs SubclassOf predicaten in een COINSContainer#description		:No rdfs:subclassOf in a semantic BIM model#reference		:COINS-NSCO#profile			:COINS 2.0 LitePREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?a ?c

 WHERE {Graph ${FULL_UNION_GRAPH}{
 ?a rdfs:subClassOf ?c
}

}


</SparqlQuery>
</ValidationRule>
<ValidationRule>
<SparqlQuery>
(#name			:Geen rdfs:subPropertyOf predicaten een COINS container#description		:No subPropertyOf in a semantic BIM model#reference		:COINS-NSPO#profile			:COINS 2.0 LitePREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?a ?c

 WHERE {Graph ${FULL_UNION_GRAPH}{
 ?a rdfs:subPropertyOf ?c
}

}


</SparqlQuery>
</ValidationRule>
<ValidationRule>
<SparqlQuery>
(#name			:valideren van disjoint individuals via Disjoint verklaringen op basis van owl:allDisjoint#description		:Disjoint via owl:allDisjointClasses #reference		:Cax-adx (listmod)#profile			:COINS 2.0 LitePREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?x ?c1 ?c2

 WHERE {Graph ${FULL_UNION_GRAPH}{
 ?y a owl:AllDisjointClasses .    ?y owl:members ?members .     ?members rdf:rest*/rdf:first ?c1 .		    ?members rdf:rest*/rdf:first ?c2 .  		    FILTER (?c1 != ?c2) .
}
Graph ${FULL_UNION_GRAPH}{
 ?x a ?c1 . 	 ?x a ?c2 
}

}


</SparqlQuery>
</ValidationRule>
<ValidationRule>
<SparqlQuery>
(#name			:valideren van disjoint individuals via Disjoint verklaringen op basis van owl:disjointWith #description		:Disjoint via owl:disjointWith #reference		:cax-dw#profile			:COINS 2.0 LitePREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?x ?c1 ?c2

 WHERE {Graph ${FULL_UNION_GRAPH}{
 ?c1 owl:disjointWith ?c2 
}
Graph ${FULL_UNION_GRAPH}{
   ?x a ?c1 .   ?x a ?c2 .
}

}


</SparqlQuery>
</ValidationRule>