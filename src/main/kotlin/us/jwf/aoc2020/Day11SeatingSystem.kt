package us.jwf.aoc2020

import java.io.Reader
import us.jwf.aoc.Day

/**
 * Day 11 of AoC 2020
 */
class Day11SeatingSystem : Day<Int, Int> {
  /**
   * Your plane lands with plenty of time to spare. The final leg of your journey is a ferry that
   * goes directly to the tropical island where you can finally start your vacation. As you reach
   * the waiting area to board the ferry, you realize you're so early, nobody else has even arrived
   * yet!
   *
   * By modeling the process people use to choose (or abandon) their seat in the waiting area,
   * you're pretty sure you can predict the best place to sit. You make a quick map of the seat
   * layout (your puzzle input).
   *
   * The seat layout fits neatly on a grid. Each position is either floor (.), an empty seat (L),
   * or an occupied seat (#). For example, the initial seat layout might look like this:
   *
   * ```
   * L.LL.LL.LL
   * LLLLLLL.LL
   * L.L.L..L..
   * LLLL.LL.LL
   * L.LL.LL.LL
   * L.LLLLL.LL
   * ..L.L.....
   * LLLLLLLLLL
   * L.LLLLLL.L
   * L.LLLLL.LL
   * ```
   *
   * Now, you just need to model the people who will be arriving shortly. Fortunately, people are
   * entirely predictable and always follow a simple set of rules. All decisions are based on the
   * number of occupied seats adjacent to a given seat (one of the eight positions immediately up,
   * down, left, right, or diagonal from the seat). The following rules are applied to every seat
   * simultaneously:
   *
   * If a seat is empty (L) and there are no occupied seats adjacent to it, the seat becomes
   *   occupied.
   * If a seat is occupied (#) and four or more seats adjacent to it are also occupied, the seat
   *   becomes empty.
   * Otherwise, the seat's state does not change.
   *
   * Floor (.) never changes; seats don't move, and nobody sits on the floor.
   *
   * After one round of these rules, every seat in the example layout becomes occupied:
   *
   * ```
   * #.##.##.##
   * #######.##
   * #.#.#..#..
   * ####.##.##
   * #.##.##.##
   * #.#####.##
   * ..#.#.....
   * ##########
   * #.######.#
   * #.#####.##
   * ```
   *
   * After a second round, the seats with four or more occupied adjacent seats become empty again:
   *
   * #.LL.L#.##
   * #LLLLLL.L#
   * L.L.L..L..
   * #LLL.LL.L#
   * #.LL.LL.LL
   * #.LLLL#.##
   * ..L.L.....
   * #LLLLLLLL#
   * #.LLLLLL.L
   * #.#LLLL.##
   *
   * This process continues for three more rounds:
   *
   * #.##.L#.##
   * #L###LL.L#
   * L.#.#..#..
   * #L##.##.L#
   * #.##.LL.LL
   * #.###L#.##
   * ..#.#.....
   * #L######L#
   * #.LL###L.L
   * #.#L###.##
   *
   * #.#L.L#.##
   * #LLL#LL.L#
   * L.L.L..#..
   * #LLL.##.L#
   * #.LL.LL.LL
   * #.LL#L#.##
   * ..L.L.....
   * #L#LLLL#L#
   * #.LLLLLL.L
   * #.#L#L#.##
   *
   * #.#L.L#.##
   * #LLL#LL.L#
   * L.#.L..#..
   * #L##.##.L#
   * #.#L.LL.LL
   * #.#L#L#.##
   * ..L.L.....
   * #L#L##L#L#
   * #.LLLLLL.L
   * #.#L#L#.##
   *
   * At this point, something interesting happens: the chaos stabilizes and further applications of
   * these rules cause no seats to change state! Once people stop moving around, you count 37
   * occupied seats.
   *
   * Simulate your seating area by applying the seating rules repeatedly until no seats change
   * state. How many seats end up occupied?
   */
  override suspend fun executePart1(input: Reader): Int {
    var seatingArrangement = SeatingArrangement(input.readLines().map { it.toCharArray() }.toList())
    var lastHashCode = -1

    while (seatingArrangement.hashCode() != lastHashCode) {
      lastHashCode = seatingArrangement.hashCode()
      seatingArrangement = seatingArrangement.nextPart1()
    }
    return seatingArrangement.takenSeats
  }

