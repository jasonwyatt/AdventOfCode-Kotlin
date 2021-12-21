package us.jwf.aoc2021

import java.io.Reader
import us.jwf.aoc.Day

/**
 * AoC 2021 - Day 21
 */
class Day21DiracDice : Day<Int, ULong> {
  override suspend fun executePart1(input: Reader): Int {
    val game = Game(intArrayOf(6, 7))
    while (game.takeTurn()) Unit

    return game.dieRolls * game.playerScores.minOf { it }
  }

  override suspend fun executePart2(input: Reader): ULong {
    val cache = mutableMapOf<State, Pair<ULong, ULong>>()

    val start = State(player1Position = 6, player2Position = 7)

    fun count(state: State): Pair<ULong, ULong> {
      cache[state]?.let { return it }
      if (state.player1Score >= 21) return 1uL to 0uL
      if (state.player2Score >= 21) return 0uL to 1uL

      val results = mutableListOf<Pair<ULong, ULong>>()
      (1..3).forEach { first ->
        (1..3).forEach { second ->
          (1..3).forEach { third ->
            if (state.turn % 2 == 0) {
              val position = (((state.player1Position + first) % 10 + second) % 10 + third) % 10
              val score = state.player1Score + position + 1
              results.add(
                count(
                  State(position, state.player2Position, score, state.player2Score, (state.turn + 1) % 2)
                )
              )
            } else {
              val position = (((state.player2Position + first) % 10 + second) % 10 + third) % 10
              val score = state.player2Score + position + 1
              results.add(
                count(
                  State(state.player1Position, position, state.player1Score, score, (state.turn + 1) % 2)
                )
              )
            }
          }
        }
      }
      return results.fold(0uL to 0uL) { acc, scores -> (acc.first + scores.first) to (acc.second + scores.second) }
        .also { cache[state] = it }
    }

    val (aWins, bWins) = count(start)
    return maxOf(aWins, bWins)
  }

  data class State(
    val player1Position: Int,
    val player2Position: Int,
    val player1Score: Int = 0,
    val player2Score: Int = 0,
    val turn: Int = 0
  )

  class Game(val playerPositions: IntArray) {
    var turn = 0
    val playerScores = intArrayOf(0, 0)

    var dieRolls = 0
    val die = iterator {
      var i = 1
      while (true) {
        dieRolls++
        yield(i)
        i++
        if (i > 100) i = 1
      }
    }

    fun takeTurn(): Boolean {
      val player = turn % 2
      playerPositions[player] = (((playerPositions[player] + die.next()) % 10 + die.next()) % 10 + die.next()) % 10
      playerScores[player] += playerPositions[player] + 1
      turn++
      return playerScores.all { it < 1000 }
    }
  }
}
