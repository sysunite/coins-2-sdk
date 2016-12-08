/**
 * MIT License
 *
 * Copyright (c) 2016 Bouw Informatie Raad
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 *
 **/
package nl.coinsweb.sdk;

import nl.coinsweb.sdk.exceptions.InvalidNamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class Namespace {

  protected static final Logger log = LoggerFactory.getLogger(Namespace.class);

  private enum Type {
    HASH, SLASH, SLASHHASH
  }
  private Type type = Type.HASH;
  private String url;

  public Namespace(String input) {

    if(input == null || input.isEmpty()) {
      throw new InvalidNamespaceException("Tried to create namespace form empty string.");
    }


    try {
      URL interpretedUrl = new URL(input);
      if(interpretedUrl.getHost().isEmpty()) {
        throw new InvalidNamespaceException("Essential host part of uri is missing.");
      }

      input = interpretedUrl.getProtocol() + "://" + interpretedUrl.getHost() + interpretedUrl.getPath();

    } catch (MalformedURLException e) {
      throw new InvalidNamespaceException("MalformedURLException while parsing uri: "+input+".");
    }






    if(input.endsWith("/#")) {
      type = Type.SLASHHASH;
      url = input.substring(0, input.length() - 2);
    } else if(input.endsWith("#")) {
      type = Type.HASH;
      url = input.substring(0, input.length()-1);
    } else if(input.endsWith("/")) {
      type = Type.SLASH;
      url = input.substring(0, input.length()-1);
    } else {
      url = input;
    }
  }

  @Override
  public String toString() {
    if(type == Type.SLASHHASH) {
      return url + "/#";
    } else if(type == Type.SLASH) {
      return url + "/";
    } else {
      return url + "#";
    }
  }

  public String withoutHash() {
    if(type == Type.SLASH || type == Type.SLASHHASH) {
      return url + "/";
    } else {
      return url;
    }
  }

  public String getBase() {
    return url;
  }

  public URI toURI() {
    try {
      return new URI(toString());
    } catch (URISyntaxException e) {
      log.error(e.getMessage(), e);
    }
    return null;
  }

  @Override
  public int hashCode() {
    return getBase().hashCode();
  }

  @Override
  public boolean equals(Object object) {

    Namespace normalizedInput = null;
    if(object instanceof Namespace) {
      normalizedInput = (Namespace)object;
    }
    if(object instanceof String) {
      try {
        normalizedInput = new Namespace((String) object);
      } catch(InvalidNamespaceException e) {
        return false;
      }
    }
    if(normalizedInput == null) {
      return false;
    }

    return normalizedInput.getBase().equals(getBase());
  }



}
