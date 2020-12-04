package us.jwf.aoc2019

import java.io.Reader
import kotlinx.coroutines.flow.toList
import us.jwf.aoc.Day
import us.jwf.aoc.toIntFlow

/**
 * Day 4 of AoC 2019
 */
class Day04SecureContainer : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    val range = input.toIntFlow("-").toList()

    return range.first().until(range.last())
      .asSequence()
      .filter { it.valid() }
      .count()
  }

  override suspend fun executePart2(input: Reader): Int {
    val range = input.toIntFlow("-").toList()

    return range.first().until(range.last())
      .asSequence()
      .filter { it.validPart2() }
      .count()
  }

  fun Int.valid(): Boolean {
    var value = this
    var foundPair = false
    while (value > 0) {
      val digit = value % 10
      value /= 10
      if (value != 0 && value % 10 > digit) return false
      foundPair = foundPair || (value % 10 == digit)
    }
    return foundPair
  }

  private val emptyCounts = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
  private val counts = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

  fun Int.validPart2(): Boolean {
    var matchDigit = this % 10
    var matchLength = 1
    var value = this / 10
    while (value > 0) {
      if (value != 0 && value % 10 > matchDigit) {
        emptyCounts.copyInto(counts)
        return false
      }
      val digit = value % 10
      if (digit == matchDigit) {
        matchLength++
        counts[digit] = maxOf(counts[digit], matchLength)
      } else {
        matchLength = 1
        matchDigit = digit
        counts[digit] = maxOf(counts[digit], matchLength)
      }
      value /= 10
    }
    return counts.any { it == 2 }.also {
      emptyCounts.copyInto(counts)
    }
  }
}