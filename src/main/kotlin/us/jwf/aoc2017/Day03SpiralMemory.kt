package us.jwf.aoc2017

import java.io.Reader
import kotlin.math.abs
import kotlin.math.sqrt
import us.jwf.aoc.Day
import us.jwf.aoc.Point

/**
 * AoC 2017 - Day 3
 */
class Day03SpiralMemory : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    val target = 265149
    val oddSquares = mutableListOf(1)
    var current = 1
    do {
      current += 2
      oddSquares.add(current * current)
    } while (current * current < target)

    if (current * current == target) return oddSquares.size - 1

    val shellStart = oddSquares[oddSquares.size - 2]
    val shellSize = current - 1
    if ((target - shellStart) % shellSize == 0) return oddSquares.size - 1

    val offset = abs((target - shellStart) % shellSize - shellSize / 2)
    return offset + oddSquares.size - 1
  }

  override suspend fun executePart2(input: Reader): Int {
    val target = 265149

    val values = mutableMapOf<Point, Int>()
    var currentPoint = Point(0, 0)
    var index = 1
    var lastValue = 1
    values[currentPoint] = lastValue
    var vector = Point(1, 0)
    do {
      vector = when {
        index.isOddSquare() -> Point(1, 0)
        (index - 1).isOddSquare() -> Point(0, -1)
        index.isCorner() -> {
          when (vector) {
            Point(0, -1) -> Point(-1, 0)
            Point(-1, 0) -> Point(0, 1)
            Point(0, 1) -> Point(1, 0)
            else -> throw IllegalStateException()
          }
        }
        else -> vector
      }
      val nextPoint = currentPoint + vector
      val value = nextPoint.neighbors().sumOf { values[it] ?: 0 }
      values[nextPoint] = value
      lastValue = value
      currentPoint = nextPoint
      index++
    } while (lastValue <= target)
    return lastValue
  }

  fun Int.isCorner(): Boolean {
    var nearestOddSquare = 0
    var i = 1
    while (i * i < this) {
      nearestOddSquare = i * i
      i += 2
    }
    val nearestOddRoot = i - 2
    val stepSize = nearestOddRoot + 1
    return this == nearestOddSquare + stepSize || this == nearestOddSquare + 2*stepSize || this == nearestOddSquare + 3*stepSize
  }

  fun Int.isOddSquare(): Boolean {
    val sqrt = sqrt(this.toDouble())
    return sqrt == sqrt.toInt().toDouble() && sqrt.toInt() % 2 == 1
  }

  operator fun Point.plus(other: Point): Point {
    return Point(x + other.x, y + other.y)
  }

  fun Point.neighbors(): List<Point> {
    val result = mutableListOf<Point>()
    (-1..1).forEach { i ->
      (-1..1).forEach inner@{ j ->
        if (i == j && i == 0) return@inner
        result.add(Point(x + i, y + j))
      }
    }
    return result
  }
}