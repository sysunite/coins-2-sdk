# coins-cli
This is the official COINS 2.0 software development kit from the Dutch RWS.

###Usage of command line interface tool
```
laptop:~ user$ coins-cli
߷ COINS 2.0
command line interface 1.0-SNAPSHOT
(c) 2016 Sysunite B.V.

usage: coins-cli [ unzip | describe | map | generate ]
 -cif,--copy-input-files <arg>   copy input files to specified directory
                                 (can be used for copying to resource
                                 folder for creating a jar)
 -e <arg>                        the encoding of the output file: RDF/XML
                                 | RDF/XML-ABBREV | N-TRIPLE | TURTLE |
                                 TTL | N3
 -i <arg>                        input file
 -m,--mapping <arg>              mapping of namespaces to package names
 -o <arg>                        output file
 -q                              quite, not output to the console
 -v                              verbose logging

```

###Running from maven
```xml
  <build>
    <plugins>
      <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>exec-maven-plugin</artifactId>
        <executions>

          <execution>
            <id>first-execution</id>
            <phase>generate-sources</phase>
            <goals>
              <goal>exec</goal>
            </goals>
            <configuration>
              <executable>
                coins-cli
              </executable>
              <commandlineArgs>
                generate
                -i ${basedir}/src/main/resources/Cbim-2.0-RC15.rdf
                   ${basedir}/src/main/resources/OTLSpoorOTL.rdf
                -o ${project.build.directory}/generated-sources/java

                --copy-input-files ${project.build.directory}/generated-sources/resources
                --mapping http://www.coinsweb.nl/cbim-2.0.rdf# = nl.rws.coins.ontology.engineclasses
                          http://www.rws.nl/COINS2.0/OTL/OTL-SPOOR-ZAD-0.2# = nl.rws.coins.ontology.otl
              </commandlineArgs>
            </configuration>
          </execution>
          
          ...
```
