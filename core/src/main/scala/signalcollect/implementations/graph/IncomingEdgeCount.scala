/*
 *  @author Philip Stutz
 *  
 *  Copyright 2010 University of Zurich
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

package signalcollect.implementations.graph

trait IncomingEdgeCount[IdType, StateType] extends AbstractVertex[IdType, StateType] {

  protected var _incomingEdgeCount = 0

  /** @return optionally the number of incoming edges of this {@link Vertex}. Modified by {@link FrameworkVertex} */
  override def incomingEdgeCount: Option[Int] = Some(_incomingEdgeCount)
  
  /**
   * Informs this vertex that there is a new incoming edge.
   * @param edgeId the id of the new incoming edge
   */
  override def addIncomingEdge(edgeId: (Any, Any, String)) {
	  _incomingEdgeCount += 1
  }

  /**
   * Informs this vertex that an incoming edge was removed.
   * @param edgeId the id of the incoming edge that was removed
   */
  override def removeIncomingEdge(edgeId: (Any, Any, String)): Option[Boolean] = {
	  _incomingEdgeCount -= 1
	  None
  }
 
}