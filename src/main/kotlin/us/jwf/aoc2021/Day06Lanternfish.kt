package us.jwf.aoc2021

import java.io.Reader
import us.jwf.aoc.Day

/**
 * AoC 2021 - Day 6
 */
class Day06Lanternfish : Day<Int, Long> {
  override suspend fun executePart1(input: Reader): Int {
    var counts = IntArray(9) { 0 }
    var next = IntArray(9) { 0 }
    var temp: IntArray
    input.readLines().joinToString("")
      .split(",")
      .map { it.toInt() }
      .forEach { counts[it]++ }

    repeat(80) {
      next[8] = counts[0]
      next[7] = counts[8]
      next[6] = counts[0] + counts[7]
      next[5] = counts[6]
      next[4] = counts[5]
      next[3] = counts[4]
      next[2] = counts[3]
      next[1] = counts[2]
      next[0] = counts[1]
      temp = next
      next = counts
      counts = temp
    }
    return counts.sum()
  }

  override suspend fun executePart2(input: Reader): Long {
    var counts = LongArray(9) { 0 }
    var next = LongArray(9) { 0 }
    var temp: LongArray
    input.readLines().joinToString("")
      .split(",")
      .map { it.toLong() }
      .forEach { counts[it.toInt()] += 1L }

    repeat(256) {
      next[8] = counts[0]
      next[7] = counts[8]
      next[6] = counts[0] + counts[7]
      next[5] = counts[6]
      next[4] = counts[5]
      next[3] = counts[4]
      next[2] = counts[3]
      next[1] = counts[2]
      next[0] = counts[1]
      temp = next
      next = counts
      counts = temp
    }
    return counts.sum()
  }
}