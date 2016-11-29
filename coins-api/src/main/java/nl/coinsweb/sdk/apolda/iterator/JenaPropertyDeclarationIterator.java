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
package nl.coinsweb.sdk.apolda.iterator;

import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ClosableIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import nl.coinsweb.sdk.apolda.language.Language;
import nl.coinsweb.sdk.apolda.ontology.PropertyDeclaration;
import nl.coinsweb.sdk.apolda.ontology.impl.PropertyDeclarationImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class JenaPropertyDeclarationIterator implements ClosableIterator {

  private static final Logger log = LoggerFactory.getLogger(JenaPropertyDeclarationIterator.class);


  OntModel ontModel;
  OntClass ontClass;
  String rangeSuperUri;

  ArrayList<PropertyDeclaration> result = new ArrayList<>();
  Iterator<PropertyDeclaration> resultIterator;


  public JenaPropertyDeclarationIterator(String classUri, OntModel model) {
    this(classUri, model, null);
  }
  public JenaPropertyDeclarationIterator(String classUri, OntModel model, String rangeSuper) {

    this.ontClass = model.getOntClass(classUri);
    this.rangeSuperUri = rangeSuper;
    this.ontModel = model;



    // Iterate property declarations

    ExtendedIterator<OntProperty> properties = ontClass.listDeclaredProperties(false);

    while(properties.hasNext()) {

      OntProperty property = properties.next();

      PropertyDeclaration declaration = new PropertyDeclarationImpl();


      declaration.setPropertyUri(property.getURI());
      declaration.setPropertyName(property.getLocalName());

      OntResource domain = property.getDomain();
      if (domain != null) {
        declaration.setCardinality(property.getDomain().getCardinality(property.asProperty()));
      }

      String label = Language.getLabel(property);
      if (label != null) {
        declaration.setPropertyLabel(label);
      }

      declaration.setOwlFunctionalProperty(property.isFunctionalProperty());


      ExtendedIterator<? extends OntResource> ranges = property.listRange();
      while (ranges.hasNext()) {

        OntResource range = ranges.next();

        if (rangeSuperUri != null) {
          OntClass rangeSuperClass = ontModel.getOntClass(rangeSuperUri);

          if (rangeSuperClass != null) {
            if (!range.hasRDFType(rangeSuperClass)) {
//              continue;
            }
          }
        }

        declaration.addRange(range.getURI(), range.getLocalName());
      }
      result.add(declaration);
    }





    // Iterate restrictions

    ExtendedIterator<OntClass> classes = ontClass.listSuperClasses(false);
    while (classes.hasNext()) {

      OntClass oc = classes.next();
      if (oc.isRestriction()) {

        PropertyDeclaration declaration = new PropertyDeclarationImpl();


        Restriction restriction = oc.asRestriction();

        OntProperty ontProperty = restriction.getOnProperty();
        if (ontProperty == null || !ontProperty.isResource()) {
          continue;
        }
        declaration.setPropertyUri(ontProperty.getURI());
        declaration.setPropertyName(ontProperty.getLocalName());


        if (restriction.isCardinalityRestriction()) {
          declaration.setCardinality(restriction.asCardinalityRestriction().getCardinality());
        }

        if (restriction.isAllValuesFromRestriction()) {
          Resource allValuesFrom = restriction.asAllValuesFromRestriction().getAllValuesFrom();

          if (allValuesFrom.isResource()) {
            declaration.addRange(allValuesFrom.getURI(), allValuesFrom.getLocalName());

          }

        }

        result.add(declaration);
      }
    }

    this.resultIterator = result.iterator();

  }




  @Override
  public void close() {
  }

  @Override
  public boolean hasNext() {
    return resultIterator.hasNext();
  }

  @Override
  public PropertyDeclaration next() {
    return resultIterator.next();
  }

  @Override
  public void remove() {
    resultIterator.remove();
  }
}
