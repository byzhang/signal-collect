# Goals of the Programming Model #

The goals of the Signal/Collect programming model are:
  * Expressive enough to tersely formulate a broad range of graph algorithms
  * Can define both synchronous and dataflow-like asynchronous computations
  * Straightforward to parallelize/distribute

The intuition behind the Signal/Collect programming model is that computations are executed on a graph, where the vertices are the computational units that interact by the means of signals that flow along the edges. This is similar to the actor programming model. All computations in the vertices are accomplished by collecting the incoming signals and then signaling the neighbors in the graph.

This means that every **vertex** has a **collect** function which computes the new vertex state based on the old vertex state and the signals that were received.
```
class Vertex {
  def collect(oldState: State, uncollectedSignals: Iterable[Signal]): State
}
```

Every **edge** has a **signal** function which computes the signal sent along this edge based on its source vertex.
```
class Edge {
  def signal(sourceVertex: SourceVertex): Signal
}
```

# Example: Single Source Shortest Path #
Imagine a graph with vertices that represent locations and edges that represent paths between locations. We would like to determine the shortest path from a special location A to all the other locations in the graph.
Every location starts out with the length of the shortest currently known path from A as the state. That means, initially, the state of A is set to 0 and the state of all the other locations is set to infinity.

[![](http://signal-collect.googlecode.com/svn/wiki/images/sssp.png)](http://www.signalcollect.com)

In a first step, all edges signal the state of their source vertex plus the edge weight (= path length, in the example above this is 1) to their respective target location.

```
class SsspEdge extends Edge {
  type SourceVertex = SsspVertex
  def signal(sourceVertex: SsspVertex) = {
    sourceVertex.state + weight  // simplified, for Int we should deal with overflows
  }
}
```

The location vertices collect those signals by setting their new state to the lowest signal received (as long as this is smaller than their state).

```
class SsspVertex extends Vertex {
  type Signal = Int
  def collect(oldState: State, uncollectedSignals: Iterable[Int]): Int = {
    uncollectedSignals.foldLeft(oldState)(math.min(_, _)))
  }
}
```

In a second step, the same Signal/Collect operations get executed using the updated vertex states. It is easy to imagine how these steps, when repeatedly executed, iteratively compute the shortest paths from A to all the other locations in the graph.