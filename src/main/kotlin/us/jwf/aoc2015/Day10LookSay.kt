package us.jwf.aoc2015

import java.io.Reader
import us.jwf.aoc.Day

/**
 * AoC 2015 - Day 10
 */
class Day10LookSay : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    return doIt(40)
  }

  override suspend fun executePart2(input: Reader): Int {
    return doIt(50)
  }

  fun doIt(times: Int): Int {
    var value = "3113322113"
    val newValue = StringBuilder()
    repeat(times) {
      newValue.clear()
      var i = 0
      while (i < value.length) {
        val digit = value[i]
        var j = 1
        while (i + j < value.length) {
          if (value[i + j] == digit) j++
          else break
        }
        newValue.append(j)
        newValue.append(digit)
        i += j
      }
      value = newValue.toString()
    }
    return value.length
  }
}