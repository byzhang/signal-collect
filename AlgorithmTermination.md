# Algorithm Termination #

There are several different mechanisms to terminate algorithm execution.

## Automated Convergence Detection ##
This is the usual way for a computation to end and it is enabled by default. See ` Continuous Execution ` below to find out how to explicitly disable it.

A computation ends when a computation has converged. Convergence is detected with the ` scoreSignal `/` scoreCollect ` functions on vertices and the respective ` signalThreshold `/` collectThreshold ` which are defined globally. The framework will execute ` signal `/` collect ` operations while the respective scores are above the thresholds. A computation ends when no score is above the respective threshold anymore.

**IMPORTANT:** Vertices are only re-scored using ` scoreCollect ` if:
  * At least one new signal was received
  * Or an outgoing edge was added/removed
  * Or an explicit re-scoring was triggered via ` Graph.recalculateScoresForVertexWithId `/` Graph.recalculateScores `

Vertices are only re-scored using ` scoreSignal ` if:
  * The collect operation was executed
  * Or an outgoing edge was added/removed
  * Or an explicit re-scoring was triggered via ` Graph.recalculateScoresForVertexWithId `/` Graph.recalculateScores `

These are the default implementations in [AbstractVertex](http://code.google.com/p/signal-collect/source/browse/trunk/core/src/main/scala/com/signalcollect/implementations/graph/AbstractVertex.scala):

```
  def scoreSignal: Double = {
    if (outgoingEdgeAddedSinceSignalOperation) {
      1
    } else {
      lastSignalState match {
        case Some(oldState) if oldState == state => 0
        case noStateOrStateChanged => 1
      }
    }
  }
```

The default implementation of the ` scoreSignal ` function indicates that a vertex should have its edges signal if a new edge was added or if the state has changed.

**IMPORTANT:** Detection of a changed vertex state is by default done with a reference comparison, so if the ` state ` reference points to a mutable collection that has changed, no change will be detected! You should in this case either use immutable collections or override the ` scoreSignal ` function.

The ` scoreSignal ` function can be overridden with an algorithm-specific implementation, for example to use the residual as a convergence criterium:
```
override def scoreSignal: Double = {
  lastSignalState match {
    case None => 1
    case Some(oldState) => (state - oldState).abs
  }
}
```

The default implementation of the ` scoreCollect ` function indicates that the importance of collecting is proportional to the number of signals it has received since the last time it collected:

```
def scoreCollect(signals: Iterable[SignalMessage[_, _, _]]): Double = {
  signals.size
}
```
It can be overridden, if necessary.

## Global Termination Conditions ##
It is possible to define termination conditions that depend on some global criterium by using AggregationOperations. There is a detailed description of how to use this including a usage example in the AggregationOperations wiki article.

## Time ##
It is possible to set a time limit in milliseconds for a computation. The framework will terminate the computation when the limit is overstepped.

Usage:
```
val execConfig = ExecutionConfiguration.withTimeLimit(10000) // 10 seconds
graph.execute(execConfig)
```

## Computation Steps ##
For synchronous computations it is possible to limit the number of computation steps that get executed. A computation step is a signal step followed by a collect step. A signal step is the parallel execution of the signal operation on all edges of vertices that have a signal score > signal threshold. A collect step is the parallel execution of collect operations on vertices that have a collect score > collect threshold.

Usage:
```
val execConfig = ExecutionConfiguration
      .withExecutionMode(ExecutionMode.Synchronous)
      .withStepsLimit(1)
graph.execute(execConfig)
```

## Continuous Execution ##
Continuous execution is an asynchronous execution mode that disables the automated termination upon convergence detection. The main use case is for dataflow computations where the system should keep running even if it converges occasionally. When in this execution mode the ` Graph.execute ` method does not block, but it returns immediately.


Usage:
```
val execConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.ContinuousAsynchronous)
graph.execute(execConfig)
```