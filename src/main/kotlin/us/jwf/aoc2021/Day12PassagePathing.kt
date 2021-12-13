package us.jwf.aoc2021

import java.io.Reader
import us.jwf.aoc.Day

/**
 * AoC 2021 - Day 12
 */
class Day12PassagePathing : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    val graph = Graph()

    input.readLines().forEach { graph.addEdge(it) }

    return graph.countPathsToEnd(Cave("start"), mutableSetOf(Cave("start")))
  }

  override suspend fun executePart2(input: Reader): Int {
    val graph = Graph()

    input.readLines().forEach { graph.addEdge(it) }

    return graph.countPathsToEnd2(Cave("start"), mutableSetOf(Cave("start")), false)
  }

  class Graph {
    val neighbors = mutableMapOf<Cave, MutableSet<Cave>>()

    fun addEdge(line: String) {
      val (end1, end2) = line.split("-").map { Cave(it) }

      val end1Neighbors = neighbors[end1] ?: mutableSetOf()
      val end2Neighbors = neighbors[end2] ?: mutableSetOf()
      end1Neighbors.add(end2)
      end2Neighbors.add(end1)

      neighbors[end1] = end1Neighbors
      neighbors[end2] = end2Neighbors
    }

    fun countPathsToEnd(loc: Cave, visitedSmall: MutableSet<Cave>, soFar: List<Cave> = listOf(loc)): Int {
      if (loc.name == "end") {
        println(soFar)
        return 1
      }
      val neighbors = neighbors[loc]!!

      return neighbors.sumOf {
        if (it.isSmall && it in visitedSmall) 0
        else {
          if (it.isSmall) visitedSmall.add(it)
          val count = countPathsToEnd(it, visitedSmall, soFar + it)
          visitedSmall.remove(it)
          count
        }
      }
    }

    fun countPathsToEnd2(
      loc: Cave,
      visitedSmall: MutableSet<Cave>,
      visitedTwice: Boolean,
      soFar: List<Cave> = listOf(loc)
    ): Int {
      if (loc.name == "end") {
        println(soFar)
        return 1
      }
      val neighbors = neighbors[loc]!!

      return neighbors.sumOf {
        if (it.isSmall) {
          if (it.name == "start") 0
          else if (it in visitedSmall) {
            if (!visitedTwice) {
              // second time visiting it. only traverse if nothing else has been visited twice
              countPathsToEnd2(it, visitedSmall, true, soFar + it)
            } else {
              0
            }
          } else {
            // First time visiting it, no problem.
            visitedSmall.add(it)
            val ct = countPathsToEnd2(it, visitedSmall, visitedTwice, soFar + it)
            visitedSmall.remove(it)
            ct
          }
        } else {
          countPathsToEnd2(it, visitedSmall, visitedTwice, soFar + it)
        }
      }
    }
  }

  data class Cave(val name: String) {
    val isSmall: Boolean = name.none { it.isUpperCase() }
    override fun toString(): String {
      return name
    }
  }
  data class Edge(val endPoints: Pair<Cave, Cave>)
}