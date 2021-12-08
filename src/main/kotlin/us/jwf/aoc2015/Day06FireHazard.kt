package us.jwf.aoc2015

import java.io.Reader
import kotlinx.coroutines.flow.fold
import us.jwf.aoc.Day
import us.jwf.aoc.toMatchFlow

class Day06FireHazard : Day<Int, Int> {
  val PATTERN = "(toggle|turn (on|off)) (\\d+),(\\d+) through (\\d+),(\\d+)".toRegex()

  override suspend fun executePart1(input: Reader): Int {
    return input.toMatchFlow(PATTERN)
      .fold(Lights()) { lights, matches ->
        val lowX = matches[3].toInt()
        val lowY = matches[4].toInt()
        val highX = matches[5].toInt()
        val highY = matches[6].toInt()
        if (matches[1] == "turn on") {
          lights.turnOn(lowX, lowY, highX, highY)
        } else if (matches[1] == "turn off") {
          lights.turnOff(lowX, lowY, highX, highY)
        } else if (matches[1] == "toggle") {
          lights.toggle(lowX, lowY, highX, highY)
        }
        lights
      }
      .onCount()
  }

  override suspend fun executePart2(input: Reader): Int {
    return input.toMatchFlow(PATTERN)
      .fold(Lights()) { lights, matches ->
        val lowX = matches[3].toInt()
        val lowY = matches[4].toInt()
        val highX = matches[5].toInt()
        val highY = matches[6].toInt()
        if (matches[1] == "turn on") {
          lights.turnOn(lowX, lowY, highX, highY)
        } else if (matches[1] == "turn off") {
          lights.turnOff(lowX, lowY, highX, highY)
        } else if (matches[1] == "toggle") {
          lights.toggle(lowX, lowY, highX, highY)
        }
        lights
      }
      .onCount2()
  }

  class Lights {
    private val lightStates = Array(1000) { BooleanArray(1000) { false } }
    private val lightStates2 = Array(1000) { IntArray(1000) { 0 } }

    fun turnOn(lowX: Int, lowY: Int, highX: Int, highY: Int) {
      (lowX..highX).forEach { x ->
        (lowY..highY).forEach { y ->
          lightStates[x][y] = true
          lightStates2[x][y]++
        }
      }
    }

    fun turnOff(lowX: Int, lowY: Int, highX: Int, highY: Int) {
      (lowX..highX).forEach { x ->
        (lowY..highY).forEach { y ->
          lightStates[x][y] = false
          lightStates2[x][y] = maxOf(lightStates2[x][y] - 1, 0)
        }
      }
    }

    fun toggle(lowX: Int, lowY: Int, highX: Int, highY: Int) {
      (lowX..highX).forEach { x ->
        (lowY..highY).forEach { y ->
          lightStates[x][y] = !lightStates[x][y]
          lightStates2[x][y] += 2
        }
      }
    }

    fun onCount(): Int = lightStates.sumOf { line -> line.count { it } }
    fun onCount2(): Int = lightStates2.sumOf { line -> line.sum() }
  }
}