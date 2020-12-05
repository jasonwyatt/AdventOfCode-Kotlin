package us.jwf.aoc2019

import java.io.Reader
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import us.jwf.aoc.Day
import us.jwf.aoc.graph.Graph
import us.jwf.aoc.toLineFlow

class Day06UniversalOrbitMap : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    val graph = Graph<String>()

    input.toLineFlow()
      .collect {
        val (from, to) = it.split(")")
        graph.addEdge(from, to)
      }
    println("Nodes (${graph.nodes.size}): ${graph.nodes}")
    println(graph.containsCycle())
    return 0
  }

  override suspend fun executePart2(input: Reader): Int {
    TODO("Not yet implemented")
  }
}