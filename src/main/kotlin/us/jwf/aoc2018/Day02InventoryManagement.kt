package us.jwf.aoc2018

import java.io.Reader
import us.jwf.aoc.Day

/**
 * AoC 2018 - Day 2
 */
class Day02InventoryManagement : Day<Int, String> {
  override suspend fun executePart1(input: Reader): Int {
    val counts = input.readLines().map { count(it) }
    val has2 = counts.count { c -> c.any { it == 2 } }
    val has3 = counts.count { c -> c.any { it == 3 } }
    return has2 * has3
  }

  override suspend fun executePart2(input: Reader): String {
    val lines = input.readLines()

    (0 until (lines.size - 1)).forEach { i ->
      (i until lines.size).forEach { j ->
        val a = lines[i]
        val b = lines[j]

        val matching =
          a.zip(b).mapNotNull { (ac, bc) -> if (ac == bc) ac else null }.joinToString("")
        if (matching.length == a.length - 1) return matching
      }
    }
    return ""
  }

  fun count(str: String): IntArray {
    val result = IntArray(26) { 0 }
    str.forEach { result[it - 'a']++ }
    return result
  }
}