package us.jwf.aoc2019

import java.io.Reader
import java.util.LinkedList
import kotlinx.coroutines.flow.toList
import us.jwf.aoc.Day
import us.jwf.aoc.toIntFlow

class Day05SunnyAsteroids : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    val intCode = input.toIntFlow(",\\s*").toList().toIntArray()
    val computer = Computer(intCode)
    val input = LinkedList(listOf(1))
    val output = mutableListOf<Int>()
    computer.execute(
      input = input,
      output = output
    )
    println(output)
    return output.last()
  }

  override suspend fun executePart2(input: Reader): Int {
    val intCode = input.toIntFlow(",\\s*").toList().toIntArray()
    val computer = Computer(intCode)
    val input = LinkedList(listOf(5))
    val output = mutableListOf<Int>()
    computer.execute(
      input = input,
      output = output
    )
    println(output)
    return output.last()
  }
}