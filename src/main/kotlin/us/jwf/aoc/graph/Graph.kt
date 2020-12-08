package us.jwf.aoc.graph

class Graph<NodeData> {
  private val _nodes = mutableSetOf<Node<NodeData>>()
  private val _edges = mutableSetOf<Edge<NodeData>>()
  private val edgesByOrigin = mutableMapOf<Node<NodeData>, List<Edge<NodeData>>>()

  val nodes: Set<Node<NodeData>>
    get() = _nodes

  val edges: Set<Edge<NodeData>>
    get() = _edges

  fun addNode(node: NodeData) {
    _nodes.add(Node(node))
  }

  fun edgesForNode(node: Node<NodeData>): List<Edge<NodeData>> = edgesByOrigin[node] ?: emptyList()

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

  /**
   * Returns a set of connected subgraphs.
   *
   * When this graph is fully-connected, returns a single-element.
   */
  fun toSubgraphs(): Set<Graph<NodeData>> {
    val result = mutableSetOf<Graph<NodeData>>()
    val nodesToTraverse = nodes.toMutableSet()

    while (nodesToTraverse.isNotEmpty()) {
      val subgraph = Graph<NodeData>()

      fun traverse(node: Node<NodeData>) {
        edgesByOrigin[node]?.asSequence()
          ?.forEach { (from, to, weight) ->
            if (!nodesToTraverse.remove(to)) return@forEach
            subgraph.addEdge(from.data, to.data, weight)
            traverse(to)
          }
      }

      val root = nodesToTraverse.first().also { nodesToTraverse.remove(it) }
      subgraph.addNode(root.data)
      traverse(root)

      result.add(subgraph)
    }

    return result
  }

  /**
   * Applies the visitor pattern to traverse the graph from a given start point.
   *
   * The [visitor] is responsible for saying whether or not traversal should continue to the
   * provided node's children.
   */
  fun traverseFrom(node: NodeData, visitor: (NodeData, Double) -> Boolean) {
    traverseFrom(Node(node), 0.0) { visited, weight -> visitor(visited.data, weight) }
  }

  /**
   * Finds the minimum spanning tree of the graph by Prim's algorithm.
   */
  fun findMSTKruskal(): Graph<NodeData> {
    val result = Graph<NodeData>()

    val firstNode = nodes.first()
    val visited = mutableSetOf<Node<NodeData>>()
    val weights = nodes.associateWithTo(mutableMapOf()) { Double.POSITIVE_INFINITY }
    weights[firstNode] = 0.0

    while (visited.size < nodes.size) {
      val current = weights.entries
        .asSequence()
        .filter { it.key !in visited }
        .minByOrNull { it.value }!!.key

    }

    return result
  }

  private fun traverseFrom(
    node: Node<NodeData>,
    weight: Double,
    visitor: (Node<NodeData>, Double) -> Boolean
  ) {
    if (!visitor(node, weight)) return
    edgesByOrigin[node]?.forEach { traverseFrom(it.to, it.weight, visitor) }
  }

  override fun toString() = "Graph(nodes=${_nodes.size}, edges=${edgesByOrigin.values})"

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is Graph<*>) return false

    if (_nodes != other._nodes) return false
    if (_edges != other._edges) return false

    return true
  }

  override fun hashCode(): Int {
    var result = _nodes.hashCode()
    result = 31 * result + _edges.hashCode()
    return result
  }
}

data class Node<T>(val data: T) {
  override fun toString() = data.toString()
}
data class Edge<T>(val from: Node<T>, val to: Node<T>, val weight: Double) {
  override fun toString() = "$from -${if (weight != 0.0) "($weight)-" else ""}> $to"
}