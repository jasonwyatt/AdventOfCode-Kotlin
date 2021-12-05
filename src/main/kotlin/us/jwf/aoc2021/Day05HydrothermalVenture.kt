package us.jwf.aoc2021

import java.io.Reader
import kotlinx.coroutines.flow.fold
import us.jwf.aoc.Day
import us.jwf.aoc.toMatchFlow

/**
 * AoC 2021 - Day 5
 */
class Day05HydrothermalVenture : Day<Int, Int> {
  private val PATTERN = "((\\d+),(\\d+)) -> ((\\d+),(\\d+))".toRegex()

  override suspend fun executePart1(input: Reader): Int {
    return input.toMatchFlow(PATTERN)
      .fold(mutableMapOf<Point, Int>()) { acc, match ->
        val seg =
          Seg(
            Point(match[2].toInt(), match[3].toInt()),
            Point(match[5].toInt(), match[6].toInt())
          )
        seg.allPoints.forEach { acc[it] = (acc[it] ?: 0) + 1 }
        acc
      }
      .count { (_, visits) -> visits >= 2 }
  }

  override suspend fun executePart2(input: Reader): Int {
    return input.toMatchFlow(PATTERN)
      .fold(mutableMapOf<Point, Int>()) { acc, match ->
        val seg =
          Seg(
            Point(match[2].toInt(), match[3].toInt()),
            Point(match[5].toInt(), match[6].toInt())
          )
        seg.allPoints2.forEach { acc[it] = (acc[it] ?: 0) + 1 }
        acc
      }
      .count { (_, visits) -> visits >= 2 }
  }

  data class Point(val x: Int, val y: Int)
  data class Seg(val start: Point, val end: Point) {
    val allPoints: List<Point>
      get() {
        return if (start.x == end.x) {
          // vertical
          val startY = minOf(start.y, end.y)
          val endY = maxOf(start.y, end.y)
          (startY..endY).map { Point(start.x, it) }
        } else if (start.y == end.y) {
          // horizontal
          val startX = minOf(start.x, end.x)
          val endX = maxOf(start.x, end.x)
          (startX..endX).map { Point(it, start.y) }
        } else emptyList()
      }

    val allPoints2: List<Point>
      get() = sequence {
        var current = start
        yield(current)
        val vector =
          Point(
            maxOf(-1, minOf(1, end.x - start.x)),
            maxOf(-1, minOf(1, end.y - start.y))
          )
        while (current != end) {
          current = Point(current.x + vector.x, current.y + vector.y)
          yield(current)
        }
      }.toList()
  }
}