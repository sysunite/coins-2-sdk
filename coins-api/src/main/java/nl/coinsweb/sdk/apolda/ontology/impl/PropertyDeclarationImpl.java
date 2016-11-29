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
package nl.coinsweb.sdk.apolda.ontology.impl;

import nl.coinsweb.sdk.apolda.ontology.PropertyDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class PropertyDeclarationImpl implements PropertyDeclaration {

  private static final Logger log = LoggerFactory.getLogger(PropertyDeclaration.class);

  private String propertyUri;
  private String propertyName;
  private String propertyLabel;
  private String propertyOwnerUri;
  private ArrayList<Range> rangeList = new ArrayList<>();
  private int cardinality = -1;
  private boolean owlFunctionalProperty = false;

  public PropertyDeclarationImpl() {

  }

  @Override
  public void setPropertyLabel(String propertyLabel) {
    if(this.propertyLabel!=null) {
      log.warn("Overriding "+this.propertyLabel+" with "+propertyLabel);
    }
    this.propertyLabel = propertyLabel;
  }

  @Override
  public String getPropertyLabel() {
    return propertyLabel;
  }

  @Override
  public void setPropertyUri(String propertyUri) {
    if(this.propertyUri!=null) {
      log.warn("Overriding "+this.propertyUri+" with "+propertyUri);
    }
    this.propertyUri = propertyUri;
  }

  @Override
  public String getPropertyUri() {
    return propertyUri;
  }

  @Override
  public void setPropertyName(String propertyName) {
    if(this.propertyName!=null) {
      log.warn("Overriding "+this.propertyName+" with "+propertyName);
    }
    this.propertyName = propertyName;
  }

  @Override
  public String getPropertyName() {
    return propertyName;
  }

  @Override
  public void setPropertyOwner(String propertyOwnerUri) {
    if(this.propertyOwnerUri!=null) {
      log.warn("Overriding "+this.propertyOwnerUri+" with "+propertyOwnerUri);
    }
    this.propertyOwnerUri = propertyOwnerUri;
  }
  @Override
  public String getPropertyOwner() {
    return propertyOwnerUri;
  }

  @Override
  public void addRange(String rangeUri, String rangeName) {
    rangeList.add(new Range(rangeUri, rangeName));
  }

  @Override
  public boolean hasRange() {
    return !rangeList.isEmpty();
  }

  @Override
  public List<Range> getRanges() {
    return rangeList;
  }


  @Override
  public void setCardinality(int cardinality) {
    if(this.cardinality!=-1) {
      log.warn("Overriding "+this.cardinality+" with "+cardinality);
    }
    this.cardinality = cardinality;
  }

  @Override
  public int getCardinality() {
    return cardinality;
  }

  @Override
  public void setOwlFunctionalProperty(boolean value) {
    this.owlFunctionalProperty = value;
  }

  @Override
  public boolean getOwlFunctionalProperty() {
    return owlFunctionalProperty;
  }

  @Override
  public boolean isSingle() {
    if(cardinality == -1) {
      return owlFunctionalProperty;
    }
    return owlFunctionalProperty || cardinality < 2;
  }





  @Override
  public int hashCode() {
    if(hasRange()) {
      return getPropertyUri().hashCode() + getRanges().hashCode();
    }
    return getPropertyUri().hashCode();
  }

  @Override
  public boolean equals(Object object) {

    PropertyDeclarationImpl propertyDeclaration = null;
    if(object instanceof PropertyDeclarationImpl) {
      propertyDeclaration = (PropertyDeclarationImpl)object;

      if(propertyDeclaration.hasRange() && hasRange()) {
        return propertyDeclaration.getPropertyUri().equals(getPropertyUri()) &&
            propertyDeclaration.getRanges().size() == getRanges().size(); // todo: compare the content of these sets
      }
      return propertyDeclaration.getPropertyUri().equals(getPropertyUri());
    }
    return false;
  }

  public class Range {
    private String uri;
    private String name;
    public Range(String uri, String name) {
      this.uri = uri;
      this.name = name;
    }
    public String getUri() {
      return uri;
    }
    public String getName() {
      return name;
    }
  }
}
