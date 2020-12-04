package us.jwf.aoc2019

import java.io.Reader
import kotlin.math.abs
import us.jwf.aoc.Day

class Day03CrossedWires : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    val wires =
      input.readLines().take(2)
        .map { raw ->
          val instructions = raw.split(",").map { Instruction.parse(it) }
          val firstSegment = WireSegment.firstSegment(instructions.first())
          instructions.drop(1).scan(firstSegment) { last, next -> last.nextSegment(next) }
        }

    var min = Int.MAX_VALUE to Int.MAX_VALUE
    for (segment1 in wires[0]) {
      for (segment2 in wires[1]) {
        val cross = (segment1 cross segment2) ?: continue
        if (cross == 0 to 0) continue
        if (abs(cross.first) + abs(cross.second) < abs(min.first.toLong()) + abs(min.second)) {
          min = cross
        }
      }
    }
    return abs(min.first) + abs(min.second)
  }

  override suspend fun executePart2(input: Reader): Int {
    val wires =
      input.readLines().take(2)
        .map { raw ->
          val instructions = raw.split(",").map { Instruction.parse(it) }
          val firstSegment = WireSegment.firstSegment(instructions.first())
          instructions.drop(1).scan(firstSegment) { last, next -> last.nextSegment(next) }
        }

    var min = Int.MAX_VALUE

    var wire1Steps = 0
    for (segment1 in wires[0]) {
      wire1Steps += segment1.len
      var wire2Steps = 0
      for (segment2 in wires[1]) {
        wire2Steps += segment2.len
        val cross = segment1 cross segment2
        if (cross == null || cross == 0 to 0) continue

        val stepsIntoSegment1 = segment1.stepsTo(cross.first, cross.second)
        val stepsIntoSegment2 = segment2.stepsTo(cross.first, cross.second)
        val totalSteps = wire1Steps + wire2Steps -
          (segment1.len - stepsIntoSegment1) -
          (segment2.len - stepsIntoSegment2)
        if (totalSteps < min) {
          min = totalSteps
        }
      }
    }

    return min
  }
}

enum class Direction {
  Up, Right, Left, Down;

  val opposite: Direction
    get() = when (this) {
      Up -> Down
      Right -> Left
      Left -> Right
      Down -> Up
    }
}

data class Instruction(val dir: Direction, val len: Int) {
  companion object {
    fun parse(strRepr: String): Instruction {
      val dirChar = strRepr[0]
      val len = strRepr.substring(1).toInt(10)
      return when (dirChar) {
        'U' -> Instruction(Direction.Up, len)
        'R' -> Instruction(Direction.Right, len)
        'L' -> Instruction(Direction.Left, len)
        'D' -> Instruction(Direction.Down, len)
        else -> throw IllegalArgumentException("Bad string")
      }
    }
  }
}

@Suppress("DataClassPrivateConstructor")
data class WireSegment private constructor(
  val x: Int,
  val y: Int,
  val len: Int,
  val dir: Direction
) {
  private val canonical = when (dir) {
    Direction.Right, Direction.Up -> this
    Direction.Left -> WireSegment(x - len, y, len, Direction.Right)
    Direction.Down -> WireSegment(x, y - len, len, Direction.Up)
  }

  fun nextSegment(instruction: Instruction): WireSegment =
    when (dir) {
      Direction.Up -> copy(x = x, y = y + len, len = instruction.len, dir = instruction.dir)
      Direction.Right -> copy(x = x + len, y = y, len = instruction.len, dir = instruction.dir)
      Direction.Left -> copy(x = x - len, y = y, len = instruction.len, dir = instruction.dir)
      Direction.Down -> copy(x = x, y = y - len, len = instruction.len, dir = instruction.dir)
    }

  fun stepsTo(x: Int, y: Int): Int {
    if (x == this.x && y == this.y) return 0

    return when (dir) {
      Direction.Up -> {
        if (this.x == x) y - this.y
        else -1
      }
      Direction.Right -> {
        if (this.y == y) x - this.x
        else -1
      }
      Direction.Left -> {
        if (this.y == y) this.x - x
        else -1
      }
      Direction.Down -> {
        if (this.x == x) this.y - y
        else -1
      }
    }
  }

  infix fun cross(other: WireSegment): Pair<Int, Int>? = when {
    this isParallelTo other -> this.canonical parallelCross other.canonical
    else -> this.canonical perpindicularCross other.canonical
  }

  private infix fun parallelCross(other: WireSegment): Pair<Int, Int>? {
    return when (dir) {
      Direction.Up -> {
        if (x != other.x) null
        else if (y <= other.y && y + len >= other.y + other.len) {
          // This segment wraps the other. Return the min distance of the endpoints of the other.
          val a = other.x to other.y
          val b = other.x to (other.y + other.len)
          if (a.first == a.second && a.first == 0) b
          else if (b.first == b.second && b.first == 0) b
          else if (abs(a.second) <= abs(b.second)) a
          else b
        } else if (y <= other.y && y + len < other.y + other.len) {
          // This segment is before the other, but ends within the other.
          val a = other.x to other.y
          val b = other.x to (y + len)
          if (a.first == a.second && a.first == 0) b
          else if (b.first == b.second && b.first == 0) b
          else if (abs(a.second) <= abs(b.second)) a
          else b
        } else if (y < other.y && y + len < other.y) null
        else other parallelCross this
      }
      Direction.Right -> {
        if (y != other.y) null
        else if (x <= other.x && x + len >= other.x + other.len) {
          // This segment wraps the other. Return the min distance of the endpoints of the other.
          val a = other.x to other.y
          val b = (other.x + other.len) to other.y
          if (a.first == a.second && a.first == 0) b
          else if (b.first == b.second && b.first == 0) b
          else if (abs(a.first) <= abs(b.first)) a
          else b
        } else if (x <= other.x && x + len < other.x + other.len) {
          // This segment is before the other, but ends within the other.
          val a = other.x to other.y
          val b = (x + len) to other.y
          if (a.first == a.second && a.first == 0) b
          else if (b.first == b.second && b.first == 0) b
          else if (abs(a.first) <= abs(b.first)) a
          else b
        } else if (x < other.x && x + len < other.x) null
        else other parallelCross this
      }
      Direction.Left, Direction.Down -> canonical parallelCross other.canonical
    }
  }

  private infix fun perpindicularCross(other: WireSegment): Pair<Int, Int>? {
    return when (dir) {
      Direction.Up -> {
        val xOverlap = x >= other.x && x <= other.x + other.len
        val yOverlap = other.y >= y && other.y <= y + len

        if (xOverlap && yOverlap) x to other.y else null
      }
      Direction.Right -> other perpindicularCross this
      Direction.Left, Direction.Down -> canonical perpindicularCross other.canonical
    }
  }

  private infix fun isParallelTo(other: WireSegment): Boolean =
    dir == other.dir || dir.opposite == other.dir

  companion object {
    fun firstSegment(instruction: Instruction): WireSegment =
      WireSegment(0, 0, instruction.len, instruction.dir)
  }
}
