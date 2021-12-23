package us.jwf.aoc2018

import java.io.Reader
import us.jwf.aoc.Day

/**
 * AoC 2018 - Day 3
 */
class Day03NoMatter : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    val claims = input.readLines().map { Claim.parse(it) }
    val counts = mutableMapOf<Point, Int>()

    claims.forEach {
      it.points.forEach { point ->
        counts[point] = (counts[point] ?: 0) + 1
      }
    }

    return counts.values.count { count -> count > 1 }
  }

  override suspend fun executePart2(input: Reader): Int {
    val claims = input.readLines().map { Claim.parse(it) }

    claims.indices.forEach { i ->
      val a = claims[i]
      val safe = claims.indices.all { j ->
        if (i == j) return@all true
        val b = claims[j]
        !(a overlaps b)
      }
      if (safe) return a.id
    }
    return 0
  }

  data class Point(val x: Int, val y: Int)

  data class Claim(val id: Int, val x: Int, val y: Int, val width: Int, val height: Int) {
    val xRange = x until x + width
    val yRange = y until y + height

    val points: Sequence<Point> = sequence {
      (x until (x + width)).forEach { i ->
        (y until (y + height)).forEach { j ->
          yield(Point(i, j))
        }
      }
    }

    infix fun overlaps(other: Claim): Boolean {
      return (x in other.xRange || other.x in xRange) && (y in other.yRange || other.y in yRange)
    }

    companion object {
      val pattern = "#(\\d+) @ (\\d+),(\\d+): (\\d+)x(\\d+)".toRegex()
      fun parse(raw: String): Claim {
        val match = pattern.matchEntire(raw)!!.groupValues
        return Claim(
          id = match[1].toInt(),
          x = match[2].toInt(),
          y = match[3].toInt(),
          width = match[4].toInt(),
          height = match[5].toInt()
        )
      }
    }
  }
}