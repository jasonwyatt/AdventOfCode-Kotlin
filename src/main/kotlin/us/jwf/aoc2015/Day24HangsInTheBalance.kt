package us.jwf.aoc2015

import java.io.Reader
import java.util.LinkedList
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import us.jwf.aoc.Day
import us.jwf.aoc.toIntFlow

/**
 * AoC 2015 - Day 24
 */
class Day24HangsInTheBalance : Day<ULong, ULong> {
  override suspend fun executePart1(input: Reader): ULong {
    val weights = input.toIntFlow().map { it.toULong() }.toList()
    val total = weights.sum()
    val target = total / 3uL

    val start = Arrangement(otherGroupMembers = weights, otherGroupSum = total)

    val queue = LinkedList<Arrangement>().apply { add(start) }
    val visited = mutableSetOf<Arrangement>()

    while (queue.isNotEmpty()) {
      val next = queue.poll()
      if (next.isDone(target)) return next.group1Product

      next.nextOptions(target)
        .forEach {
          if (it in visited) return@forEach
          queue.add(it)
          visited.add(it)
        }
    }

    return 0uL
  }

  override suspend fun executePart2(input: Reader): ULong {
    val weights = input.toIntFlow().map { it.toULong() }.toList()
    val total = weights.sum()
    val target = total / 4uL

    val start = Arrangement(otherGroupMembers = weights, otherGroupSum = total)

    val queue = LinkedList<Arrangement>().apply { add(start) }
    val visited = mutableSetOf<Arrangement>()

    while (queue.isNotEmpty()) {
      val next = queue.poll()
      if (next.isDonePart2(target)) return next.group1Product

      next.nextOptions(target)
        .forEach {
          if (it in visited) return@forEach
          queue.add(it)
          visited.add(it)
        }
    }

    return 0uL
  }

  data class Arrangement(
    val group1Sum: ULong = 0uL,
    val group1Product: ULong = 1uL,
    val otherGroupMembers: List<ULong>,
    val otherGroupSum: ULong,
  ) {
    fun nextOptions(target: ULong): List<Arrangement> {
      return otherGroupMembers.mapNotNull { member ->
        if (group1Sum + member > target) return@mapNotNull null
        Arrangement(
          group1Sum + member,
          group1Product * member,
          otherGroupMembers - member,
          otherGroupSum - member
        )
      }
    }

    fun isDone(groupSize: ULong): Boolean {
      if (group1Sum != groupSize || otherGroupMembers.sum() != groupSize * 2uL) return false

      // Check that otherGroupMembers can be split into even weights.
      fun canSplit(remainingTotal: ULong, remainingOptions: Set<ULong>): Boolean {
        if (remainingTotal == 0uL) return true
        val possibleOptions = remainingOptions.filter { it <= remainingTotal }
        if (possibleOptions.isEmpty()) return false

        return possibleOptions.any { canSplit(remainingTotal - it, remainingOptions - it) }
      }

      // only works if they're all different, if they were the same we'd need a map of counts.
      return canSplit(groupSize, otherGroupMembers.toSet())
    }

    fun isDonePart2(groupSize: ULong): Boolean {
      if (group1Sum != groupSize || otherGroupMembers.sum() != groupSize * 3uL) return false

      // Check that otherGroupMembers can be split into even weights.
      val cache = mutableMapOf<Triple<ULong, ULong, Set<ULong>>, Boolean>()
      fun canSplit(remainingInA: ULong, remainingInB: ULong, remainingOptions: Set<ULong>): Boolean {
        val args = Triple(remainingInA, remainingInB, remainingOptions)
        cache[args]?.let { return it }

        if (remainingInA == 0uL && remainingInB == 0uL) return true
        val possibleAOptions = remainingOptions.filter { it <= remainingInA }
        val possibleBOptions = remainingOptions.filter { it <= remainingInB }

        return (possibleAOptions.any { canSplit(remainingInA - it, remainingInB, remainingOptions - it) } ||
          possibleBOptions.any { canSplit(remainingInA, remainingInB - it, remainingOptions - it) })
          .also { cache[args] = it }
      }

      // only works if they're all different, if they were the same we'd need a map of counts.
      return canSplit(groupSize, groupSize, otherGroupMembers.toSet())
    }
  }
}
