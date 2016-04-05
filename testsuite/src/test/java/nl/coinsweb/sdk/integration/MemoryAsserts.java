package nl.coinsweb.sdk.integration;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class MemoryAsserts {

  private static Set<Double> samplesFree = new HashSet<>();
  private static Set<Double> samplesTotal = new HashSet<>();

  public static void sample() {

    System.gc();

    samplesFree.add(new Double(Runtime.getRuntime().freeMemory()));
    samplesTotal.add(new Double(Runtime.getRuntime().totalMemory()));
  }

  public static boolean neverUnderMbFree(int min) {

    for(Double sample : samplesFree) {
      if(sample < (min * 1024 * 1024)) {
        return false;
      }
    }

    return true;
  }

  public static boolean neverOverMbUsed(int max) {

    for(Double sample : samplesTotal) {
      if(sample > (max * 1024 * 1024)) {
        return false;
      }
    }

    return true;
  }
}
