package us.jwf.aoc2017

import java.io.Reader
import us.jwf.aoc.Day

/**
 * AoC 2017 - Day 6
 */
class Day06MemoryReallocation : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    var banks = input.readLines().flatMap { it.split("\\s+".toRegex()).map(String::toInt) }
    val seenBanks = mutableSetOf<List<Int>>()

    var ops = 0
    while (banks !in seenBanks) {
      seenBanks.add(banks)
      banks = nextState(banks)
      ops++
    }
    return ops
  }

  override suspend fun executePart2(input: Reader): Int {
    var banks = input.readLines().flatMap { it.split("\\s+".toRegex()).map(String::toInt) }
    val seenBanks = mutableSetOf<List<Int>>()

    var ops = 0
    while (banks !in seenBanks) {
      seenBanks.add(banks)
      banks = nextState(banks)
      ops++
    }
    val target = banks
    ops = 0
    do {
      seenBanks.add(banks)
      banks = nextState(banks)
      ops++
    } while (banks != target)
    return ops
  }

  fun nextState(banks: List<Int>): List<Int> {
    val newBanks = banks.toMutableList()
    var maxIndex = 0
    var maxValue = banks[0]
    banks.forEachIndexed { i, v ->
      if (v > maxValue) {
        maxIndex = i
        maxValue = v
      }
    }
    newBanks[maxIndex] = 0
    newBanks.indices.forEach { newBanks[it] += maxValue / banks.size }
    val remainder = maxValue % banks.size
    repeat(remainder) { i -> newBanks[(maxIndex + 1 + i) % banks.size]++ }
    return newBanks.toList()
  }
}