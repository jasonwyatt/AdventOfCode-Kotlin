package us.jwf.aoc2015

import java.io.Reader
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toSet
import kotlinx.coroutines.flow.withIndex
import us.jwf.aoc.Day
import us.jwf.aoc.toIntFlow

/**
 * AoC 2015 - Day 17
 */
class Day17NoSuchThing : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    val sizes = input.toIntFlow().withIndex().map { v -> v.index to v.value }.toSet()

    val cache = mutableMapOf<Pair<Int, Set<Pair<Int, Int>>>, Int>()
    fun count(liters: Int, remaining: Set<Pair<Int, Int>>): Int {
      val args = liters to remaining
      return (cache[args]?.let { 0 }
        ?: if (liters == 0) 1
        else if (liters < 0) 0
        else {
          remaining.sumOf { idAndSize ->
            val size = idAndSize.second
            count(liters - size, remaining - idAndSize)
          }
        }).also { cache[args] = it }
    }

    return count(150, sizes)
  }

  override suspend fun executePart2(input: Reader): Int {
    val sizes = input.toIntFlow().withIndex().map { v -> v.index to v.value }.toSet()

    val cache = mutableMapOf<Pair<Int, Set<Pair<Int, Int>>>, Int>()
    val answers = mutableSetOf<Set<Pair<Int, Int>>>()
    fun count(liters: Int, remaining: Set<Pair<Int, Int>>, soFar: Set<Pair<Int, Int>>): Int {
      val args = liters to remaining
      return (cache[args]?.let { 0 }
        ?: if (liters == 0) 1.also { answers.add(soFar) }
        else if (liters < 0) 0
        else {
          remaining.sumOf { idAndSize ->
            val size = idAndSize.second
            count(liters - size, remaining - idAndSize, soFar + idAndSize)
          }
        }).also { cache[args] = it }
    }

    count(150, sizes, emptySet())

    val minSize = answers.minOf { it.size }
    return answers.count { it.size == minSize }
  }
}