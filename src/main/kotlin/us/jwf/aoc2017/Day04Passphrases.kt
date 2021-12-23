package us.jwf.aoc2017

import java.io.Reader
import us.jwf.aoc.Day

/**
 * AoC 2017 - Day 4
 */
class Day04Passphrases : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    return input.readLines().count { it.noDuplicates() }
  }

  override suspend fun executePart2(input: Reader): Int {
    return input.readLines().count { it.noAnagrams() }
  }

  fun String.noDuplicates(): Boolean {
    return !split("\\s+".toRegex())
      .asSequence()
      .runningFold(mutableMapOf<String, Int>()) { map, word ->
        map[word] = (map[word] ?: 0) + 1
        map
      }
      .any { it.values.any { v -> v > 1 } }
  }

  fun String.noAnagrams(): Boolean {
    return !split("\\s+".toRegex())
      .asSequence()
      .map { it.toCharArray().sorted().toString() }
      .runningFold(mutableMapOf<String, Int>()) { map, word ->
        map[word] = (map[word] ?: 0) + 1
        map
      }
      .any { it.values.any { v -> v > 1 } }
  }
}
