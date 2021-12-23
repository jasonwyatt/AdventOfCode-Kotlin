package us.jwf.aoc2018

import java.io.Reader
import kotlinx.coroutines.flow.toList
import us.jwf.aoc.Day
import us.jwf.aoc.toIntFlow

/**
 * AoC 2018 - Day 1
 */
class Day01ChronalCalibration : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    return input.toIntFlow().toList().sum()
  }

  override suspend fun executePart2(input: Reader): Int {
    val changes = input.toIntFlow().toList()
    val frequenciesSeen = mutableSetOf<Int>()
    var frequency = 0
    while (true) {
      changes.forEach { change ->
        frequency += change
        if (frequency in frequenciesSeen) return frequency
        frequenciesSeen.add(frequency)
      }
    }
  }
}