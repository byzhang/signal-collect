# How to interact with the graph #

## Graph ##
The main way to interact with the graph is via the graph object which is usually created by calling `  GraphBuilder.build  `. This returns an instance of [Graph](http://code.google.com/p/signal-collect/source/browse/trunk/core/src/main/scala/com/signalcollect/Graph.scala). [Graph](http://code.google.com/p/signal-collect/source/browse/trunk/core/src/main/scala/com/signalcollect/Graph.scala) extends [GraphEditor](http://code.google.com/p/signal-collect/source/browse/trunk/core/src/main/scala/com/signalcollect/GraphEditor.scala) which offers various methods for interacting with the graph, such as ` addVertex ` and ` addEdge `. There are also functions to send signals along virtual edges.

Example how to build a graph:
```
val graph = GraphBuilder.build
graph.addVertex(new PageRankVertex(id=1))
graph.addVertex(new PageRankVertex(id=2))
graph.addEdge(new PageRankEdge(sourceId=1, targetId=2))
graph.addEdge(new PageRankEdge(sourceId=2, targetId=1))
```

## VertexGraphEditor ##
A vertex may extend the trait [VertexGraphEditor](http://code.google.com/p/signal-collect/source/browse/trunk/core/src/main/scala/com/signalcollect/implementations/graph/VertexGraphEditor.scala). This adds the field ` graphEditor ` to the vertex, which is an instance of [GraphEditor](http://code.google.com/p/signal-collect/source/browse/trunk/core/src/main/scala/com/signalcollect/GraphEditor.scala). This allows vertices to send signals along virtual edges and to modify the graph during algorithm execution. There are some exciting applications for this, for example one can recursively build index structures or [crawl the web](http://code.google.com/p/signal-collect/source/browse/trunk/core/src/test/scala/com/signalcollect/examples/WebCrawler.scala).

**IMPORTANT:** Currently graph operations are always executed asynchronously. If there is an ongoing synchronous computation, then non-determinism is introduced, because there is no guarantee about the computation step in which an operation gets executed. We intend to address this in 2.0.0 ([Issue 44](http://code.google.com/p/signal-collect/issues/detail?id=44)).

Example usage of vertex graph editor:
```
class WeirdVertex(id: String) extends DataGraphVertex(id, 1) {

  def collect(oldState: Int, mostRecentSignals: Iterable[Double]): Double = {
    val neighborId = Random.nextInt(100)
    graphEditor.addVertex(new WeirdVertex(neighborId))
    graphEditor.addEdge(new StateForwarderEdge(id, neighborId))
    oldState + 1
  }

}
```