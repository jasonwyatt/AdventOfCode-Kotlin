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
    return input.toIntFlow().toList()
      .windowed(2) { if (it[0] < it[1]) 1 else 0 }
      .sum()
  }

  override suspend fun executePart2(input: Reader): Int {
    return input.toIntFlow().toList()
      .windowed(3) { it.sum() }
      .windowed(2) { if (it[0] < it[1]) 1 else 0 }
      .sum()
  }
}