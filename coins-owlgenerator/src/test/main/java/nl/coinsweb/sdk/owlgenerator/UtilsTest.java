package nl.coinsweb.sdk.owlgenerator;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class UtilsTest {

  @Test
  public void testNamespaceUriToPackage() throws Exception {
    assertEquals("nl.co.host.sub1.sub2.folder.name", Utils.namespaceUriToPackage("http://sub2.sub1.host.co.nl/folder/1/name#wonders"));
    assertEquals("nl.co.hostspecial.sub1.sub2.folder4.name", Utils.namespaceUriToPackage("http://sub2.sub1.host-special.co.nl/folder4/1/name#wonders"));
    assertEquals("nl.co.host.sub1.sub2.folder.name", Utils.namespaceUriToPackage("http://su~b2.sub1.host%.co.nl/fol*der/1/name#wonders"));
  }
}