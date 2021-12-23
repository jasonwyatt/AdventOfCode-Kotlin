package us.jwf.aoc2021

import java.io.Reader
import java.util.LinkedList
import us.jwf.aoc.Day
import us.jwf.aoc2021.Day23Amphipod.Amphipod.Type
import us.jwf.aoc2021.Day23Amphipod.Amphipod.Type.A
import us.jwf.aoc2021.Day23Amphipod.Amphipod.Type.B
import us.jwf.aoc2021.Day23Amphipod.Amphipod.Type.C
import us.jwf.aoc2021.Day23Amphipod.Amphipod.Type.D
import us.jwf.aoc2021.Day23Amphipod.Board.Companion.SPACES
import us.jwf.aoc2021.Day23Amphipod.Board.Companion.SPACES2
import us.jwf.aoc2021.Day23Amphipod.Board.Spaces
import us.jwf.aoc2021.Day23Amphipod.Space.EntranceWay
import us.jwf.aoc2021.Day23Amphipod.Space.Hallway
import us.jwf.aoc2021.Day23Amphipod.Space.Room
import us.jwf.aoc2021.Day23Amphipod.Space.Wall

/**
 * AoC 2021 - Day 23
 */
class Day23Amphipod : Day<Int, Int> {
  val actual = setOf(
    Amphipod(C, 2, 3),
    Amphipod(D, 3, 3),
    Amphipod(A, 2, 5),
    Amphipod(C, 3, 5),
    Amphipod(B, 2, 7),
    Amphipod(A, 3, 7),
    Amphipod(D, 2, 9),
    Amphipod(B, 3, 9),
  )
  val actual2 = setOf(
    Amphipod(C, 2, 3),
    Amphipod(D, 3, 3),
    Amphipod(D, 4, 3),
    Amphipod(D, 5, 3),

    Amphipod(A, 2, 5),
    Amphipod(C, 3, 5),
    Amphipod(B, 4, 5),
    Amphipod(C, 5, 5),

    Amphipod(B, 2, 7),
    Amphipod(B, 3, 7),
    Amphipod(A, 4, 7),
    Amphipod(A, 5, 7),

    Amphipod(D, 2, 9),
    Amphipod(A, 3, 9),
    Amphipod(C, 4, 9),
    Amphipod(B, 5, 9),
  )

  val sample = setOf(
    Amphipod(B, 2, 3),
    Amphipod(A, 3, 3),
    Amphipod(C, 2, 5),
    Amphipod(D, 3, 5),
    Amphipod(B, 2, 7),
    Amphipod(C, 3, 7),
    Amphipod(D, 2, 9),
    Amphipod(A, 3, 9),
  )
  val sample2 = setOf(
    Amphipod(B, 2, 3),
    Amphipod(D, 3, 3),
    Amphipod(D, 4, 3),
    Amphipod(A, 5, 3),

    Amphipod(C, 2, 5),
    Amphipod(C, 3, 5),
    Amphipod(B, 4, 5),
    Amphipod(D, 5, 5),

    Amphipod(B, 2, 7),
    Amphipod(B, 3, 7),
    Amphipod(A, 4, 7),
    Amphipod(C, 5, 7),

    Amphipod(D, 2, 9),
    Amphipod(A, 3, 9),
    Amphipod(C, 4, 9),
    Amphipod(A, 5, 9),
  )

  override suspend fun executePart1(input: Reader): Int {
    val start = Board(0, 1, 2, 3, 0, actual, Spaces(SPACES))
    return solve(start)
  }

  override suspend fun executePart2(input: Reader): Int {
    val start = Board(0, 1, 2, 3, 0, actual2, Spaces(SPACES2))
    return solve(start)
  }

  private fun solve(start: Board): Int {
    val cache = mutableMapOf<Board, Int>()
    var minSeenYet = Int.MAX_VALUE

    fun recurse(state: Board, steps: List<Set<Amphipod>> = listOf(state.amphipods)): Int {
      cache[state]?.let { return it }
      if (state.isDone()) {
        cache[state] = state.totalCostPaid
        if (state.totalCostPaid < minSeenYet) {
          minSeenYet = state.totalCostPaid
        }
        return state.totalCostPaid
      }
      val nextBoards = state.findNextBoards()
        .filter {
          it.totalCostPaid < minSeenYet
        }
        .sortedWith { a, b ->
          if (a.totalCostPaid != b.totalCostPaid) a.totalCostPaid - b.totalCostPaid
          else a.distance - b.distance
        }
      return (nextBoards.minOfOrNull { recurse(it, steps + listOf(it.amphipods)) } ?: Int.MAX_VALUE)
        .also { cache[state] = it }
    }
    return recurse(start)
  }

  sealed class Space(i: Int, j: Int) {
    abstract val neighbors: MutableSet<Space>
    abstract val i: Int
    abstract val j: Int
    val location = i to j

