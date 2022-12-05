package us.jwf.aoc2022

import java.io.Reader
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.map
import us.jwf.aoc.Day
import us.jwf.aoc.toLineFlow

class Day03Rucksack : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    return input.toLineFlow()
      .map { ruck ->
        val left = (0 until ( ruck.length/2 ))
          .fold(setOf<Char>()) { acc, idx -> acc + ruck[idx] }
        val dupe = ((ruck.length / 2) until ruck.length).find { ruck[it] in left }
        ruck[dupe!!]
      }
      .fold(0) { acc, value -> acc + (if (value >= 'a') value - 'a' + 1 else value - 'A' + 27) }
  }

  override suspend fun executePart2(input: Reader): Int {
    return input.readLines()
      .windowed(3, 3) { group ->
        val counts = IntArray(256)
        group.forEachIndexed { elfIdx, ruck ->
          ruck.forEach { c ->
            counts[c.code] = counts[c.code] or (1 shl elfIdx)
            if (counts[c.code] == 0b111) return@windowed c
          }
        }
        throw IllegalArgumentException("Bad input")
      }
      .fold(0) { acc, value -> acc + (if (value >= 'a') value - 'a' + 1 else value - 'A' + 27) }
  }
}