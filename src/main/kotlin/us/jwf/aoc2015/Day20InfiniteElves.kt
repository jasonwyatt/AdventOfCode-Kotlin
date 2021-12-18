package us.jwf.aoc2015

import java.io.Reader
import us.jwf.aoc.Day

/**
 * AoC 2015 - Day 20
 */
class Day20InfiniteElves : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    val presents = LongArray(1000000)
    var elfNum = 1
    while (elfNum <= presents.size) {
      (elfNum until presents.size).step(elfNum).forEach { i ->
        presents[i] += elfNum * 10L
      }
      elfNum++
    }
    return presents.withIndex().first { (_, v) -> v >= 33100000L }.index
  }

  override suspend fun executePart2(input: Reader): Int {
    val presents = LongArray(1000000)
    var elfNum = 1
    while (elfNum <= presents.size) {
      (elfNum until 50*elfNum).step(elfNum).forEach { i ->
        if (i >= presents.size) return@forEach
        presents[i] += elfNum * 11L
      }
      elfNum++
    }
    return presents.withIndex().first { (_, v) -> v >= 33100000L }.index
  }
}