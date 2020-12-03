package us.jwf.aoc2020

import java.io.Reader
import us.jwf.aoc.AdventOfCode
import us.jwf.aoc.Day

class AdventOfCode2020 : AdventOfCode {
  private val lookup: Map<Int, () -> Day<*, *>> = mapOf(
    1 to ::DayOne,
    2 to ::DayTwo
  )

  override suspend fun printResult(day: Int, part: Int, input: Reader) {
    val ctor = requireNotNull(lookup[day]) { "Day $day not supported yet." }
    val result = if (part == 1) ctor().executePart1(input) else ctor().executePart2(input)
    println(result.toString())
  }
}