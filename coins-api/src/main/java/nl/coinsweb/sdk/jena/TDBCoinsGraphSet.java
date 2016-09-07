package nl.coinsweb.sdk.jena;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;
import nl.coinsweb.sdk.CoinsGraphSet;
import org.apache.commons.lang3.RandomStringUtils;
import org.codehaus.plexus.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Bastiaan Bijl
 */
public class TDBCoinsGraphSet extends JenaCoinsGraphSet implements CoinsGraphSet {

  private static final Logger log = LoggerFactory.getLogger(TDBCoinsGraphSet.class);

  private static final String DEFAULT_TEMP_FOLDER = "/tmp/";
  private static final String TDB_FOLDER = "coinstdb/";

  private Path path;







  public TDBCoinsGraphSet(String namespace) {
    super(namespace);
    log.info("start new tdb for namespace given file");
    Path tempPath = Paths.get(DEFAULT_TEMP_FOLDER);
    Path path = tempPath.resolve(TDB_FOLDER + RandomStringUtils.random(8, true, true) + "/");
    path.toFile().mkdirs();
    this.path = path;
  }

  @Override
  public Dataset getEmptyDataset() {
    return TDBFactory.createDataset(path.toString());
  }







  public void close(Dataset dataset) {

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
