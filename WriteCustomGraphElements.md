# Implementing Custom Graph Elements #

For many applications, writing custom graph elements such as vertices and possibly edges might considerably improve the overall performance. The available vertex templates in the framework are designed to facilitate the implementation of algorithms in the Signal/Collect computing paradigm and are therefore held very general to support a variety of use cases. If you would like to improve the performance for your specific use case it might be a good idea to try to implement the needed interfaces of the respective classes by yourself.

## Custom Serialization ##

Implementing the [Vertex Trait](http://code.google.com/p/signal-collect/source/browse/trunk/core/src/main/scala/com/signalcollect/Vertex.scala) requires every vertex to be ` Serializable ` so you have to make sure that your own vertex implementations are serializable as well. By default each vertex is serialized using standard Java serialization with ObjectOutputStreams. This ensures that a wide variety of types can be serialized.

Custom vertex implementations where the types are given explicitly can help speeding up the serialization process considerably, because less type inspection is needed and the serialization process is much more straightforward. If you would like to improve the serialization speed of your custom implementation even further you can also provide an ` Externalizable ` implementation, which describes explicitly how to write your objects to an output stream and how to read them back from it. To give a practical example on how such an implementation could look like, the externalizable code for a rather simplistic vertex that represents a page for a PageRank calculation is shown here. Note that all this page needs to store are the id, its state, its last signal state, the pages it links to and a map with the signals it received.

```
class MemoryEfficientPage(var id: Int) extends Vertex with Externalizable {
//...
  def writeExternal(out: ObjectOutput) {
    out.writeInt(id)
    out.writeFloat(state)
    out.writeFloat(lastSignalState)
    // Write links
    out.writeInt(targetIdArray.length)
    for (i <- 0 until targetIdArray.length) {
      out.writeInt(targetIdArray(i))
    }
    //write most recent signals
    out.writeInt(mostRecentSignalMap.values.size)
    mostRecentSignalMap.foreach(signal => {
      out.writeInt(signal._1)
      out.writeFloat(signal._2)
    })
  }

  def readExternal(in: ObjectInput) {
    id = in.readInt
    state = in.readFloat
    lastSignalState = in.readFloat
    //read Links
    val numberOfLinks = in.readInt
    targetIdArray = new Array[Int](numberOfLinks)
    for (i <- 0 until numberOfLinks) {
      targetIdArray(i) = in.readInt
    }
    //read most recent signals
    mostRecentSignalMap = Map[Int, Float]()
    val numberOfMostRecentSignals = in.readInt
    for (i <- 0 until numberOfMostRecentSignals) {
      mostRecentSignalMap += ((in.readInt, in.readFloat))
    }
  }
}
```

The effect of having a custom vertex implementation and an externalizable serialization implemented, is clearly visible in the time consumed to serialize and deserialize objects.

![http://chart.apis.google.com/chart?chxr=0,0,199996.667&chxs=0,676767,11.5,-0.167,l,676767&chxt=x&chbh=19,5,21&chs=460x217&cht=bhg&chco=224499,3366CC,3072F3&chds=0,200000,0,200000,0,200000&chd=t:75635,176802|57399,121488|47582,60959&chdl=Default+Page%2C+Default+Serializer|Custom+Page%2C+Default+Serializer|Custom+Page%2C+Custom+Serializer&chma=5,5,2,5&chm=t++Serialization,676767,1,0,14|t++Deserialization,676767,1,1,14&chtt=++++Serialization+Time+PageRank+Vertex+(ns)&chts=676767,14.5.png](http://chart.apis.google.com/chart?chxr=0,0,199996.667&chxs=0,676767,11.5,-0.167,l,676767&chxt=x&chbh=19,5,21&chs=460x217&cht=bhg&chco=224499,3366CC,3072F3&chds=0,200000,0,200000,0,200000&chd=t:75635,176802|57399,121488|47582,60959&chdl=Default+Page%2C+Default+Serializer|Custom+Page%2C+Default+Serializer|Custom+Page%2C+Custom+Serializer&chma=5,5,2,5&chm=t++Serialization,676767,1,0,14|t++Deserialization,676767,1,1,14&chtt=++++Serialization+Time+PageRank+Vertex+(ns)&chts=676767,14.5.png)