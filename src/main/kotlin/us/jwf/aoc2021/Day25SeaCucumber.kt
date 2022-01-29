package us.jwf.aoc2021

import java.io.Reader
import us.jwf.aoc.Day

/**
 * AoC 2021 - Day 25
 */
class Day25SeaCucumber : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    var width = 0
    var height = 0
    val seaCucumbers =
      input.readLines().withIndex()
        .flatMap { (y, line) ->
          line.withIndex().mapNotNull { (x, c) ->
            when (c) {
              '>' -> SeaCucumber.EastMover(x, y)
              'v' -> SeaCucumber.SouthMover(x, y)
              else -> null
            }.also { width = maxOf(width, x + 1) }
          }.also { height = y + 1 }
        }

    println("$width")
    println("$height")

    var current: MutableSet<SeaCucumber>
    var next: MutableSet<SeaCucumber> = seaCucumbers.toMutableSet()
    var i = 0
    next.print(width, height)
    println()
    do {
      val start = next

      // East movers first
      val first = mutableSetOf<SeaCucumber>()
      start
        .forEach {
          if (it is SeaCucumber.EastMover) {
            first.add(it.move(start, width, height))
          } else {
            first.add(it)
          }
        }
      next = mutableSetOf()
      // Now South Movers
      first
        .forEach {
          if (it is SeaCucumber.SouthMover) {
            next.add(it.move(first, width, height))
          } else {
            next.add(it)
          }
        }
      i++
      next.print(width, height)
      println()
    } while (next != start)
    return i
  }

  fun Set<SeaCucumber>.print(width: Int, height: Int) {
    (0 until height).forEach { y ->
      (0 until width)
        .map { x ->
          when {
            SeaCucumber.EastMover(x, y) in this -> '>'
            SeaCucumber.SouthMover(x, y) in this -> 'v'
            else -> '.'
          }
        }
        .joinToString("").also { println(it) }
    }
  }

  override suspend fun executePart2(input: Reader): Int {
    TODO("Not yet implemented")
  }

  sealed interface SeaCucumber {
    fun move(state: Set<SeaCucumber>, width: Int, height: Int): SeaCucumber

    data class EastMover(val x: Int, val y: Int) : SeaCucumber {
      override fun move(state: Set<SeaCucumber>, width: Int, height: Int): SeaCucumber {
        if (state.has((x + 1) % width, y)) return this
        return EastMover((x + 1) % width, y)
      }
    }

    data class SouthMover(val x: Int, val y: Int) : SeaCucumber {
      override fun move(state: Set<SeaCucumber>, width: Int, height: Int): SeaCucumber {
        if (state.has(x, (y + 1) % height)) return this
        return SouthMover(x, (y + 1) % height)
      }
    }

    companion object {
      fun Set<SeaCucumber>.has(x: Int, y: Int): Boolean {
        return EastMover(x, y) in this || SouthMover(x, y) in this
      }
    }
  }
}