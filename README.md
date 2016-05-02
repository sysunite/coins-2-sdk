# COINS 2 SDK
This is the official COINS 2 software development kit for Java and .NET.

###Getting started

Download the latest build, for .NET:

(dlls needed from [ikvm 7.2](https://sourceforge.net/projects/ikvm/files/ikvm/7.2.4630.5/ikvmbin-7.2.4630.5.zip)/bin, see list [DLL dependencies](https://github.com/sysunite/coins-2-sdk/wiki/DLL-dependencies))
* [coins-api.dll](/dist/sdk/dll/coins-api.dll?raw=true)
* [coins-cli.exe](/dist/sdk/dll/coins-cli.exe?raw=true) 
* [pre-generated libraries (dll)](/dist/library/dll)

For Java:
* [coins-api.jar](/dist/sdk/jar/coins-api.jar?raw=true)
* [coins-cli.jar](/dist/sdk/jar/coins-cli.jar?raw=true)
* [pre-generated libraries (jar)](/dist/library/jar)

###Main components
The SDK consists of a set of tools:
* [coins-api](https://github.com/sysunite/coins-2-sdk/wiki/Coins-api-reference)
* [coins-cli](https://github.com/sysunite/coins-2-sdk/wiki/Coins-cli-installation)
* [coins-owlgenerator](https://github.com/sysunite/coins-2-sdk/wiki/Coins-cli-owlgenerator)
* [coins-viewer](https://github.com/sysunite/coins-2-sdk/wiki/Coins-cli-viewer)


###Documentation
Visit the [documentation](https://github.com/sysunite/coins-2-sdk/wiki) page for more information.
 

###Examples
```java
ModelFactory factory = new JenaModelFactory();
JenaCoinsContainer model = new JenaCoinsContainer(factory, "http://playground.com/");

Assembly a = new Assembly(model);

Iterator<String> individuals = model.listIndividualUris(LengthUnit.classUri).iterator();
while(individuals.hasNext()) {

    System.out.println(individuals.next());

    // Result:
    // ...
    // http://qudt.org/vocab/unit#Kilometer
    // http://qudt.org/vocab/unit#Point
    // http://qudt.org/vocab/unit#Decimeter
    // http://qudt.org/vocab/unit#Millimeter
    // http://qudt.org/vocab/unit#NauticalMile
    // ...
}

FloatProperty property = new FloatProperty(model);
a.addHasProperties(property);

property.setSimpleProperty(11.3f);
property.setUnit(new LengthUnit(model, LengthUnit.MILLIMETER));

DatasetAsserts.logTriples(model.getJenaModel());
DatasetAsserts.logTriples(model.getWoaModel());

Iterator<Map<String, String>> result =  model.query("SELECT * WHERE { GRAPH <http://playground.com/> { ?s ?p ?o}}");
while(result.hasNext()) {
  Map<String, String> row = result.next();
  log.debug(row.toString());
}

model.export("/tmp/container.ccr");
```

This results in a container.cr with in it a content.rdf file with these triples:

```ttl
[http://playground.com/ab5856a0-3cfe-45f6-90b9-6b8ad8e20afd, http://www.coinsweb.nl/cbim-2.0.rdf#hasProperties, http://playground.com/43ab5cdb-d575-4ea7-9d68-597b3510a020]
[http://playground.com/ab5856a0-3cfe-45f6-90b9-6b8ad8e20afd, http://www.coinsweb.nl/cbim-2.0.rdf#creationDate, "2016-05-01T23:54:37.193Z"^^http://www.w3.org/2001/XMLSchema#dateTime]
[http://playground.com/ab5856a0-3cfe-45f6-90b9-6b8ad8e20afd, http://www.coinsweb.nl/cbim-2.0.rdf#creator, http://sandbox.coinsweb.nl/defaultUser]
[http://playground.com/ab5856a0-3cfe-45f6-90b9-6b8ad8e20afd, http://www.w3.org/1999/02/22-rdf-syntax-ns#type, http://www.coinsweb.nl/cbim-2.0.rdf#Assembly]
[http://playground.com/, http://www.w3.org/2002/07/owl#imports, http://www.coinsweb.nl/coins2/referencemodel/BranchVersioning#]
[http://playground.com/, http://www.w3.org/2002/07/owl#imports, http://www.coinsweb.nl/COINSWOA.rdf#]
[http://playground.com/, http://www.w3.org/2002/07/owl#imports, http://www.coinsweb.nl/units-2.0.rdf#]
[http://playground.com/, http://www.w3.org/2002/07/owl#imports, http://www.coinsweb.nl/cbim-2.0.rdf#]
[http://playground.com/, http://www.coinsweb.nl/cbim-2.0.rdf#containerId, "e737a9a6-138d-489e-9547-d3aec7ab5a30"^^http://www.w3.org/2001/XMLSchema#string]
[http://playground.com/, http://www.coinsweb.nl/cbim-2.0.rdf#creator, http://sandbox.coinsweb.nl/defaultUser]
[http://playground.com/, http://www.w3.org/1999/02/22-rdf-syntax-ns#type, http://www.w3.org/2002/07/owl#Ontology]
[http://playground.com/43ab5cdb-d575-4ea7-9d68-597b3510a020, http://www.coinsweb.nl/cbim-2.0.rdf#unit, http://qudt.org/vocab/unit#Millimeter]
[http://playground.com/43ab5cdb-d575-4ea7-9d68-597b3510a020, http://www.coinsweb.nl/cbim-2.0.rdf#datatypeValue, "11.3"^^http://www.w3.org/2001/XMLSchema#float]
[http://playground.com/43ab5cdb-d575-4ea7-9d68-597b3510a020, http://www.coinsweb.nl/cbim-2.0.rdf#creationDate, "2016-05-01T23:54:37.669Z"^^http://www.w3.org/2001/XMLSchema#dateTime]
[http://playground.com/43ab5cdb-d575-4ea7-9d68-597b3510a020, http://www.coinsweb.nl/cbim-2.0.rdf#creator, http://sandbox.coinsweb.nl/defaultUser]
[http://playground.com/43ab5cdb-d575-4ea7-9d68-597b3510a020, http://www.w3.org/1999/02/22-rdf-syntax-ns#type, http://www.coinsweb.nl/cbim-2.0.rdf#FloatProperty]
```

```
{p=http://www.coinsweb.nl/cbim-2.0.rdf#creator, s=http://playground.com/, o=http://sandbox.coinsweb.nl/defaultUser}
{p=http://www.w3.org/1999/02/22-rdf-syntax-ns#type, s=http://playground.com/, o=http://www.w3.org/2002/07/owl#Ontology}
{p=http://www.w3.org/2002/07/owl#imports, s=http://playground.com/, o=http://www.coinsweb.nl/coins2/referencemodel/BranchVersioning#}
{p=http://www.w3.org/2002/07/owl#imports, s=http://playground.com/, o=http://www.coinsweb.nl/COINSWOA.rdf#}
{p=http://www.w3.org/2002/07/owl#imports, s=http://playground.com/, o=http://www.coinsweb.nl/units-2.0.rdf#}
{p=http://www.w3.org/2002/07/owl#imports, s=http://playground.com/, o=http://www.coinsweb.nl/cbim-2.0.rdf#}
{p=http://www.coinsweb.nl/cbim-2.0.rdf#containerId, s=http://playground.com/, o=e737a9a6-138d-489e-9547-d3aec7ab5a30}
{p=http://www.coinsweb.nl/cbim-2.0.rdf#hasProperties, s=http://playground.com/ab5856a0-3cfe-45f6-90b9-6b8ad8e20afd, o=http://playground.com/43ab5cdb-d575-4ea7-9d68-597b3510a020}
{p=http://www.coinsweb.nl/cbim-2.0.rdf#creationDate, s=http://playground.com/ab5856a0-3cfe-45f6-90b9-6b8ad8e20afd, o=2016-05-01T23:54:37.193Z}
{p=http://www.coinsweb.nl/cbim-2.0.rdf#creator, s=http://playground.com/ab5856a0-3cfe-45f6-90b9-6b8ad8e20afd, o=http://sandbox.coinsweb.nl/defaultUser}
{p=http://www.w3.org/1999/02/22-rdf-syntax-ns#type, s=http://playground.com/ab5856a0-3cfe-45f6-90b9-6b8ad8e20afd, o=http://www.coinsweb.nl/cbim-2.0.rdf#Assembly}
{p=http://www.coinsweb.nl/cbim-2.0.rdf#creationDate, s=http://playground.com/43ab5cdb-d575-4ea7-9d68-597b3510a020, o=2016-05-01T23:54:37.669Z}
{p=http://www.coinsweb.nl/cbim-2.0.rdf#creator, s=http://playground.com/43ab5cdb-d575-4ea7-9d68-597b3510a020, o=http://sandbox.coinsweb.nl/defaultUser}
{p=http://www.w3.org/1999/02/22-rdf-syntax-ns#type, s=http://playground.com/43ab5cdb-d575-4ea7-9d68-597b3510a020, o=http://www.coinsweb.nl/cbim-2.0.rdf#FloatProperty}
{p=http://www.coinsweb.nl/cbim-2.0.rdf#unit, s=http://playground.com/43ab5cdb-d575-4ea7-9d68-597b3510a020, o=http://qudt.org/vocab/unit#Millimeter}
{p=http://www.coinsweb.nl/cbim-2.0.rdf#datatypeValue, s=http://playground.com/43ab5cdb-d575-4ea7-9d68-597b3510a020, o=11.3}
```
