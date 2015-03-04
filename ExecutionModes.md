# Signal/Collect Operations #
A Signal/Collect computation consists of vertices executing two operations:
  * **signaling:** All outgoing edges of a vertex compute and send signals to their target vertex
  * **collecting:** Vertices update their state based on the signals they received

A Signal/Collect computation is guided by two elements:
  * ` scoreSignal `/` scoreCollect ` functions and the respective ` signalThreshold `/` collectThreshold `
  * The execution mode

The scoring functions and thresholds tell the framework if a vertex should signal/collect at all. There is a more in-depth description of scoring in AlgorithmTermination. The order of execution of the signal/collect operations is determined by the execution mode.

# Execution Modes #
Signal/Collect supports different execution modes. The execution mode determines what kind of guarantees about the execution order of the signal/collect operations a user gets.

## Synchronous execution ##
A synchronous computation consists of alternating parallel signaling/collecting phases for vertices. Phase transitions are globally synchronized, which guarantees that all vertices are in the same phase at the same time. One signaling phase followed by one collecting phase is referred to as a computation ` step `. This execution mode is closely related to the [Bulk Synchronous Parallel (BSP)](http://en.wikipedia.org/wiki/Bulk_Synchronous_Parallel) model and [Pregel](http://www-bd.lip6.fr/ens/grbd2011/extra/SIGMOD10_pregel.pdf).

How to start a computation using the synchronous execution mode:
```
val graph = GraphBuilder.build
val execConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.Synchronous)
graph.execute(execConfig)
```

## Asynchronous execution ##
In an asynchronous computation the framework does not give any guarantees about the order in which signal/collect operations get executed. This allows ` workers ` to operate without central synchronisation, which can improve performance. Some algorithms also have better convergence when run asynchronously, because it can prevent oscillations. This execution mode is closely related to the [Actor Model](http://en.wikipedia.org/wiki/Actor_model). **The default execution mode of Signal/Collect is to start with one synchronous signaling step followed by an asynchronous execution**, we refer to this mode as ` OptimizedAsynchronousExecutionMode `.

How to start a computation using the default execution mode:
```
val graph = GraphBuilder.build
graph.execute
```


How to start a computation using the pure asynchronous execution mode:
```
val graph = GraphBuilder.build
val execConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.PureAsynchronous)
graph.execute(execConfig)
```

There is also a continuous asynchronous execution mode, but apart from not blocking, but immediately returning and from not using termination detection (see AlgorithmTermination), it is equivalent to the pure asynchronous execution mode:
```
val graph = GraphBuilder.build
val execConfig = ExecutionConfiguration.withExecutionMode(ExecutionMode.ContinuousAsynchronous)
graph.execute(execConfig)
```