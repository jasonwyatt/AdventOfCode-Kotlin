package us.jwf.aoc2015

import java.io.Reader
import kotlin.math.abs
import kotlinx.coroutines.flow.count
import us.jwf.aoc.Day
import us.jwf.aoc.toLineFlow

class Day05InternElves : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    return input.toLineFlow().count { it.isNice() }
  }

  override suspend fun executePart2(input: Reader): Int {
    return input.toLineFlow().count { it.isNice2() }
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

  fun String.isNice2(): Boolean {
    return hasNonOverlappingRepeat() && hasGappedRepeat()
  }

  fun String.hasNonOverlappingRepeat(): Boolean {
    val pairs = mutableMapOf<String, Set<Int>>()
    (0 until (length - 1)).forEach { i ->
      val pair = substring(i, i + 2)
      pairs[pair] = (pairs[pair] ?: emptySet()) + i
    }
    return pairs.any { (_, indices) ->
      if (indices.size > 2) return@any true
      if (indices.size <= 1) return@any false
      val indexList = indices.toList()
      abs(indexList[0] - indexList[1]) > 1
    }
  }

  fun String.hasGappedRepeat(): Boolean {
    (0 until (length - 2)).forEach { i ->
      if (get(i) == get(i + 2)) return true
    }
    return false
  }
}