package us.jwf.aoc2021

import java.io.Reader
import us.jwf.aoc.Day

/**
 * AoC 2021 - Day 13
 */
class Day13TransparentOrigami : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    val lines = input.readLines()
    val paper = Paper()
    var folding = false
    var folds = 0
    val maxFolds = 1
    lines.forEach {
      if (folding) {
        if (folds < maxFolds) {
          paper.fold(it)
          folds++
        }
      } else if (!it.isEmpty() && !it.isBlank()) {
        paper.add(it)
      } else {
        folding = true
      }
      if (it.isEmpty() || it.isBlank()) folding = true
    }

    return paper.count()
  }

  override suspend fun executePart2(input: Reader): Int {
    val lines = input.readLines()
    val paper = Paper()
    var folding = false
    lines.forEach {
      if (folding) {
        paper.fold(it)
      } else if (!it.isEmpty() && !it.isBlank()) {
        paper.add(it)
      } else {
        folding = true
      }
      if (it.isEmpty() || it.isBlank()) folding = true
    }

    println(paper.toString())

    return paper.count()
  }

  class Paper {
    private var dots = mutableSetOf<Pair<Int, Int>>()
    private val pattern = "fold along (x|y)=(\\d+)".toRegex()

    fun add(line: String) {
      val (x, y) = line.split(",").map { it.toInt() }
      dots.add(x to y)
    }

    fun fold(line: String) {
      val match = pattern.matchEntire(line)!!
      val value = match.groupValues[2].toInt()

      if (match.groupValues[1] == "x") foldX(value) else foldY(value)
    }

    fun foldX(x: Int) {
      val newDots = mutableSetOf<Pair<Int, Int>>()

      dots.forEach { dot ->
        val (dotX, dotY) = dot
        if (dotX < x) {
          newDots.add(dot)
          return@forEach
        } else {
          newDots.add(2 * x - dotX to dotY)
        }
      }

      dots = newDots
    }

    fun foldY(y: Int) {
      val newDots = mutableSetOf<Pair<Int, Int>>()

      dots.forEach { dot ->
        val (dotX, dotY) = dot
        if (dotY < y) {
          newDots.add(dot)
          return@forEach
        } else {
          newDots.add(dotX to 2 * y - dotY)
        }
      }

      dots = newDots
    }

    fun count(): Int = dots.size

    override fun toString(): String {
      val maxX = dots.maxOf { it.first }
      val maxY = dots.maxOf { it.second }
      val output = Array(maxY + 1) { CharArray(maxX + 1) { '.' } }
      dots.forEach { (x, y) -> output[y][x] = 'X' }
      return output.joinToString("\n") { it.joinToString("") }
    }
  }
}