package us.jwf.aoc

import java.io.Reader

/**
 * Represents a day's goals during the Advent of Code.
 */
interface Day<PartOneResult, PartTwoResult> {
  /**
   * Executes part 1 of the day against the provided [input] and returns
   * the calculated [PartOneResult].
   */
  suspend fun executePart1(input: Reader): PartOneResult

  /**
   * Executes part 2 of the day against the provided [input] and returns
   * the calculated [PartTwoResult].
   */
  suspend fun executePart2(input: Reader): PartTwoResult
}