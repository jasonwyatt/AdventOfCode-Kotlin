package us.jwf.aoc2020

import java.io.Reader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toSet
import us.jwf.aoc.Day

/**
 * Day 17 of AoC 2020
 */
class Day17ConwayCubes : Day<Int, Int> {
  /**
   * As your flight slowly drifts through the sky, the Elves at the Mythical Information Bureau at
   * the North Pole contact you. They'd like some help debugging a malfunctioning experimental
   * energy source aboard one of their super-secret imaging satellites.
   *
   * The experimental energy source is based on cutting-edge technology: a set of Conway Cubes
   * contained in a pocket dimension! When you hear it's having problems, you can't help but agree
   * to take a look.
   *
   * The pocket dimension contains an infinite 3-dimensional grid. At every integer 3-dimensional
   * coordinate (x,y,z), there exists a single cube which is either active or inactive.
   *
   * In the initial state of the pocket dimension, almost all cubes start inactive. The only
   * exception to this is a small flat region of cubes (your puzzle input); the cubes in this
   * region start in the specified active (#) or inactive (.) state.
   *
   * The energy source then proceeds to boot up by executing six cycles.
   *
   * Each cube only ever considers its neighbors: any of the 26 other cubes where any of their
   * coordinates differ by at most 1. For example, given the cube at x=1,y=2,z=3, its neighbors
   * include the cube at x=2,y=2,z=2, the cube at x=0,y=2,z=3, and so on.
   *
   * During a cycle, all cubes simultaneously change their state according to the following rules:
   *
   * * If a cube is active and exactly 2 or 3 of its neighbors are also active, the cube remains
   *   active. Otherwise, the cube becomes inactive.
   * * If a cube is inactive but exactly 3 of its neighbors are active, the cube becomes active.
   *   Otherwise, the cube remains inactive.
   *
   * The engineers responsible for this experimental energy source would like you to simulate the
   * pocket dimension and determine what the configuration of cubes should be at the end of the
   * six-cycle boot process.
   */
  override suspend fun executePart1(input: Reader): Int {
    val active = mutableSetOf<Cube>()
    input.readLines().forEachIndexed { x, line ->
      line.forEachIndexed { y, c ->
        if (c == '#') {
          active.add(Cube(x, y, 0))
        }
      }
    }
    val dimension = Dimension(active).next().next().next().next().next().next()
    return dimension.active.size
  }

  /**
   * For some reason, your simulated results don't match what the experimental energy source
   * engineers expected. Apparently, the pocket dimension actually has four spatial dimensions,
   * not three.
   *
   * The pocket dimension contains an infinite 4-dimensional grid. At every integer 4-dimensional
   * coordinate (x,y,z,w), there exists a single cube (really, a hypercube) which is still either
   * active or inactive.
   *
   * Each cube only ever considers its neighbors: any of the 80 other cubes where any of their
   * coordinates differ by at most 1. For example, given the cube at x=1,y=2,z=3,w=4, its neighbors
   * include the cube at x=2,y=2,z=3,w=3, the cube at x=0,y=2,z=3,w=4, and so on.
   *
   * The initial state of the pocket dimension still consists of a small flat region of cubes.
   * Furthermore, the same rules for cycle updating still apply: during each cycle, consider the
   * number of active neighbors of each cube.
   */
  override suspend fun executePart2(input: Reader): Int {
    val active = mutableSetOf<Cube>()
    input.readLines().forEachIndexed { x, line ->
      line.forEachIndexed { y, c ->
        if (c == '#') {
          active.add(Cube(x, y, 0, 0))
        }
      }
    }
    val dimension = Dimension(active).next().next().next().next().next().next()
    return dimension.active.size
  }

  data class Dimension(val active: Set<Cube>) {
    suspend fun next(): Dimension {
      return Dimension(
        Extents.from(active).cubeFlow
          .filter { testCube ->
            val isActive = testCube in active
            val activeNeighbors = testCube.neighbors.count { neighbor -> neighbor in active }
            if (isActive) {
              activeNeighbors == 2 || activeNeighbors == 3
            } else {
              activeNeighbors == 3
            }
          }
          .toSet()
      )
    }
  }

  data class Extents(val x: IntRange, val y: IntRange, val z: IntRange, val w: IntRange?) {
    val cubeFlow = flow {
      x.forEach { i ->
        y.forEach { j ->
          z.forEach { k ->
            if (w == null) emit(Cube(i, j, k))
            else {
              w.forEach { p ->
                emit(Cube(i, j, k, p))
              }
            }
          }
        }
      }
    }

    companion object {
      fun from(activeCubes: Set<Cube>): Extents {
        var minX = Int.MAX_VALUE
        var maxX = Int.MIN_VALUE
        var minY = Int.MAX_VALUE
        var maxY = Int.MIN_VALUE
        var minZ = Int.MAX_VALUE
        var maxZ = Int.MIN_VALUE
        var minW = Int.MAX_VALUE
        var maxW = Int.MIN_VALUE
        var hasW = false
        activeCubes.forEach { (x, y, z, w) ->
          minX = minOf(x, minX)
          maxX = maxOf(x, maxX)
          minY = minOf(y, minY)
          maxY = maxOf(y, maxY)
          minZ = minOf(z, minZ)
          maxZ = maxOf(z, maxZ)
          w?.let {
            hasW = true
            minW = minOf(it, minW)
            maxW = maxOf(it, maxW)
          }
        }
        return Extents(
          (minX - 1)..(maxX + 1),
          (minY - 1)..(maxY + 1),
          (minZ - 1)..(maxZ + 1),
          if (hasW) (minW - 1)..(maxW + 1) else null
        )
      }
    }
  }

  data class Cube(val x: Int, val y: Int, val z: Int, val w: Int? = null) {
    val neighbors = flow {
      (-1..1).forEach { i ->
        (-1..1).forEach { j ->
          (-1..1).forEach kLoop@{ k ->
            if (w == null) {
              if (i == 0 && j == 0 && k == 0) return@kLoop
              emit(Cube(x + i, y + j, z + k))
            } else {
              (-1..1).forEach pLoop@{ p ->
                if (i == 0 && j == 0 && k == 0 && p == 0) return@pLoop
                emit(Cube(x + i, y + j, z + k, w + p))
              }
            }
          }
        }
      }
    }
  }
}