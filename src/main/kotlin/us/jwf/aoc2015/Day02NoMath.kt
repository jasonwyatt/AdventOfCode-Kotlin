package us.jwf.aoc2015

import java.io.Reader
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.map
import us.jwf.aoc.Day
import us.jwf.aoc.toMatchFlow

class Day02NoMath : Day<Int, Int> {
  private val PATTERN = "(\\d+)x(\\d+)x(\\d+)".toRegex()

  override suspend fun executePart1(input: Reader): Int {
    return input.toMatchFlow(PATTERN)
      .map { Box(it[1].toInt(), it[2].toInt(), it[3].toInt()) }
      .fold(0) { acc, box -> acc + box.required }
  }

  override suspend fun executePart2(input: Reader): Int {
    return input.toMatchFlow(PATTERN)
      .map { Box(it[1].toInt(), it[2].toInt(), it[3].toInt()) }
      .fold(0) { acc, box -> acc + box.ribbon }
  }

  data class Box(val l: Int, val w: Int, val h: Int) {
    val totalArea: Int = 2 * l * w + 2 * l * h + 2 * h * w
    val slop: Int = minOf(l * w, l * h, w * h)
    val ribbon: Int = 2 * minOf(l + w, l + h, w + h) + l * w * h
    val required: Int = totalArea + slop
  }
}