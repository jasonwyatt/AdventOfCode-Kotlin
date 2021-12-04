package us.jwf.aoc2021

import java.io.Reader
import us.jwf.aoc.Day

/**
 * AoC 2021 Day 4
 */
class Day04GiantSquid : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    val (calls, boards) = readGame(input.readLines())

    var result = 0
    for (num in calls) {
      val winner = boards.find { it.call(num) }
      if (winner != null) {
        result = winner.score(num)
        break
      }
    }
    return result
  }

  override suspend fun executePart2(input: Reader): Int {
    val (calls, boards) = readGame(input.readLines())

    var result = 0
    for ((i, num) in calls.withIndex()) {
      val winners = boards.filter { it.call(num) }
      winners.forEach { boards.remove(it) }
      if (boards.size == 0 || i == calls.size - 1) {
        result = winners.last().score(num)
        break
      }
    }
    return result
  }

  private fun readGame(lines: List<String>): Pair<List<Int>, MutableList<Board>> {
    val calls = lines[0].split(",").map { it.toInt(10) }
    val boards = mutableListOf<Board>()

    var currentLine = 2
    while (currentLine < lines.size) {
      // skip the blank line
      boards.add(
        Board.build(
          lines[currentLine],
          lines[currentLine + 1],
          lines[currentLine + 2],
          lines[currentLine + 3],
          lines[currentLine + 4]
        )
      )
      currentLine += 6
    }
    return calls to boards
  }

  class Board(numbers: List<Int>) {
    private val rows =
      Array(5) {
        mutableSetOf(
          numbers[5 * it],
          numbers[5 * it + 1],
          numbers[5 * it + 2],
          numbers[5 * it + 3],
          numbers[5 * it + 4]
        )
      }
    private val cols =
      Array(5) {
        mutableSetOf(
          numbers[it],
          numbers[it + 5],
          numbers[it + 10],
          numbers[it + 15],
          numbers[it + 20]
        )
      }

    fun call(value: Int): Boolean {
      rows.forEach { it.remove(value) }
      cols.forEach { it.remove(value) }
      return rows.any { it.isEmpty() } || cols.any { it.isEmpty() }
    }

    fun score(num: Int): Int {
      return rows.flatMap { it.toList() }.sum() * num
    }

    companion object {
      fun build(vararg raw: String): Board =
        Board(raw.joinToString(" ").trim().split("\\s+".toRegex()).map { it.toInt() })
    }
  }
}