ProfileName COINS 2.0 Lite
ProfileVersion Jena/Fuseki Alternative version 0.9.5
ProfileAuthor Hans Schevers
<ProfileCheck>
Reference COINS 2.0 Lite
Description "COINS 2.0 Lite profile check on predicate usage "
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
filter (?b not in (owl:equivalentClass,owl:allValuesFrom,owl:oneOf,owl:cardinality,owl:disjointWith,owl:distinctMembers,owl:hasValue,owl:imports,owl:intersectionOf,owl:inverseOf,owl:maxCardinality,owl:maxQualifiedCardinality,owl:members,owl:minCardinality,owl:minQualifiedCardinality,owl:onClass,owl:onProperty,owl:qualifiedCardinality,owl:unionOf,owl:annotatedProperty,owl:annotatedSource,owl:annotatedTarget,owl:backwardCompatibleWith,owl:deprecated,owl:incompatibleWith,owl:priorVersion,owl:versionInfo,owl:versionIRI))}}
</SparqlQuery>

</ProfileCheck>
<ProfileCheck>
Reference COINS 2.0 Lite
Description "COINS 2.0 Lite profile check on object usage"
ResultFormat "The subject ${a} has a predicate ${b} to an unsupported object ${c}"

<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> 
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
select distinct ?a ?b ?c 
 WHERE {Graph ${SCHEMA_UNION_GRAPH}{ 
 ?a ?b ?c   filter (STRSTARTS(STR(?c),"http://www.w3.org/2002/07/owl#")).
filter (?c not in (owl:AllDisjointClasses,owl:Annotation,owl:AnnotationProperty,owl:Class,owl:DatatypeProperty,owl:FunctionalProperty,owl:NamedIndividual,owl:ObjectProperty,owl:Ontology,owl:Restriction,owl:TransitiveProperty,owl:Thing,owl:topDataProperty,owl:topObjectProperty)) }}
</SparqlQuery>

</ProfileCheck>

<SchemaInference>

Reference Scm-otp
Description "defining object Properties"
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

insert {Graph ${FULL_UNION_GRAPH}{
 ?prop a owl:ObjectProperty 
}}

 WHERE {Graph ${FULL_UNION_GRAPH}{
 ?prop rdfs:range ?class. 
 ?class a owl:Class.
		filter (?class!=rdfs:Resource). 
 	filter not exists{?prop a owl:ObjectProperty} 
}

}
</SparqlQuery>

</SchemaInference>


<SchemaInference>

Reference scm-avf1 (49)
Description "Entailment of restriction subclasses via the allvaluesfrom construct part 1"
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