    data class Wall(
      override val i: Int,
      override val j: Int,
      override val neighbors: MutableSet<Space> = mutableSetOf()
    ) : Space(i, j)
    data class Hallway(
      override val i: Int,
      override val j: Int,
      override val neighbors: MutableSet<Space> = mutableSetOf()
    ) : Space(i, j)
    data class EntranceWay(
      override val i: Int,
      override val j: Int,
      override val neighbors: MutableSet<Space> = mutableSetOf()
    ) : Space(i, j)
    data class Room(
      override val i: Int,
      override val j: Int,
      val roomId: Int,
      override val neighbors: MutableSet<Space> = mutableSetOf()
    ) : Space(i, j)
  }

  data class Amphipod(val type: Type, val i: Int, val j: Int) {
    enum class Type(val moveCost: Int) {
      A(1), B(10), C(100), D(1000)
    }
  }

  /**
   * #############
   * #...........#
   * ###C#A#B#D###
   *   #D#C#A#B#
   *   #########
   */
  data class Board(
    val aTarget: Int,
    val bTarget: Int,
    val cTarget: Int,
    val dTarget: Int,
    val totalCostPaid: Int,
    val amphipods: Set<Amphipod>,
    val spaces: Spaces
  ) {
    val distance: Int = amphipods.count { it.i == 1 || it.j != 3 * it.targetRoomId }

    val Amphipod.targetRoomId: Int
      get() {
        return when (type) {
          A -> aTarget
          B -> bTarget
          C -> cTarget
          D -> dTarget
        }
      }

    // Returns the collection of possible next board states.
    fun findNextBoards(): Set<Board> {
      val result = mutableSetOf<Board>()
      amphipods.forEach { current ->
        val next = current.findNextLocations()//.also { println("$current can go to $it") }
        next.forEach { (replacement, cost) ->
          result.add(
            copy(
              totalCostPaid = totalCostPaid + cost,
              amphipods = amphipods - current + replacement
            )
          )
        }
      }
      return result
    }

    fun typesInRoom(roomId: Int): Set<Type> {
      return amphipods.filter { it.j == 3 + roomId * 2 && it.i > 1 }.map { it.type }.toSet()
    }

    // Returns list of possible new values for this amphipod along with the cost to get there
    fun Amphipod.findNextLocations(): List<Pair<Amphipod, Int>> {
      // If it's done, we can skip it.
      if (j == 3 + targetRoomId * 2) {
        val typesInRoom = typesInRoom(targetRoomId)
        if (typesInRoom.isEmpty() || typesInRoom == setOf(type)) return emptyList()
      }

      return when (val currentSpace = spaces.spacesByLocation[i to j]) {
        is Hallway, is Room -> {
          val possibilities = mutableSetOf<Pair<Amphipod, Int>>()

          val queue = LinkedList<Pair<Pair<Int, Int>, Int>>()
          queue.add((this.i to this.j) to 0)
          val visited = mutableSetOf(this.i to this.j)

          while (queue.isNotEmpty()) {
            val (loc, moves) = queue.poll()

            if (loc != this.i to this.j) {
              val space = spaces.spacesByLocation[loc]!!

              // If we've actually moved, and the position is a room or a hallway spot, add it to
              // the possibilities.
              val shouldAddPossibility =
                if (currentSpace is Hallway) {
                  // Can only move to destination room, and only if the room is empty or has our
                  // buddy.
                  if (space is Room && space.roomId == targetRoomId) {
                    val typesInRoom = typesInRoom(targetRoomId)
                    typesInRoom.isEmpty() || typesInRoom == setOf(type)
                  } else false
                } else {
                  if (space is Room && space.j != loc.second && space.roomId == targetRoomId) {
                    // Can only move to destination room if the room is empty or has our buddy.
                    val typesInRoom = typesInRoom(targetRoomId)
                    typesInRoom.isEmpty() || typesInRoom == setOf(type)
                  } else space is Hallway
                }

              if (shouldAddPossibility) {
                possibilities.add(Amphipod(type, loc.first, loc.second) to type.moveCost * moves)
              }
            }

            // Determine new locations.
            spaces.neighborsByLocation[loc]!!
              // No walls needed.
              .filter { space -> space !is Wall }
              // No occupied spaces.
              .filter { space -> amphipods.find { it.i == space.i && it.j == space.j } == null }
              .filter { it.location !in visited }
              .forEach {
                visited.add(it.location)
                queue.add(it.location to moves + 1)
              }
          }

          possibilities.toList()
        }
        is Wall, is EntranceWay, null -> emptyList()
      }
    }

    fun isDone(): Boolean {
      return amphipods.all {
        val roomId = (spaces.spacesByLocation[it.i to it.j] as? Room)?.roomId ?: return@all false
        when (it.type) {
          A -> roomId == aTarget
          B -> roomId == bTarget
          C -> roomId == cTarget
          D -> roomId == dTarget
        }
      }
    }

    class Spaces(val raw: Set<Space>) {
      val spacesByLocation = buildMap { raw.forEach { put(it.location, it) } }
      val neighborsByLocation = buildMap {
        raw.forEach { space ->
          put(
            space.location,
            buildSet {
              (-1..1).forEach { iOffset ->
                (-1..1).forEach jOffset@{ jOffset ->
                  if (iOffset == jOffset || iOffset == -jOffset) return@jOffset
                  spacesByLocation[space.i + iOffset to space.j + jOffset]?.let { add(it) }
                }
              }
            }
          )
        }
      }
    }

    companion object {
      val SPACES = setOf(
        Wall(0, 0),
        Wall(0, 1),
        Wall(0, 2),
        Wall(0, 3),
        Wall(0, 4),
        Wall(0, 5),
        Wall(0, 6),
        Wall(0, 7),
        Wall(0, 8),
        Wall(0, 9),
        Wall(0, 10),
        Wall(0, 11),
        Wall(0, 12),

        Wall(1, 0),
        Hallway(1, 1),
        Hallway(1, 2),
        EntranceWay(1, 3),
        Hallway(1, 4),
        EntranceWay(1, 5),
        Hallway(1, 6),
        EntranceWay(1, 7),
        Hallway(1, 8),
        EntranceWay(1, 9),
        Hallway(1, 10),
        Hallway(1, 11),
        Wall(1, 12),

        Wall(2, 0),
        Wall(2, 1),
        Wall(2, 2),
        Room(2, 3, 0),
        Wall(2, 4),
        Room(2, 5, 1),
        Wall(2, 6),
        Room(2, 7, 2),
        Wall(2, 8),
        Room(2, 9, 3),
        Wall(2, 10),
        Wall(2, 11),
        Wall(2, 12),

        Wall(3, 2),
        Room(3, 3, 0),
        Wall(3, 4),
        Room(3, 5, 1),
        Wall(3, 6),
        Room(3, 7, 2),
        Wall(3, 8),
        Room(3, 9, 3),
        Wall(3, 10),

        Wall(4, 2),
        Wall(4, 3),
        Wall(4, 4),
        Wall(4, 5),
        Wall(4, 6),
        Wall(4, 7),
        Wall(4, 8),
        Wall(4, 9),
        Wall(4, 10),
      )
      val SPACES2 = setOf(
        Wall(0, 0),
        Wall(0, 1),
        Wall(0, 2),
        Wall(0, 3),
        Wall(0, 4),
        Wall(0, 5),
        Wall(0, 6),
        Wall(0, 7),
        Wall(0, 8),
        Wall(0, 9),
        Wall(0, 10),
        Wall(0, 11),
        Wall(0, 12),

        Wall(1, 0),
        Hallway(1, 1),
        Hallway(1, 2),
        EntranceWay(1, 3),
        Hallway(1, 4),
        EntranceWay(1, 5),
        Hallway(1, 6),
        EntranceWay(1, 7),
        Hallway(1, 8),
        EntranceWay(1, 9),
        Hallway(1, 10),
        Hallway(1, 11),
        Wall(1, 12),

        Wall(2, 0),
        Wall(2, 1),
        Wall(2, 2),
        Room(2, 3, 0),
        Wall(2, 4),
        Room(2, 5, 1),
        Wall(2, 6),
        Room(2, 7, 2),
        Wall(2, 8),
        Room(2, 9, 3),
        Wall(2, 10),
        Wall(2, 11),
        Wall(2, 12),

        Wall(3, 2),
        Room(3, 3, 0),
        Wall(3, 4),
        Room(3, 5, 1),
        Wall(3, 6),
        Room(3, 7, 2),
        Wall(3, 8),
        Room(3, 9, 3),
        Wall(3, 10),

        Wall(4, 2),
        Room(4, 3, 0),
        Wall(4, 4),
        Room(4, 5, 1),
        Wall(4, 6),
        Room(4, 7, 2),
        Wall(4, 8),
        Room(4, 9, 3),
        Wall(4, 10),

        Wall(5, 2),
        Room(5, 3, 0),
        Wall(5, 4),
        Room(5, 5, 1),
        Wall(5, 6),
        Room(5, 7, 2),
        Wall(5, 8),
        Room(5, 9, 3),
        Wall(5, 10),

        Wall(6, 2),
        Wall(6, 3),
        Wall(6, 4),
        Wall(6, 5),
        Wall(6, 6),
        Wall(6, 7),
        Wall(6, 8),
        Wall(6, 9),
        Wall(6, 10),
      )
    }
  }
}