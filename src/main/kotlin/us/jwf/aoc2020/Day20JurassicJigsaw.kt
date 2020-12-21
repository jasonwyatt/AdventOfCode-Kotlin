package us.jwf.aoc2020

import java.io.Reader
import java.util.*
import kotlin.collections.HashSet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.transform
import us.jwf.aoc.Day
import us.jwf.aoc.product
import us.jwf.aoc.toLineFlow

/**
 * Day 20 of AoC 2020
 */
class Day20JurassicJigsaw : Day<Long, Long> {
  override suspend fun executePart1(input: Reader): Long {
    val tiles = input.toTileFlow().toList()
    val matchCounts = mutableMapOf<Int, List<Long>>()

    tiles.forEach { first ->
      var matches = 0
      var matchedTiles = 0
      tiles.forEach inner@{ second ->
        if (first == second) return@inner
        matches += first.matchTo(second).count().also { if (it > 0) matchedTiles++ }
      }
      matchCounts[matchedTiles] = (matchCounts[matchedTiles] ?: emptyList()) + listOf(first.id)
    }

    return matchCounts[2]!!.product()
  }

  override suspend fun executePart2(input: Reader): Long {
    val tiles = input.toTileFlow().toList()
    val matchCounts = mutableMapOf<Int, List<Tile>>()

    tiles.forEach { first ->
      var matches = 0
      var matchedTiles = 0
      tiles.forEach inner@{ second ->
        if (first == second) return@inner
        matches += first.matchTo(second).count().also { if (it > 0) matchedTiles++ }
      }
      matchCounts[matchedTiles] = (matchCounts[matchedTiles] ?: emptyList()) + listOf(first)
    }

    // Corners are at matchCounts[2].. start at corner where we can find a right and a bottom.
    val imageTiles = mutableListOf<MutableList<Tile>>()
    imageTiles.add(mutableListOf())
    val unvisitedTiles: MutableSet<Tile> = HashSet(tiles)
    var currentTile: Tile? = matchCounts[2]?.find { tile ->
      val hasRight = tiles.find { other ->
        tile != other && tile.matchRightTo(other) != null
      } != null
      val hasBottom = tiles.find { other ->
        tile != other && tile.matchBottomTo(other) != null
      } != null

      hasBottom && hasRight
    }

    while (currentTile != null) {
      unvisitedTiles.remove(currentTile)
      imageTiles.last().add(currentTile)

      val rightTile = unvisitedTiles.mapNotNull { currentTile!!.matchRightTo(it) }.firstOrNull()
      if (rightTile == null) {
        val firstOfThisRow = imageTiles.last().first()
        currentTile = unvisitedTiles
          .mapNotNull { firstOfThisRow.matchBottomTo(it) }
          .firstOrNull()
        if (currentTile != null) {
          imageTiles.add(mutableListOf())
        }
      } else {
        currentTile = rightTile
      }
    }

    val tileSize = imageTiles[0][0].image.size
    val imageDimension = imageTiles.size * (tileSize - 2)
    val image = Array(imageDimension) { row ->
      BooleanArray(imageDimension) { col ->
        val tileRow = row / (tileSize - 2)
        val tilePixelRow = row % (tileSize - 2) + 1
        val tileCol = col / (tileSize - 2)
        val tilePixelCol = col % (tileSize - 2) + 1
        imageTiles[tileRow][tileCol].image[tilePixelRow][tilePixelCol]
      }
    }

    val monster = """
        |                  # 
        |#    ##    ##    ###
        | #  #  #  #  #  #   
      """.trimMargin()
      .split("\n")
      .map { it.map { c -> c == '#' }.toBooleanArray() }
      .toTypedArray()
    val monsterTile = Tile(-1L, monster)
    val imageTile = Tile(-1L, image)
    val imageFilled = image.sumBy { row -> row.sumBy { if (it) 1 else 0 } }
    return monsterTile.orientations
      .mapNotNull { m ->
        var monstersCounted = 0
        for (rowOffset in 0 until (imageDimension - m.rows)) {
          for (colOffset in 0 until (imageDimension - m.cols)) {
            if (imageTile.contains(m, rowOffset, colOffset)) monstersCounted++
          }
        }
        if (monstersCounted > 0) {
          imageFilled - monstersCounted * 15L
        } else null
      }
      .toList()
      .maxByOrNull { it }!!
  }

  data class Tile(val id: Long, val image: Array<BooleanArray>) {
    val rows: Int
      get() = image.size
    val cols: Int
      get() = image[0].size

