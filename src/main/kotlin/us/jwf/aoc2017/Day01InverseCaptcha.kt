package us.jwf.aoc2017

import java.io.Reader
import us.jwf.aoc.Day

/**
 * AoC 2017 - Day 1
 */
class Day01InverseCaptcha : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    val line = input.readLines().first()
    var sum = 0
    line.forEachIndexed { index, c ->
      if (line[(index + 1) % line.length] == c) sum += c.digitToInt()
    }
    return sum
  }

  override suspend fun executePart2(input: Reader): Int {
    val line = input.readLines().first()
    var sum = 0
    line.forEachIndexed { index, c ->
      if (line[(index + line.length / 2) % line.length] == c) sum += c.digitToInt()
    }
    return sum
  }
}