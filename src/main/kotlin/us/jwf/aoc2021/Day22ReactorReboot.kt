package us.jwf.aoc2021

import java.io.Reader
import us.jwf.aoc.Day

/**
 * AoC 2021 - Day 22
 */
@Suppress("ReplaceRangeToWithUntil")
class Day22ReactorReboot : Day<Int, ULong> {
  override suspend fun executePart1(input: Reader): Int {
    val litLights = mutableSetOf<Point>()

    input.readLines()
      .forEach { line ->
        if (line.startsWith("on ")) {
          val cuboid = Cuboid.parse(line.substring(3))
          cuboid.allValidCoords.forEach { litLights.add(it) }
        } else {
          val cuboid = Cuboid.parse(line.substring(4))
          cuboid.allValidCoords.forEach { litLights.remove(it) }
        }
      }

    return litLights.size
  }

  override suspend fun executePart2(input: Reader): ULong {
    var litCuboids = setOf<Cuboid>()

    input.readLines()
      .forEach { line ->
        val (command, raw) = line.split(" ")
        val cuboid = Cuboid.parse(raw)
        val newCuboids = mutableSetOf<Cuboid>()

        litCuboids.forEach { old -> newCuboids += old - cuboid }
        if (command == "on") newCuboids += cuboid

        litCuboids = newCuboids
      }

    return litCuboids.sumOf { it.uLongSize }
  }

  data class Point(val x: Int, val y: Int, val z: Int)

  data class Cuboid(val x: IntRange, val y: IntRange, val z: IntRange) {
    val size: Int = (x.last - x.first + 1) * (y.last - y.first + 1) * (z.last - z.first + 1)
    val uLongSize: ULong =
      (x.last - x.first + 1).toULong() * (y.last - y.first + 1).toULong() * (z.last - z.first + 1).toULong()

    val allValidCoords: Iterator<Point> = iterator {
      x.forEach x@{ i ->
        if (i !in -50..50) return@x
        y.forEach y@{ j ->
          if (j !in -50..50) return@y
          z.forEach z@{ k ->
            if (k !in -50..50) return@z
            yield(Point(i, j, k))
          }
        }
      }
    }

    infix fun overlaps(other: Cuboid): Boolean {
      fun overlap(a: Cuboid, b: Cuboid): Boolean {
        return (a.x.first in b.x || a.x.last in b.x) &&
          (a.y.first in b.y || a.y.last in b.y) &&
          (a.z.first in b.z || a.z.last in b.z)
      }
      return overlap(this, other) || overlap(other, this)
    }

    operator fun minus(other: Cuboid): List<Cuboid> {
      if (other.x.last < x.first || x.last < other.x.first || other.y.last < y.first || y.last < other.y.first || other.z.last < z.first || z.last < other.z.first) {
        return listOf(this)
      }
      val xBelow = (minOf(x.first, other.x.first)..maxOf(x.first - 1, other.x.first - 1))
      val xMid = (maxOf(x.first, other.x.first))..(minOf(x.last, other.x.last))
      val xAbove = (minOf(x.last + 1, other.x.last + 1)..maxOf(x.last, other.x.last))

      val yBelow = (minOf(y.first, other.y.first)..maxOf(y.first - 1, other.y.first - 1))
      val yMid = (maxOf(y.first, other.y.first))..(minOf(y.last, other.y.last))
      val yAbove = (minOf(y.last + 1, other.y.last + 1)..maxOf(y.last, other.y.last))

      val zBelow = (minOf(z.first, other.z.first)..maxOf(z.first - 1, other.z.first - 1))
      val zMid = (maxOf(z.first, other.z.first))..(minOf(z.last, other.z.last))
      val zAbove = (minOf(z.last + 1, other.z.last + 1)..maxOf(z.last, other.z.last))

      val xs = listOf(xBelow, xAbove, xMid).filter { !it.isEmpty() }
      val ys = listOf(yBelow, yAbove, yMid).filter { !it.isEmpty() }
      val zs = listOf(zBelow, zAbove, zMid).filter { !it.isEmpty() }
      val remaining = mutableListOf<Cuboid>()
      xs.forEach { newX ->
        ys.forEach { newY ->
          zs.forEach z@{ newZ ->
            if (newX.isEmpty() || newY.isEmpty() || newZ.isEmpty()) return@z
            val c = Cuboid(newX, newY, newZ)
            if (c overlaps this && !(c overlaps other)) remaining.add(c)
          }
        }
      }
      return remaining
    }

    companion object {
      fun parse(raw: String): Cuboid {
        val (xRaw, yRaw, zRaw) = raw.split(",")
        val xRangeRaw = xRaw.substring(2).split("..").map { it.toInt() }
        val yRangeRaw = yRaw.substring(2).split("..").map { it.toInt() }
        val zRangeRaw = zRaw.substring(2).split("..").map { it.toInt() }
        return Cuboid(xRangeRaw[0]..xRangeRaw[1], yRangeRaw[0]..yRangeRaw[1], zRangeRaw[0]..zRangeRaw[1])
      }
    }
  }
}