package us.jwf.aoc2017

import java.io.Reader
import us.jwf.aoc.Day

/**
 * AoC 2017 - Day 5
 */
class Day05TwistyTrampolines : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    val offsets = input.readLines().map { it.toInt() }.toIntArray()
    var position = 0
    var steps = 0
    while (position in offsets.indices) {
      val newPosition = position + offsets[position]
      offsets[position]++
      position = newPosition
      steps++
    }
    return steps
  }

  override suspend fun executePart2(input: Reader): Int {
    val offsets = input.readLines().map { it.toInt() }.toIntArray()
    var position = 0
    var steps = 0
    while (position in offsets.indices) {
      val newPosition = position + offsets[position]
      if (offsets[position] >= 3) offsets[position]-- else offsets[position]++
      position = newPosition
      steps++
    }
    return steps
  }
}