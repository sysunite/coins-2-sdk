# COINS 2 SDK
This is the official COINS 2 software development kit for Java and .NET.

###Getting started

Download the latest build, for dotNET:
* dist/sdk/dll/[coins-api.dll](/dist/sdk/dll/coins-api.dll?raw=true)
* dist/sdk/dll/[coins-cli.exe](/dist/sdk/dll/coins-cli.exe?raw=true) (dlls needed from [ikvm 7.2](https://sourceforge.net/projects/ikvm/files/ikvm/7.2.4630.5/ikvmbin-7.2.4630.5.zip)/bin, see list [DLL dependencies](https://github.com/sysunite/coins-2-sdk/wiki/DLL-dependencies)). 

For Java:
* dist/sdk/jar/[coins-api.jar](/dist/sdk/jar/coins-api.jar?raw=true)
* dist/sdk/jar/[coins-cli.jar](/dist/sdk/jar/coins-cli.jar?raw=true)

###Main components
The SDK consists of a set of tools:
* coins-api
* coins-cli
* coins-owlgenerator

And a set of [generated libraries](/dist/library):
* core
* qudt


###Documentation
Visit the [documentation](https://github.com/sysunite/coins-2-sdk/wiki) page for more information.
 

###Examples
```java
JenaCoinsContainer model = new TDBCoinsContainer("http://playground.com/");

Assembly a = new Assembly(model);

Iterator<String> individuals = model.listIndividualUris(LengthUnit.classUri).iterator();
while(individuals.hasNext()) {
  System.out.println(individuals.next());

//  Result:
//  ...
//  http://qudt.org/vocab/unit#Point
//  http://qudt.org/vocab/unit#Decimeter
//  http://qudt.org/vocab/unit#Millimeter
//  http://qudt.org/vocab/unit#NauticalMile
//  http://qudt.org/vocab/unit#Centimeter
//  http://qudt.org/vocab/unit#Fathom
//  ...
}

FloatProperty property = new FloatProperty(model);
a.addHasProperties(property);

property.setSimpleProperty(11.3f);
property.setUnit(new LengthUnit(model, LengthUnit.MILLIMETER));

model.export("/tmp/container.ccr");
```

This results in a container.cr with in it a content.rdf file with these triples:

```ttl
<http://playground.com/> <http://www.w3.org/2002/07/owl#imports> <http://www.coinsweb.nl/COINSWOA.rdf#> .
<http://playground.com/> <http://www.w3.org/2002/07/owl#imports> <http://www.coinsweb.nl/units-2.0.rdf#> .
<http://playground.com/> <http://www.w3.org/2002/07/owl#imports> <http://www.coinsweb.nl/cbim-2.0.rdf#> .
<http://playground.com/> <http://www.coinsweb.nl/cbim-2.0.rdf#containerId> "7a0a5aa4-a7bc-4f19-a69b-1b11f9da82b4"^^<http://www.w3.org/2001/XMLSchema#string> .
<http://playground.com/> <http://www.coinsweb.nl/cbim-2.0.rdf#creator> <http://sandbox.coinsweb.nl/defaultUser> .
<http://playground.com/> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Ontology>
<http://playground.com/06b9d440-a0ba-41ea-ad79-1c35dbf18f19> <http://www.coinsweb.nl/cbim-2.0.rdf#unit> <http://qudt.org/vocab/unit#Millimeter> .
<http://playground.com/06b9d440-a0ba-41ea-ad79-1c35dbf18f19> <http://www.coinsweb.nl/cbim-2.0.rdf#datatypeValue> "11.3"^^<http://www.w3.org/2001/XMLSchema#float> .
<http://playground.com/06b9d440-a0ba-41ea-ad79-1c35dbf18f19> <http://www.coinsweb.nl/cbim-2.0.rdf#creationDate> "2016-04-06T08:14:14.629Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
<http://playground.com/06b9d440-a0ba-41ea-ad79-1c35dbf18f19> <http://www.coinsweb.nl/cbim-2.0.rdf#creator> <http://sandbox.coinsweb.nl/defaultUser> .
<http://playground.com/06b9d440-a0ba-41ea-ad79-1c35dbf18f19> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.coinsweb.nl/cbim-2.0.rdf#FloatProperty>.
<http://playground.com/b3b46ce3-a1e0-4e23-90f3-1bbdad1c77c6> <http://www.coinsweb.nl/cbim-2.0.rdf#hasProperties> <http://playground.com/06b9d440-a0ba-41ea-ad79-1c35dbf18f19> .
<http://playground.com/b3b46ce3-a1e0-4e23-90f3-1bbdad1c77c6> <http://www.coinsweb.nl/cbim-2.0.rdf#creationDate> "2016-04-06T08:14:14.038Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
<http://playground.com/b3b46ce3-a1e0-4e23-90f3-1bbdad1c77c6> <http://www.coinsweb.nl/cbim-2.0.rdf#creator> <http://sandbox.coinsweb.nl/defaultUser> .
<http://playground.com/b3b46ce3-a1e0-4e23-90f3-1bbdad1c77c6> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.coinsweb.nl/cbim-2.0.rdf#Assembly> .
```
