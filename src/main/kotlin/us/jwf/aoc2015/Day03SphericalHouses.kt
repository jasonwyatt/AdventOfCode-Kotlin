package us.jwf.aoc2015

import java.io.Reader
import us.jwf.aoc.Day

class Day03SphericalHouses : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    val directions = input.readLines().joinToString("")
    var current = Position(0, 0)
    val visited = mutableSetOf<Position>()

    directions.forEach {
      visited.add(current)
      current = when (it) {
        '>' -> current.copy(x = current.x + 1)
        '<' -> current.copy(x = current.x - 1)
        '^' -> current.copy(y = current.y + 1)
        'v' -> current.copy(y = current.y - 1)
        else -> current
      }
    }

    return visited.size
  }

  override suspend fun executePart2(input: Reader): Int {
    val directions = input.readLines().joinToString("")
    var santa = Position(0, 0)
    var robosanta = Position(0, 0)
    var turn = 0
    val visited = mutableSetOf(Position(0, 0))

    directions.forEach {
      val current = if (turn % 2 == 0) santa else robosanta

      val update = when (it) {
        '>' -> current.copy(x = current.x + 1)
        '<' -> current.copy(x = current.x - 1)
        '^' -> current.copy(y = current.y + 1)
        'v' -> current.copy(y = current.y - 1)
        else -> current
      }

      if (turn % 2 == 0) {
        santa = update
      } else {
        robosanta = update
      }
      visited.add(update)
      turn++
    }

    return visited.size
  }

  data class Position(val x: Int, val y: Int)
}