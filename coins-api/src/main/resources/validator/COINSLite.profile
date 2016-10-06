ProfileName COINS 2.0 Lite
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
Reference COINS 2.0 Lite
Description "COINS 2.0 Lite profile check on object (subject predicate object) usage"
ResultFormat "The subject ${a} has a predicate ${b} to an unsupported object ${c}"

<SparqlQuery>
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

Reference scm-avf1 (49)
Description "Entailment of restriction subclasses via the allvaluesfrom construct part 1"
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
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

Reference scm-avf2 (50)
Description "Entailment of restriction subclasses via the allvaluesfrom construct part 2"
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
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

Reference scm-dom2 (53)
Description "entailment of domains for subproperties"
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
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

Reference scm-hv (59)
Description "Entailment of restriction subclasses via the hasValue construct"
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
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

Reference scm-int (60) (member:list replaced)
Description "Restriction subclassing for hasValue restrictions. dependency on list:member removed "
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
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

Reference Scm-rng1 (62)
Description "entailment of ranges for subclasses"
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
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

Reference Scm-rng2 (63)
Description "entailment of ranges for subproperties"
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
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

Reference Scm-sco (64)
Description "entailment of the subclass hierarchy"
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
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

Reference Scm-spo (65)
Description "entailment of the subproperty hierarchy"
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
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

Reference scm-svf1 (66)
Description "Restriction subclassing based upon someValuesFrom"
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
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

Reference scm-svf2 (67)
Description "Restriction subclassing based upon someValuesFrom"
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
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


<DataInference>

Reference CAXSCO (18)
Description "Entailment of superclass typing"
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
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

Reference Prp-spo1 (45)
Description "Entailment of subproperty values"
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
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

Reference Prp-trp (48)
Description "Entailment of transitive property values"
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
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

Reference cls-hv1 (20)
Description "Default values entailment via the hasValue constraint"
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
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

Reference Prp-inv (41)
Description "Entailment of inverseOf values part 1"
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
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

Reference Prp-inv2 (42)
Description "Entailment of inverse values part 2"
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
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

Reference Cls-uni (30)
Description "Typing to Union Class entailment"
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
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


<ValidationRule>

Reference COINS-minCar
Description "minimal cardinality constraint validation"
ResultFormat " ${this} has ${aantal} properties of ${property}.  The minimum is ${min}. "
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?this  ?min ?aantal ?property 

 WHERE {{
 select distinct ?this ?class ?min (count(distinct ?value) as ?aantal) ?property 

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
   ?class a owl:Restriction .     ?class owl:minCardinality ?min .    ?class owl:onProperty ?property  
}
Graph ${INSTANCE_GRAPH}{
 {?this a ?class . ?this ?property ?value. filter not exists {?value a <http://www.coinsweb.nl/cbim-2.0.rdf#ExpiredEntity>} } union {?this a ?class . filter not exists {?this ?property ?value} }	
}

}group by ?this ?class ?min ?property
}
filter(?aantal<?min)

}
</SparqlQuery>

</ValidationRule>

<ValidationRule>

Reference COINS-CAREX
Description "Exactly cardinality validatie"
ResultFormat " ${this} has ${aantal} properties of ${property}.  Only ${min} is allowed. "
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?this ?min ?aantal ?property 

 WHERE {{
 select distinct ?this ?min (count(distinct ?value) as ?aantal) ?property 

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
  ?class a owl:Restriction .    ?class owl:cardinality ?min .    ?class owl:onProperty ?property .
}
Graph ${INSTANCE_GRAPH}{
 {?this a ?class . ?this ?property ?value. filter not exists {?value a <http://www.coinsweb.nl/cbim-2.0.rdf#ExpiredEntity>} } union {?this a ?class . filter not exists {?this ?property ?value} }	
}

}  group by ?class ?this ?min ?property  
}
filter((?aantal<?min) || (?aantal>?min) )

}
</SparqlQuery>

</ValidationRule>

<ValidationRule>

