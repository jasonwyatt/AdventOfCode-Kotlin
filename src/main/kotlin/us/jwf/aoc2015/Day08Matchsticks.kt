package us.jwf.aoc2015

import java.io.Reader
import us.jwf.aoc.Day

/**
 * AoC 2015 - Day 8
 */
class Day08Matchsticks : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    return input.readLines()
      .sumOf { it.length - it.drop(1).dropLast(1).countRenderedCharacters() }
  }

  override suspend fun executePart2(input: Reader): Int {
    return input.readLines()
      .sumOf { it.countEncodedCharacters() - it.length }
  }

  fun String.countEncodedCharacters(): Int {
    var count = 0
    var i = 0
    while (i < length) {
      count += when (this[i]) {
        '"', '\\' -> 2
        else -> 1
      }
      i++
    }
    return count + 2
  }

  fun String.countRenderedCharacters(): Int {
    var count = 0
    var i = 0
    while (i < length) {
      i += when (this[i]) {
        '\\' -> {
          if (this[i + 1] == '\\' || this[i + 1] == '"') {
            2
          } else if (this[i + 1] == 'x') {
            4
          } else {
            throw IllegalArgumentException("oops: $this - $i")
          }
        }
        else -> 1
      }
      count++
    }
    return count
  }

  private val HEX = "abcdef012345789ABCDEF".toSet()
}