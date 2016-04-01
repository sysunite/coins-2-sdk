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

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class PropertyDeclarationImpl implements PropertyDeclaration {

  private static final Logger log = LoggerFactory.getLogger(PropertyDeclaration.class);

  private String propertyUri;
  private String propertyName;
  private String propertyLabel;
  private String propertyOwnerUri;
  private String rangeUri;
  private String rangeName;
  private int cardinality = -1;
  private boolean owlFunctionalProperty = false;

  public PropertyDeclarationImpl() {

  }

  @Override
  public void setPropertyLabel(String propertyLabel) {
    if(this.propertyLabel!=null) {
      log.warn("overriding "+this.propertyLabel+" with "+propertyLabel);
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
      log.warn("overriding "+this.propertyUri+" with "+propertyUri);
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
      log.warn("overriding "+this.propertyName+" with "+propertyName);
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
      log.warn("overriding "+this.propertyOwnerUri+" with "+propertyOwnerUri);
    }
    this.propertyOwnerUri = propertyOwnerUri;
  }
  @Override
  public String getPropertyOwner() {
    return propertyOwnerUri;
  }

  @Override
  public void setRangeUri(String rangeUri) {
    if(this.rangeUri!=null) {
      log.warn("overriding "+this.rangeUri+" with "+rangeUri);
    }
    this.rangeUri = rangeUri;
  }

  @Override
  public boolean hasRange() {
    return rangeUri != null;
  }
  @Override
  public String getRangeUri() {
    if(!hasRange()) {
      return null;
    }
    return rangeUri;
  }
  @Override
  public void setRangeName(String rangeName) {
    if(this.rangeName!=null) {
      log.warn("overriding "+this.rangeName+" with "+rangeName);
    }
    this.rangeName = rangeName;
  }
  @Override
  public String getRangeName() {
    return rangeName;
  }

  @Override
  public void setCardinality(int cardinality) {
    if(this.cardinality!=-1) {
      log.warn("overriding "+this.cardinality+" with "+cardinality);
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
      return getPropertyUri().hashCode() + getRangeUri().hashCode();
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
            propertyDeclaration.getRangeUri().equals(getRangeUri());
      }
      return propertyDeclaration.getPropertyUri().equals(getPropertyUri());
    }
    return false;
  }
}
