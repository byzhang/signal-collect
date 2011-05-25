package signalcollect.benchmark
import scala.util.Random
import signalcollect.interfaces.ComputeGraph
import signalcollect.api.DefaultBuilder
import signalcollect.algorithms.Page
import signalcollect.algorithms.Link

class LogNormal(vertices: Int, seed: Long = 0, sigma: Double = 1, mu: Double = 3) extends Traversable[(Int, Int)] {

  def foreach[U](f: ((Int, Int)) => U) = {
    val r = new Random(seed)
    var i = 0
    while (i < vertices) {
      val from = i
      val outDegree: Int = scala.math.exp(mu + sigma * (r.nextGaussian)).round.toInt //log-normal
      var j = 0
      while (j < outDegree) {
        val to = ((r.nextDouble * (vertices - 1))).round.toInt
        if (from != to) {
          f(from, to)
          j += 1
        }
      }
      i += 1
    }
  }

}

/** A simple benchmark to test how uch the generall performance of signal/collect has increased with each build. */
object Benchmark extends App {

  def buildPageRankGraph(cg: ComputeGraph, edgeTuples: Traversable[Tuple2[Int, Int]]): ComputeGraph = {
    edgeTuples foreach {
      case (sourceId, targetId) =>
        cg.addVertex(classOf[Page], sourceId, 0.85)
        cg.addVertex(classOf[Page], targetId, 0.85)
        cg.addEdge(classOf[Link], sourceId, targetId)
    }
    cg
  }

  val et = new LogNormal(500 * 1000, 0, 1, 2.5)
  var evalGraph = buildPageRankGraph(DefaultBuilder.withNumberOfWorkers(100).build, et)

  evalGraph.setSignalThreshold(0.001)
  evalGraph.setCollectThreshold(0.0)

  var stats = evalGraph.execute
  var computationTime = stats.computationTimeInMilliseconds
  var performanceScore = 100.0 * computationTime.get / 91424.0
  println("Performance Score: " + performanceScore.toInt + "%")
  evalGraph.shutDown

  evalGraph = buildPageRankGraph(DefaultBuilder.withNumberOfWorkers(3).build, et)
  System.gc
  stats = evalGraph.execute
  val computationTime3Workers = stats.computationTimeInMilliseconds
  evalGraph.shutDown
  
  evalGraph = buildPageRankGraph(DefaultBuilder.withNumberOfWorkers(6).build, et)
  System.gc
  stats = evalGraph.execute
  val computationTime6Workers = stats.computationTimeInMilliseconds
  val scalabilityScore = ((computationTime3Workers.get / computationTime6Workers.get) - 1.0) * 100.0
  println("Scalability Score: " + scalabilityScore.toInt)
  evalGraph.shutDown
    
}