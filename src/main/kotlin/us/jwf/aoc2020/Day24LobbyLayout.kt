package us.jwf.aoc2020

import java.io.Reader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import us.jwf.aoc.Day
import us.jwf.aoc.toLineFlow
import us.jwf.aoc2020.Day24LobbyLayout.Direction.Companion.parseDirections

/**
 * Day 24 of AoC 2020
 */
class Day24LobbyLayout : Day<Int, Int> {
  /**
   * Your raft makes it to the tropical island; it turns out that the small crab was an excellent
   * navigator. You make your way to the resort.
   *
   * As you enter the lobby, you discover a small problem: the floor is being renovated. You can't
   * even reach the check-in desk until they've finished installing the new tile floor.
   *
   * The tiles are all hexagonal; they need to be arranged in a hex grid with a very specific color
   * pattern. Not in the mood to wait, you offer to help figure out the pattern.
   *
   * The tiles are all white on one side and black on the other. They start with the white side
   * facing up. The lobby is large enough to fit whatever pattern might need to appear there.
   *
   * A member of the renovation crew gives you a list of the tiles that need to be flipped over
   * (your puzzle input). Each line in the list identifies a single tile that needs to be flipped
   * by giving a series of steps starting from a reference tile in the very center of the room.
   * (Every line starts from the same reference tile.)
   *
   * Because the tiles are hexagonal, every tile has six neighbors: east, southeast, southwest,
   * west, northwest, and northeast. These directions are given in your list, respectively, as e,
   * se, sw, w, nw, and ne. A tile is identified by a series of these directions with no delimiters;
   * for example, esenee identifies the tile you land on if you start at the reference tile and then
   * move one tile east, one tile southeast, one tile northeast, and one tile east.
   *
   * Each time a tile is identified, it flips from white to black or from black to white. Tiles
   * might be flipped more than once. For example, a line like esew flips a tile immediately
   * adjacent to the reference tile, and a line like nwwswee flips the reference tile itself.
   *
   * Go through the renovation crew's list and determine which tiles they need to flip. After all
   * of the instructions have been followed, how many tiles are left with the black side up?
   */
  override suspend fun executePart1(input: Reader): Int {
    val flipped = mutableSetOf<Pair<Int, Int>>()
    input.toLineFlow().map { it.parseDirections() }
      .collect {
        val finalLoc = it.fold(0 to 0) { acc, dir ->
          val x = acc.first + dir.vec.first
          val y = acc.second + dir.vec.second
          x to y
        }
        if (finalLoc in flipped) flipped.remove(finalLoc)
        else flipped.add(finalLoc)
      }

    return flipped.size
  }

  /**
   * The tile floor in the lobby is meant to be a living art exhibit. Every day, the tiles are all
   * flipped according to the following rules:
   *
   * * Any black tile with zero or more than 2 black tiles immediately adjacent to it is flipped to
   *   white.
   * * Any white tile with exactly 2 black tiles immediately adjacent to it is flipped to black.
   *
   * Here, tiles immediately adjacent means the six tiles directly touching the tile in question.
   *
   * The rules are applied simultaneously to every tile; put another way, it is first determined
   * which tiles need to be flipped, then they are all flipped at the same time.
   *
   * How many tiles will be black after 100 days?
   */
  override suspend fun executePart2(input: Reader): Int {
    var flipped = mutableSetOf<Pair<Int, Int>>()
    input.toLineFlow().map { it.parseDirections() }
      .collect {
        val finalLoc = it.fold(0 to 0) { acc, dir ->
          val x = acc.first + dir.vec.first
          val y = acc.second + dir.vec.second
          x to y
        }
        if (finalLoc in flipped) flipped.remove(finalLoc)
        else flipped.add(finalLoc)
      }

    repeat(100) {
      val currentUnflipped = mutableSetOf<Pair<Int, Int>>()
      val newFlipped = mutableSetOf<Pair<Int, Int>>()

      flipped.forEach { currentlyFlipped ->
        var flippedNeighbors = 0
        currentlyFlipped.neighbors().collect { neighbor ->
          if (neighbor in flipped) flippedNeighbors++
          else currentUnflipped.add(neighbor)
        }
        if (flippedNeighbors == 1 || flippedNeighbors == 2) newFlipped.add(currentlyFlipped)
      }

      currentUnflipped.forEach { unflipped ->
        var flippedNeighbors = 0
        unflipped.neighbors().collect { neighbor ->
          if (neighbor in flipped) flippedNeighbors++
        }
        if (flippedNeighbors == 2) newFlipped.add(unflipped)
      }
      flipped = newFlipped
    }

    return flipped.size
  }

  fun Pair<Int, Int>.neighbors(): Flow<Pair<Int, Int>> = flow {
    emit(Direction.NorthEast.adjust(this@neighbors))
    emit(Direction.NorthWest.adjust(this@neighbors))
    emit(Direction.SouthEast.adjust(this@neighbors))
    emit(Direction.SouthWest.adjust(this@neighbors))
    emit(Direction.East.adjust(this@neighbors))
    emit(Direction.West.adjust(this@neighbors))
  }

  enum class Direction(val vec: Pair<Int, Int>) {
    NorthEast(500 to 866),
    NorthWest( -500 to 866),
    SouthEast(500 to -866),
    SouthWest(-500 to -866),
    East(1000 to 0),
    West(-1000 to 0);

    fun adjust(point: Pair<Int, Int>): Pair<Int, Int> =
      (point.first + vec.first) to (point.second + vec.second)

    companion object {
      fun String.parseDirections(): List<Direction> {
        val result = mutableListOf<Direction>()
        var i = 0
        while (i < length) {
          val next = when {
            i < length - 1 -> {
              val nextTwo = substring(i, i + 2)
              i++
              when (nextTwo) {
                "ne" -> NorthEast
                "nw" -> NorthWest
                "se" -> SouthEast
                "sw" -> SouthWest
                else -> {
                  i -= 1
                  if (nextTwo[0] == 'e') East else West
                }
              }
            }
            this[i] == 'e' -> East
            else -> West
          }
          result.add(next)
          i++
        }
        return result
      }
    }
  }
}