package us.jwf.aoc2019

import java.io.Reader
import kotlinx.coroutines.flow.collect
import us.jwf.aoc.Day
import us.jwf.aoc.graph.Graph
import us.jwf.aoc.graph.Node
import us.jwf.aoc.toLineFlow

class Day06UniversalOrbitMap : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    val graph = Graph<String>()
    input.toLineFlow()
      .collect {
        val (from, to) = it.split(")")
        graph.addEdge(from, to)
      }

    val depths = mutableMapOf<String, Int>()
    fun traverse(node: Node<String>, depthSoFar: Int) {
      depths[node.data] = depthSoFar
      val edges = graph.edgesForNode(node)
      edges.forEach { traverse(it.to, depthSoFar + 1) }
    }
    traverse(Node("COM"), 0)
    return depths.values.sum()
  }

  override suspend fun executePart2(input: Reader): Int {
    val graph = Graph<String>()
    input.toLineFlow()
      .collect {
        val (from, to) = it.split(")")
        graph.addEdge(from, to)
      }
    val stack = mutableListOf<String>()
    var youStack: List<String>? = null
    var sanStack: List<String>? = null

    fun traverse(node: Node<String>) {
      if (youStack != null && sanStack != null) return
      stack.add(node.data)
      if (node.data == "YOU") {
        youStack = stack.toList()
        stack.removeLast()
        return
      } else if (node.data == "SAN") {
        sanStack = stack.toList()
        stack.removeLast()
        return
      }
      graph.edgesForNode(node).forEach { traverse(it.to) }
      stack.removeLast()
    }
    traverse(Node("COM"))
    var i = 0
    while (youStack?.get(i) == sanStack?.get(i)) { i++ }
    return (youStack!!.size - i - 1) + (sanStack!!.size - i - 1)
  }
}