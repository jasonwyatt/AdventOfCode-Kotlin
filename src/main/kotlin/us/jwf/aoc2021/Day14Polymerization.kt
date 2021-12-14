package us.jwf.aoc2021

import java.io.Reader
import java.lang.StringBuilder
import us.jwf.aoc.Day

/**
 * AoC 2021 - Day 14
 */
class Day14Polymerization : Day<Long, Long> {
  override suspend fun executePart1(input: Reader): Long {
    val lines = input.readLines()
    val template = lines[0]
    val rules = lines.drop(2).map { it.split(" -> ") }.associate { it[0] to it[1] }

    var current = template
    repeat(10) {
      val next = StringBuilder()
      next.append(current[0])
      current.windowed(2, 1).forEach {
        rules[it]?.let { i -> next.append(i) }
        next.append(it[1])
      }
      current = next.toString()
    }
    val counts = mutableMapOf<Char, Long>()
    current.forEach { counts[it] = (counts[it] ?: 0) + 1 }
    return counts.maxOf {it.value} - counts.minOf { it.value }
  }

  override suspend fun executePart2(input: Reader): Long {
    val lines = input.readLines()
    val template = lines[0]
    val rules = lines.drop(2).map { it.split(" -> ") }.associate { it[0] to it[1][0] }

    val lookup: MutableMap<Triple<Int, Char, Char>, Map<Char, Long>> = mutableMapOf()

    fun dive(depthLeft: Int, c1: Char, c2: Char): Map<Char, Long> {
      val args = Triple(depthLeft, c1, c2)
      lookup[args]?.let { return it }

      val substr = "$c1$c2"
      if (depthLeft == 0 || "$c1$c2" !in rules) {
        return mutableMapOf<Char, Long>()
          .also {
            it[c2] = (it[c2] ?: 0) + 1
            lookup[args] = it
          }
      }
      val inner = rules[substr]!!
      val left = dive(depthLeft - 1, c1, inner)
      val right = dive(depthLeft - 1, inner, c2)
      return mutableMapOf<Char, Long>()
        .also {
          left.forEach { (c, i) -> it[c] = (it[c] ?: 0) + i }
          right.forEach { (c, i) -> it[c] = (it[c] ?: 0) + i }
          lookup[args] = it
        }
    }

    val counts = template.windowed(2).map { dive(40, it[0], it[1]) }
      .reduce { acc, map ->
        mutableMapOf<Char, Long>().also {
          acc.forEach { c, l -> it[c] = (it[c] ?: 0) + l }
          map.forEach { c, l -> it[c] = (it[c] ?: 0) + l }
        }
      }.toMutableMap()
    counts[template[0]] = counts[template[0]] ?: 0 + 1

    return counts.maxOf {it.value} - counts.minOf { it.value }
  }
}