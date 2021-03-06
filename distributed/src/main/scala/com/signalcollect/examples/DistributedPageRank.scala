/*
 *  @author Francisco de Freitas
 *  
 *  Copyright 2011 University of Zurich
 *      
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *         http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 */

package com.signalcollect.examples

import com.signalcollect._
import akka.actor.Actor._
import com.signalcollect.configuration.DistributedGraphBuilder

object DistributedPageRank {

  var cg: Graph = _

  def main(args: Array[String]) {

    val optionalCg = new DistributedGraphBuilder().withNumberOfMachines(2).withNumberOfWorkers(2).withExecutionConfiguration(ExecutionConfiguration(executionMode = SynchronousExecutionMode)).build

    if (optionalCg.isDefined) {
      cg = optionalCg.get

      cg.addVertex(new Page(1))
      cg.addVertex(new Page(2))
      cg.addVertex(new Page(3))
      cg.addEdge(new Link(1, 2))
      cg.addEdge(new Link(2, 1))
      cg.addEdge(new Link(2, 3))
      cg.addEdge(new Link(3, 2))
      val stats = cg.execute
      println(stats)
      cg.foreachVertex(println(_))
      cg.shutdown
    }
  }

}