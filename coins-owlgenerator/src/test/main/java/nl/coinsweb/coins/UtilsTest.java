package nl.coinsweb.sdk;

import nl.coinsweb.sdk.owlgenerator.Utils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class UtilsTest {

  @Test
  public void testNamespaceMapping() {
    assertEquals("nl.coinsweb.cbim",             Utils.namespaceUriToPackage("http://www.coinsweb.nl/cbim-2.0.rdf#"));
    assertEquals("nl.rws.coins.otl.otlspoorzad", Utils.namespaceUriToPackage("http://www.rws.nl/COINS2.0/OTL/OTL-SPOOR-ZAD-0.2#"));
    assertEquals("nl.rws.coins.otl.otlspoorzad", Utils.namespaceUriToPackage("http://www.rws.nl/COINS2.0/OTL/OTL-SPOOR-ZAD-0.2#blabla"));
  }

  @Test
  public void testJavaClassNameGeneration() {

    assertEquals("FinancieleLease",                                 Utils.asLegalJavaID("FinanciëleLease", true));
    assertEquals("EcologischeVerbindingszoneNatuurgebied",          Utils.asLegalJavaID("Ecologische verbindingszone-Natuurgebied", true));
    assertEquals("RoeiKanoZeilEnSurfsportED",                       Utils.asLegalJavaID("Roei-, kano-, zeil- en surfsport e.d.", true));
    assertEquals("ecologischeVerbindingszoneNatuurgebied",          Utils.asLegalJavaID("Ecologische verbindingszone-Natuurgebied", false));
    assertEquals("roeiKanoZeilEnSurfsportED",                       Utils.asLegalJavaID("Roei-, kano-, zeil- en surfsport e.d.", false));
    assertEquals("BUSSymbool",                                      Utils.asLegalJavaID("BUS symbool", true));
    assertEquals("Bussymbool",                                      Utils.asLegalJavaID("Bussymbool", true));
  }
}
