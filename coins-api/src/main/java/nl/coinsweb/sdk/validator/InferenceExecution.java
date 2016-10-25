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

  private int numRuns;
  private Map<String, Long> triplesAdded;

  private List<InferenceQueryResult> inferences = new ArrayList<>();
  public InferenceExecution() {
    this.numRuns = 0;
    this.triplesAdded = new HashMap<>();
  }


  public void setExecutionTime(long executionTime) {
    this.executionTime = executionTime;
  }
  public long getExecutionTime() {
    return executionTime;
  }


  public List<InferenceQueryResult> getQueryResults() {
    return inferences;
  }

  public int addNumRuns(int c ) {
    numRuns += c;
    return numRuns;
  }
  public int getNumRuns() {
    return numRuns;
  }

  public void addTriplesAdded(Map<String, Long> counts) {
    Iterator<String> graphNameIterator = counts.keySet().iterator();
    while(graphNameIterator.hasNext()) {
      String graphName = graphNameIterator.next();

      Long oldValue;
      if(triplesAdded.containsKey(graphName)) {
        oldValue = triplesAdded.get(graphName);
      } else {
        oldValue = 0l;
      }
      Long diff = counts.get(graphName);
      triplesAdded.put(graphName, oldValue + diff);
    }
  }
  public Map<String, Long> getTriplesAdded() {
    return triplesAdded;
  }
}