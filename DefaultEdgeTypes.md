# Default Implementations #

**Analogous to the [default vertex implementations](http://code.google.com/p/signal-collect/w/edit/DefaultVertexTypes) Signal/Collect also provides default implementations for edges. Depending on the specific algorithm these edges can directly be used for constructing a graph or provide some predefined functionality and therefore reduce the functionality that needs to be implemented on top of them when subclassing.**

Three abstract edge implementations exist (the signal functionality needs to be provided on top of them):
  * **[DefaultEdge](http://code.google.com/p/signal-collect/source/browse/trunk/core/src/main/scala/com/signalcollect/DefaultEdge.scala)** is the default edge implementation and provides the basic structure for linking vertices and sending a signal from the source vertex to the target vertex.
  * **[OnlySignalOnChangeEdge](http://code.google.com/p/signal-collect/source/browse/trunk/core/src/main/scala/com/signalcollect/OnlySignalOnChangeEdge.scala)** extends the default edge implementation but keeps track of the last signal sent and only sends a new signal if the current signal differs from it.
  * **[OptionalSignalEdge](http://code.google.com/p/signal-collect/source/browse/trunk/core/src/main/scala/com/signalcollect/OptionalSignalEdge.scala)** extends the default edge implementation but requires the signal function to return an ` Option[_] `- type and sends the contained signal if the return differed from None.

Edges don't necessarily have to be algorithm specific. Sometimes one of the provided concrete edge implementations can be used without any modifications:
  * **[StateForwarderEdge](http://code.google.com/p/signal-collect/source/browse/trunk/core/src/main/scala/com/signalcollect/StateForwarderEdge.scala)** forwards its current state whenever the signal method is invoked by the source vertex.