Reference COINS-maxCar
Description "maximum cardinality constraint validation"
ResultFormat " ${this} has ${aantal} properties of ${property}.  The maximum is ${min}. "
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?this ?class ?min ?aantal ?property 

 WHERE {{
 select distinct ?this  ?min (count(distinct ?value) as ?aantal) ?property 

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
   ?class a owl:Restriction .     ?class owl:maxCardinality ?min .    ?class owl:onProperty ?property   
}
Graph ${INSTANCE_GRAPH}{
 ?this a ?class . ?this ?property ?value. filter not exists {?value a <http://www.coinsweb.nl/cbim-2.0.rdf#ExpiredEntity>}	
}

}group by ?this ?class ?min  ?property
}
filter(?aantal>?min)

}
</SparqlQuery>

</ValidationRule>

<ValidationRule>

Reference COINS-QCREx
Description "Exactly Qualitative Cardinality restriction validatie"
ResultFormat " ${this} has ${aantal} qualified properties of ${property} of type ${onClass}.  Only ${min} is allowed. "
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?this  ?min ?aantal ?property  ?onClass

 WHERE {{
 select distinct ?this ?class ?min (count(distinct ?value) as ?aantal) ?property ?onClass

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
  ?class a owl:Restriction .    ?class owl:onClass ?onClass .  ?class owl:qualifiedCardinality ?min .    ?class owl:onProperty ?property .
}
Graph ${INSTANCE_GRAPH}{
    ?this a ?class .			optional{   ?this ?property ?value.	filter not exists {?value a <http://www.coinsweb.nl/cbim-2.0.rdf#ExpiredEntity>}. ?value a ?onClass }
}

}  group by ?this ?class ?min  ?property  ?onClass
}
filter((?aantal<?min) || (?aantal>?min) )

}
</SparqlQuery>

</ValidationRule>

<ValidationRule>

Reference COINS-QCRMax
Description "Maximum Qualitative Cardinality restriction validatie"
ResultFormat " ${this} has ${aantal} qualified properties of ${property} .  the maximum is ${min}. "
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?this ?class ?min ?aantal ?property 

 WHERE {{
 select distinct ?this ?class ?min  (count(distinct ?value) as ?aantal)	 ?property 

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
    ?class a owl:Restriction .   ?class owl:onClass ?onClass.   ?class owl:maxQualifiedCardinality ?min .    ?class owl:onProperty ?property .
}
Graph ${INSTANCE_GRAPH}{
    ?this a ?class .	 optional{ ?this ?property ?value. filter not exists {?value a <http://www.coinsweb.nl/cbim-2.0.rdf#ExpiredEntity>}.?value a ?onClass}		
}

}  group by  ?this ?class ?min  ?property  
}
 filter((?aantal>?min) )  

}
</SparqlQuery>

</ValidationRule>

<ValidationRule>

Reference COINS-QCRMin
Description "Minimal Qualitative Cardinality restriction validatie"
ResultFormat " ${this} has ${aantal} qualified properties of ${property} .  the minimum is ${min}. "
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?this  ?min ?aantal ?property 

 WHERE {{
 select distinct ?this ?class ?min (count(distinct ?value) as ?aantal)  ?property 

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
  ?class a owl:Restriction .   ?class owl:onClass ?onClass.   ?class owl:minQualifiedCardinality ?min .    ?class owl:onProperty ?property .
}
Graph ${INSTANCE_GRAPH}{
    ?this a ?class .			optional{   ?this ?property ?value.	filter not exists {?value a <http://www.coinsweb.nl/cbim-2.0.rdf#ExpiredEntity>}.?value a ?onClass}	  	
}

}  group by ?this ?class ?min  ?property  
}
filter((?aantal<?min) )  

}
</SparqlQuery>

</ValidationRule>

<ValidationRule>

