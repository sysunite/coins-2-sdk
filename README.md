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
