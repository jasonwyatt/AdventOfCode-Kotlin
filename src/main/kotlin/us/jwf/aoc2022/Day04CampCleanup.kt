package us.jwf.aoc2022

import java.io.Reader
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.filter
import us.jwf.aoc.Day
import us.jwf.aoc.toLineFlow

class Day04CampCleanup : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    return input.toLineFlow()
      .filter {
        val ranges = it.split(",")
          .map { rangeStr ->
            val range = rangeStr.split("-").map { end -> end.toInt() }
            range[0]..range[1]
          }
        ranges[0] in ranges[1] || ranges[1] in ranges[0]
      }
      .count()
  }

  override suspend fun executePart2(input: Reader): Int {
    return input.toLineFlow()
      .filter {
        val ranges = it.split(",")
          .map { rangeStr ->
            val range = rangeStr.split("-").map { end -> end.toInt() }
            range[0]..range[1]
          }
        ranges[0].first in ranges[1] || ranges[0].last in ranges[1]
          || ranges[1].first in ranges[0] || ranges[1].last in ranges[0]
      }
      .count()
  }

  operator fun IntRange.contains(other: IntRange): Boolean =
    other.first in this && other.last in this
}