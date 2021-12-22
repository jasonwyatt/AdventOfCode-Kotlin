package us.jwf.aoc2015

import java.io.Reader
import us.jwf.aoc.Day

/**
 * AoC 2015 - Day 25
 */
class Day25LetItSnow : Day<ULong, ULong> {
  val multiplier = 252533uL
  val divisor = 33554393uL

  override suspend fun executePart1(input: Reader): ULong {
    val row = 2947
    val col = 3029

    val triangleHeight = row + col - 1
    val total = (triangleHeight * (triangleHeight - 1)) / 2 + col - 1

    var value = 20151125uL
    repeat(total) {
      value = (value * multiplier) % divisor
    }

    return value
  }

  override suspend fun executePart2(input: Reader): ULong {
    return 0uL
  }
}