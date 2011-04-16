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

package signalcollect.algorithms

import signalcollect.api._

/**
 * Signal/Collect implementation of finding Hamiltonian paths in graphs.
 */
object Hamiltonian {

	def main(args : Array[String]) : Unit = {

	val cg = new AsynchronousComputeGraph()

	/**
	 * Still need to test performance on complete and larger graphs
	 */

	cg.addVertex[MyVertex]( "a", Map( List("a") -> 0 ) )
	cg.addVertex[MyVertex]( "b", Map( List("b") -> 0 ) )
	cg.addVertex[MyVertex]( "c", Map( List("c") -> 0 ) )
	cg.addVertex[MyVertex]( "d", Map( List("d") -> 0 ) )
	cg.addVertex[MyVertex]( "e", Map( List("e") -> 0 ) )

	cg.addEdge[MyEdge]( "a", "d", 3 ); cg.addEdge[MyEdge]( "d", "a", 3 )
	cg.addEdge[MyEdge]( "a", "b", 1 ); cg.addEdge[MyEdge]( "b", "a", 1 )
	cg.addEdge[MyEdge]( "d", "b", 2 ); cg.addEdge[MyEdge]( "b", "d", 2 )
	cg.addEdge[MyEdge]( "d", "c", 1 ); cg.addEdge[MyEdge]( "c", "d", 1 )
	cg.addEdge[MyEdge]( "b", "c", 1 ); cg.addEdge[MyEdge]( "c", "b", 1 )

	// a problem with isolated vertices is that it is not able to find hamiltonian paths depending on the starting vertex
	cg.addEdge[MyEdge]( "e", "a", 1 ); cg.addEdge[MyEdge]( "a", "e", 1 )

	val stats = cg.execute
	println(stats)
	cg.foreach { x => println(x) }
	cg.shutDown

	}

}

/**
 * The state of a vertex is all the paths currently collected from the graph
 * Each path will be kept such that there will be no "revisiting" of vertices (each path will not have a repeated vertex id)
 * Implementation is rather inefficient since it keeps a map where the value is the weights sum and keys as lists
 */
class MyVertex ( id : String, initialState : Map[ List[String], Int ] ) extends SignalMapVertex ( id, initialState ) {
	
	type UpperSignalTypeBound = Map[ List[String], Int ]
	
	type StateType = Map[ List[String], Int ]
	
	/*
	 * The state will contain all paths visited so far, not mattering the size of the path
	 */
	def collect : Map[ List[String], Int ] = {
		
		val signalsMap = mostRecentSignalMap toMap
		
		// so that I can get the maps
		val signals = (signalsMap keySet) map { x => signalsMap.get(x).get }
		
		// consolidate the maps into one map
		val pathMap = signals reduceLeft (_ ++ _)
		
		// add signal maps to state as a one map
		state = List(pathMap, state) reduceLeft (_ ++ _)
		
		state
		
	}
	
	/*
	 * Prints the shortest Hamiltonian path from vertex such as if the vertex were the initial one
	 */
	override def toString = {
		
		val max = (state keySet).foldLeft(0)((i,s) => i max s.length) 
		
		val longests = ((state filter { x => x._1.length == max }))
		
		var min = Int.MaxValue
		var key = List("")
		
		for ( k <- longests keySet )
			if ( longests.get(k).get < min ) {
				min = longests.get(k).get
				key = k
			}
		
		"Id: " + id + " | Path: [" + key.mkString("->") + "]=" + min
		
	}
	
}
/**
 * The edge implementation of the signal function will signal to all its connected vertexes the 
 * current collected paths (ignoring those paths that contain the target vertex) by the source 
 * vertex in order to determine the hamiltonian paths.
 * 
 * @param w the initial weight of the vertex
 */
class MyEdge(s: Any, t: Any, w: Int) extends OnlySignalOnChangeEdge(s, t) {

	override def weight : Double = w
	
  type SourceVertexType = MyVertex

  def signal: Map[List[String], Int] = {
  	
		// signals only paths that do not contain the target vertex id
  	((source.state keySet) filterNot { x => x contains(targetId) }).map { 
  		k => Pair (k.::(targetId.toString), source.state.get(k).get + weight.toInt) 
  	} toMap
  	
  }
   
}