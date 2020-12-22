package us.jwf.aoc2020

import java.io.Reader
import java.util.*
import kotlinx.coroutines.flow.collect
import us.jwf.aoc.Day
import us.jwf.aoc.toLineFlow

/**
 * Day 22 of AoC 2020.
 */
class Day22CrabCombat : Day<Long, Long> {
  /**
   * It only takes a few hours of sailing the ocean on a raft for boredom to sink in. Fortunately,
   * you brought a small deck of space cards! You'd like to play a game of Combat, and there's even
   * an opponent available: a small crab that climbed aboard your raft before you left.
   *
   * Fortunately, it doesn't take long to teach the crab the rules.
   *
   * Before the game starts, split the cards so each player has their own deck (your puzzle input).
   * Then, the game consists of a series of rounds: both players draw their top card, and the player
   * with the higher-valued card wins the round. The winner keeps both cards, placing them on the
   * bottom of their own deck so that the winner's card is above the other card. If this causes a
   * player to have all of the cards, they win, and the game ends.
   *
   * Once the game ends, you can calculate the winning player's score. The bottom card in their
   * deck is worth the value of the card multiplied by 1, the second-from-the-bottom card is worth
   * the value of the card multiplied by 2, and so on. With 10 cards, the top card is worth the
   * value on the card multiplied by 10.
   *
   * Play the small crab in a game of Combat using the two decks you just dealt. What is the winning
   * player's score?
   */
  override suspend fun executePart1(input: Reader): Long {
    val (playerOne, playerTwo) = input.parsePlayers()

    while (playerOne.deck.isNotEmpty() && playerTwo.deck.isNotEmpty()) {
      val topOne = playerOne.deck.pollFirst()
      val topTwo = playerTwo.deck.pollFirst()

      if (topOne > topTwo) {
        playerOne.deck.addLast(topOne)
        playerOne.deck.addLast(topTwo)
      } else {
        playerTwo.deck.addLast(topTwo)
        playerTwo.deck.addLast(topOne)
      }
    }

    return if (playerOne.deck.isNotEmpty()) playerOne.score else playerTwo.score
  }

  /**
   * You lost to the small crab! Fortunately, crabs aren't very good at recursion. To defend your
   * honor as a Raft Captain, you challenge the small crab to a game of Recursive Combat.
   *
   * Recursive Combat still starts by splitting the cards into two decks (you offer to play with
   * the same starting decks as before - it's only fair). Then, the game consists of a series of
   * rounds with a few changes:
   *
   * * Before either player deals a card, if there was a previous round in this game that had
   *   exactly the same cards in the same order in the same players' decks, the game instantly ends
   *   in a win for player 1. Previous rounds from other games are not considered. (This prevents
   *   infinite games of Recursive Combat, which everyone agrees is a bad idea.)
   * * Otherwise, this round's cards must be in a new configuration; the players begin the round by
   *   each drawing the top card of their deck as normal.
   * * If both players have at least as many cards remaining in their deck as the value of the card
   *   they just drew, the winner of the round is determined by playing a new game of Recursive
   *   Combat (see below).
   * * Otherwise, at least one player must not have enough cards left in their deck to recurse; the
   *   winner of the round is the player with the higher-value card.
   *
   * As in regular Combat, the winner of the round (even if they won the round by winning a
   * sub-game) takes the two cards dealt at the beginning of the round and places them on the bottom
   * of their own deck (again so that the winner's card is above the other card). Note that the
   * winner's card might be the lower-valued of the two cards if they won the round due to winning
   * a sub-game. If collecting cards by winning the round causes a player to have all of the cards,
   * they win, and the game ends.
   *
   * During a round of Recursive Combat, if both players have at least as many cards in their own
   * decks as the number on the card they just dealt, the winner of the round is determined by
   * recursing into a sub-game of Recursive Combat. (For example, if player 1 draws the 3 card,
   * and player 2 draws the 7 card, this would occur if player 1 has at least 3 cards left and
   * player 2 has at least 7 cards left, not counting the 3 and 7 cards that were drawn.)
   *
   * To play a sub-game of Recursive Combat, each player creates a new deck by making a copy of the
   * next cards in their deck (the quantity of cards copied is equal to the number on the card they
   * drew to trigger the sub-game). During this sub-game, the game that triggered it is on hold and
   * completely unaffected; no cards are removed from players' decks to form the sub-game. (For
   * example, if player 1 drew the 3 card, their deck in the sub-game would be copies of the next
   * three cards in their deck.)
   *
   * After the game, the winning player's score is calculated from the cards they have in their
   * original deck using the same rules as regular Combat.
   *
   * Defend your honor as Raft Captain by playing the small crab in a game of Recursive Combat
   * using the same two decks as before. What is the winning player's score?
   */
  override suspend fun executePart2(input: Reader): Long {
    val (playerOne, playerTwo) = input.parsePlayers()

    fun findWinner(playerOne: Player, playerTwo: Player): Player {
      val historicalGames = mutableSetOf<Pair<Player, Player>>()

      while (playerOne.deck.isNotEmpty() && playerTwo.deck.isNotEmpty()) {
        val historyItem =
          Player(1, LinkedList(playerOne.deck)) to Player(2, LinkedList(playerTwo.deck))

        if (historyItem in historicalGames) return playerOne
        historicalGames.add(historyItem)

        val topOne = playerOne.deck.pollFirst()
        val topTwo = playerTwo.deck.pollFirst()

        if (playerOne.deck.size >= topOne && playerTwo.deck.size >= topTwo) {
          val newPlayer1 = Player(1, LinkedList(playerOne.deck.subList(0, topOne)))
          val newPlayer2 = Player(2, LinkedList(playerTwo.deck.subList(0, topTwo)))
          val winner = findWinner(newPlayer1, newPlayer2)
          if (winner.id == newPlayer1.id) {
            playerOne.deck.addLast(topOne)
            playerOne.deck.addLast(topTwo)
          } else {
            playerTwo.deck.addLast(topTwo)
            playerTwo.deck.addLast(topOne)
          }
        } else if (topOne > topTwo) {
          playerOne.deck.addLast(topOne)
          playerOne.deck.addLast(topTwo)
        } else {
          playerTwo.deck.addLast(topTwo)
          playerTwo.deck.addLast(topOne)
        }
      }

      return if (playerOne.deck.isEmpty()) playerTwo else playerOne
    }

    return findWinner(playerOne, playerTwo).score
  }

  private suspend fun Reader.parsePlayers(): Pair<Player, Player> {
    var playerDeck = LinkedList<Int>()
    lateinit var playerOne: Player
    toLineFlow().collect { line ->
      when {
        line.isBlank() -> {
          playerOne = Player(1, playerDeck)
          playerDeck = LinkedList()
        }
        line.startsWith("Player") -> return@collect
        else -> playerDeck.add(line.toInt(10))
      }
    }
    val playerTwo = Player(2, playerDeck)
    return playerOne to playerTwo
  }

  data class Player(val id: Int, val deck: LinkedList<Int>) {
    val score: Long
      get() = deck.reversed().withIndex().sumOf { (index, value) -> (index + 1L) * value }
  }
}