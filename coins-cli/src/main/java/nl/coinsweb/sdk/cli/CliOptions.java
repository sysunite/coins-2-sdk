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
package nl.coinsweb.sdk.cli;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class CliOptions {

  protected static final Logger log = LoggerFactory.getLogger(CliOptions.class);







  private String modeString = null;



  /** Print usage message and abort */
  protected void usage() {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(
        "\n coins-cli unzip" +
        "\n coins-cli viewer" +
        "\n coins-cli map" +
        "\n coins-cli generate" +
        "\n coins-cli validate", new Options() );

  }





  public boolean viewerMode() {
    return "viewer".equals(modeString);
  }
  public boolean unzipMode() {
    return "unzip".equals(modeString);
  }
  public boolean mapMode() {
    return "map".equals(modeString);
  }
  public boolean generateMode() {
    return "generate".equals(modeString);
  }
  public boolean validateMode() {
    return "validate".equals(modeString);
  }

  // Constructor
  public CliOptions(String[] args) {

    if(args.length > 0) {
      modeString = args[0];
    }
  }














  public static Path resolvePath(String path) {

    try {
      String userDir = System.getProperty("user.dir");
      log.info("Resolving path "+path.toString()+" to user dir: "+userDir);
      Path currentPath = Paths.get(new File(userDir).getCanonicalPath());
      return currentPath.resolve(path);
    } catch (IOException e) {
      log.warn("Failed to locate path "+path+" relative to user dir "+System.getProperty("user.dir"));
      return null;
    }
  }
  public static List<String> breakSemicolonSeparated(String path) {
    return Arrays.asList(path.split(";"));
  }

  public static List<Path> resolvePaths(List<String> paths) {
    List<Path> result = new ArrayList<>();
    try {
      for(String path : paths) {
        result.add(Paths.get(new File(".").getCanonicalPath()).resolve(path));
      }
    } catch (IOException e) {
    }
    return result;
  }


}