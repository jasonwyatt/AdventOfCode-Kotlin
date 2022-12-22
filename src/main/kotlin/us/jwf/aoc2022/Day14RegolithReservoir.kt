package us.jwf.aoc2022

import java.io.Reader
import kotlin.math.max
import kotlin.math.min
import us.jwf.aoc.Day

class Day14RegolithReservoir : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    val field = buildField(1000, 1000)
    input.forEachLine { field.parseAndLoad(it) }
    var i = 0
    while (!field.simulateFrom(500, 0)) {
      i++
    }
    return i
  }

  override suspend fun executePart2(input: Reader): Int {
    val field = buildField(1000, 502)
    val maxY = input.readLines().maxOf { field.parseAndLoad(it) }
    (0..999).forEach { field[maxY + 2][it] = 'X' }
    var i = 0
    while (!field.simulateFrom(500, 0)) {
      i++
    }
    return i + 1
  }

  /**
   * Returns whether or not the sand at the start point goes to the abyss. If not - also updates
   * the receiver with the final location.
   */
  fun Array<CharArray>.simulateFrom(x: Int, y: Int): Boolean {
    var point: Point = Point.Open(x to y)
    do {
      point = step(point.pt)
    } while (point is Point.Open)
    if (point is Point.Blocked) {
      this[point.y][point.x] = 'O'
      return point.x == x && point.y == y
    }
    return true
  }

  fun Array<CharArray>.step(pt: Pair<Int, Int>): Point {
    val (x, y) = pt
    if (y + 1 == size) return Point.Abyss(x to (y + 1))

    return if (this[y + 1][x] == ' ') {
      Point.Open(x to (y + 1))
    } else if (x - 1 >= 0 && this[y + 1][x - 1] == ' ') {
      Point.Open((x - 1) to (y + 1))
    } else if (x + 1 < this[0].size && this[y + 1][x + 1] == ' ') {
      Point.Open((x + 1) to (y + 1))
    } else if (x - 1 < 0) {
      Point.Abyss((x - 1) to y)
    } else if (x + 1 == this[0].size) {
      Point.Abyss((x + 1) to y)
    } else Point.Blocked(x to y)
  }

  sealed class Point {
    abstract val pt: Pair<Int, Int>
    val x: Int
      get() = pt.first
    val y: Int
      get() = pt.second

    data class Blocked(override val pt: Pair<Int, Int>) : Point()
    data class Abyss(override val pt: Pair<Int, Int>) : Point()
    data class Open(override val pt: Pair<Int, Int>) : Point()
  }

  companion object {
    fun buildField(width: Int, height: Int) = Array(height) { CharArray(width) { ' ' } }

    fun Array<CharArray>.print() {
      forEach { println(String(it)) }
    }

    fun Array<CharArray>.parseAndLoad(line: String): Int {
      var maxY = 0
      line.split(" -> ").asSequence()
        .map {
          val parts = it.split(",")
          parts[0].toInt() to parts[1].toInt()
        }
        .windowed(2).map { it[0] to it[1] }
        .forEach { (start, end) ->
          val xRange = min(start.first, end.first) .. max(start.first, end.first)
          val yRange = min(start.second, end.second) .. max(start.second, end.second)
          for (x in xRange) {
            for (y in yRange) {
              this[y][x] = 'X'
              if (y > maxY) {
                maxY = y
              }
            }
          }
        }
      return maxY
    }
  }
}