package us.jwf.aoc2015

import java.io.Reader
import us.jwf.aoc.Day

/**
 * AoC 2015 - Day 18
 */
class Day18LikeAGif : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    var lights =
      input.readLines().map { it.map { c -> if (c == '.') 0 else 1 }.toIntArray() }.toTypedArray()

    repeat(100) {
      val newLights = Array(100) { IntArray(100) }

      newLights.indices.forEach { i ->
        newLights[i].indices.forEach { j ->
          var neighbors = 0
          (-1..1).forEach { k ->
            (-1..1).forEach inner@{ l ->
              if (k == 0 && l == 0) return@inner
              if (i + k in newLights.indices && j + l in newLights.indices) {
                neighbors += lights[i + k][j + l]
              }
            }
          }
          newLights[i][j] = if (lights[i][j] == 1) {
            if (neighbors == 2 || neighbors == 3) 1
            else 0
          } else {
            if (neighbors == 3) 1
            else 0
          }
        }
      }

      lights = newLights
    }

    return lights.sumOf { it.sum() }
  }

  override suspend fun executePart2(input: Reader): Int {
    var lights =
      input.readLines().map { it.map { c -> if (c == '.') 0 else 1 }.toIntArray() }.toTypedArray()
    lights[0][0] = 1
    lights[99][0] = 1
    lights[0][99] = 1
    lights[99][99] = 1

    repeat(100) {
      val newLights = Array(100) { IntArray(100) }

      newLights.indices.forEach { i ->
        newLights[i].indices.forEach { j ->
          var neighbors = 0
          (-1..1).forEach { k ->
            (-1..1).forEach inner@{ l ->
              if (k == 0 && l == 0) return@inner
              if (i + k in newLights.indices && j + l in newLights.indices) {
                neighbors += lights[i + k][j + l]
              }
            }
          }
          newLights[i][j] = if (lights[i][j] == 1) {
            if (neighbors == 2 || neighbors == 3) 1
            else 0
          } else {
            if (neighbors == 3) 1
            else 0
          }
        }
      }
      newLights[0][0] = 1
      newLights[99][0] = 1
      newLights[0][99] = 1
      newLights[99][99] = 1

      lights = newLights
    }

    return lights.sumOf { it.sum() }
  }
}
