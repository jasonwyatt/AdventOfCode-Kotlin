package us.jwf.aoc2021

import java.io.Reader
import us.jwf.aoc.Day

/**
 * AoC 2021 Day 3
 */
class Day03BinaryDiagnostic : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    val report = input.readLines()
    val oneCounts = IntArray(report[0].length) { 0 }
    report.forEach { line ->
      line.withIndex().forEach { (i, value) ->
        oneCounts[i] += if (value == '1') 1 else 0
      }
    }

    val gamma = oneCounts.joinToString(separator = "") { if (it >= report.size / 2) "1" else "0" }
    val epsilon = oneCounts.joinToString(separator = "") { if (it < report.size / 2) "1" else "0" }
    return gamma.toInt(2) * epsilon.toInt(2)
  }

  override suspend fun executePart2(input: Reader): Int {
    val report = input.readLines()

    fun countDigits(report: List<String>): Pair<IntArray, IntArray> {
      val oneCounts = IntArray(report[0].length) { 0 }
      val zeroCounts = IntArray(report[0].length) { 0 }
      report.forEach { line ->
        line.withIndex().forEach { (i, value) ->
          oneCounts[i] += if (value == '1') 1 else 0
          zeroCounts[i] += if (value == '0') 1 else 0
        }
      }
      return oneCounts to zeroCounts
    }

    fun operate(comparison: (ones: Int, zeros: Int) -> Boolean): Int {
      var current = report
      var next: List<String>
      var index = 0
      while (current.size > 1 && index < current[0].length) {
        val (oneCounts, zeroCounts) = countDigits(current)
        val lookingFor = if (comparison(oneCounts[index], zeroCounts[index])) '1' else '0'
        next = current.filter { it[index] == lookingFor }
        current = next
        index++
      }
      return current[0].toInt(2)
    }

    return operate { ones, zeros -> ones >= zeros } * operate { ones, zeros -> ones < zeros }
  }
}