  /**
   * As soon as people start to arrive, you realize your mistake. People don't just care about
   * adjacent seats - they care about the first seat they can see in each of those eight directions!
   *
   * Now, instead of considering just the eight immediately adjacent seats, consider the first seat
   * in each of those eight directions. For example, the empty seat below would see eight occupied
   * seats:
   *
   * ```
   * .......#.
   * ...#.....
   * .#.......
   * .........
   * ..#L....#
   * ....#....
   * .........
   * #........
   * ...#.....
   * ```
   *
   * The leftmost empty seat below would only see one empty seat, but cannot see any of the occupied
   * ones:
   *
   * ```
   * .............
   * .L.L.#.#.#.#.
   * .............
   * ```
   *
   * The empty seat below would see no occupied seats:
   *
   * ```
   * .##.##.
   * #.#.#.#
   * ##...##
   * ...L...
   * ##...##
   * #.#.#.#
   * .##.##.
   * ```
   *
   * Also, people seem to be more tolerant than you expected: it now takes five or more visible
   * occupied seats for an occupied seat to become empty (rather than four or more from the previous
   * rules). The other rules still apply: empty seats that see no occupied seats become occupied,
   * seats matching no rule don't change, and floor never changes.
   *
   * Given the same starting layout as above, these new rules cause the seating area to shift around
   * as follows:
   *
   * ```
   * L.LL.LL.LL
   * LLLLLLL.LL
   * L.L.L..L..
   * LLLL.LL.LL
   * L.LL.LL.LL
   * L.LLLLL.LL
   * ..L.L.....
   * LLLLLLLLLL
   * L.LLLLLL.L
   *
   * L.LLLLL.LL
   * #.##.##.##
   * #######.##
   * #.#.#..#..
   * ####.##.##
   * #.##.##.##
   * #.#####.##
   * ..#.#.....
   * ##########
   * #.######.#
   *
   * #.#####.##
   * #.LL.LL.L#
   * #LLLLLL.LL
   * L.L.L..L..
   * LLLL.LL.LL
   * L.LL.LL.LL
   * L.LLLLL.LL
   * ..L.L.....
   * LLLLLLLLL#
   * #.LLLLLL.L
   *
   * #.LLLLL.L#
   * #.L#.##.L#
   * #L#####.LL
   * L.#.#..#..
   * ##L#.##.##
   * #.##.#L.##
   * #.#####.#L
   * ..#.#.....
   * LLL####LL#
   * #.L#####.L
   *
   * #.L####.L#
   * #.L#.L#.L#
   * #LLLLLL.LL
   * L.L.L..#..
   * ##LL.LL.L#
   * L.LL.LL.L#
   * #.LLLLL.LL
   * ..L.L.....
   * LLLLLLLLL#
   * #.LLLLL#.L
   * #.L#LL#.L#
   *
   * #.L#.L#.L#
   * #LLLLLL.LL
   * L.L.L..#..
   * ##L#.#L.L#
   * L.L#.#L.L#
   * #.L####.LL
   * ..#.#.....
   * LLL###LLL#
   * #.LLLLL#.L
   * #.L#LL#.L#
   *
   * #.L#.L#.L#
   * #LLLLLL.LL
   * L.L.L..#..
   * ##L#.#L.L#
   * L.L#.LL.L#
   * #.LLLL#.LL
   * ..#.L.....
   * LLL###LLL#
   * #.LLLLL#.L
   * #.L#LL#.L#
   * ```
   *
   * Again, at this point, people stop shifting around and the seating area reaches equilibrium.
   * Once this occurs, you count 26 occupied seats.
   *
   * Given the new visibility method and the rule change for occupied seats becoming empty, once
   * equilibrium is reached, how many seats end up occupied?
   */
  override suspend fun executePart2(input: Reader): Int {
    var seatingArrangement = SeatingArrangement(input.readLines().map { it.toCharArray() }.toList())
    var lastHashCode = -1

    while (seatingArrangement.hashCode() != lastHashCode) {
      lastHashCode = seatingArrangement.hashCode()
      seatingArrangement = seatingArrangement.nextPart2()
    }
    return seatingArrangement.takenSeats
  }

