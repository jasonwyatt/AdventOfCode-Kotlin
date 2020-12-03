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