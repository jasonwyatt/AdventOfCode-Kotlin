package us.jwf.aoc2021

import java.io.Reader
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.flow.toList
import us.jwf.aoc.Day
import us.jwf.aoc.toIntFlow

/**
 * Day1 of AoC 2021
 */
class Day01SonarSweep : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    val depths = input.toIntFlow().toList()

    return depths.withIndex()
      .fold(0) { acc, (i, value) ->
        if (i > 0 && depths[i - 1] < value) acc + 1 else acc
      }
  }

  override suspend fun executePart2(input: Reader): Int {
    val depths = input.toIntFlow().toList()
    var depthWindows: List<Int>

    return depths.withIndex()
      .fold(emptyList<Int>()) { acc, (i, value) ->
        if (i > 1) acc + (value + depths[i - 1] + depths[i - 2]) else acc
      }
      .also { depthWindows = it }
      .withIndex()
      .fold(0) { acc, (i, value) ->
        if (i > 0 && depthWindows[i - 1] < value) acc + 1 else acc
      }
  }
}