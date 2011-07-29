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

package com.signalcollect.implementations.messaging

import akka.actor.Actor
import com.signalcollect.interfaces.MessageRecipient
import com.signalcollect.interfaces.Manager
import com.signalcollect.interfaces.Manager._

class AkkaCoordinatorForwarder extends Actor {

  var coordinator: Any = _

  def receive = {

    case CoordinatorReference(x) =>
      coordinator = x

    case x => coordinator.asInstanceOf[MessageRecipient[Any]].receive(x)

  }

}