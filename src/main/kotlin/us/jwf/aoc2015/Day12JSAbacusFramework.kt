package us.jwf.aoc2015

import java.io.Reader
import org.json.JSONArray
import org.json.JSONObject
import us.jwf.aoc.Day

/**
 * AoC 2015 - Day 12
 */
class Day12JSAbacusFramework : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    return JSONObject(input.readText()).sumNumbers()
  }

  override suspend fun executePart2(input: Reader): Int {
    return JSONObject(input.readText()).sumNumbers(true)
  }

  fun JSONObject.sumNumbers(skipRed: Boolean = false): Int {
    var sum = 0
    keys().forEach {
      sum += when (val value = get(it)) {
        is JSONObject -> value.sumNumbers(skipRed)
        is JSONArray -> value.sumNumbers(skipRed)
        is Int -> value
        is String -> {
          if (value == "red" && skipRed) return 0
          0
        }
        else -> 0
      }
    }
    return sum
  }

  fun JSONArray.sumNumbers(skipRed: Boolean = false): Int {
    var sum = 0
    forEach {
      sum += when (it) {
        is JSONObject -> it.sumNumbers(skipRed)
        is JSONArray -> it.sumNumbers(skipRed)
        is Int -> it
        else -> 0
      }
    }
    return sum
  }
}