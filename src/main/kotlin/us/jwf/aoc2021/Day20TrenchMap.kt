package us.jwf.aoc2021

import java.io.Reader
import us.jwf.aoc.Day

/**
 * AoC 2021 - Day 20
 */
class Day20TrenchMap : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    val lines = input.readLines()
    val algorithm = lines[0].map { it == '#' }.toBooleanArray()

    var image =
      Image.parse(lines.subList(2, lines.size)).also { it.print2() }
        .applyAlgorithm2(algorithm).also { it.print2() }
        .applyAlgorithm2(algorithm).also { it.print2() }

    image = image.trim()

    return image.litPixels.size
  }

  override suspend fun executePart2(input: Reader): Int {
    val lines = input.readLines()
    val algorithm = lines[0].map { it == '#' }.toBooleanArray()

    var image = Image.parse(lines.subList(2, lines.size), 150)
    repeat(25) {
      image = image.applyAlgorithm2(algorithm).applyAlgorithm2(algorithm)
      image = image.trim()

      image.print2()
      println()
    }

    return image.litPixels.size
  }

  data class Pixel(val x: Int, val y: Int)
  data class Image(val litPixels: Set<Pixel>, val unlitPixels: Set<Pixel>, val xRange: IntRange, val yRange: IntRange) {
    fun print() {
      yRange.forEach { y ->
        val line = xRange.map { x -> if (Pixel(x, y) in litPixels) '#' else '.' }
          .joinToString("")
        println(line)
      }
      println()
    }

    fun trim(): Image {
      var minX = Int.MAX_VALUE
      var minY = Int.MAX_VALUE
      var maxX = Int.MIN_VALUE
      var maxY = Int.MIN_VALUE
      litPixels.forEach { (x, y) ->
        minX = minOf(x, minX)
        minY = minOf(y, minY)
        maxX = maxOf(x, maxX)
        maxY = maxOf(y, maxY)
      }
      var i = 0
      var offCount = 0
      while (offCount < 2) {
        if (Pixel(minX + i, minY + i) in litPixels) {
          offCount = 0
        } else {
          offCount++
        }
        i++
      }

      val newLit = mutableSetOf<Pixel>()

      ((minY + i) until maxY - i).forEach { y ->
        ((minX + i) until maxX - i).forEach { x ->
          val pixel = Pixel(x, y)
          if (pixel in litPixels) newLit.add(pixel)
        }
      }

      return Image(newLit, emptySet(), 1..2, 2..3)
    }

    fun print2() {
      var minX = Int.MAX_VALUE
      var minY = Int.MAX_VALUE
      var maxX = Int.MIN_VALUE
      var maxY = Int.MIN_VALUE
      litPixels.forEach { (x, y) ->
        minX = minOf(x, minX)
        minY = minOf(y, minY)
        maxX = maxOf(x, maxX)
        maxY = maxOf(y, maxY)
      }
      (minY..maxY).forEach { y ->
        val line = (minX..maxX).map { x -> if (Pixel(x, y) in litPixels) '#' else '.' }
          .joinToString("")
        println(line)
      }
    }

    fun applyAlgorithm2(algorithm: BooleanArray): Image {
      val newLit = mutableSetOf<Pixel>()

      var minX = Int.MAX_VALUE
      var minY = Int.MAX_VALUE
      var maxX = Int.MIN_VALUE
      var maxY = Int.MIN_VALUE
      litPixels.forEach { (x, y) ->
        minX = minOf(x, minX)
        minY = minOf(y, minY)
        maxX = maxOf(x, maxX)
        maxY = maxOf(y, maxY)
      }

      ((minY - 10)..(maxY + 10)).forEach { y ->
        ((minX - 10)..(maxX + 10)).forEach { x ->
          if (shouldBeLit(x, y, algorithm)) newLit.add(Pixel(x, y))
        }
      }
      return Image(newLit, emptySet(), 0..1, 0..1)
    }

    fun shouldBeLit(x: Int, y: Int, algorithm: BooleanArray): Boolean {
      var neighbors = 0

      (-1..1).forEach { j ->
        (-1..1).forEach { i ->
          val pixel = Pixel(x + i, y + j)
          if (pixel in litPixels) neighbors++
          neighbors = neighbors shl 1
        }
      }
      neighbors = neighbors ushr 1

      return algorithm[neighbors]
    }

    fun applyAlgorithm(algorithm: BooleanArray): Image {
      val newLit = mutableSetOf<Pixel>()
      val newUnlit = mutableSetOf<Pixel>()

      (0..(xRange.last + 1)).forEach { x ->
        val top = Pixel(x, 0)
        val bottom = Pixel(x, yRange.last + 1)
        (if (top in litPixels) newUnlit else newLit).add(top)
        (if (bottom in litPixels) newUnlit else newLit).add(bottom)
      }

      yRange.forEach { y ->
        val left = Pixel(0, y)
        val right = Pixel(xRange.last + 1, y)
        (if (left in litPixels) newUnlit else newLit).add(left)
        (if (right in litPixels) newUnlit else newLit).add(right)
      }

      yRange.forEach { y ->
        xRange.forEach { x ->
          (if (shouldBeLit(x, y, algorithm)) newLit else newUnlit).add(Pixel(x, y))
        }
      }

      return Image(newLit, newUnlit, xRange, yRange)
    }

    companion object {
      fun parse(lines: List<String>, padding: Int = 6): Image {
        val lit = mutableSetOf<Pixel>()
        val unlit = mutableSetOf<Pixel>()
        val height = lines.size
        val width = lines[0].length

        (0 until (height+padding)).forEach { y ->
          (0 until (width+padding)).forEach { x ->
            unlit.add(Pixel(x, y))
          }
        }

        lines.withIndex().forEach { (y, line) ->
          line.withIndex().forEach { (x, value) ->
            if (value == '#') {
              val pixel = Pixel(x + (padding / 2), y + (padding / 2))
              unlit.remove(pixel)
              lit.add(pixel)
            }
          }
        }

        return Image(lit, unlit, 1 until (width+padding - 1), 1 until (height+padding - 1))
      }
    }
  }
}