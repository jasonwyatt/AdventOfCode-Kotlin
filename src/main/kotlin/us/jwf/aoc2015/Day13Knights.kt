package us.jwf.aoc2015

import java.io.Reader
import kotlinx.coroutines.flow.toList
import us.jwf.aoc.Day
import us.jwf.aoc.toMatchFlow

class Day13Knights : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    val names = mutableSetOf<String>()
    val measures = input.toMatchFlow(PATTERN).toList()
      .associate { parts ->
        val sign = if (parts[2] == "gain") 1 else -1
        names += parts[1]
        names += parts[5]
        (parts[1] to parts[5]) to sign * parts[3].toInt()
      }

    return names.permute()
      .maxOf { it.scoreHappiness(measures) }
  }

  override suspend fun executePart2(input: Reader): Int {
    val names = mutableSetOf<String>()
    val measures = input.toMatchFlow(PATTERN).toList()
      .associate { parts ->
        val sign = if (parts[2] == "gain") 1 else -1
        names += parts[1]
        names += parts[5]
        (parts[1] to parts[5]) to sign * parts[3].toInt()
      }
      .toMutableMap()
    names.forEach {
      measures["Me" to it] = 0
      measures[it to "Me"] = 0
    }
    names.add("Me")

    return names.permute()
      .maxOf { it.scoreHappiness(measures) }
  }

  fun Set<String>.permute(): List<List<String>> {
    if (size == 1) return listOf(toList())
    val result = mutableListOf<List<String>>()
    forEach { head ->
      result += (this - head).permute().map { listOf(head) + it }
    }
    return result
  }

  fun List<String>.scoreHappiness(measures: Map<Pair<String, String>, Int>): Int {
    var sum = 0
    forEachIndexed { i, name ->
      val leftNeighbor = if (i > 0) this[i - 1] else last()
      val rightNeighbor = if (i < size - 1) this[i + 1] else first()
      sum += measures[name to leftNeighbor]!! + measures[name to rightNeighbor]!!
    }
    return sum
  }

  companion object {
    val PATTERN = "([A-Za-z]+) would (gain|lose) (\\d+) happiness unit(s)? by sitting next to ([A-Za-z]+)".toRegex()
  }
}