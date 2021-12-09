package us.jwf.aoc2015

import java.io.Reader
import us.jwf.aoc.Day

/**
 * AoC 2015 - Day 9
 */
class Day09SingleNight : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    val distances = Distances()
    input.readLines()
      .forEach { distances.addLine(it) }

    return distances.minDistance()
  }

  override suspend fun executePart2(input: Reader): Int {
    val distances = Distances()
    input.readLines()
      .forEach { distances.addLine(it) }

    return distances.maxDistance()
  }

  class Distances {
    private val distances = mutableMapOf<Pair<String, String>, Int>()
    private val cities = mutableSetOf<String>()

    fun addLine(line: String) {
      val (first, _, second, _, distStr) = line.trim().split(" ")
      val dist = distStr.toInt()
      distances[first to second] = dist
      distances[second to first] = dist
      cities += first
      cities += second
    }

    fun minDistance(): Int {
      return cities.minOf { minDistance(it, cities - it) }
    }

    fun minDistance(from: String, remaining: Set<String>): Int {
      if (remaining.size == 1) return remaining.toList().let { distances[from to it[0]]!! }

      var minDistance = Int.MAX_VALUE
      remaining.forEach {
        val current = distances[from to it]!!
        val next = minDistance(it, remaining - it)
        minDistance = minOf(minDistance, current + next)
      }
      return minDistance
    }

    fun maxDistance(): Int {
      return cities.maxOf { maxDistance(it, cities - it) }
    }

    fun maxDistance(from: String, remaining: Set<String>): Int {
      if (remaining.size == 1) return remaining.toList().let { distances[from to it[0]]!! }

      var maxDistance = Int.MIN_VALUE
      remaining.forEach {
        val current = distances[from to it]!!
        val next = maxDistance(it, remaining - it)
        maxDistance = maxOf(maxDistance, current + next)
      }
      return maxDistance
    }
  }
}