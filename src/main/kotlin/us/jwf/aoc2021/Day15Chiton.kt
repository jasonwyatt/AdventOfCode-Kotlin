package us.jwf.aoc2021

import java.io.Reader
import java.util.PriorityQueue
import us.jwf.aoc.Day

/**
 * AoC 2021 - Day 15
 */
class Day15Chiton : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    val lines = input.readLines()
    val grid = Array(lines.size) { lines[it].map { it.digitToInt() }.toIntArray() }
    return minDistance(grid)
  }

  override suspend fun executePart2(input: Reader): Int {
    val lines = input.readLines()
    val orig = Array(lines.size) { lines[it].map { it.digitToInt() }.toIntArray() }
    val grid = Array(lines.size * 5) { gy ->
      IntArray(lines[0].length * 5) { gx ->
        val oy = gy % lines.size
        val ox = gx % lines[0].length
        val newValue = orig[oy][ox] + gy / lines.size + gx / lines[0].length
        wrapLookup[newValue]!!
      }
    }
    return minDistance(grid)
  }

  fun minDistance(grid: Array<IntArray>): Int {
    val queue =
      PriorityQueue<Pos> { a, b -> a.risk - b.risk }
        .apply { add(Pos(Point(0, 0), 0)) }
    val visited = mutableSetOf(Point(0, 0))

    val target = Point(grid[0].size - 1, grid.size - 1)
    while (queue.isNotEmpty()) {
      val current = queue.poll()
      if (current.point == target) return current.risk

      val currentPoint = current.point
      val (cx, cy) = currentPoint
      if (currentPoint.y + 1 < grid.size) {
        val down = Pos(
          point = Point(cx, cy + 1),
          risk = current.risk + grid[cy + 1][cx]
        )
        if (down.point !in visited) {
          visited.add(down.point)
          queue.add(down)
        }
      }
      if (currentPoint.y - 1 >= 0) {
        val up = Pos(
          point = Point(cx, cy - 1),
          risk = current.risk + grid[cy - 1][cx]
        )
        if (up.point !in visited) {
          visited.add(up.point)
          queue.add(up)
        }
      }
      if (currentPoint.x + 1 < grid[0].size) {
        val right = Pos(
          point = Point(cx + 1, cy),
          risk = current.risk + grid[cy][cx + 1]
        )
        if (right.point !in visited) {
          visited.add(right.point)
          queue.add(right)
        }
      }
      if (currentPoint.x - 1 >= 0) {
        val left = Pos(
          point = Point(cx - 1, cy),
          risk = current.risk + grid[cy][cx - 1]
        )
        if (left.point !in visited) {
          visited.add(left.point)
          queue.add(left)
        }
      }
    }
    return 0
  }

  val wrapLookup = mapOf(
    1 to 1,
    2 to 2,
    3 to 3,
    4 to 4,
    5 to 5,
    6 to 6,
    7 to 7,
    8 to 8,
    9 to 9,
    10 to 1,
    11 to 2,
    12 to 3,
    13 to 4,
    14 to 5,
    15 to 6,
    16 to 7,
    17 to 8,
    18 to 9,
    19 to 1,
    20 to 2,
    21 to 3
  )

  data class Point(val x: Int, val y: Int)
  data class Pos(val point: Point, val risk: Int)
}