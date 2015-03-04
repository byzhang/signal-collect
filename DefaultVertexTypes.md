# Default Implementations #
**In any Signal/Collect graph different vertices and edges can be freely combined. Usually a vertex is implemented by extending one of the default vertex implementations and an edge is implemented by extending ` DefaultEdge `. If the default implementations are not memory-efficient enough for a specific purpose, then it is possible to [write custom graph elements.](http://code.google.com/p/signal-collect/wiki/WriteCustomGraphElements)**

Signal/Collect has two different default vertex implementations:
  * **[DataGraphVertex](http://code.google.com/p/signal-collect/source/browse/trunk/core/src/main/scala/com/signalcollect/DataGraphVertex.scala)** is suitable for algorithms that iteratively update state associated with vertices and edges.
  * **[DataFlowVertex](http://code.google.com/p/signal-collect/source/browse/trunk/core/src/main/scala/com/signalcollect/DataFlowVertex.scala)** is suitable for dataflow computations where data is routed through a network of processing vertices.

For the user the main noticeable difference between these implementations is how received signals are submitted to the collect function.

## [DataGraphVertex](http://code.google.com/p/signal-collect/source/browse/trunk/core/src/main/scala/com/signalcollect/DataGraphVertex.scala) ##
In most iterative computations old values get overridden by newer ones . This is why the ` collect ` function of DataGraphVertex receives signals as a parameter ` mostRecentSignals ` which contains only the most recently received signal for each incoming edge that has received at least one signal already.

## [DataFlowVertex](http://code.google.com/p/signal-collect/source/browse/trunk/core/src/main/scala/com/signalcollect/DataFlowVertex.scala) ##
In a dataflow computation no signal should ever be lost. This is why the ` collect ` function of DataFlowVertex receives a parameter ` uncollectedSignals ` which contains all the signals that have been received since the collect operation was last executed. The signals are ordered by their time of arrival, oldest signals first. Also the vertex state is supposed to hold the processed elements and it is automatically reset to a customizable ` resetState ` whenever the edges are done with signaling.