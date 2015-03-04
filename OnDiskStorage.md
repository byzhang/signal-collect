# On-Disk Storage #

When using the default [InMemoryStorage](http://code.google.com/p/signal-collect/source/browse/trunk/core/src/main/scala/com/signalcollect/implementations/storage/InMemoryStorage.scala) implementation all the vertices and their outgoing edges are stored in memory. For smaller applications or in a distributed scenario this will most likely suffice. For architectures where a large graph needs to be processed on one single machine, the footprint in memory of the graph might easily exceed the available resources on that machine. To overcome this limitation Signal/Collect allows to store parts or the entire graph in a database on disk. Storing the graph elements such as vertices and edges on disk however does not completely solve the problem of limited main memory because the collections that hold the IDs of the vertices that need to signal (toSignal) or collect (toCollect) will also grow with an increased size of the graph. A possible solution for this problem is to also store these collections in a database at the cost of a considerable performance penalty, since these collections are accessed very frequently.

## Implementational Details ##
Just as in the in-memory implementation, the vertices are uniquely identified by their ID so they can be stored, retrieved and updated. The provided on-disk storage backend saves the vertices to a [Berkeley DB Java Edition](http://www.oracle.com/technetwork/database/berkeleydb/overview/index-093405.html) database. This has the advantage that no additional data base system needs to be installed since all the necessary libraries are included in the framework.

Compared with the default in-memory implementation the performance of the BerkeleyDB implementation is slower because vertices need to be serialized and deserialized on every access or update respectively. In addition to that the latency of accesses to the secondary storage device is much higher than for memory accesses.

## How to use on-disk storage ##
The [storage](http://code.google.com/p/signal-collect/source/browse/trunk/core/src/main/scala/com/signalcollect/factory/Factories.scala) package defines a set of [StorageFactories](http://code.google.com/p/signal-collect/source/browse/trunk/core/src/main/scala/com/signalcollect/interfaces/Factory.scala) that can be plugged into the graph builder to replace the default implementation.

Currently the following StorageFactories are available:
  * **InMemory (default)** Stores the vertices, edges and the toSignal & toCollect lists in main memory.
  * **BerkeleyDB** Stores the vertices and edges on disk while the toSignal & toCollect lists remain in main memory.
  * **CompressedBerkeleyDB** Like BerkeleyDB but the serialized representations of the vertices are compressed in order to save disk space (at the cost of additional compression and decompression overhead).
  * **CachedBerkeleyDB** Like BerkeleyDB but with an additional in-memory cache for some vertices.
  * **AllOnDiskBerkeley** Stores vertices, toSignal & toCollect on disk. This is very slow because the toSignal & toCollect lists need to be serialized and deserialized frequently.

The suitable StorageFactory can then be used to override the default implementation. For example to use BerkeleyDB as the storage backend use:
```
import com.signalcollect.factory.storage._ 
  ... 
  val graph = GraphBuilder.withStorageFactory(BerkeleyDB).build
```
This will then create a folder called "sc-berkeley" in the program environment where all the graph data about will be stored. The rest is handled by the BerkeleyDB storage module and requires no changes to the algorithm.