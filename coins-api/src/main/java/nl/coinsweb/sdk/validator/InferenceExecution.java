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
package nl.coinsweb.sdk.validator;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Bastiaan Bijl, Sysunite 2016
 */
public class InferenceExecution {

  private static final Logger log = LoggerFactory.getLogger(InferenceExecution.class);

  private long executionTime = -1l;

  // Keep a map of run.nr -> query -> graph -> count
  private Map<String, Map<String, Map<String, Long>>> triplesAdded = new HashMap();


//  private List<InferenceQueryResult> inferences = new ArrayList<>();

  public InferenceExecution() {
    this.triplesAdded = new HashMap<>();
  }


  public void setExecutionTime(long executionTime) {
    this.executionTime = executionTime;
  }
  public long getExecutionTime() {
    return executionTime;
  }


//  public List<InferenceQueryResult> getQueryResults() {
//    return inferences;
//  }

  public int getNumRuns() {
    int max = 0;
    for(String runNr : triplesAdded.keySet()) {
      max = Math.max(max, Integer.parseInt(runNr));
    }
    return max;
  }

  // Set per run per query
  public void addTriplesAdded(String runNr, String queryRef, Map<String, Long> counts) {

    if(!triplesAdded.containsKey(runNr)) {
      triplesAdded.put(runNr, new HashMap());
    }
    Map<String, Map<String, Long>> queryCounts = triplesAdded.get(runNr);

    if(!queryCounts.containsKey(queryRef)) {
      queryCounts.put(queryRef, counts);
    } else {
      log.warn("Trying to add the results for this query again for the same run.");
    }
  }
  public Map<String, Map<String, Map<String, Long>>> getTriplesAddedDetailed() {
    return triplesAdded;
  }
  public Map<String, Long> getTriplesAdded() {
    Map<String, Long> result = new HashMap();
    for(String runNr : triplesAdded.keySet()) {
      for(String queryRef : triplesAdded.get(runNr).keySet()) {
        for(String graphName : triplesAdded.get(runNr).get(queryRef).keySet()) {
          if(result.containsKey(graphName)) {
            result.put(graphName, triplesAdded.get(runNr).get(queryRef).get(graphName) + result.get(graphName));
          } else {
            result.put(graphName, triplesAdded.get(runNr).get(queryRef).get(graphName));
          }
        }
      }
    }
    return result;
  }
  public Map<String, Long> getTriplesAdded(String runNr) {
    Map<String, Long> result = new HashMap();
    if(!triplesAdded.containsKey(runNr)) {
      log.warn("RunNr not found: "+runNr);
      return result;
    }
    for(String queryRef : triplesAdded.get(runNr).keySet()) {
      for(String graphName : triplesAdded.get(runNr).get(queryRef).keySet()) {
        if(result.containsKey(graphName)) {
          result.put(graphName, triplesAdded.get(runNr).get(queryRef).get(graphName) + result.get(graphName));
        } else {
          result.put(graphName, triplesAdded.get(runNr).get(queryRef).get(graphName));
        }
      }
    }
    return result;
  }
}