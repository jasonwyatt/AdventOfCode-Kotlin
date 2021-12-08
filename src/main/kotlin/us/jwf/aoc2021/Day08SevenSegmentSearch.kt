package us.jwf.aoc2021

import java.io.Reader
import us.jwf.aoc.Day

/**
 * AoC 2021 - Day 8
 */
class Day08SevenSegmentSearch : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    return input.readLines()
      .sumOf { it.countDigitsDumb() }
  }

  override suspend fun executePart2(input: Reader): Int {
    return input.readLines()
      .map { it.findOutput() }
      .sum()
  }

  fun String.countDigitsDumb(): Int {
    val (_, output) = split("|")

    return output.trim().split(" ").count { it.length in setOf(2, 3, 7, 4) }
  }

  fun String.findOutput(): Int {
    val (samples, output) = split(" | ").map { it.trim().split(" ") }

    val oneStr = samples.find { it.length == 2 }!!
    val fourStr = samples.find { it.length == 4 }!!
    val sevenStr = samples.find { it.length == 3 }!!

    val findings = mutableMapOf(
      samples.find { it.length == 2 }!!.toSet().sorted().joinToString("") to 1,
      samples.find { it.length == 4 }!!.toSet().sorted().joinToString("") to 4,
      samples.find { it.length == 3 }!!.toSet().sorted().joinToString("") to 7,
      samples.find { it.length == 7 }!!.toSet().sorted().joinToString("") to 8,
    )
    val nonBasics = samples.filter { it.length !in setOf(2, 3, 4, 7) }.map { it.toSet() }

    val bottomAndBottomLeft = (nonBasics.fold(setOf<Char>()) { acc, i -> acc + i }) - (sevenStr.toSet() + fourStr.toSet())
    val bottom = bottomAndBottomLeft - (sevenStr.toSet() - fourStr.toSet())
    val middleAndTopLeft = fourStr.toSet() - oneStr.toSet()
    val five = nonBasics.find { it.size == 5 && (it - middleAndTopLeft).size == 3 }!!
    findings[five.sorted().joinToString("")] = 5
    val three = nonBasics.find { it.size == 5 && it != five && (it - sevenStr.toSet()).size == 2 }!!
    findings[three.sorted().joinToString("")] = 3
    val middle = three - sevenStr.toSet() - bottom
    val two = nonBasics.find { it.size == 5 && it != three && it != five }!!
    findings[two.sorted().joinToString("")] = 2

    val zero = nonBasics.find { it.size == 6 && it - middle == it }!!
    println("zero = $zero")
    findings[zero.sorted().joinToString("")] = 0
    val nine = nonBasics.find { it.size == 6 && it != zero && (it - sevenStr.toSet()).size == 3 }!!
    findings[nine.sorted().joinToString("")] = 9
    val six = nonBasics.find { it.size == 6 && it != zero && it != nine }!!
    findings[six.sorted().joinToString("")] = 6

    return output.map { findings[it.toList().sorted().joinToString("")]!! }
      .joinToString("")
      .toInt()
  }
}