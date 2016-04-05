package nl.coinsweb.sdk.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class TimeAsserts {

  protected static final Logger log = LoggerFactory.getLogger(TimeAsserts.class);

  private static long previous = 0;
  private static List<Long> samples = new ArrayList<>();

  public static void sample() {

    long now = System.currentTimeMillis();

    if(previous == 0) {
      previous = now;
      return;
    }

    long difference = now - previous;
    samples.add(new Long(difference));

    previous = now;
  }

  public static boolean neverLongerSec(int max) {

    for(Long sample : samples) {
      if(sample > (max * 1000)) {
        return false;
      }
    }

    return true;
  }

  public static void report() {
    log.info("Time report (in ms):");
    for(Long sample : samples) {
      log.info(sample.toString());
    }
  }
}
