package us.jwf.aoc2022

import java.io.Reader
import kotlin.math.sqrt
import us.jwf.aoc.Day

class Day09RopeBridge : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    var head = Vec(0, 0)
    var tail = Vec(0, 0)
    val tailPositions = mutableSetOf<Vec>()
    tailPositions.add(tail)

    input.readLines()
      .flatMap { parseHeadMovements(it) }
      .forEach { headMovement ->
        head += headMovement
        tail = drag(head, tail)
        tailPositions.add(tail)
      }
    return tailPositions.size
  }

  override suspend fun executePart2(input: Reader): Int {
    val rope = Rope(Array(10) { Vec(0, 0) })
    input.readLines()
      .asSequence()
      .flatMap { parseHeadMovements(it) }
      .forEach { headMovement -> rope.moveHead(headMovement) }
    return rope.tailPositions.size
  }

  @Suppress("ArrayInDataClass")
  data class Rope(val pieces: Array<Vec>) {
    val tailPositions = mutableSetOf<Vec>()

    init {
      tailPositions += pieces.last()
    }
    var i = 0

    fun moveHead(vec: Vec) {
      pieces[0] += vec
      (1 until pieces.size).forEach { i ->
        pieces[i] = drag(pieces[i - 1], pieces[i])
      }
      tailPositions += pieces.last()
    }

    override fun toString(): String = "[${pieces.joinToString(", ")}]"
  }

  data class Vec(val x: Int, val y: Int) {
    val mag: Double
      get() = sqrt(x.toDouble() * x + y * y)

    operator fun minus(other: Vec): Vec = Vec(x - other.x, y - other.y)
    operator fun plus(other: Vec): Vec = Vec(x + other.x, y + other.y)

    override fun toString(): String = "[$x, $y]"
  }

  companion object {
    val sqrt2 = sqrt(2.0)
    fun drag(headPos: Vec, tailPos: Vec): Vec {
      val vec = headPos - tailPos
      if (vec.mag <= sqrt2 + 0.00001) return tailPos

      val offset = when {
        vec.x == 1 && vec.y == 2 || vec.x == 2 && vec.y == 1 -> Vec(1, 1)
        vec.x == -1 && vec.y == 2 || vec.x == -2 && vec.y == 1 -> Vec(-1, 1)
        vec.x == 1 && vec.y == -2 || vec.x == 2 && vec.y == -1-> Vec(1, -1)
        vec.x == -1 && vec.y == -2 || vec.x == -2 && vec.y == -1 -> Vec(-1, -1)
        vec.x == 2 && vec.y == 2 -> Vec(1, 1)
        vec.x == -2 && vec.y == 2 -> Vec(-1, 1)
        vec.x == 2 && vec.y == -2 -> Vec(1, -1)
        vec.x == -2 && vec.y == -2 -> Vec(-1, -1)
        vec.x == 2 -> Vec(1, 0)
        vec.y == 2 -> Vec(0, 1)
        vec.x == -2 -> Vec(-1, 0)
        vec.y == -2 -> Vec(0, -1)
        else -> throw IllegalArgumentException("Invalid vector: $vec")
      }
      return tailPos + offset
    }

    fun parseHeadMovements(line: String): List<Vec> {
      val parts = line.split(" ")
      val dir = when (parts[0]) {
        "R" -> Vec(1, 0)
        "L" -> Vec(-1, 0)
        "U" -> Vec(0, 1)
        "D" -> Vec(0, -1)
        else -> throw IllegalArgumentException("Bad line: $line")
      }
      val times = parts[1].toInt(10)

      val res = mutableListOf<Vec>()
      repeat(times) { res.add(dir) }
      return res
    }
  }
}