    val orientations: Flow<Tile> = flow {
      emit(this@Tile)
      rotate().also { emit(it) }
        .rotate().also { emit(it) }
        .rotate().also { emit(it) }
      val horizontalFlip = flipHorizontal()
      emit(horizontalFlip)
      horizontalFlip.rotate().also { emit(it) }
        .rotate().also { emit(it) }
        .rotate().also { emit(it) }
      val verticalFlip = flipVertical()
      emit(verticalFlip)
      verticalFlip.rotate().also { emit(it) }
        .rotate().also { emit(it) }
        .rotate().also { emit(it) }
      val bothFlip = flipVertical().flipHorizontal()
      emit(bothFlip)
      bothFlip.rotate().also { emit(it) }
        .rotate().also { emit(it) }
        .rotate().also { emit(it) }
    }

    fun contains(smaller: Tile, offsetRow: Int, offsetCol: Int): Boolean {
      for (smallerRow in 0 until smaller.rows) {
        for (smallerCol in 0 until smaller.cols) {
          if (smaller.image[smallerRow][smallerCol] && !image[smallerRow + offsetRow][smallerCol + offsetCol]) {
            return false
          }
        }
      }
      return true
    }

    fun rotate(): Tile {
      // Rotate 90 degrees to the left.
      // Rows become Columns.
      val nextArray = Array(image[0].size) { BooleanArray(image.size) { false } }
      image.indices.forEach { rowIndex ->
        image[rowIndex].indices.forEach { colIndex ->
          nextArray[colIndex][rowIndex] = image[rowIndex][colIndex]
        }
      }
      return Tile(id, nextArray)
    }

    fun flipVertical(): Tile {
      val nextArray = Array(image.size) { BooleanArray(image[0].size) { false } }
      image.indices.forEach { rowIndex ->
        image[rowIndex].indices.forEach { colIndex ->
          nextArray[nextArray.size - 1 - rowIndex][colIndex] = image[rowIndex][colIndex]
        }
      }
      return Tile(id, nextArray)
    }

    fun flipHorizontal(): Tile {
      val nextArray = Array(image.size) { BooleanArray(image[0].size) { false } }
      image.indices.forEach { rowIndex ->
        image[rowIndex].indices.forEach { colIndex ->
          nextArray[rowIndex][nextArray[rowIndex].size - 1 - colIndex] = image[rowIndex][colIndex]
        }
      }
      return Tile(id, nextArray)
    }

    fun topMatchesBottomOf(other: Tile): Boolean {
      return image[0].withIndex().all { (col, value) ->
        other.image[other.image.size - 1][col] == value
      }
    }

    fun bottomMatchesTopOf(other: Tile): Boolean = other.topMatchesBottomOf(this)

    fun rightMatchesLeftOf(other: Tile): Boolean {
      return image.indices.all { rowIndex ->
        other.image[rowIndex][0] == image[rowIndex][image[rowIndex].size - 1]
      }
    }

    fun leftMatchesRightOf(other: Tile): Boolean = other.rightMatchesLeftOf(this)

    suspend fun matchRightTo(other: Tile): Tile? {
      return other.orientations
        .filter { rightMatchesLeftOf(it) }
        .firstOrNull()
    }

    suspend fun matchBottomTo(other: Tile): Tile? {
      return other.orientations
        .filter { bottomMatchesTopOf(it) }
        .firstOrNull()
    }

    suspend fun matchTo(other: Tile): List<Tile> {
      return other.orientations
        .transform { orientation ->
          if (
            rightMatchesLeftOf(orientation) ||
            leftMatchesRightOf(orientation) ||
            topMatchesBottomOf(orientation) ||
            bottomMatchesTopOf(orientation)
          ) emit(orientation)
        }
        .toList()
    }

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (javaClass != other?.javaClass) return false
      other as Tile
      if (id != other.id) return false
      return true
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String {
      return "$id"
    }
  }

  private fun Reader.toTileFlow(): Flow<Tile> {
    return flow {
      var id: Long = -1
      var rows = mutableListOf<BooleanArray>()
      this@toTileFlow.toLineFlow()
        .collect { line ->
          val titleMatch = ID_PATTERN.matchEntire(line)
          if (titleMatch != null) {
            id = titleMatch.groupValues[1].toLong(10)
          } else if (line.isNotEmpty()) {
            rows.add(line.map { it == '#' }.toBooleanArray())
          } else {
            emit(Tile(id, rows.toTypedArray()))
            id = -1
            rows = mutableListOf()
          }
        }
      if (id != -1L) {
        emit(Tile(id, rows.toTypedArray()))
      }
    }
  }

  companion object {
    private val ID_PATTERN = Regex("Tile ([0-9]+):")
  }
}