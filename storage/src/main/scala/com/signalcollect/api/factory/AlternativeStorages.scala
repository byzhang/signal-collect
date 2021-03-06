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

package com.signalcollect.api

import com.signalcollect.implementations.storage._
import com.signalcollect.implementations.messaging._
import com.signalcollect.interfaces._

/**
 * Provides additional storage functionality as alternatives to the ones contained in signal/collect's Factory object
 */
package factory {

  package storage {

    //Mongo DB Storage (requires a running mongoDB installation)
    object MongoDB extends StorageFactory {
      class MongoDBStorage extends DefaultStorage with MongoDB
      def createInstance: Storage = new MongoDBStorage
    }

    //Mongo DB Storage that also stores all toSignal/toCollect lists on disk
    object AllOnDiskMongoDB extends StorageFactory {
      class MongoDBStorage extends DefaultStorage with MongoDB
      class AllOnDiskMongoDBStorage extends MongoDBStorage with MongoDBToDoList
      def createInstance: Storage = new AllOnDiskMongoDBStorage
    }

    //Orient DB Storage (can be run directly from jar, pure java)
    object OrientDB extends StorageFactory {
      class OrientDBStorage extends DefaultStorage with Orient
      def createInstance: Storage = new OrientDBStorage
    }
    
    //Orient DB Storage (can be run directly from jar, pure java)
    object SerializingInMemory extends StorageFactory {
      class SerializingInMemoryStorage extends DefaultStorage with Serializing
      def createInstance: Storage = new SerializingInMemoryStorage
    }
  }
}