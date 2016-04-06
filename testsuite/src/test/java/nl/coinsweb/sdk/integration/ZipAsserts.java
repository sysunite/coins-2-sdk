package nl.coinsweb.sdk.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class ZipAsserts {

  protected static final Logger log = LoggerFactory.getLogger(ZipAsserts.class);

  public static boolean containsFiles(File zipFile, Set<String> requiredFiles, boolean allowOtherFiles) {

    Set<String> packingList = new HashSet<>(requiredFiles);

    try {

      ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
      ZipEntry ze = zis.getNextEntry();

      while (ze != null) {

        String fileName = ze.getName();

        if (ze.isDirectory()) {

          // Check if this folderName was contained in the requiredFiles list
          if(requiredFiles.contains(fileName)) {
            packingList.remove(fileName);
          }
          ze = zis.getNextEntry();
          continue;
        }

        // Check if this fileName was contained in the requiredFiles list
        if(requiredFiles.contains(fileName)) {
          packingList.remove(fileName);
        } else if(!allowOtherFiles) {
          log.error("File should not be there: "+fileName);
          return false;
        }

        ze = zis.getNextEntry();
      }

      zis.closeEntry();
      zis.close();

    } catch (IOException e) {
      log.error("Problem reading zip file "+zipFile.getAbsolutePath(), e);
      return false;
    }

    // If all the files from the requiredFiles where in the zip archive, this list is now supposed to be empty
    Iterator<String> iterator = packingList.iterator();
    while(iterator.hasNext()) {
      log.error("File not contained in zip: "+iterator.next());
    }
    return packingList.isEmpty();
  }
}
