package us.jwf.aoc2015

import java.io.Reader
import java.util.PriorityQueue
import kotlinx.coroutines.flow.toList
import us.jwf.aoc.Day
import us.jwf.aoc.product
import us.jwf.aoc.toIntFlow

/**
 * AoC 2015 - Day 24
 */
class Day24HangsInTheBalance : Day<ULong, Int> {
  override suspend fun executePart1(input: Reader): ULong {
    val weights = input.toIntFlow().toList()
    val total = weights.sum()
    val target = total / 3

    val start = Arrangement(weights.toSet())
    val queue = PriorityQueue<Arrangement> { a, b -> a compareTo b }
      .also { it.offer(start) }
    val visited = mutableSetOf(start)

    val results = mutableSetOf<ULong>()
    while (queue.isNotEmpty()) {
      val next = queue.poll()

      if (next.group1Sum == target && next.group2Sum == target && next.group3Sum == target) {
        results.add(next.entanglement)
        println(next.entanglement)
      }

      next.remaining.forEach { wt ->
        val newRemaining = next.remaining - wt
        next.addToGroup1(wt, newRemaining, target)?.takeIf { it !in visited }?.let {
          visited += it
          queue.offer(it)
        }
        next.addToGroup2(wt, newRemaining, target)?.takeIf { it !in visited }?.let {
          visited += it
          queue.offer(it)
        }
        next.addToGroup3(wt, newRemaining, target)?.takeIf { it !in visited }?.let {
          visited += it
          queue.offer(it)
        }
      }
    }

    return results.minOf { it }
  }

  override suspend fun executePart2(input: Reader): Int {
    TODO("Not yet implemented")
  }

  data class Arrangement(
    val remaining: Set<Int>,
    val group1Count: Int = 0,
    val group1Product: ULong = 1uL,
    val group1Sum: Int = 0,
    val group2Sum: Int = 0,
    val group3Sum: Int = 0,
  ) : Comparable<Arrangement> {
    val entanglement: ULong = group1Product

    fun isLegal(target: Int): Boolean = group1Sum <= target && group2Sum <= target && group3Sum <= target

    fun addToGroup1(weight: Int, newRemaining: Set<Int>, target: Int): Arrangement? {
      return Arrangement(
        newRemaining,
        group1Count + 1,
        group1Product * weight.toULong(),
        group1Sum + weight,
        group2Sum,
        group3Sum
      ).takeIf { it.isLegal(target) }
    }

    fun addToGroup2(weight: Int, newRemaining: Set<Int>, target: Int): Arrangement? {
      return copy(remaining = newRemaining, group2Sum = group2Sum + weight).takeIf { it.isLegal(target) }
    }

    fun addToGroup3(weight: Int, newRemaining: Set<Int>, target: Int): Arrangement? {
      return copy(remaining = newRemaining, group3Sum = group3Sum + weight).takeIf { it.isLegal(target) }
    }

    override operator fun compareTo(other: Arrangement): Int {
      val totalSum = group1Sum + group2Sum + group3Sum
      val otherTotalSum = other.group1Sum + other.group2Sum + other.group3Sum
      if (totalSum != otherTotalSum) return otherTotalSum - totalSum
      if (group1Count != other.group1Count) return group1Count - other.group1Count
      return entanglement compareTo other.entanglement
    }
  }
}
