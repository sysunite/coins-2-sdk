package nl.coinsweb.sdk;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class NamespaceTest {

  @Test
  public void testNamespace() {

    assertFalse(new Namespace("http://baab.nl/dingdong/#").equals(new Namespace("http://baab.nl/dingdong#")));

    assertTrue(new Namespace("http://baab.nl/dingdong").equals(new Namespace("http://baab.nl/dingdong#")));
    assertTrue(new Namespace("http://baab.nl/dingdong").equals(new Namespace("http://baab.nl/dingdong#").toString()));
    assertTrue(new Namespace("http://baab.nl/dingdong").equals(new Namespace("http://baab.nl/dingdong#").toString()));
    assertTrue(new Namespace("http://baab.nl/dingdong").equals(new Namespace("http://baab.nl/dingdong#")));
    assertTrue(new Namespace("http://baab.nl/dingdong").equals("http://baab.nl/dingdong#"));

    assertFalse(new Namespace("http://baab.nl/dingdong").equals(new Namespace("http://baab.nl/dingdong/")));
    assertFalse(new Namespace("http://baab.nl/dingdong").equals(new Namespace("http://baab.nl/dingdong/").toString()));
    assertFalse(new Namespace("http://baab.nl/dingdong").equals(new Namespace("http://baab.nl/dingdong/").toString()));
    assertFalse(new Namespace("http://baab.nl/dingdong").equals(new Namespace("http://baab.nl/dingdong/")));
    assertFalse(new Namespace("http://baab.nl/dingdong").equals("http://baab.nl/dingdong/"));

    assertFalse(new Namespace("http://baab.nl/dingdong/").equals(new Namespace("http://baab.nl/dingdong#")));
    assertFalse(new Namespace("http://baab.nl/dingdong/").equals(new Namespace("http://baab.nl/dingdong#").toString()));
    assertFalse(new Namespace("http://baab.nl/dingdong/").equals(new Namespace("http://baab.nl/dingdong#").toString()));
    assertFalse(new Namespace("http://baab.nl/dingdong/").equals(new Namespace("http://baab.nl/dingdong#")));
    assertFalse(new Namespace("http://baab.nl/dingdong/").equals("http://baab.nl/dingdong#"));


    assertTrue(new Namespace("http://baab.nl/dingdong").hashCode() == new Namespace("http://baab.nl/dingdong#").hashCode());
    assertTrue(new Namespace("http://baab.nl/dingdong").hashCode() == new Namespace("http://baab.nl/dingdong#").hashCode());
    assertTrue(new Namespace("http://baab.nl/dingdong").hashCode() == new Namespace("http://baab.nl/dingdong#").hashCode());
    assertTrue(new Namespace("http://baab.nl/dingdong").hashCode() == new Namespace("http://baab.nl/dingdong#").hashCode());


    assertFalse(new Namespace("http://baab.nl/dingdong").hashCode() == new Namespace("http://baab.nl/dingdong/").hashCode());
    assertFalse(new Namespace("http://baab.nl/dingdong").hashCode() == new Namespace("http://baab.nl/dingdong/").hashCode());
    assertFalse(new Namespace("http://baab.nl/dingdong").hashCode() == new Namespace("http://baab.nl/dingdong/").hashCode());
    assertFalse(new Namespace("http://baab.nl/dingdong").hashCode() == new Namespace("http://baab.nl/dingdong/").hashCode());


    assertFalse(new Namespace("http://baab.nl/dingdong/").hashCode() == new Namespace("http://baab.nl/dingdong#").hashCode());
    assertFalse(new Namespace("http://baab.nl/dingdong/").hashCode() == new Namespace("http://baab.nl/dingdong#").hashCode());
    assertFalse(new Namespace("http://baab.nl/dingdong/").hashCode() == new Namespace("http://baab.nl/dingdong#").hashCode());
    assertFalse(new Namespace("http://baab.nl/dingdong/").hashCode() == new Namespace("http://baab.nl/dingdong#").hashCode());


    assertTrue(new Namespace("http://baab.nl/dingdong/").hashCode() == new Namespace("http://baab.nl/dingdong/").hashCode());
    assertTrue(new Namespace("http://baab.nl/dingdong/").hashCode() == new Namespace("http://baab.nl/dingdong/").hashCode());
    assertTrue(new Namespace("http://baab.nl/dingdong/").hashCode() == new Namespace("http://baab.nl/dingdong/").hashCode());
    assertTrue(new Namespace("http://baab.nl/dingdong/").hashCode() == new Namespace("http://baab.nl/dingdong/").hashCode());

    assertEquals(new Namespace("http://baab.nl/dingdong/#babab"), new Namespace("http://baab.nl/dingdong/"));
  }
}