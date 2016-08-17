package nl.coinsweb.sdk.jena;

import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;
import nl.coinsweb.sdk.ModelFactory;
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
public class TDBModelFactory extends JenaModelFactory implements ModelFactory {

  private static final Logger log = LoggerFactory.getLogger(TDBModelFactory.class);

  private static final String DEFAULT_TEMP_FOLDER = "/tmp/";
  private static final String TDB_FOLDER = "coinstdb/";

  private Path path;







  public TDBModelFactory() {
    log.info("start new tdb for namespace given file");
    Path tempPath = Paths.get(DEFAULT_TEMP_FOLDER);
    Path path = tempPath.resolve(TDB_FOLDER + RandomStringUtils.random(8, true, true) + "/");
    path.toFile().mkdirs();
    this.path = path;
    dataset = TDBFactory.createDataset(path.toString());
  }






  @Override
  public void close() {

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
