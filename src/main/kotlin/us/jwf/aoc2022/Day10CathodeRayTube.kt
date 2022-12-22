package us.jwf.aoc2022

import java.io.Reader
import kotlin.math.abs
import us.jwf.aoc.Day

class Day10CathodeRayTube : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    val registerValues = input.readLines()
      .asSequence()
      .flatMap { line ->
        if (line == "noop") listOf(0)
        else listOf(0, line.substring(5).toInt(10))
      }
      .fold(listOf(1)) { acc, item -> acc + (acc.last() + item) }
      .toList()
    return 20 * registerValues[19] +
      60 * registerValues[59] +
      100 * registerValues[99] +
      140 * registerValues[139] +
      180 * registerValues[179] +
      220 * registerValues[219]
  }

  override suspend fun executePart2(input: Reader): Int {
    val registerValues = input.readLines()
      .asSequence()
      .flatMap { line ->
        if (line == "noop") listOf(0)
        else listOf(0, line.substring(5).toInt(10))
      }
      .fold(listOf(1)) { acc, item -> acc + (acc.last() + item) }
      .toList()
    repeat(240) { i ->
      val b = abs(registerValues[i] - ((i) % 40)) < 2
      print(if (b) "#" else ".")
      if ((i + 1) % 40 == 0) println()
    }
    println()
    return 0
  }
}