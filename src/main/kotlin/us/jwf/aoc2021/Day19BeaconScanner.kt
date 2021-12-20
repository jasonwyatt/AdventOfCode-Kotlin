package us.jwf.aoc2021

import java.io.Reader
import kotlin.math.abs
import kotlin.math.sqrt
import us.jwf.aoc.Day

/**
 * AoC 2021 - Day 19
 */
class Day19BeaconScanner : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    val scanners = Scanner.parse(input.readLines())

    var mergedScanners = scanners.toSet()
    println("Before (${mergedScanners.size}): $mergedScanners")
    var loops = 0

    val scannerLocations = mutableListOf<Vec>(
      Vec(intArrayOf(0, 0, 0))
    )

    while(mergedScanners.size > 1) {
      val newMerged = mutableSetOf<Scanner>()
      val skipIds = mutableSetOf<String>()
      val mergedAsList = mergedScanners.toList()
      var foundMerge = false
      mergedAsList.withIndex().forEach { (index, i) ->
        if (foundMerge) return@forEach
        mergedAsList.drop(index + 1).forEach inner@{ j ->
          if (foundMerge) return@inner
          val overlap = i.findOverlap(j)
          if (overlap != null) {
            val (jOverlap, jOffset) = overlap
            skipIds += i.id
            skipIds += j.id
            scannerLocations.add(jOffset)

            val mergedBeacons = i.relativeBeacons.toMutableSet()
            jOverlap.relativeBeacons.forEach { beacon -> mergedBeacons.add(beacon + jOffset) }
            val merged = Scanner("${i.id}-${j.id}", mergedBeacons.toList())
            newMerged.add(merged)
            foundMerge = true
          }
        }
      }

      mergedScanners.forEach { s ->
        if (s.id !in skipIds) newMerged.add(s)
      }
      if (newMerged.size == mergedScanners.size) break
      mergedScanners = newMerged
      println("Result of merge (${mergedScanners.size})")//: $mergedScanners")
      loops++
    }
    println()
    println()
    println("Max Manhattan")
    var maxManhattan = 0
    scannerLocations.withIndex().forEach { (i, loc) ->
      scannerLocations.drop(i + 1).forEach {
        val diff = it - loc
        val manhattan = diff.components.map { c->abs(c) }.sum()
        maxManhattan = maxOf(maxManhattan, manhattan)
      }
    }
    println("  $maxManhattan")
    return mergedScanners.first().relativeBeacons.size
  }

  override suspend fun executePart2(input: Reader): Int {
    TODO("Not yet implemented")
  }

  data class Scanner(val id: String, val relativeBeacons: List<Vec>) {
    override fun hashCode(): Int {
      return relativeBeacons.hashCode()
    }
    override fun equals(other: Any?): Boolean {
      return (other as? Scanner)?.relativeBeacons == relativeBeacons
    }

    // If this scanner overlaps with the other scanner - return the vector
    fun findOverlap(other: Scanner): Pair<Scanner, Vec>? {
      val myBeaconsByNeighbors = beaconsByNeighbors
      other.rotations.withIndex().forEach { (rotationIndex, rotatedOther) ->
        val matchingBeaconIndices = mutableListOf<Pair<Int, Int>>()
        val otherBeaconsByNeighbors = rotatedOther.beaconsByNeighbors

        myBeaconsByNeighbors.withIndex().forEach { (i, iNeighbors) ->
          otherBeaconsByNeighbors.withIndex().forEach { (j, jNeighbors) ->
            if (iNeighbors.matches(jNeighbors, 3)) matchingBeaconIndices.add(i to j)
          }
        }

        if (matchingBeaconIndices.size >= minOf(12, relativeBeacons.size, other.relativeBeacons.size)) {
          println("Found match of ${this.id} to ${other.id} at rotation: $rotationIndex")
          val (mine, theirs) = matchingBeaconIndices[0]
          val offset = relativeBeacons[mine] - rotatedOther.relativeBeacons[theirs]
          return rotatedOther to offset
        }
      }
      return null
    }

    val rotations: List<Scanner> by lazy {
      val result = mutableListOf<Scanner>()
      repeat(24) { rotationId ->
        result.add(copy(relativeBeacons = relativeBeacons.map { it.rotations[rotationId] }))
      }
      result
    }

    val beaconsByNeighbors: List<List<Vec>> by lazy {
      val result = mutableListOf<List<Vec>>()
      relativeBeacons.indices.forEach { i ->
        val iNeighbors = mutableListOf<Vec>()
        relativeBeacons.indices.forEach inner@{ j ->
          if (i == j) return@inner
          iNeighbors.add(relativeBeacons[j] - relativeBeacons[i])
        }
        result.add(iNeighbors.sortedBy { it.length })//.take(12))
      }
      result
    }

    companion object {
      // I think something is wrong with this?
      fun List<Vec>.matches(other: List<Vec>, threshold: Int = other.size): Boolean {
        var count = 0
        indices.forEach { i ->
          other.indices.forEach { j ->
            if (this[i] == other[j]) count++
          }
        }
        return count >= threshold
      }


      fun parse(lines: List<String>): List<Scanner> {
        val result = mutableListOf<Scanner>()
        var id = 0
        var beaconsSoFar = mutableListOf<Vec>()
        var i = 1
        while (i < lines.size) {
          val line = lines[i]
          if (line.isEmpty()) {
            result.add(Scanner("$id", beaconsSoFar))
            id++
            beaconsSoFar = mutableListOf()
            i++
          } else {
            val coords = line.split(",").map { it.toInt() }.toIntArray()
            beaconsSoFar.add(Vec(coords))
          }
          i++
        }
        result.add(Scanner("$id", beaconsSoFar))
        return result
      }
    }
  }

  data class Vec(val components: IntArray) {
    val length: Double = sqrt(components.map { it * it }.sum().toDouble())

    operator fun minus(other: Vec): Vec =
      Vec(IntArray(components.size) { components[it] - other.components[it] })

    operator fun plus(other: Vec): Vec =
      Vec(IntArray(components.size) { components[it] + other.components[it] })

    operator fun unaryMinus(): Vec =
      Vec(IntArray(components.size) { -components[it] })

    override fun toString(): String = "[${components.joinToString(",")}]"

    override fun equals(other: Any?): Boolean {
      if (other !is Vec) return false
      return other.components[0] == components[0] &&
        other.components[1] == components[1] &&
        other.components[2] == components[2]
    }

    val rotations: List<Vec> by lazy {
      val (x, y, z) = components
      fun roll(v: IntArray) = intArrayOf(v[0], v[2], -v[1])
      fun turn(v: IntArray) = intArrayOf(-v[1], v[0], v[2])
      val result = mutableListOf<Vec>()
      var vec = components
      sequence {
        repeat(2) {
          repeat(3) {
            vec = roll(vec)
            yield(Vec(vec))
            repeat(3) {
              vec = turn(vec)
              yield(Vec(vec))
            }
          }
          vec = roll(turn(roll(vec)))
        }
      }.toList()
      /*
      listOf(
        Vec(intArrayOf(x, y, z)),
        Vec(intArrayOf(-x, y, z)),
        Vec(intArrayOf(x, -y, z)),
        Vec(intArrayOf(-x, -y, z)),
        Vec(intArrayOf(x, y, -z)),
        Vec(intArrayOf(-x, y, -z)),
        Vec(intArrayOf(x, -y, -z)),
        Vec(intArrayOf(-x, -y, -z)),

        Vec(intArrayOf(y, z, x)),
        Vec(intArrayOf(-y, z, x)),
        Vec(intArrayOf(y, -z, x)),
        Vec(intArrayOf(-y, -z, x)),
        Vec(intArrayOf(y, z, -x)),
        Vec(intArrayOf(-y, z, -x)),
        Vec(intArrayOf(y, -z, -x)),
        Vec(intArrayOf(-y, -z, -x)),

        Vec(intArrayOf(z, x, y)),
        Vec(intArrayOf(-z, x, y)),
        Vec(intArrayOf(z, -x, y)),
        Vec(intArrayOf(-z, -x, y)),
        Vec(intArrayOf(z, x, -y)),
        Vec(intArrayOf(-z, x, -y)),
        Vec(intArrayOf(z, -x, -y)),
        Vec(intArrayOf(-z, -x, -y)),
      )

       */
    }

    fun rotate(gamma: Int, beta: Int, alpha: Int): Vec {
      val x = cosTurns(alpha) * cosTurns(beta) * components[0] +
        (cosTurns(alpha) * sinTurns(beta) * sinTurns(gamma) - sinTurns(alpha) * cosTurns(gamma)) * components[1] +
        (cosTurns(alpha) * sinTurns(beta) * cosTurns(gamma) + sinTurns(alpha) * sinTurns(gamma)) * components[2]
      val y = sinTurns(alpha) * cosTurns(beta) * components[0] +
        (sinTurns(alpha) * sinTurns(beta) * sinTurns(gamma) + cosTurns(alpha) * cosTurns(gamma)) * components[1] +
        (sinTurns(alpha) * sinTurns(beta) * cosTurns(gamma) - cosTurns(alpha) * sinTurns(gamma)) * components[2]
      val z = -sinTurns(beta) * components[0] +
        cosTurns(beta) * sinTurns(gamma) * components[1] +
        cosTurns(beta) * sinTurns(gamma) * components[2]

      return Vec(intArrayOf(x, y, z))
    }

    override fun hashCode(): Int = components.contentHashCode()

    companion object {
      fun cosTurns(turns: Int): Int {
        return when (turns) {
          1 -> 0
          2 -> -1
          3 -> 0
          else -> 1
        }
      }
      fun sinTurns(turns: Int): Int {
        return when (turns) {
          1 -> 1
          2 -> 0
          3 -> -1
          else -> 0
        }
      }
    }
  }
}