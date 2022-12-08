package us.jwf.aoc2022

import java.io.Reader
import us.jwf.aoc.Day

class Day08TreetopTreeHouse : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    val trees = input.readLines()
      .map { it.map { c -> c.digitToInt() }.toTypedArray() }
      .toTypedArray()

    fun isVisible(i: Int, j: Int): Boolean {
      val myHeight = trees[i][j]
      var xP = 1
      var xN = -1
      var yP = 1
      var yN = -1
      do {
        var moved = false
        if (i + xP < trees.size && trees[i + xP][j] < myHeight) {
          xP++
          moved = true
        }
        if (i + xN >= 0 && trees[i + xN][j] < myHeight) {
          xN--
          moved = true
        }
        if (j + yP < trees[0].size && trees[i][j + yP] < myHeight) {
          yP++
          moved = true
        }
        if (j + yN >= 0 && trees[i][j + yN] < myHeight) {
          yN--
          moved = true
        }
      } while (moved)
      return i + xP == trees.size || i + xN == -1 || j + yP == trees[0].size || j + yN == -1
    }

    var visible = trees.size * 2 + trees[0].size * 2 - 4
    (1 until (trees.size - 1)).forEach { i ->
      (1 until (trees[0].size - 1)).forEach { j ->
        if (isVisible(i, j)) visible++
      }
    }
    return visible
  }

  override suspend fun executePart2(input: Reader): Int {
    val trees = input.readLines()
      .map { it.map { c -> c.digitToInt() }.toTypedArray() }
      .toTypedArray()

    fun scenicScore(i: Int, j: Int): Int {
      val myHeight = trees[i][j]
      var xPScore = 0
      var xPBlocked = false
      var xP = 1
      var xNScore = 0
      var xNBlocked = false
      var xN = -1
      var yPScore = 0
      var yPBlocked = false
      var yP = 1
      var yNScore = 0
      var yNBlocked = false
      var yN = -1
      do {
        if (!xPBlocked && i + xP <= trees.size - 1) {
          xPBlocked = trees[i + xP][j] >= myHeight || i + xP == trees.size - 1
          xP++
          xPScore++
        }
        if (!xNBlocked && i + xN >= 0) {
          xNBlocked = trees[i + xN][j] >= myHeight || i + xN == 0
          xN--
          xNScore++
        }
        if (!yPBlocked && j + yP <= trees[0].size - 1) {
          yPBlocked = trees[i][j + yP] >= myHeight || j + yP == trees[0].size - 1
          yP++
          yPScore++
        }
        if (!yNBlocked && j + yN >= 0) {
          yNBlocked = trees[i][j + yN] >= myHeight || j + yN == 0
          yN--
          yNScore++
        }
      } while (!xPBlocked || !xNBlocked || !yPBlocked || !yNBlocked)
      return xPScore * xNScore * yPScore * yNScore
    }

    var maxScenicScore = 0
    (1 until (trees.size - 1)).forEach { i ->
      (1 until (trees[0].size - 1)).forEach { j ->
        val score = scenicScore(i, j)
        maxScenicScore = maxOf(score, maxScenicScore)
      }
    }
    return maxScenicScore
  }
}