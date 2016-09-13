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

import com.hp.hpl.jena.rdf.model.Model;

import java.io.File;
import java.util.List;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public interface CoinsContainer {


  /**
   * Get the containerId of this container.
   *
   * @return  a string used as identifier for this container
   */
  public String getContainerId();


  /**
   * Get filename.
   *
   * @return  a string containing the file name, or null when no saved yet
   */
  public String getFileName();


  /**
   * Import the model content and all registered attachments from a .ccr file specified by the target file path.
   *
   * This triggers a reinitialisation of the complete CoinsContainer object.
   *
   * @param target  a file path ('/tmp/container.ccr' or 'C:\tmp\container.ccr')
   *
   */
  public void load(String target);



  /**
   * Export the model content and all registered attachments to the .ccr file that was loaded.
   */
  public void export();


  /**
   * Export the model content and all registered attachments to a .ccr file specified by the target file path.
   *
   * @param target  a file path ('/tmp/container.ccr' or 'C:\tmp\container.ccr')
   */
  public void export(String target);

  /**
   * Export the instance data to a .ttl, .rdf or .owl file.
   *
   * @param target  a file path ('/tmp/instance_model.rdf' or 'C:\tmp\content.ttl')
   */
  public void exportModel(Model model, String target);

  /**
   * Return a list of library uri's of the available libraries.
   *
   * @return  a List of uri's ('http://www.coinsweb.nl/cbim-2.0.rdf#', 'http://example.com#')
   */
  public List<String> getLibraries();

  /**
   * Export the content of a library to a .ttl, .rdf or .owl file.
   *
   * @param libraryUri an uri representing a vocabulary ('http://www.coinsweb.nl/cbim-2.0.rdf', 'http://example.com')
   * @param filePath a file path ('/tmp/reference_model.rdf' or 'C:\tmp\otl.ttl')
   */
  public void exportLibrary(String libraryUri, String filePath);

  /**
   * List all attachments by file name.
   *
   * @return  list of file names ('map.pdf', 'pic.jpg')
   */
  public List<String> getAttachments();

  /**
   * Return a pointer to an attachment file.
   *
   * @param fileName  the name of the file ('map.pdf')
   * @return  a File pointing to the location where the attachment is temporarily stored ('/tmp/coinsreg/map.pdf')
   */
  public File getAttachment(String fileName);

  /**
   * Copy a file to the doc folder and register it as a candidate to create an Document (or related) instance.
   *
   * @param filePath  a file path ('/tmp/map.pdf' or 'C:\tmp\pic.jpg')
   */
  public void addAttachment(String filePath);


  /**
   * Return the associated CoinsModel object.
   *
   * @return  the CoinsModel related to this container
   */
  public CoinsModel getCoinsModel();

}
