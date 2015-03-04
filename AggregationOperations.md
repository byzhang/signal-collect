# What aggregation operations are used for #
Aggregation functions calculate a global value over the entire graph. This has many uses:
  * Calculate the final result of a computation
  * Compute a global normalization constant
  * Sample some values from the graph
  * Specify a global termination condition for a computation

# How to define aggregation operations #
An aggregation operation is specified by implementing the [AggregationOperation](http://code.google.com/p/signal-collect/source/browse/trunk/core/src/main/scala/com/signalcollect/interfaces/AggregationOperation.scala) trait.
```
/**
 *  An aggregation operation aggregates some value of type `ValueType` over all the vertices in a graph.
 */
trait AggregationOperation[ValueType] {
  /**
   *  Extracts values of type `ValueType` from vertices.
   */
  def extract(v: Vertex): ValueType

  /**
   *  Aggregates all the values extracted by the `extract` function.
   *
   *  @note There is no guarantee about the order in which this function gets executed on the extracted values.
   */
  def aggregate(a: ValueType, b: ValueType): ValueType

  /**
   *  Neutral element of the `aggregate` function:
   *  `aggregate(x, neutralElement) == x`
   */
  val neutralElement: ValueType
}
```

Every aggregation operation aggregates some value of type ` ValueType ` over all the vertices in a graph.
The values that get aggregated first get extracted from the vertices with the ` extract ` function. The extracted values then  get aggregated by the ` aggregate ` function. There is no guarantee about the order in which this operation gets executed on the graph. Also required is the ` neutralElement `, which represents the neutral element of the aggregation function ` aggregate `: ` aggregate(x, neutralElement) == x `.

Once an instance of this trait has been created it can be submitted to the ` aggregate ` function on an instance of [Graph](http://code.google.com/p/signal-collect/source/browse/trunk/core/src/main/scala/com/signalcollect/Graph.scala). The returned result is the aggregated value:

```
  /**
   *  Applies an aggregation operation to the graph and returns the result.
   *
   *  @param aggregationOperation The aggregation operation that will get executed on the graph
   *
   *  @return The result of the aggregation operation.
   *
   *  @note There is no guarantee about the order in which the aggregation operations get executed on the vertices.
   *
   *  @example See concrete implementations of other aggregation operations, i.e. `SumOfStates`.
   */
trait Graph {
  def aggregate[ValueType](aggregationOperation: AggregationOperation[ValueType]): ValueType
  ...
}
```

# Global Termination Conditions #

In order to use an aggregation operation as a termination condition an instance of [GloablTerminationCondition](http://code.google.com/p/signal-collect/source/browse/trunk/core/src/main/scala/com/signalcollect/ExecutionConfiguration.scala) has to be specified:
```
/**
 *  GlobalTerminationCondition defines a termination condition that depends on the global state of the graph.
 *  This class is abstract because the should terminate predicate on the aggregated value is not implemented.
 *
 *  @param aggregationOperation The aggregation operation used to compute the globally aggregated value
 *  @param aggregationInterval In a synchronous computation: aggregation interval in computation steps.
 *  In an asynchronous computation: aggregation interval in milliseconds
 */
abstract class GlobalTerminationCondition[ValueType](
  val aggregationOperation: AggregationOperation[ValueType],
  val aggregationInterval: Long = 1000l) {

  /**
   *  Determines if the computation should terminate when the aggregated value is `value`.
   *
   *   @param value The current value computed by `aggregationOperation`
   *   @return If the computation should terminate
   */
  def shouldTerminate(value: ValueType): Boolean
}
```

[GlobalTerminationCondition](http://code.google.com/p/signal-collect/source/browse/trunk/core/src/main/scala/com/signalcollect/ExecutionConfiguration.scala) defines a termination condition that depends on the global state of the graph. The value ` aggregationOperation ` is used to compute the globally aggregated value, while ` aggregationInterval ` has a different semantic depending on the execution type: In a synchronous computation it specifies the aggregation interval in computation steps. An interval of 2 would mean that the global aggregation condition is checked every other computation step. In an asynchronous computation the parameter ` aggregationInterval ` specifies the interval between aggregations in milliseconds. The predicate ` shouldTerminate ` is applied to the result of the aggregation operation. If it returns ` true ` this means that the computation should end.

Examples for aggregation operations can be found in [AggregationOperations](http://code.google.com/p/signal-collect/source/browse/trunk/core/src/main/scala/com/signalcollect/AggregationOperations.scala).

# Usage examples #
SumOfStates is defined in [AggregationOperations](http://code.google.com/p/signal-collect/source/browse/trunk/core/src/main/scala/com/signalcollect/AggregationOperations.scala). It represents an aggregation operation that calculates the sum of numeric states. This might be useful to calculate a global normalization factor:
```
    val graph = GraphBuilder.build
    graph.addVertex(new PageRankVertex(1))
    graph.addVertex(new PageRankVertex(2))
    graph.addEdge(new PageRankEdge(1, 2))
    graph.addEdge(new PageRankEdge(2, 1))
    graph.execute
    val sumOfStates = graph.aggregate(new SumOfStates[Double]).get
```

This example shows how to specify a global termination condition that gets checked every two synchronous computation steps and terminates when the sum of states is larger than 1.0:
```
    val graph = GraphBuilder.build
    graph.addVertex(new PageRankVertex(1))
    graph.addVertex(new PageRankVertex(2))
    graph.addEdge(new PageRankEdge(1, 2))
    graph.addEdge(new PageRankEdge(2, 1))
      val terminationCondition = new GlobalTerminationCondition(new SumOfStates[Double], 2) {
        def shouldTerminate(sum: Option[Double]): Boolean = {
          sum.isDefined && sum.get > 1.0
        }
      }
    val execConfig = ExecutionConfiguration
      .withGlobalTerminationCondition(terminationCondition)
      .withExecutionMode(ExecutionMode.Synchronous)
    val info = graph.execute(execConfig)
```

More usage examples can be found among the tests of the core project in [AggregationOperationsSpec](http://code.google.com/p/signal-collect/source/browse/trunk/core/src/test/scala/com/signalcollect/features/AggregationOperationsSpec.scala).