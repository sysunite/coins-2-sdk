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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents an instance of an owl:Class.
 * All generated classes extend this base class. It has no dependency to Jena.
 *
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class RuntimeCoinsObject extends AbstractCoinsObject {

  protected static final Logger log = LoggerFactory.getLogger(RuntimeCoinsObject.class);



  private String classUri = null;
  private String label = null;
  private String comment = null;
  private String[] sourceFiles = {};





  /**
   * Constructor for new ...
   */
  public RuntimeCoinsObject(BindingCoinsModel model, String classUri) {

    this.model = model;
    this.classUri = classUri;






    // Create fields for this new instance
    this.uri = model.generateUri();


    // Save this new instance to model
    model.addType(getUri(), getClassUri());
    model.addCreator(getUri(), model.getActiveParty());
    model.addCreatedNow(getUri());
  }

  /**
   * Constructor for existing ...
   */
  public RuntimeCoinsObject(ExpertCoinsModel model, String classUri, String uri) {
    this.model = model;
    this.classUri = classUri;
    this.uri = uri;
  }






  @Override
  public String getClassUri() {
    return classUri;
  }

  @Override
  public String getClassLabel() {
    return label;
  }

  @Override
  public String getClassComment() {
    return comment;
  }
}