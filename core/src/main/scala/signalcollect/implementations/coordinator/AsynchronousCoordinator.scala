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

package signalcollect.implementations.coordinator

import signalcollect.interfaces._
import signalcollect.api.Factory._
import signalcollect.api.Factory

class AsynchronousCoordinator(
  numberOfWorkers: Int,
  workerFactory: WorkerFactory,
  messageBusFactory: MessageBusFactory,
  storageFactory: StorageFactory,
  logger: Option[MessageRecipient[Any]],
  signalThreshold: Double,
  collectThreshold: Double,
  messageInboxFactory: QueueFactory = Factory.Queue.Default)
  extends AbstractCoordinator(
    numberOfWorkers,
    workerFactory,
    messageBusFactory,
    storageFactory,
    logger,
    signalThreshold,
    collectThreshold,
    messageInboxFactory) {

  override def performComputation: collection.mutable.Map[String, Any] = {
    executeComputationStep
    startComputation
    awaitStalledComputation
    collection.mutable.LinkedHashMap[String, Any]()
  }

}