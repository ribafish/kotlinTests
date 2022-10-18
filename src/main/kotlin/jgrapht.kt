import org.jgrapht.graph.AbstractGraph
import org.jgrapht.graph.DefaultWeightedEdge
import org.jgrapht.graph.DirectedAcyclicGraph
import org.jgrapht.util.SupplierUtil

fun main() {
    graph1()
}

data class Module(val name: String)

fun graph1 () {
    val graph = DirectedAcyclicGraph<String, DefaultWeightedEdge>(null, SupplierUtil.createSupplier(DefaultWeightedEdge::class.java), true, false)

    val modules = mutableMapOf<String, Module>()
    for (name in listOf("a", "b", "c", "d")) {
        modules[name] = Module(name)
        graph.addVertex(name)
    }

    graph.addEdge("a", "b", 1)
    graph.addEdge("b", "c", 1)
    graph.addEdge("c", "d", 1)

    println("roots: ${graph.getRoots()}")
    println("leaves: ${graph.getLeaves()}")
    println("\nGraph1:")
    graph.print()

    graph.addVertex("a")
    println("\nGraph2:")
    graph.print()


    graph.addEdge("d", "a", 1)
    println("\nGraph3:")
    graph.print()
}


// Helper functions

fun <V, E>AbstractGraph<V, E>.addEdge(sourceVertex: V, targetVertex:V, weight: Int): E {
    val edge = this.addEdge(sourceVertex, targetVertex)
    this.setEdgeWeight(edge, weight.toDouble())
    return edge
}

fun <V, E>AbstractGraph<V, E>.getRoots(): List<V> {
    return this.vertexSet().filter {key ->
        this.incomingEdgesOf(key).size == 0
    }
}

fun <V, E>AbstractGraph<V, E>.getLeaves(): List<V> {
    return this.vertexSet().filter {key ->
        this.outgoingEdgesOf(key).size == 0
    }
}

fun <V, E> AbstractGraph<V, E>.print(rootVertex: V? = null) {
    if (rootVertex == null) {
        this.getRoots().forEach { this.print(it) }
    } else {
        val childrenWeights = this.outgoingEdgesOf(rootVertex).map {
            this.getEdgeTarget(it) to this.getEdgeWeight(it)
        }
        val childEdges = childrenWeights.map { (target, weight) ->
            "$target [$weight]"
        }
        println("Vertex '$rootVertex', children: $childEdges")
        childrenWeights.forEach { (child, _) -> this.print(child) }
    }
}



