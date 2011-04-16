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

package signalcollect.interfaces

import signalcollect.implementations.worker.DirectDeliverySynchronousWorker
import signalcollect.implementations.worker.AsynchronousPriorityWorker
import signalcollect.implementations.worker.DirectDeliveryAsynchronousWorker
import signalcollect.implementations.worker.AsynchronousWorker
import signalcollect.implementations.worker.SynchronousWorker
import signalcollect.interfaces.Queue._

object Worker {
	type WorkerFactory = (MessageBus[Any, Any], QueueFactory) => Worker
	
	lazy val defaultFactory = asynchronousDirectDeliveryWorkerFactory
	
	def createSynchronousWorker(mb: MessageBus[Any, Any], qf: QueueFactory) = new SynchronousWorker(mb, qf)
	lazy val synchronousWorkerFactory = createSynchronousWorker _
	def createSynchronousDirectDeliveryWorker(mb: MessageBus[Any, Any], qf: QueueFactory) = new DirectDeliverySynchronousWorker(mb, qf)
	lazy val synchronousDirectDeliveryWorkerFactory = createSynchronousDirectDeliveryWorker _
	def createAsynchronousWorker(mb: MessageBus[Any, Any], qf: QueueFactory) = new AsynchronousWorker(mb, qf)
	lazy val asynchronousWorkerFactory = createAsynchronousWorker _
	def createAsynchronousDirectDeliveryWorker(mb: MessageBus[Any, Any], qf: QueueFactory) = new DirectDeliveryAsynchronousWorker(mb, qf)
	lazy val asynchronousDirectDeliveryWorkerFactory = createAsynchronousDirectDeliveryWorker _
	def createAsynchronousPriorityWorker(mb: MessageBus[Any, Any], qf: QueueFactory) = new AsynchronousPriorityWorker(mb, qf)
	lazy val asynchronousPriorityWorkerFactory = createAsynchronousPriorityWorker _
}

trait Worker extends MessageRecipient[Any] with Runnable