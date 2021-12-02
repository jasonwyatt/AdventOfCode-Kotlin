package us.jwf.aoc2021

import java.io.Reader
import kotlinx.coroutines.flow.fold
import us.jwf.aoc.Day
import us.jwf.aoc.toLineFlow
import us.jwf.aoc.toMatchFlow

/**
 * AoC 2021 Day 2
 */
class Day02Dive : Day<Long, Long> {
  private val pattern = "(forward|up|down) (\\d+)".toRegex()

  override suspend fun executePart1(input: Reader): Long {
    val pos = input.toMatchFlow(pattern)
      .fold(0L to 0L) { acc, match ->
        when (match[1]) {
          "forward" -> acc.copy(first = acc.first + match[2].toLong())
          "up" -> acc.copy(second = acc.second - match[2].toLong())
          "down" -> acc.copy(second = acc.second + match[2].toLong())
          else -> acc
        }
      }

    return pos.first * pos.second
  }

  override suspend fun executePart2(input: Reader): Long {
    val pos = input.toMatchFlow(pattern)
      .fold(Loc(0, 0, 0)) { acc, match ->
        when (match[1]) {
          "forward" -> acc.copy(x = acc.x + match[2].toLong(), y = acc.y + acc.aim * match[2].toLong())
          "up" -> acc.copy(aim = acc.aim - match[2].toLong())
          "down" -> acc.copy(aim = acc.aim + match[2].toLong())
          else -> acc
        }
      }
    return pos.x * pos.y
  }

  data class Loc(val x: Long, val y: Long, val aim: Long)
}