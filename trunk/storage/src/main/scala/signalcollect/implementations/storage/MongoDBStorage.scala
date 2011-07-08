/*
 *  @author Daniel Strebel
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
 */

package signalcollect.implementations.storage

import com.mongodb.casbah.Imports._
import signalcollect.interfaces._
import signalcollect.api.DefaultVertex
import java.util.Set
import java.util.HashMap
import org.apache.log4j.helpers.LogLog
import signalcollect.implementations.serialization._

/**
 * Uses Mongo DB to Store the vertices and their according edges on disk
 * The lists of Vertices to collect/signal are kept in memory.
 */
class MongoDBStorage(storage: Storage) extends VertexStore with DefaultSerializer {
  LogLog.setQuietMode(true) //make Log4J remain quiet
  val messageBus = storage.getMessageBus

//  val randomID = getRandomString("sc-", 16) //To make sure that different workers operate on different MongoDB collections 
  val connectionID = "sc-mongo"
  var mongoStore = MongoConnection()("sc")(connectionID) //connect to localhost at port 27017 

  def put(vertex: Vertex[_, _]): Boolean = {
    val builder = MongoDBObject.newBuilder
    builder += "id" -> vertex.id.toString
    if (mongoStore.findOne(builder.result) != None) {
      false
    } else {
      vertex.setMessageBus(messageBus)
      builder += "obj" -> write(vertex)
      mongoStore += builder.result
      storage.toCollect.add(vertex.id)
      storage.toSignal.add(vertex.id)
      true
    }
  }

  def get(id: Any): Vertex[_, _] = {
    mongoStore.findOne(MongoDBObject("id" -> id.toString)) match {
      case Some(x) => val serialized = x.getAs[Array[Byte]]("obj"); val vertex = read(serialized.get).asInstanceOf[Vertex[_, _]]; vertex.setMessageBus(messageBus); vertex;
      case _ => null
    }
  }

  def remove(id: Any) = {
    mongoStore.remove(MongoDBObject("id" -> id.toString))
    storage.toCollect.remove(id)
    storage.toSignal.remove(id)
  }

  def updateStateOfVertex(vertex: Vertex[_, _]) = {
    val q = MongoDBObject("id" -> vertex.id.toString)
    val serialized = write(vertex)
    val updated = MongoDBObject("id" -> vertex.id.toString, "obj" -> serialized)
    mongoStore.update(q, updated)
  }

  def foreach[U](f: (Vertex[_, _]) => U) {
    mongoStore.foreach(dbobj => {
      val vertex = read(dbobj.getAs[Array[Byte]]("obj").get).asInstanceOf[Vertex[_, _]]
      f(vertex)
      updateStateOfVertex(vertex)
    })
  }

  def size = mongoStore.size
}

trait MongoDB extends DefaultStorage {
  override protected def vertexStoreFactory = new MongoDBStorage(this)
}