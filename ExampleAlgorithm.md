# Example Algorithm - Tutorial #

This example algorithm shows how to use the Signal/Collect programming model for a simple simulation.

## The general algorithm ##

For our example we use the a variation of the [Schelling Segregation model](http://web.mit.edu/rajsingh/www/lab/alife/schelling.html). Let's assume the following properties:

  * We have a space represented by a two dimensional m\*n grid. This space could be thought of as a neighborhood in a city district. Each cell in this grid could stand for a housing unit that can be occupied by residents of a binary type e.g. rich or poor.
  * Each cell in the grid has a preference for its surrounding cells to be the same type. E.g. a resident could require to have at least 40% of its neighbors of the same income as himself.
  * If the minimum conformity is not met, the cell changes its state. E.g. the resident moves away and the unit will be occupied by someone from the opposite income level.

The model predicts that the space will be clearly separated even if the individual cells are relatively tolerant.

## Design of the Signal/Collect computation ##

To map the algorithm described above to a Signal/Collect computation we need to construct a graph that represents the given grid. For this reason we view each cell as a vertex that is connected to its neighboring vertices. The easiest way to start of is to use one of the predefined vertices. They already handle a lot of the necessary householding tasks and we can focus on the signal and collect parts of the algorithm.

### Collect ###

Each cell determines if it should change its state according to the states of its neighbors. To keep track of the states of the neighbors a data structure is needed that stores the most recent state update received by every neighbor. Because keeping track of the states of the neighbors is a relatively frequent task, this functionality is already implemented by the vertex in the library and can therefore simply be extended to fit our purpose. To count the number of neighbors with equal state we can use a higher order function to sum over all entries in the ` mostRecentSignalMap ` that stores all current signal states:
```
val equalCount = mostRecentSignalMap.values.foldLeft(0)((b, otherState) => if (otherState == this.state) b + 1 else b)
```

This is semantically equivalent to the following loop:

```
var equalCount = 0
for (otherState <- mostRecentSignalMap.values) {
   if(otherState == this.state) {
        equalCount += 1
   }
}
```

The rest of the collect function is then rather straightforward. If the count of all neighbors in relation to all the neighbors is lower then the threshold, the state should alternate and if the ratio is above the threshold we leave the state unchanged.

```
def collect(oldState: State, mostRecentSignals: Iterable[Int]): Int = {
    //val equalCount = mostRecentSignalMap.values.foldLeft(0)((b, otherState) => if (otherState == this.state) b + 1 else b)
    var equalCount = 0
    for (otherState <- mostRecentSignalMap.values) {
      if (otherState == this.state) {
        equalCount += 1
      }
    }
    val totalNeighbors = mostRecentSignalMap.size
    if (equalCount.toFloat / totalNeighbors >= equalityThreshold) {
      changedState = false
      this.state
    } else {
      changedState = true
      ((this.state) + 1) % 2
    }
  }
```

### Signal ###

For signaling we can use another useful element provided by the framework called ` StateForwarderEdge ` which simply takes the state of its source vertex and signals it to the target vertex. That is all we need here.

### Termination ###
To make sure the vertices are only sending status updates when their state has changed, we use an additional boolean variable that states at any time if the vertex has recently changed its state or not.
To determine whether a vertex should signal its new state or not the framework uses the ` scoreSignal ` function on a vertex is called. If the return value is zero the vertex will not propagate its state to its neighbors. Therefore we also need to include our boolean variable in the ` scoreSignal ` method.
```
override def scoreSignal = if (changedState || lastSignalState == None) 1 else 0
```

Now we have all the elements to build vertices that can simulate a Schelling-Segregation model. The final vertex looks like this:

```
class SegregationAgent(id: Any, initialState: Int, equalityThreshold: Float) extends DataGraphVertex(id, initialState) {
  type Signal = Int
  var changedState: Boolean = false

  def collect(oldState: State, mostRecentSignals: Iterable[Int]): Int = {
    val equalCount = mostRecentSignalMap.values.foldLeft(0)((b, otherState) => if (otherState == this.state) b + 1 else b)
    val totalNeighbors = mostRecentSignalMap.size
    if (equalCount.toFloat / totalNeighbors >= equalityThreshold) {
      changedState = false
      this.state
    } else {
      changedState = true
      ((this.state) + 1) % 2
    }
  }

  override def scoreSignal = if (changedState || lastSignalState == None) 1 else 0
}
```


## Run the algorithm ##
To see our new vertex in action, we need to construct a grid of vertices, where the initial states are randomly chosen to be either 0 or 1. Note that in order to guarantee termination of the algorithm a time limit of 5s is specified.

```
object SchellingSegregation extends App {
  val graph = GraphBuilder.build

  //Dimensions of the grid
  val m = 40
  val n = 20

  //Create all agents
  for (i <- 0 until (m * n)) {
    graph.addVertex(new SegregationAgent(i, (Math.random * 2.0).floor.toInt, 0.4f))
  }


  /* 
   * Grid construction
   * 
   * To construct the actual grid we need to connect the neighboring cells.
   * The following sketch shows all the neighboring 
   * cells that a cell "XX" needs to be connected to:
   * 
   * N1 | N2 | N3
   * ------------
   * N4 | XX | N5
   * ------------
   * N6 | N7 | N8
   * 
   * The names N1..N8 can also be found in the code to show the connection
   * that is currently drawn.
   * 
   * We further need to make sure that cells only link to cells within the grid
   */
  for (i <- 0 until m) { 
    for (j <- 0 until n) {
      if ((i - 1) >= 0) { //Make sure all neighbors left of the cell are within the grid
        graph.addEdge(new StateForwarderEdge(j * m + i, j * m + i - 1)) //N4
        if ((j - 1) >= 0) { 
          graph.addEdge(new StateForwarderEdge(j * m + i, (j - 1) * m + i - 1)) //N1
        }
        if ((j + 1) < n) { 
          graph.addEdge(new StateForwarderEdge(j * m + i, (j + 1) * m + i - 1)) //N6
        }
      }
      if ((i + 1) < m) { //Make sure all neighbors right of the cell are within the grid
        graph.addEdge(new StateForwarderEdge(j * m + i, j * m + i + 1)) //N5
        if ((j - 1) >= 0) { 
          graph.addEdge(new StateForwarderEdge(j * m + i, (j - 1) * m + i + 1)) //N3
        }
        if ((j + 1) < n) { 
          graph.addEdge(new StateForwarderEdge(j * m + i, (j + 1) * m + i + 1)) //N8
        }
      }
      if ((j - 1) >= 0) { // Top neighbor
        graph.addEdge(new StateForwarderEdge(j * m + i, (j - 1) * m + i)) //N2
      }
      if ((j + 1) < n) { // Bottom neighbor
        graph.addEdge(new StateForwarderEdge(j * m + i, (j + 1) * m + i)) //N7
      }
    }
  }

  //Time limit is used to guarantee that the algorithm will terminate
  val stats = graph.execute(ExecutionConfiguration.withTimeLimit(5000))
  
  //Print general computation statistics
  println(stats)
  
  //Print the resulting grid
  for(i <- 0 until (m*n)) {
    if(i%m == 0) {
      print("\n")
    }
    val status: Option[Int] = graph.forVertexWithId(i, (v: SegregationAgent) => v.state)
    print(status.get)
  }
  
  graph.shutdown
}
```

The [source](http://code.google.com/p/signal-collect/source/browse/trunk/core/src/test/scala/com/signalcollect/examples/SchellingSegregation.scala) for this algorithm can also be found in the example section in the core.