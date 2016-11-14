#!/usr/bin/env bash

mkdir tmp
rm -rf testsuite/lib
mkdir testsuite/lib


## generate generate elephant model
coins-cli generate \
  -i testsuite/src/test/resources/D4/olifant.rdf \
  -o tmp/generated-sources/java \
  -jarto testsuite/lib \
  -q

## generate lifecycle
coins-cli generate \
  -i testsuite/src/test/resources/B15/lifecycle.rdf \
  -o tmp/generated-sources/java \
  -jarto testsuite/lib \
  -sources \
  -q

# generate se library
coins-cli generate \
  -i testsuite/src/test/resources/B15/SE.rdf \
  -o tmp/generated-sources/java \
  -jarto testsuite/lib \
  -sources \
  -q

# generate core model
coins-cli generate \
  -i coins-api/src/main/resources/libraries/cbim-2.0.rdf \
     coins-api/src/main/resources/libraries/units-2.0.rdf \
     coins-api/src/main/resources/libraries/BranchVersioning.rdf \
     coins-api/src/main/resources/libraries/COINSWOA.rdf \
  -o tmp/generated-sources/java \
  -order "http://qudt.org/schema/qudt#;http://www.coinsweb.nl/cbim-2.0.rdf#;http://www.coinsweb.nl/coins2/referencemodel/BranchVersioning#;http://www.coinsweb.nl/COINSWOA.rdf#" \
  -jarto testsuite/lib \
  -sources \
  -q

rm -rf tmp