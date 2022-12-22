package us.jwf.aoc2022

import java.io.Reader
import java.util.PriorityQueue
import us.jwf.aoc.Day

class Day12HillClimbing : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    val map = input.readLines().map { it.toCharArray() }.toTypedArray()
    val start = map.findStart()
    val queue = PriorityQueue<PointDistance>()
    queue.offer(PointDistance(start, 0))
    val visited = mutableSetOf(start)
    while (queue.isNotEmpty()) {
      val current = queue.poll()
      val (i, j) = current.point
      val currentChar = map[i][j].takeIf { it != 'S' } ?: 'a'
      if (currentChar == 'E') return current.distance
      map.neighbors(current.point)
        .filter { it !in visited }
        .filter { (nextI, nextJ) ->
          val nextChar = map[nextI][nextJ].takeIf { it != 'E' } ?: 'z'
          nextChar <= currentChar + 1
        }
        .forEach {
          visited.add(it)
          queue.offer(PointDistance(it, current.distance + 1))
        }
    }
    throw IllegalArgumentException("Bad input, end not found")
  }

  override suspend fun executePart2(input: Reader): Int {
    val map = input.readLines().map { it.toCharArray() }.toTypedArray()
    val end = map.findEnd()
    val queue = PriorityQueue<PointDistance>()
    queue.offer(PointDistance(end, 0))
    val visited = mutableSetOf(end)
    while (queue.isNotEmpty()) {
      val current = queue.poll()
      val (i, j) = current.point
      val currentChar = map[i][j].takeIf { it != 'E' } ?: 'z'
      if (currentChar == 'S' || currentChar == 'a') return current.distance
      map.neighbors(current.point)
        .filter { it !in visited }
        .filter { (nextI, nextJ) ->
          val nextChar = map[nextI][nextJ].takeIf { it != 'S' } ?: 'a'
          currentChar <= nextChar + 1
        }
        .forEach {
          visited.add(it)
          queue.offer(PointDistance(it, current.distance + 1))
        }
    }
    throw IllegalArgumentException("Bad input, end not found")
  }

  data class PointDistance(val point: Pair<Int, Int>, val distance: Int) : Comparable<PointDistance> {
    override fun compareTo(other: PointDistance): Int = distance - other.distance
  }

  fun Array<CharArray>.findStart(): Pair<Int, Int> {
    forEachIndexed { i, chars ->
      chars.forEachIndexed { j, c -> if (c == 'S') return i to j }
    }
    throw IllegalArgumentException("Bad input, no start found.")
  }

  fun Array<CharArray>.findEnd(): Pair<Int, Int> {
    forEachIndexed { i, chars ->
      chars.forEachIndexed { j, c -> if (c == 'E') return i to j }
    }
    throw IllegalArgumentException("Bad input, no end found.")
  }

  fun Array<CharArray>.neighbors(point: Pair<Int, Int>): Sequence<Pair<Int, Int>> = sequence {
    val (i, j) = point
    if (i - 1 >= 0) yield((i - 1) to j)
    if (i + 1 < size) yield((i + 1) to j)
    if (j - 1 >= 0) yield(i to (j - 1))
    if (j + 1 < this@neighbors[0].size) yield(i to (j + 1))
  }
}