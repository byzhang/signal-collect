# Maven Central Release 1.1.2 #
Released on March 15th, 2012
  * Some changes to the ` pom.xml `, so it could be synced with Maven Central. Fully backward compatible with 1.1.1.

# Bugfix Release 1.1.1 #
Released on December 5th, 2011
  * [Issue 55](https://code.google.com/p/signal-collect/issues/detail?id=55) Fixed a bug related to Java/Scala interoperability.

# Java Release 1.1 #
Released on November 28th, 2011
  * [Issue 53](https://code.google.com/p/signal-collect/issues/detail?id=53) [Added Java support](http://code.google.com/p/signal-collect/wiki/JavaExampleAlgorithm)
  * Changed implementation of factory objects, execution mode objects and termination reason objects to make access from Java straightforward and moved them to the package ` com.signalcollect.configuration `
  * [Issue 45](https://code.google.com/p/signal-collect/issues/detail?id=45) ` Graph.forVertexWithId ` is much faster now, but doesn't guarantee that all graph modifications have been executed on the vertex yet. To get the old semantics, call ` Graph.awaitIdle() ` first.
  * [Issue 46](https://code.google.com/p/signal-collect/issues/detail?id=46) ` GraphEditor ` can be used concurrently now, which allows parallel graph loading (for example using parallel collections).
  * [Issue 47](https://code.google.com/p/signal-collect/issues/detail?id=47) ` ExecutionInformation.graphLoadingWaitInMilliseconds ` was renamed to ` ExecutionInformation.graphIdleWaitingTimeInMilliseconds `, because this wait might have other reasons than waiting for the graph to load.
  * [Issue 48](https://code.google.com/p/signal-collect/issues/detail?id=48) The reported ` ExecutionInformation.graphLoadingWaitInMilliseconds ` was wrong. Bug fixed (field was also renamed).
  * [Issue 49](https://code.google.com/p/signal-collect/issues/detail?id=49) ` DefaultEdgeId ` now caches its ` hashCode `. This uses more memory but saves CPU time.
  * [Issue 50](https://code.google.com/p/signal-collect/issues/detail?id=50) ` ExecutionInformation ` now has an additional field ` totalExecutionTimeInMilliseconds `, which measures  the total algorithm execution time comprised of idle waiting, garbage collection and computation time.
  * [Issue 51](https://code.google.com/p/signal-collect/issues/detail?id=51) Added @specialized Annotations to ` Vertex `/` Edge `, this improves performance when when using primitives as ` id `/` signal `.
  * [Issue 52](https://code.google.com/p/signal-collect/issues/detail?id=52) Using ` ConcurrentHashMap ` with concurrency level of 1 within the worker, which saves memory compared to the default.
  * Added a Java implementation of the ` IdStateAggregator ` called ` IdStateJavaAggregator `