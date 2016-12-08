package nl.coinsweb.sdk;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class NamespaceTest {

  @Test
  public void testNamespace() {

    assertTrue(new Namespace("http://sandbox.com").equals(new Namespace("http://sandbox.com#")));
    assertTrue(new Namespace("http://sandbox.com/").equals(new Namespace("http://sandbox.com#")));
    assertTrue(new Namespace("http://sandbox.com#").equals(new Namespace("http://sandbox.com#")));
    assertTrue(new Namespace("http://sandbox.com/#").equals(new Namespace("http://sandbox.com#")));
    assertTrue(new Namespace("http://sandbox.com").equals(new Namespace("http://sandbox.com/")));
    assertTrue(new Namespace("http://sandbox.com/").equals(new Namespace("http://sandbox.com/")));
    assertTrue(new Namespace("http://sandbox.com#").equals(new Namespace("http://sandbox.com/")));
    assertTrue(new Namespace("http://sandbox.com/#").equals(new Namespace("http://sandbox.com/")));

    assertTrue(new Namespace("http://sandbox.com#").equals(new Namespace("http://sandbox.com")));
    assertTrue(new Namespace("http://sandbox.com#").equals(new Namespace("http://sandbox.com/")));
    assertTrue(new Namespace("http://sandbox.com#").equals(new Namespace("http://sandbox.com#")));
    assertTrue(new Namespace("http://sandbox.com#").equals(new Namespace("http://sandbox.com/#")));
    assertTrue(new Namespace("http://sandbox.com/").equals(new Namespace("http://sandbox.com")));
    assertTrue(new Namespace("http://sandbox.com/").equals(new Namespace("http://sandbox.com/")));
    assertTrue(new Namespace("http://sandbox.com/").equals(new Namespace("http://sandbox.com#")));
    assertTrue(new Namespace("http://sandbox.com/").equals(new Namespace("http://sandbox.com/#")));

    assertTrue(new Namespace("http://baab.nl/dingdong").equals(new Namespace("http://baab.nl/dingdong#")));
    assertTrue(new Namespace("http://baab.nl/dingdong").equals(new Namespace("http://baab.nl/dingdong#").toString()));
    assertTrue(new Namespace("http://baab.nl/dingdong").equals(new Namespace("http://baab.nl/dingdong#").toString()));
    assertTrue(new Namespace("http://baab.nl/dingdong").equals(new Namespace("http://baab.nl/dingdong#")));
    assertTrue(new Namespace("http://baab.nl/dingdong").equals("http://baab.nl/dingdong#"));

    assertEquals(new Namespace("http://baab.nl/dingdong/#babab"), new Namespace("http://baab.nl/dingdong/"));
  }
}