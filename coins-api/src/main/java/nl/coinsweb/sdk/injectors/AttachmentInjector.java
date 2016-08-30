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
package nl.coinsweb.sdk.injectors;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import nl.coinsweb.sdk.CoinsModel;
import nl.coinsweb.sdk.exceptions.AttachmentNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class AttachmentInjector implements Injector {

  protected static final Logger log = LoggerFactory.getLogger(AttachmentInjector.class);

  @Override
  public boolean proposeRead(CoinsModel model, String subject) {
    return true;
  }

  @Override
  public boolean proposeRead(CoinsModel model, String subject, String predicate) {
    return true;
  }

  @Override
  public boolean proposeWrite(CoinsModel model, String subject, String predicate, String object) {

    // When proposing a remove, always allow
    if(object == null) {
      return true;
    }

    // Allow anything other than filePath relationship
    if(!"http://www.coinsweb.nl/cbim-2.0.rdf#filePath".equals(predicate)) {
      return true;
    } else {

      ExtendedIterator<Triple> iterator = ((OntModel) model.getCoinsGraphSet().getInstanceOntModel()).getGraph().find(
          new ResourceImpl(object).asNode(),
          new PropertyImpl("http://www.coinsweb.nl/cbim-2.0.rdf#datatypeValue").asNode(),
          Node.ANY);

      if(iterator.hasNext()) {

        Triple triple = iterator.next();
        Node filePath = triple.getObject();
        if (filePath.isLiteral()) {

          String filePathString = filePath.getLiteral().getLexicalForm();


          if(!model.getCoinsContainer().getAttachments().contains(filePathString)) {
            throw new AttachmentNotFoundException("Adding file " + filePathString + " not found as Attachment, not connecting (instance Model now contains an orphaned String property).");
          }


          log.info("Request adding file "+filePathString + ", found!");
          return true;

        }
      }

      throw new AttachmentNotFoundException("Not clear which filename is being added.");
    }
  }
}
