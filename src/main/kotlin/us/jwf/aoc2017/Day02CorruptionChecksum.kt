package us.jwf.aoc2017

import java.io.Reader
import us.jwf.aoc.Day

/**
 * AoC 2017 - Day 2
 */
class Day02CorruptionChecksum : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    return input.readLines()
      .map {
        val row = it.split("\t")
          .fold(Int.MAX_VALUE to Int.MIN_VALUE) { acc, num ->
            val intVal = num.toInt()
            minOf(acc.first, intVal) to maxOf(acc.second, intVal)
          }
        row.second - row.first
      }
      .sum()
  }

  override suspend fun executePart2(input: Reader): Int {
    return input.readLines()
      .map {
        val row = it.split("\t").map(String::toInt)
        row.forEachIndexed { i, item ->
          ((i + 1) until row.size).forEach { j ->
            val second = row[j]
            if (item % second == 0) {
              return@map item / second
            }
            if (second % item == 0) {
              return@map second / item
            }
          }
        }
        0
      }
      .sum()
  }
}