Reference COINS-FUP
Description "functional property as a restriction validation"
ResultFormat " ${this} has ${aantal} properties of ${property}.  Only 1 is allowed (functional property). "
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?this  ?property ?aantal

 WHERE {{
 select distinct ?this ?property  (count(distinct ?value) as ?aantal) 

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
   ?property a owl:FunctionalProperty
}
Graph ${INSTANCE_GRAPH}{
 ?this ?property ?value. filter not exists {?value a <http://www.coinsweb.nl/cbim-2.0.rdf#ExpiredEntity>}  
}

} group by  ?this ?property  
}
filter(?aantal>1) 

}
</SparqlQuery>

</ValidationRule>

<ValidationRule>

Reference COINS-DTVC
Description "Datatype check"
ResultFormat " ${value} is of the type ${?dat} which is not valid for ${prop} for individual ${a}. It should be a ${range}. "
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?a prop ?range ?value (Datatype(?value) as ?dat)

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
  ?prop  a owl:DatatypeProperty . 			   ?prop rdfs:range ?range. 
}
Graph ${INSTANCE_GRAPH}{
  ?a ?prop ?value
}
filter (Datatype(?value) !=?range)

}
</SparqlQuery>

</ValidationRule>

<ValidationRule>

Reference COINS-DPVL
Description "Datatype waarde check"
ResultFormat " ${value} is not a valid value for datatype property ${prop} for individual ${a}"
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?a ?prop ?value

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
  ?prop  a owl:DatatypeProperty 
}
Graph ${INSTANCE_GRAPH}{
  ?a ?prop ?value.	filter(!isLiteral(?value)) 
}

}
</SparqlQuery>

</ValidationRule>

<ValidationRule>

Reference COINS-OPVU
Description "ObjectProperty waarde check"
ResultFormat " ${value} is not a valid value for object property ${prop} for individual ${a}"
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?a ?prop ?value

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
  ?prop  a owl:ObjectProperty 
}
Graph ${INSTANCE_GRAPH}{
  ?a ?prop ?value.	filter(!isIRI(?value)) 
}

}
</SparqlQuery>

</ValidationRule>

<ValidationRule>

Reference COINS-dom
Description "Domain als een restrictie"
ResultFormat "individual ${a} has a property ${prop} with value ${b}. ${a} is not of the type ${domain} "
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?a ?prop ?b ?domain

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
     ?prop a owl:ObjectProperty. ?prop rdfs:domain  ?domain.
}
Graph ${INSTANCE_GRAPH}{
    ?a ?prop ?b
}
filter not exists {{Graph ${INSTANCE_GRAPH}{
    ?a a ?domain
}}  union { Graph ${SCHEMA_UNION_GRAPH}{
 ?a a ?domain}
}}

}
</SparqlQuery>

</ValidationRule>

<ValidationRule>

Reference COINS-rng
Description "Range als een restrictie"
ResultFormat "individual ${a} has a property ${prop} with value ${b}. ${b} is not of the type ${range} "
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?a ?prop ?b ?range

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
     ?prop a owl:ObjectProperty. ?prop rdfs:range  ?range.
}
Graph ${INSTANCE_GRAPH}{
 ?a ?prop ?b
}
filter not exists {{Graph ${INSTANCE_GRAPH}{
    ?b a ?range 
}}  union { Graph ${SCHEMA_UNION_GRAPH}{
 ?b a ?range}
}}

}
</SparqlQuery>

</ValidationRule>

<ValidationRule>

Reference COINS-UO
Description "UnionOf as a restriction validation"
ResultFormat " ${a} does not fulfill the UnionOf restriction ${restriction} "
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?restriction ?a ?first

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
     ?restriction owl:unionOf  ?list.
}
Graph ${INSTANCE_GRAPH}{
    ?a a ?restriction. 
}
filter not exists{Graph ${INSTANCE_GRAPH}{
 ?a a ?first . 
}Graph ${SCHEMA_UNION_GRAPH}{
 ?list rdf:rest*/rdf:first ?first
}}

}
</SparqlQuery>

</ValidationRule>

<ValidationRule>

