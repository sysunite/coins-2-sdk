# COINS 2.0 SDK
This is the official COINS 2.0 software development kit for Java and .NET. The SDK can help you in the following situations:
* you have a .ccr file and you want to **view the content**
* you want to **write some software** to read or create .ccr files
* you write software that works with **Object Libraries** that are specified in **RDF**

###Main components
The SDK consists of a set of tools:
* [coins-api](https://github.com/sysunite/coins-2-sdk/wiki/coins-api-reference) - software library to include in your Java or .NET project
```xml
<dependency>
  <groupId>com.sysunite.coinsweb</groupId>
  <artifactId>coins-api</artifactId>
  <version>1.1.502</version>
</dependency>
```
(currently only the api is available via maven)
* [coins-cli](https://github.com/sysunite/coins-2-sdk/wiki/installation-of-coins-cli) - command line tool for Windows / Linux / Mac that contains all tools
* [viewer](https://github.com/sysunite/coins-2-sdk/wiki/coins-cli-viewer) - view the content of a .ccr file
* [validator](https://github.com/sysunite/coins-2-sdk/wiki/coins-cli-validate) - validate the content of a .ccr file
* [generator](https://github.com/sysunite/coins-2-sdk/wiki/coins-cli-generate) - generate Java or .NET classes from an RDF Object Library


###Getting started

For using the tools:
* either run the jar version of the CLI [coins-cli.jar](https://github.com/sysunite/coins-2-sdk/releases/tag/v1.1.501)
* or run the Windows executable [coins-cli.exe](https://github.com/sysunite/coins-2-sdk/releases/tag/v1.1.501) (don't forget to add the needed [ikvm dlls](https://github.com/sysunite/coins-2-sdk/wiki/DLL-dependencies))

For a Java project:
* use the maven dependency showed above
* or download the most recent [coins-api.jar](https://github.com/sysunite/coins-2-sdk/releases/tag/v1.1.501)
* optionally downloaded [pre-generated libraries (jar)](https://github.com/sysunite/coins-2-sdk/releases/tag/v1.1.501)

For a dotNET project:
* download the most recent [coins-api.dll](https://github.com/sysunite/coins-2-sdk/releases/tag/v1.1.501)
* download the dlls needed from [ikvm 7.2](https://sourceforge.net/projects/ikvm/files/ikvm/7.2.4630.5/ikvmbin-7.2.4630.5.zip)/bin, see list [DLL dependencies](https://github.com/sysunite/coins-2-sdk/wiki/DLL-dependencies))
* optionally downloaded [pre-generated libraries (dll)](https://github.com/sysunite/coins-2-sdk/releases/tag/v1.1.501)

###Documentation
Visit the [documentation](https://github.com/sysunite/coins-2-sdk/wiki) page for more information.
