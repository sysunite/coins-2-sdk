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

import org.apache.jena.riot.system.InitRIOT;
import org.apache.jena.sparql.system.InitARQ;
import org.apache.jena.system.InitJenaCore;
import org.apache.jena.system.JenaSubsystemRegistry;
import org.apache.jena.system.JenaSubsystemRegistryBasic;
import org.apache.jena.tdb.sys.InitTDB;

import java.util.ServiceLoader;

/** Implementation of {@link JenaSubsystemRegistry} for use in the simple 
 *  but common case of running Jena as a collection of jars
 *  on the classpath. 
 *  <p>
 *  Uses {@link ServiceLoader} to find sub-systems. 
 */
public class JenaSubsystemRegistryHard extends JenaSubsystemRegistryBasic implements JenaSubsystemRegistry {

  private Object registryLock = new Object() ;



  @Override
  public void load() {
    synchronized (registryLock) {
      System.out.println("do it the hard way");
      this.add(new InitTDB());
      this.add(new InitRIOT());
      this.add(new InitARQ());
      this.add(new InitJenaCore());
    }
  }
}