package us.jwf.aoc

import java.io.Reader

/**
 * Wraps functionality for a given year's Advent of Code for use by the CLI.
 */
interface AdventOfCode {
  /**
   * Executes and prints the result of the provided [day]'s [part] against
   * the given [input].
   */
  suspend fun printResult(day: Int, part: Int, input: Reader)
}

abstract class BaseAdventOfCode(vararg ctors: () -> Day<*, *>) : AdventOfCode {
  private val lookup = ctors.withIndex().associate { (index, ctor) -> (index + 1) to ctor }

  override suspend fun printResult(day: Int, part: Int, input: Reader) {
    val ctor = requireNotNull(lookup[day]) { "Day $day not supported yet." }
    val result = if (part == 1) ctor().executePart1(input) else ctor().executePart2(input)
    println(result.toString())
  }
}