  data class SeatingArrangement(val seats: List<CharArray>) {
    private val width = seats[0].size

    val takenSeats: Int
      get() = seats.map {
        it.fold(0) { acc, value ->
          if (value == '#') acc + 1 else acc
        }
      }.sum()

    fun nextPart1(): SeatingArrangement {
      val newSeats = ArrayList<CharArray>(seats.size)
      for (i in seats.indices) {
        val newRow = CharArray(width)
        for (j in 0 until width) {
          var occupiedSeatCount = 0
          var seatCount = 0
          for (k in -1..1) {
            for (l in -1..1) {
              if (k == 0 && l == 0) continue

              if (i + k >= 0 && i + k < seats.size && j + l >= 0 && j + l < width) {
                if (seats[i+k][j+l] == '#') occupiedSeatCount++
                if (seats[i+k][j+l] == 'L' || seats[i+k][j+l] == '#') seatCount++
              }
            }
          }

          newRow[j] = seats[i][j]
          if (seats[i][j] == '#') {
            if (occupiedSeatCount >= 4) newRow[j] = 'L'
            else newRow[j] = '#'
          }
          if (seats[i][j] == 'L') {
            if (occupiedSeatCount == 0) newRow[j] = '#'
            else newRow[j] = 'L'
          }
        }
        newSeats.add(newRow)
      }
      return SeatingArrangement(newSeats)
    }

    fun nextPart2(): SeatingArrangement {
      val newSeats = ArrayList<CharArray>(seats.size)
      for (i in seats.indices) {
        val newRow = CharArray(width)
        for (j in 0 until width) {
          val collisions = VECTORS.asSequence().map { hitsAlong(i, j, it) }.sum()
          newRow[j] = seats[i][j]
          if (seats[i][j] == '#') {
            if (collisions >= 5) newRow[j] = 'L'
            else newRow[j] = '#'
          }
          if (seats[i][j] == 'L') {
            if (collisions == 0) newRow[j] = '#'
            else newRow[j] = 'L'
          }
        }
        newSeats.add(newRow)
      }
      return SeatingArrangement(newSeats)
    }

    private fun hitsAlong(row: Int, col: Int, vector: Pair<Int, Int>): Int {
      if (row + vector.first < 0 || row + vector.first >= seats.size) return 0
      if (col + vector.second < 0 || col + vector.second >= width) return 0
      if (seats[row + vector.first][col + vector.second] == '#') return 1
      if (seats[row + vector.first][col + vector.second] == 'L') return 0
      return hitsAlong(row + vector.first, col + vector.second, vector)
    }

    override fun hashCode(): Int = toString().hashCode()
    override fun toString(): String = seats.joinToString("\n") { String(it) }
    override fun equals(other: Any?): Boolean =
      other is SeatingArrangement && other.hashCode() == hashCode()

    companion object {
      val VECTORS = listOf(
        -1 to 0, // up
        1 to 0, // down
        0 to -1, // left
        0 to 1, // right
        -1 to -1, // up-left
        -1 to 1, // up-right
        1 to -1, // down-left
        1 to 1, // down-right
      )
    }
  }
}