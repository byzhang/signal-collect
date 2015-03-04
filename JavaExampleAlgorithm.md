# Example Algorithm Java API #

An algorithm that can be naturally expressed as a Signal/Collect computation is the Single Source Shortest Path (SSSP) algorithm. For this algorithm we need a graph of connected vertices where exactly one vertex serves as the source of the graph and we compute the shortest distance from this source vertex to all other vertices in the graph. The distance is expressed as the sum of the weights of all the edges traversed on the path.

To fit this algorithm in the programming model of Signal/Collect we need to describe the computation from the perspective of a vertex. A vertex can learn about its distance to the source by comparing the distance of its neighbors to the source plus the weight of the incoming edge from this neighbor. When the vertex has learned about a shorter path, then it needs to inform its own neighbors about this fact. If the distance does not change there is no need for the vertex to send out new signals, because its neighbors already know about a path that is at least as short. This guarantees the global convergence of the SSSP algorithm (assuming a non-negative edge weight).

## Vertex implementation (Collect) ##
To achieve the behavior described above, a vertex collects its received signals by computing the minimum among all signals and the current state. The full implementation of the collect function is shown here:
```
public Integer collect(Integer oldState, Iterable<Integer> mostRecentSignals) {
	int minDistance = oldState;
	for (int signal : mostRecentSignals) {
		if (signal < minDistance) {
			minDistance = signal;
		}
	}
	return minDistance;
}
```
The returned value of the collect function then automatically becomes the new state of the vertex. If this assignment changes the current value of the vertex's state, this vertex needs to forward its new state to all its neighbors. In case the state did not change after the execution of the collect function, the vertex does not have to signal any state updates. This local decision is responsible for the overall convergence of the SSSP algorithm which means that each vertex reachable from the source vertex has found its minimal distance to the source.

Initially all vertices are initialized to a value that is strictly larger than the longest shortest distance between the source vertex and any other vertex in the graph. This means that whenever the first update message arrives at any vertex, the state will definitely update to a smaller value than the initialization state. If the algorithm converges and a vertex still holds its initialization state, it means that the vertex never received any update message and is therefore not reachable from the source vertex of the SSSP graph.

Here is the complete implementation of the vertex:
```
import com.signalcollect.javaapi.DataGraphVertex;
import java.lang.Iterable;

@SuppressWarnings("serial")
public class SSSPNode extends DataGraphVertex<Integer, Integer, Integer> {

	public SSSPNode(int id) {
		this(id, Integer.MAX_VALUE);
	}

	public SSSPNode(int id, int initialDistance) {
		super(id, initialDistance);
	}

	@Override
	public Integer collect(Integer oldState, Iterable<Integer> mostRecentSignals) {
		int minDistance = oldState;
		for (int signal : mostRecentSignals) {
			if (signal < minDistance) {
				minDistance = signal;
			}
		}
		return minDistance;
	}

}
```

## Edge implementation (Signal) ##
Now that we know how to process incoming signals to compute the new state for a vertex, we take a closer look at how to send these state-update signals from one vertex to the vertices it links to. To forward the state update to neighbors, we use edge objects that handle the generation and forwarding of the signals.

For our SSSP graph, an edge holds the following information:
  * The id of the source vertex
  * The id of the target vertex
  * The weight of the edge, which represents distance (default value = 1.0)

Together with the current state of the source vertex this is sufficient to compute the signal that should be sent along the edge from the source to the target vertex. Again we use the a predefined library class that handles generic edge functionality such as how to forward the computed signal to the target vertex. The library class ` DefaultEdge ` that is used as a basis for the ` SSSPEdge ` needs to be parametrized with the source vertex's type to know how to extract its state information.

```
import com.signalcollect.javaapi.*;

@SuppressWarnings("serial")
public class SSSPEdge extends DefaultEdge<SSSPNode> {

	SSSPEdge(int sourceID, int targetID) {
		super(sourceID, targetID);
	}

	@Override
	public Object signal(SSSPNode sourceVertex) {
		if(sourceVertex.getState()<Integer.MAX_VALUE) { //To prevent integer overflow.
			return sourceVertex.getState() + (int)this.weight();
		}
		else {
			return sourceVertex.getState();
		}
	}
}
```

## Run the algorithm ##
So far we have implemented the components of the graph that are responsible for computing the states based on signals from neighboring vertices and for computing and forwarding signals from one vertex to its neighboring vertices. To build an SSSP graph all we have to do is to construct a graph with the elements above and start the computation. For the SSSP algorithm we don't need to enforce synchronous execution because the algorithm will also converge when executed asynchronously. After the termination of the algorithm we print some statistics about the execution and the state of each vertex to the standard output to check whether the distances were computed correctly.

```
import com.signalcollect.ExecutionInformation;
import com.signalcollect.Graph;
import com.signalcollect.Vertex;
import com.signalcollect.javaapi.*;

public class SSSP {

	public static void main(String[] args) {
		Graph graph = GraphBuilder.build();
		
		graph.addVertex(new SSSPNode(1, 0));
		graph.addVertex(new SSSPNode(2));
		graph.addVertex(new SSSPNode(3));
		graph.addVertex(new SSSPNode(4));
		graph.addVertex(new SSSPNode(5));
		graph.addVertex(new SSSPNode(6));
		
		graph.addEdge(new SSSPEdge(1, 2));
		graph.addEdge(new SSSPEdge(2, 3));
		graph.addEdge(new SSSPEdge(3, 4));
		graph.addEdge(new SSSPEdge(1, 5));
		graph.addEdge(new SSSPEdge(4, 6));
		graph.addEdge(new SSSPEdge(5, 6));
		
		ExecutionInformation stats = graph.execute();
		System.out.println(stats);
		
		//print the state of every vertex in the graph.
                graph.foreachVertex(FunUtil.convert(new VertexCommand(){
                    public void f(Vertex v) {
                            System.out.println(v);
                    }
                }));
		graph.shutdown();
	}
}
```

After the execution of the algorithm the printout of the vertices should look similar to this this (note that the order of execution by the ` graph.foreachVertex(..); ` method is not guaranteed):

```
SSSPNode(id=2, state=1)
SSSPNode(id=4, state=3)
SSSPNode(id=6, state=2)
SSSPNode(id=1, state=0)
SSSPNode(id=3, state=2)
SSSPNode(id=5, state=1)
```


