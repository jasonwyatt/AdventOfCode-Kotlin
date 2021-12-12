package us.jwf.aoc2015

import java.io.Reader
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.flow.toList
import us.jwf.aoc.Day
import us.jwf.aoc.toMatchFlow

/**
 * AoC 2015 - Day 14
 */
class Day14 : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    return input.toMatchFlow(Reindeer.PATTERN)
      .map { Reindeer(it[1], it[2].toInt(), it[3].toInt(), it[5].toInt()) }
      .map { r -> r.distance(2503).also { println("$r: $it") } }
      .reduce(::maxOf)
  }

  override suspend fun executePart2(input: Reader): Int {
    val reindeer = input.toMatchFlow(Reindeer.PATTERN)
      .map { Reindeer(it[1], it[2].toInt(), it[3].toInt(), it[5].toInt()) }
      .toList()

    repeat(2503) {
      var max = 0
      reindeer.forEach {
        it.tick()
        max = maxOf(max, it.location)
      }
      reindeer.filter { it.location == max }.forEach { it.score++ }
    }
    return reindeer.maxOf { it.score }
  }

  data class Reindeer(val name: String, val speedMps: Int, val endurance: Int, val restLen: Int) {
    var location = 0
    private var time = 0
    var score = 0

    fun tick() {
      val remainder = time % (endurance + restLen)

      if (remainder < endurance) {
        location += speedMps
      }
      time++
    }

    fun distance(time: Int): Int {
      val totalLoops = time / (endurance + restLen)
      val loopTime = totalLoops * (endurance + restLen)
      println("$name - totalLoops: $totalLoops, loopTime = $loopTime")
      val extra = minOf(time - loopTime, endurance) * speedMps
      return totalLoops * speedMps * endurance + extra
    }

    companion object {
      val PATTERN =
        "([A-Za-z]+) can fly (\\d+) km/s for (\\d+) second(s)?, but then must rest for (\\d+) second(s)?.".toRegex()
    }
  }
}