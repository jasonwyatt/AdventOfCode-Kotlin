package us.jwf.aoc2021

import java.io.Reader
import java.util.LinkedList
import us.jwf.aoc.Day
import us.jwf.aoc.product

/**
 * AoC 2021 - Day 9
 */
class Day09SmokeBasin : Day<Int, Long> {
  override suspend fun executePart1(input: Reader): Int {
    val heights = input.readLines().map { it.map { it.digitToInt() } }
    var risks = 0

    repeat(heights.size) { i ->
      repeat(heights[i].size) { j ->
        val candidates = mutableListOf<Int>()
        if (i > 0) candidates += heights[i-1][j]
        if (j > 0) candidates += heights[i][j-1]
        if (i < heights.size - 1) candidates += heights[i+1][j]
        if (j < heights[i].size - 1) candidates += heights[i][j+1]

        val height = minOf(candidates.minOrNull()!!)
        if (height > heights[i][j]) risks += (heights[i][j] + 1)
      }
    }
    return risks
  }

  override suspend fun executePart2(input: Reader): Long {
    val heights = input.readLines().map { it.map { it.digitToInt() } }

    val lowPoints = mutableListOf<Pair<Int, Int>>()
    repeat(heights.size) { i ->
      repeat(heights[i].size) { j ->
        val candidates = mutableListOf<Int>()
        if (i > 0) candidates += heights[i-1][j]
        if (j > 0) candidates += heights[i][j-1]
        if (i < heights.size - 1) candidates += heights[i+1][j]
        if (j < heights[i].size - 1) candidates += heights[i][j+1]

        val height = minOf(candidates.minOrNull()!!)
        if (height > heights[i][j]) lowPoints += i to j
      }
    }

    return lowPoints
      .map { point ->
        val visited = mutableSetOf(point)
        val queue = LinkedList<Pair<Int, Int>>().apply { add(point) }
        while (queue.isNotEmpty()) {
          val (i, j) = queue.poll()
          if (i > 0) {
            val point = (i-1) to j
            if (heights[i-1][j] < 9 && point !in visited) {
              visited.add(point)
              queue.add(point)
            }
          }
          if (j > 0) {
            val point = i to (j-1)
            if (heights[i][j-1] < 9 && point !in visited) {
              visited.add(point)
              queue.add(point)
            }
          }
          if (i < heights.size - 1) {
            val point = (i+1) to j
            if (heights[i+1][j] < 9 && point !in visited) {
              visited.add(point)
              queue.add(point)
            }
          }
          if (j < heights[i].size - 1) {
            val point = i to (j+1)
            if (heights[i][j+1] < 9 && point !in visited) {
              visited.add(point)
              queue.add(point)
            }
          }
        }
        visited.size.toLong()
      }
      .sortedDescending()
      .take(3)
      .product()
  }

}