package us.jwf.aoc2022

import java.io.Reader
import kotlinx.coroutines.flow.fold
import us.jwf.aoc.Day
import us.jwf.aoc.toLineFlow

class Day02RockPaperScissors : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    return input.toLineFlow()
      .fold(0) { acc, line ->
        val pieces = line.split(" ")
        acc + part1(pieces[0], pieces[1])
      }
  }

  override suspend fun executePart2(input: Reader): Int {
    return input.toLineFlow()
      .fold(0) { acc, line ->
        val pieces = line.split(" ")
        acc + part2(pieces[0], pieces[1])
      }
  }

  private fun part1(them: String, me: String): Int {
    val winLose = when (them) {
      "A" -> when (me) {
        "X" -> 3
        "Y" -> 6
        "Z" -> 0
        else -> throw IllegalArgumentException("wtf")
      }
      "B" -> when (me) {
        "X" -> 0
        "Y" -> 3
        "Z" -> 6
        else -> throw IllegalArgumentException("wtf")
      }
      "C" -> when (me) {
        "X" -> 6
        "Y" -> 0
        "Z" -> 3
        else -> throw IllegalArgumentException("wtf")
      }
      else -> throw IllegalArgumentException("wtf")
    }
    return winLose + when (me) {
      "X" -> 1
      "Y" -> 2
      "Z" -> 3
      else -> throw IllegalArgumentException("wtf")
    }
  }

  private fun part2(them: String, result: String): Int {
    val (toPick, gameScore) = when (them) {
      "A" -> when (result) {
        "X" -> "C" to 0
        "Y" -> "A" to 3
        "Z" -> "B" to 6
        else -> throw IllegalArgumentException("wtf")
      }
      "B" -> when (result) {
        "X" -> "A" to 0
        "Y" -> "B" to 3
        "Z" -> "C" to 6
        else -> throw IllegalArgumentException("wtf")
      }
      "C" -> when (result) {
        "X" -> "B" to 0
        "Y" -> "C" to 3
        "Z" -> "A" to  6
        else -> throw IllegalArgumentException("wtf")
      }
      else -> throw IllegalArgumentException("wtf")
    }
    return gameScore + when (toPick) {
      "A" -> 1
      "B" -> 2
      "C" -> 3
      else -> throw IllegalArgumentException("wtf")
    }
  }
}