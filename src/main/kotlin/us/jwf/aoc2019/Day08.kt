package us.jwf.aoc2019

import java.io.Reader
import kotlinx.coroutines.flow.toList
import us.jwf.aoc.Day
import us.jwf.aoc.toLineFlow

class Day08 : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    val layer = input.toLineFlow().toList().first().toCharArray().toList()
      .chunked(150)
      .asSequence()
      .minByOrNull { it.count { c -> c == '0' } }
    var ones = 0
    var twos = 0
    layer!!.forEach {
      if (it == '1') ones++
      else if (it == '2') twos++
    }
    return ones * twos
  }

  override suspend fun executePart2(input: Reader): Int {
    val layers = input.toLineFlow().toList().first().toCharArray().toList().chunked(150)

    val result = mutableListOf<Char>()
    (0 until 150).forEach { pos ->
      val char = layers.first { it[pos] != '2' }[pos]
      result.add(if (char == '0') '#' else ' ')
    }
    result.chunked(25).forEach {
      println(it.toCharArray().concatToString())
    }
    return -1
  }
}