insert {Graph ${FULL_UNION_GRAPH}{
?c1 rdfs:subClassOf ?c2
}}

 WHERE {Graph ${FULL_UNION_GRAPH}{
  ?c1 owl:allValuesFrom ?y1 . 
  ?y1 rdfs:subClassOf ?y2. 
 ?c2 owl:allValuesFrom ?y2 . 
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

insert {Graph ${FULL_UNION_GRAPH}{
?c2 rdfs:subClassOf ?c1 
}}

 WHERE {Graph ${FULL_UNION_GRAPH}{
  ?c1 owl:allValuesFrom ?y . 
    ?c1 owl:onProperty ?p1 .  
  ?c2 owl:allValuesFrom ?y .  
  ?c2 owl:onProperty ?p2 .  
  ?p1 rdfs:subPropertyOf ?p2 
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

insert {Graph ${FULL_UNION_GRAPH}{
?p1 rdfs:domain ?c  
}}

 WHERE {Graph ${FULL_UNION_GRAPH}{
  ?p2 rdfs:domain ?c . 
   ?p1 rdfs:subPropertyOf ?p2 
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

insert {Graph ${FULL_UNION_GRAPH}{
?c1 rdfs:subClassOf ?c2
}}

 WHERE {Graph ${FULL_UNION_GRAPH}{
  ?c1 owl:hasValue ?i .  
  ?c1 owl:onProperty ?p1 .  
  ?c2 owl:hasValue ?i . 
   ?c2 owl:onProperty ?p2 . 
   ?p1 rdfs:subPropertyOf ?p2 
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

insert {Graph ${FULL_UNION_GRAPH}{
?c rdfs:subClassOf ?cl 
}}

 WHERE {Graph ${FULL_UNION_GRAPH}{
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

insert {Graph ${FULL_UNION_GRAPH}{
?p rdfs:range ?c2
}}

 WHERE {Graph ${FULL_UNION_GRAPH}{
  ?p rdfs:range ?c1 .  
  ?c1 rdfs:subClassOf ?c2 
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

insert {Graph ${FULL_UNION_GRAPH}{
?p1 rdfs:range ?c
}}

 WHERE {Graph ${FULL_UNION_GRAPH}{
    ?p2 rdfs:range ?c .	
    ?p1 rdfs:subPropertyOf ?p2 
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

insert {Graph ${FULL_UNION_GRAPH}{
?c1 rdfs:subClassOf ?c3
}}

 WHERE {Graph ${FULL_UNION_GRAPH}{
  ?c1 rdfs:subClassOf ?c2 . 
   ?c2 rdfs:subClassOf ?c3 
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

insert {Graph ${FULL_UNION_GRAPH}{
?p1 rdfs:subPropertyOf ?p3
}}

 WHERE {Graph ${FULL_UNION_GRAPH}{
  ?p1 rdfs:subPropertyOf ?p2 . 
  ?p2 rdfs:subPropertyOf ?p3 
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

insert {Graph ${FULL_UNION_GRAPH}{
?c1 rdfs:subClassOf ?c2 
}}

 WHERE {Graph ${FULL_UNION_GRAPH}{
  ?c1 owl:someValuesFrom ?y1 .  
  ?c1 owl:onProperty ?p . 
   ?c2 owl:someValuesFrom ?y2 .  
  ?c2 owl:onProperty ?p .  
  ?y1 rdfs:subClassOf ?y2 
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

insert {Graph ${FULL_UNION_GRAPH}{
?c1 rdfs:subClassOf ?c2  
}}

 WHERE {Graph ${FULL_UNION_GRAPH}{
  ?c1 owl:someValuesFrom ?y .  
  ?c1 owl:onProperty ?p1 .  
  ?c2 owl:someValuesFrom ?y .  
  ?c2 owl:onProperty ?p2 .  
  ?p1 rdfs:subPropertyOf ?p2  
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

insert {Graph ${FULL_UNION_GRAPH}{
?x a ?c2
}}

 WHERE {Graph ${FULL_UNION_GRAPH}{
 ?x a ?c1  
}
Graph ${FULL_UNION_GRAPH}{
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

insert {Graph ${FULL_UNION_GRAPH}{
?x ?p2 ?y 
}}

 WHERE {Graph ${FULL_UNION_GRAPH}{
 ?p1 rdfs:subPropertyOf ?p2 
}
Graph ${FULL_UNION_GRAPH}{
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

insert {Graph ${FULL_UNION_GRAPH}{
?x ?p ?z  
}}

 WHERE {Graph ${FULL_UNION_GRAPH}{
   ?p a owl:TransitiveProperty 
}
Graph ${FULL_UNION_GRAPH}{
    ?x ?p ?y .	
    ?y ?p ?z  
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

insert {Graph ${FULL_UNION_GRAPH}{
?u a ?x  
}}

 WHERE {Graph ${FULL_UNION_GRAPH}{
  ?x owl:hasValue ?y . 
   ?x owl:onProperty ?p .
}
Graph ${FULL_UNION_GRAPH}{
 ?u ?p ?y 
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

insert {Graph ${FULL_UNION_GRAPH}{
?y ?p2 ?x
}}

 WHERE {Graph ${FULL_UNION_GRAPH}{
      ?p1 owl:inverseOf ?p2 .  
}
Graph ${FULL_UNION_GRAPH}{
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

insert {Graph ${FULL_UNION_GRAPH}{
?y ?p1 ?x 
}}

 WHERE {Graph ${FULL_UNION_GRAPH}{
       ?p1 owl:inverseOf ?p2 
}
Graph ${FULL_UNION_GRAPH}{
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

insert {Graph ${FULL_UNION_GRAPH}{
 ?y a ?c 
}}

 WHERE {Graph ${FULL_UNION_GRAPH}{
?y a ?ci 
}
Graph ${FULL_UNION_GRAPH}{
    ?c owl:unionOf ?x . 
    ?x rdf:rest*/rdf:first ?ci 
}

}
</SparqlQuery>

</DataInference>


<ValidationRule>

Reference COINS-CAREX
Description "Exactly cardinality validatie Alt Existing"
ResultFormat " ${this} has ${aantal} of ${property}.  ${min} is necessary. "
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?this  ?min ?aantal ?property 

 WHERE {{
 select distinct ?this ?class ?min (count(distinct ?value) as ?aantal2) (count(?not) as ?notaantal)  ?property 

 WHERE {Graph ${FULL_UNION_GRAPH}{
   ?class a owl:Restriction .   
  ?class owl:cardinality ?min . 
   ?class owl:onProperty ?property  
}
Graph ${FULL_UNION_GRAPH}{
 ?this a ?class .
 	filter not exists {?this a <http://www.coinsweb.nl/cbim-2.0.rdf#ExpiredEntity>}  
   ?this ?property ?value 
 optional {?value a <http://www.coinsweb.nl/cbim-2.0.rdf#ExpiredEntity>. bind ("1"^^xsd:integer as ?not)}  
}

}group by ?this ?class ?min ?property 
}
 bind ((xsd:integer(?aantal2) - xsd:integer(?notaantal)) as ?aantal) 

filter((xsd:integer(?aantal)<xsd:integer(?min)) || (xsd:integer(?aantal)>xsd:integer(?min)) )

}
</SparqlQuery>

</ValidationRule>

<ValidationRule>

Reference COINS-CAREX
Description "Exactly cardinality validatie alt Zero"
ResultFormat " ${this} has no properties of ${property}.  ${min} is necessary. "
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?this ?min ?property 

 WHERE {Graph ${FULL_UNION_GRAPH}{
   ?class a owl:Restriction .   
  ?class owl:cardinality ?min . 
   ?class owl:onProperty ?property.  filter (xsd:integer(?min)>0)
}
Graph ${FULL_UNION_GRAPH}{
 ?this a ?class .
 	filter not exists {?this a <http://www.coinsweb.nl/cbim-2.0.rdf#ExpiredEntity>} 
 filter not exists{ ?this ?property ?value   } 
}

}
</SparqlQuery>

</ValidationRule>

<ValidationRule>

Reference COINS-minCarAltZero
Description "minimal cardinality constraint validation alternative zero values"
ResultFormat " ${this} has no properties of ${property}.  The minimum is ${min}. "
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?this ?min ?property 

 WHERE {Graph ${FULL_UNION_GRAPH}{
   ?class a owl:Restriction .   
  ?class owl:minCardinality ?min . 
   ?class owl:onProperty ?property.  
}
Graph ${FULL_UNION_GRAPH}{
 ?this a ?class .
 	filter not exists {?this a <http://www.coinsweb.nl/cbim-2.0.rdf#ExpiredEntity>} 
 filter not exists{ ?this ?property ?value   } 
}

}
</SparqlQuery>

</ValidationRule>

<ValidationRule>

Reference COINS-minCarAltExisting
Description "minimal cardinality constraint validation alternative existing values"
ResultFormat " ${this} has ${aantal} properties of ${property}.  The minimum is ${min}. "
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?this  ?min ?aantal ?property 

 WHERE {{
 select distinct ?this ?class ?min (count(distinct ?value) as ?aantal2) (count(?not) as ?notaantal)  ?property 

 WHERE {Graph ${FULL_UNION_GRAPH}{
   ?class a owl:Restriction .   
  ?class owl:minCardinality ?min . 
   ?class owl:onProperty ?property  
}
Graph ${FULL_UNION_GRAPH}{
 ?this a ?class .
 	filter not exists {?this a <http://www.coinsweb.nl/cbim-2.0.rdf#ExpiredEntity>}  
   ?this ?property ?value 
 optional {?value a <http://www.coinsweb.nl/cbim-2.0.rdf#ExpiredEntity>. bind ("1"^^xsd:integer as ?not)}  
}

}group by ?this ?class ?min ?property 
}
 bind ((xsd:integer(?aantal2) - xsd:integer(?notaantal)) as ?aantal) 

filter(xsd:integer(?aantal)<xsd:integer(?min))

}
</SparqlQuery>

</ValidationRule>

<ValidationRule>

Reference COINS-maxCarAltZero
Description "maximum cardinality constraint validation alternative zero values"
ResultFormat " ${this} has no properties of ${property}.  The maximum is ${min}. "
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?this ?min ?property 

 WHERE {Graph ${FULL_UNION_GRAPH}{
   ?class a owl:Restriction .   
  ?class owl:maxCardinality ?min . 
   ?class owl:onProperty ?property.  filter (STR(?min)="0")
}
Graph ${FULL_UNION_GRAPH}{
 ?this a ?class .
 	filter not exists {?this a <http://www.coinsweb.nl/cbim-2.0.rdf#ExpiredEntity>} 
 filter not exists{ ?this ?property ?value   } 
}

}
</SparqlQuery>

</ValidationRule>

<ValidationRule>

Reference COINS-maxCarAltExisting
Description "maximum cardinality constraint validation alternative existing values"
ResultFormat " ${this} has ${aantal} properties of ${property}.  The maximum is ${min}. "
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?this  ?min ?aantal ?property 

 WHERE {{
 select distinct ?this ?class ?min (count(distinct ?value) as ?aantal2) (count(?not) as ?notaantal)  ?property 

 WHERE {Graph ${FULL_UNION_GRAPH}{
   ?class a owl:Restriction .   
  ?class owl:maxCardinality ?min . 
   ?class owl:onProperty ?property  
}
Graph ${FULL_UNION_GRAPH}{
 ?this a ?class .
 	filter not exists {?this a <http://www.coinsweb.nl/cbim-2.0.rdf#ExpiredEntity>}  
   ?this ?property ?value 
 optional {?value a <http://www.coinsweb.nl/cbim-2.0.rdf#ExpiredEntity>. bind ("1"^^xsd:integer as ?not)}  
}

}group by ?this ?class ?min ?property 
}
 bind ((xsd:integer(?aantal2) - xsd:integer(?notaantal)) as ?aantal) 

filter(xsd:integer(?aantal)>xsd:integer(?min))

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

 WHERE {Graph ${FULL_UNION_GRAPH}{
   ?property a owl:FunctionalProperty
}
Graph ${FULL_UNION_GRAPH}{
 ?this ?property ?value. 
	filter not exists {?this a <http://www.coinsweb.nl/cbim-2.0.rdf#ExpiredEntity>} 
 filter not exists {?value a <http://www.coinsweb.nl/cbim-2.0.rdf#ExpiredEntity>}  
}

} group by  ?this ?property  
}
filter(xsd:integer(?aantal)>1) 

}
</SparqlQuery>

</ValidationRule>

<ValidationRule>

Reference COINS-QCRExAE
Description "Exactly Qualitative Cardinality restriction validatie AltExisting"
ResultFormat " ${this} has ${aantal} qualified properties of ${property} of type ${onClass}.  ${min} values is necessary. "
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?this  ?min ?aantal ?property  ?onClass

 WHERE {{
 select distinct ?this ?class ?min (count(distinct ?value) as ?aantal2) (count(?not) as ?notaantal)  ?property  ?onClass

 WHERE {Graph ${FULL_UNION_GRAPH}{
   ?class a owl:Restriction .   
  ?class owl:qualifiedCardinality ?min . 
   ?class owl:onProperty ?property  . ?class owl:onClass ?onClass 
}
Graph ${FULL_UNION_GRAPH}{
 ?this a ?class .
 	filter not exists {?this a <http://www.coinsweb.nl/cbim-2.0.rdf#ExpiredEntity>}  
   ?this ?property ?value . ?value a ?onClass
 optional {?value a <http://www.coinsweb.nl/cbim-2.0.rdf#ExpiredEntity>. bind ("1"^^xsd:integer as ?not)}  
}

}group by ?this ?class ?min ?property  ?onClass
}
 bind ((xsd:integer(?aantal2) - xsd:integer(?notaantal)) as ?aantal) 

filter((xsd:integer(?aantal)<xsd:integer(?min)) || (xsd:integer(?aantal)>xsd:integer(?min)) )

}
</SparqlQuery>

</ValidationRule>

<ValidationRule>

Reference COINS-QCRExAZ
Description "Exactly Qualitative Cardinality restriction validatie AltZero"
ResultFormat " ${this} has no qualified properties of ${property} of type ${onClass}.  ${min} values is necessary. "
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?this ?min ?property  ?onClass

 WHERE {Graph ${FULL_UNION_GRAPH}{
   ?class a owl:Restriction .   
  ?class owl:qualifiedCardinality ?min . ?class owl:onClass ?onClass . 
   ?class owl:onProperty ?property.  
}
Graph ${FULL_UNION_GRAPH}{
 ?this a ?class .
 	filter not exists {?this a <http://www.coinsweb.nl/cbim-2.0.rdf#ExpiredEntity>} 
 filter not exists{ ?this ?property ?value . ?value a ?onClass  } 
}

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

 select distinct ?this  ?min ?aantal ?property  ?onClass

 WHERE {{
 select distinct ?this ?class ?min (count(distinct ?value) as ?aantal2) (count(?not) as ?notaantal)  ?property  ?onClass

 WHERE {Graph ${FULL_UNION_GRAPH}{
   ?class a owl:Restriction .   
  ?class owl:minQualifiedCardinality ?min . 
   ?class owl:onProperty ?property  . ?class owl:onClass ?onClass 
}
Graph ${FULL_UNION_GRAPH}{
 ?this a ?class .
 	filter not exists {?this a <http://www.coinsweb.nl/cbim-2.0.rdf#ExpiredEntity>}  
   ?this ?property ?value . ?value a ?onClass
 optional {?value a <http://www.coinsweb.nl/cbim-2.0.rdf#ExpiredEntity>. bind ("1"^^xsd:integer as ?not)}  
}

}group by ?this ?class ?min ?property  ?onClass
}
 bind ((xsd:integer(?aantal2) - xsd:integer(?notaantal)) as ?aantal) 

filter((xsd:integer(?aantal)<xsd:integer(?min)) )   

}
</SparqlQuery>

</ValidationRule>

<ValidationRule>

Reference COINS-QCRMinAZ
Description "Minimal Qualitative Cardinality restriction validatie Alt Zero"
ResultFormat " ${this} has no qualified properties of ${property} .  the minimum is ${min}. "
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?this ?min ?property  ?onClass

 WHERE {Graph ${FULL_UNION_GRAPH}{
   ?class a owl:Restriction .   
  ?class owl:minQualifiedCardinality ?min . ?class owl:onClass ?onClass . 
   ?class owl:onProperty ?property.   filter (xsd:integer(?min)>0) 
}
Graph ${FULL_UNION_GRAPH}{
 ?this a ?class .
 	filter not exists {?this a <http://www.coinsweb.nl/cbim-2.0.rdf#ExpiredEntity>} 
 filter not exists{ ?this ?property ?value . ?value a ?onClass  } 
}

}
</SparqlQuery>

</ValidationRule>

<ValidationRule>

Reference COINS-QCRMaxAE
Description "Maximum Qualitative Cardinality restriction validatie AltExisting"
ResultFormat " ${this} has ${aantal} qualified properties of ${property} .  the maximum is ${min}. "
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?this  ?min ?aantal ?property  ?onClass

 WHERE {{
 select distinct ?this ?class ?min (count(distinct ?value) as ?aantal2) (count(?not) as ?notaantal)  ?property  ?onClass

 WHERE {Graph ${FULL_UNION_GRAPH}{
   ?class a owl:Restriction .   
  ?class owl:maxQualifiedCardinality ?min . 
   ?class owl:onProperty ?property  . ?class owl:onClass ?onClass 
}
Graph ${FULL_UNION_GRAPH}{
 ?this a ?class .
 	filter not exists {?this a <http://www.coinsweb.nl/cbim-2.0.rdf#ExpiredEntity>}  
   ?this ?property ?value . ?value a ?onClass
 optional {?value a <http://www.coinsweb.nl/cbim-2.0.rdf#ExpiredEntity>. bind ("1"^^xsd:integer as ?not)}  
}

}group by ?this ?class ?min ?property  ?onClass
}
 bind ((xsd:integer(?aantal2) - xsd:integer(?notaantal)) as ?aantal) 

filter((xsd:integer(?aantal)>xsd:integer(?min)) ) 

}
</SparqlQuery>

</ValidationRule>

<ValidationRule>

Reference COINS-QCRMaxAZ
Description "Maximum Qualitative Cardinality restriction validatie AltZero"
ResultFormat " ${this} has no qualified properties of ${property} .  the maximum is ${min}. "
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?this ?min ?property  ?onClass

 WHERE {Graph ${FULL_UNION_GRAPH}{
   ?class a owl:Restriction .   
  ?class owl:maxQualifiedCardinality ?min . ?class owl:onClass ?onClass . 
   ?class owl:onProperty ?property.  
}
Graph ${FULL_UNION_GRAPH}{
 ?this a ?class .
 	filter not exists {?this a <http://www.coinsweb.nl/cbim-2.0.rdf#ExpiredEntity>} 
 filter not exists{ ?this ?property ?value . ?value a ?onClass  } 
}

}
</SparqlQuery>

</ValidationRule>

<ValidationRule>

Reference COINS-DTVC
Description "Datatype check"
ResultFormat " ${value} is of the type ${dat} which is not valid for ${prop} for individual ${a}. It should be a ${range}. "
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?a ?prop ?range ?value (Datatype(?value) as ?dat)

 WHERE {Graph ${FULL_UNION_GRAPH}{
  ?prop  a owl:DatatypeProperty . 	
		   ?prop rdfs:range ?range. 
}
Graph ${FULL_UNION_GRAPH}{
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

 WHERE {Graph ${FULL_UNION_GRAPH}{
  ?prop  a owl:DatatypeProperty 
}
Graph ${FULL_UNION_GRAPH}{
  ?a ?prop ?value. 
	filter(!isLiteral(?value)) 
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

 WHERE {Graph ${FULL_UNION_GRAPH}{
  ?prop  a owl:ObjectProperty 
}
Graph ${FULL_UNION_GRAPH}{
  ?a ?prop ?value. 
	filter(!isIRI(?value)) 
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

Reference COINS-rng
Description "Range als een restrictie"
ResultFormat "individual ${a} has a property ${prop} with value ${b}. ${b} is not of the type ${range} "
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?a ?prop ?b ?range

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

Reference COINS-HVC
Description "hasValue check"
ResultFormat " ${value} is not set for object property ${prop} for individual ${a}"
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?res ?a ?prop ?value

 WHERE {Graph ${FULL_UNION_GRAPH}{
 ?res  a owl:Restriction. ?res owl:hasValue ?value. ?res owl:onProperty ?prop
}
{
Graph ${FULL_UNION_GRAPH}{
 ?a a ?res
}
} minus {
Graph ${FULL_UNION_GRAPH}{
 ?a a ?res. ?a ?prop ?value 
}
}

}
</SparqlQuery>

</ValidationRule>

<ValidationRule>

Reference COINS-AVFOneOf
Description "owl:allValuesFrom oneOf value check"
ResultFormat " ${a} is not correct for property ${prop}"
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?res ?a ?prop 

 WHERE {{
{
 select distinct ?res ?a ?prop 

 WHERE {Graph ${FULL_UNION_GRAPH}{
 ?res  a owl:Restriction. ?res owl:allValuesFrom/owl:oneOf/rdf:rest*/rdf:first ?value. ?res owl:onProperty ?prop
}
Graph ${FULL_UNION_GRAPH}{
 ?a a ?res
}

}}
} minus {
{
 select distinct ?res ?a ?prop 

 WHERE {Graph ${FULL_UNION_GRAPH}{
 ?res  a owl:Restriction. ?res owl:allValuesFrom/owl:oneOf/rdf:rest*/rdf:first ?value. ?res owl:onProperty ?prop
}
Graph ${FULL_UNION_GRAPH}{
  ?a a ?res. ?a ?prop ?value
}

}}
}

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

Reference COINS-IS
Description "Intersection as a restriction validation"
ResultFormat " ${a} does not fulfill the Intersection restriction ${restriction} "
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
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

Reference Cax-adx (listmod)
Description "Disjoint via owl:allDisjointClasses "
ResultFormat "instance ${x} is a member of ${c1} and of ${c2}. ${c1} and ${c2} are disjoint so instance ${x} cannot be a member of both classes "
<SparqlQuery>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

 select distinct ?x ?c1 ?c2

 WHERE {Graph ${FULL_UNION_GRAPH}{
 ?y a owl:AllDisjointClasses .  
  ?y owl:members ?members .  
   ?members rdf:rest*/rdf:first ?c1 .	
	    ?members rdf:rest*/rdf:first ?c2 .  	
	    FILTER (?c1 != ?c2) .
}
Graph ${FULL_UNION_GRAPH}{
 ?x a ?c1 . 	
 ?x a ?c2 
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

 WHERE {Graph ${FULL_UNION_GRAPH}{
 ?c1 owl:disjointWith ?c2 
}
Graph ${FULL_UNION_GRAPH}{
   ?x a ?c1 . 
  ?x a ?c2 .
}

}
</SparqlQuery>

</ValidationRule>