Reference COINS-IS
Description "Intersection as a restriction validation"
ResultFormat " ${a} does not fulfill the Intersection restriction ${restriction} "
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?restriction ?a 

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
     ?restriction owl:intersectionOf  ?list.
}
Graph ${INSTANCE_GRAPH}{
    ?a a ?restriction. 
}
filter not exists{Graph ${INSTANCE_GRAPH}{
 ?a a ?first  
}Graph ${SCHEMA_UNION_GRAPH}{
 ?list rdf:rest*/rdf:first ?first
}}

}
</SparqlQuery>

</ValidationRule>

<ValidationRule>

Reference COINS-CCO
Description "Only COINS concept instances check"
ResultFormat "instance ${x} is a member of ${type}.${type} is not a COINS Concept "
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?x ?type

 WHERE {Graph ${INSTANCE_GRAPH}{
  ?x a ?type. Filter not exists {?x a <http://www.coinsweb.nl/cbim-2.0.rdf#Concept>}.  filter not exists{ ?x a owl:Ontology}
}

}
</SparqlQuery>

</ValidationRule>

<ValidationRule>

Reference COINS-NSWO
Description "only instances in a semantic BIM model"
ResultFormat "instance ${a} contains the following illegal semantic BIM constructs: ${b} ${c}. Only instances of COINS objects are allowed"
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?a ?b ?c

 WHERE {Graph ${INSTANCE_GRAPH}{
    ?a ?b ?c.   filter (?c!=owl:Thing).  filter (?c!=owl:Ontology). filter (?c!=owl:NamedIndividual). filter (?c!=owl:Nothing) . filter (STRSTARTS(STR(?c),"http://www.w3.org/1999/02/22-rdf-syntax-ns#")||(STRSTARTS(STR(?c),"http://www.w3.org/2000/01/rdf-schema#"))||(STRSTARTS(STR(?c),"http://www.w3.org/2002/07/owl#")))
}

}
</SparqlQuery>

</ValidationRule>

<ValidationRule>

Reference COINS-NSCO
Description "No rdfs:subclassOf in a semantic BIM model"
ResultFormat " ${a} rdfs:subClassOf ${c} is not allowed in a semantic BIM graph "
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?a ?c

 WHERE {Graph ${INSTANCE_GRAPH}{
 ?a rdfs:subClassOf ?c
}

}
</SparqlQuery>

</ValidationRule>

<ValidationRule>

Reference COINS-NSPO
Description "No subPropertyOf in a semantic BIM model"
ResultFormat " ${a} rdfs:subPropertyOf ${c} is not allowed in a semantic BIM graph "
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?a ?c

 WHERE {Graph ${INSTANCE_GRAPH}{
 ?a rdfs:subPropertyOf ?c
}

}
</SparqlQuery>

</ValidationRule>

<ValidationRule>

Reference Cax-adx (listmod)
Description "Disjoint via owl:allDisjointClasses "
ResultFormat "instance ${x} is a member of ${c1} and of ${c2}. ${c1} and ${c2} are disjoint so instance ${x} cannot be a member of both classes "
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?x ?c1 ?c2

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
 ?y a owl:AllDisjointClasses .    ?y owl:members ?members .     ?members rdf:rest*/rdf:first ?c1 .		    ?members rdf:rest*/rdf:first ?c2 .  		    FILTER (?c1 != ?c2) .
}
Graph ${INSTANCE_GRAPH}{
 ?x a ?c1 . 	 ?x a ?c2 
}

}
</SparqlQuery>

</ValidationRule>

<ValidationRule>

Reference cax-dw
Description "Disjoint via owl:disjointWith "
ResultFormat "instance ${x} is a member of ${c1} and of ${c2}. ${c1} and ${c2} are disjoint so instance ${x} cannot be a member of both classes. "
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?x ?c1 ?c2

 WHERE {Graph ${SCHEMA_UNION_GRAPH}{
 ?c1 owl:disjointWith ?c2 
}
Graph ${INSTANCE_GRAPH}{
   ?x a ?c1 .   ?x a ?c2 .
}

}
</SparqlQuery>

</ValidationRule>
