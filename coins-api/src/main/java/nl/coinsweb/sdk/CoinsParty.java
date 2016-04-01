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


import nl.coinsweb.sdk.exceptions.CoinsObjectCastNotAllowedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class CoinsParty {

  private static final Logger log = LoggerFactory.getLogger(CoinsParty.class);

  private ExpertCoinsModel model;
  private String uri;


  public CoinsParty(String uri) {
    this.uri = uri;
  }

  public String getUri() {
    return uri;
  }

  public void setModel(ExpertCoinsModel model) {
    this.model = model;
  }

  public <T extends CoinsObject> T as(Class<T> clazz) {
    if(model == null) {
      throw new CoinsObjectCastNotAllowedException("CoinsParty can only be cast if it is used to open a CoinsContainer class.");
    }
    log.info("try to cast a CoinsParty to "+clazz.getCanonicalName() + " with uri "+uri);
    try {
      Constructor constructor = clazz.getConstructor(ExpertCoinsModel.class, String.class);
      T constructed = (T) constructor.newInstance(model, uri);
      return constructed;
    } catch (NoSuchMethodException e) {
    } catch (InvocationTargetException e) {
    } catch (InstantiationException e) {
    } catch (IllegalAccessException e) {
    }

    throw new CoinsObjectCastNotAllowedException("Could not cast CoinsParty to "+clazz.getCanonicalName()+".");
  }
}
