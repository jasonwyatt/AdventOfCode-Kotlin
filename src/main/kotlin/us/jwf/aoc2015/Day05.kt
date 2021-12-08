package us.jwf.aoc2015

import java.io.Reader
import kotlinx.coroutines.flow.count
import us.jwf.aoc.Day
import us.jwf.aoc.toLineFlow

class Day05 : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    return input.toLineFlow().count { println(); it.isNice() }
  }

  override suspend fun executePart2(input: Reader): Int {
    TODO("Not yet implemented")
  }

  fun String.isNice(): Boolean {
    return hasThreeVowels() && hasDouble() && doesntHaveBlocklist()
  }

  fun String.hasThreeVowels(): Boolean {
    return (count { it in setOf('a', 'e', 'i', 'o', 'u') } >= 3)
  }

  fun String.hasDouble(): Boolean {
    return windowed(2).any { it[0] == it[1] }
  }

  fun String.doesntHaveBlocklist(): Boolean {
    return windowed(2, 1)
      .none { it in setOf("ab", "cd", "pq", "xy") }
  }
}