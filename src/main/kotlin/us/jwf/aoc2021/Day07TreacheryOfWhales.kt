package us.jwf.aoc2021

import java.io.Reader
import kotlin.math.abs
import kotlinx.coroutines.flow.toList
import us.jwf.aoc.Day
import us.jwf.aoc.toIntFlow

/**
 * AoC 2021 - Day 7
 */
class Day07TreacheryOfWhales : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    val locations = input.toIntFlow(",").toList()

    val min = locations.minOf { it }
    val max = locations.maxOf { it }

    var minFuel = Int.MAX_VALUE
    (min..max).forEach { chosen ->
      minFuel = minOf(minFuel, locations.sumOf { abs(chosen - it) })
    }
    return minFuel
  }

  override suspend fun executePart2(input: Reader): Int {
    val locations = input.toIntFlow(",").toList()

    val min = locations.minOf { it }
    val max = locations.maxOf { it }

    var minFuel = Int.MAX_VALUE
    (min..max).forEach { chosen ->
      val totalFuel = locations.sumOf {
        val n = abs(chosen - it)
        ((n + 1) * n) / 2
      }
      minFuel = minOf(minFuel, totalFuel)
    }
    return minFuel
  }
}