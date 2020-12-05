package us.jwf.aoc.graph

class Graph<NodeData> {
  private val _edges = mutableListOf<Edge<NodeData>>()
  private val _nodes = mutableSetOf<Node<NodeData>>()
  private val edgesByOrigin = mutableMapOf<Node<NodeData>, List<Edge<NodeData>>>()

  val edges: List<Edge<NodeData>>
    get() = _edges
  val nodes: Set<Node<NodeData>>
    get() = _nodes

  // Note: Assumes two edges connecting the same nodes in the same direction are different.
  fun addEdge(from: NodeData, to: NodeData, weight: Double = 0.0) {
    val fromNode = Node(from).also { _nodes.add(it) }
    val toNode = Node(to).also { _nodes.add(it) }
    val edge = Edge(fromNode, toNode, weight)

    _edges.add(edge)
    edgesByOrigin.compute(fromNode) { _, value ->
      if (value == null) listOf(edge)
      else value + listOf(edge)
    }
  }

  fun containsCycle(): Boolean {
    val nodePool = nodes.toMutableSet()

    var count = 0
    var containsCycle = false
    while (nodePool.isNotEmpty()) {
      count++
      val queue = ArrayDeque<Edge<NodeData>>()

      val origin = nodePool.first()
      val visited = mutableSetOf<Node<NodeData>>()
      queue.addAll(edgesByOrigin[origin] ?: emptyList())
      nodePool.remove(origin)
      visited.add(origin)

      var subgraphContainsCycle = false
      while (queue.isNotEmpty()) {
        val edge = queue.removeFirst()
        visited.add(edge.to)
        if (!nodePool.remove(edge.to)) {
          subgraphContainsCycle = true
        } else {
          queue.addAll(edgesByOrigin[edge.to] ?: emptyList())
        }
      }
      println("Subgraph $count (${if (subgraphContainsCycle) "cycle" else "no cycle"}): $visited")
      containsCycle = containsCycle || subgraphContainsCycle
    }

    return containsCycle
  }

  fun toplologicalSort(): List<Node<NodeData>> {
    TODO("blah")
  }
}

data class Node<T>(private val data: T) {
  override fun toString(): String = data.toString()
}
data class Edge<T>(val from: Node<T>, val to: Node<T>, val weight: Double)