package nl.coinsweb.sdk.integration;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class IntegrationHelper {

  public static File getResourceFile(String tag, String fileName) {

    String relativePath = "" + tag + "/" + fileName;
    URI fullPath = null;
    try {
      URL fullPathUrl = IntegrationHelper.class.getClassLoader().getResource(relativePath);
      if(fullPathUrl != null) {
        fullPath = new URI(fullPathUrl.toString());
      }
    } catch (URISyntaxException e) {
      // fullPath remains null
    }

    if(fullPath == null) {
      throw new RuntimeException("File \""+fileName+"\" not found in resource folder \""+tag+"\" of integration test suite.");
    }

    return new File(fullPath);
  }

}
