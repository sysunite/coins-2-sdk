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
package nl.coinsweb.sdk.jena;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;
import nl.coinsweb.sdk.CoinsParty;
import nl.coinsweb.sdk.ExpertCoinsModel;
import org.apache.commons.lang3.RandomStringUtils;
import org.codehaus.plexus.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class TDBCoinsContainer extends JenaCoinsContainer {

  private static final Logger log = LoggerFactory.getLogger(TDBCoinsContainer.class);

  private static final String DEFAULT_TEMP_FOLDER = "/tmp/";
  private static final String TDB_FOLDER = "coinstdb/";

  private Path path;

  public TDBCoinsContainer(String namespace) {
    super(namespace);
  }
  public TDBCoinsContainer(CoinsParty party, String namespace) {
    super(party, namespace);
  }
  public TDBCoinsContainer(String namespace, boolean loadCoreModels) {
    super(namespace, loadCoreModels);
  }
  public TDBCoinsContainer(CoinsParty party, String namespace, boolean loadCoreModels) {
    super(party, namespace, loadCoreModels);
  }

  public TDBCoinsContainer(String filePath, String namespace) {
    super(filePath, namespace);
  }
  public TDBCoinsContainer(CoinsParty party, String filePath, String namespace) {
    super(party, filePath, namespace);
  }


  @Override
  protected void doModelPreparation() {
    log.info("start new tdb for namespace given file");
    Path tempPath = Paths.get(DEFAULT_TEMP_FOLDER);
    Path path = tempPath.resolve(TDB_FOLDER + RandomStringUtils.random(8, true, true) + "/");
    path.toFile().mkdirs();
    this.path = path;
  }



  @Override
  protected Dataset createDataset() {
    log.info("create new tdb dataset");
    return TDBFactory.createDataset(path.toString());
  }



  @Override
  public void close() {
    log.info("distribute close");
    for (ExpertCoinsModel model : coinsModels.values()) {
      log.info("send close to " + model.getInstanceNamespace());
      model.close();
    }
    TDB.sync(dataset);
    dataset.close();
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      log.error(e.getMessage(), e);
    }

    // destroy
    try {
      FileUtils.deleteDirectory(path.toFile());
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }
}