package us.jwf.aoc2022

import java.io.Reader
import us.jwf.aoc.Day

class Day05SupplyStacks : Day<String, String> {
  val stacks =
    mutableListOf(
      mutableListOf('N', 'C', 'R', 'T', 'M', 'Z', 'P'),
      mutableListOf('D', 'N', 'T', 'S', 'B', 'Z'),
      mutableListOf('M', 'H', 'Q', 'R', 'F', 'C', 'T', 'G'),
      mutableListOf('G', 'R', 'Z'),
      mutableListOf('Z', 'N', 'R', 'H'),
      mutableListOf('F', 'H', 'S', 'W', 'P', 'Z', 'L', 'D'),
      mutableListOf('W', 'D', 'Z', 'R', 'C', 'G', 'M'),
      mutableListOf('S', 'J', 'F', 'L', 'H', 'W', 'Z', 'Q'),
      mutableListOf('S', 'Q', 'P', 'W', 'N'),
    )

  override suspend fun executePart1(input: Reader): String {
    val linesToSkip = stacks.map { it.size }.maxOrNull()!! + 2
    input.readLines().drop(linesToSkip)
      .forEach { Command.parse(it).execute(stacks) }
    return stacks.map { it.last() }.joinToString(separator = "")
  }

  override suspend fun executePart2(input: Reader): String {
    val linesToSkip = stacks.map { it.size }.maxOrNull()!! + 2
    input.readLines().drop(linesToSkip)
      .forEach { Command.parse(it).execute2(stacks) }
    return stacks.map { it.last() }.joinToString(separator = "")
  }

  data class Command(val count: Int, val from: Int, val to: Int) {
    fun execute(stacks: MutableList<MutableList<Char>>) {
      repeat(count) { stacks[to].add(stacks[from].removeLast()) }
    }

    fun execute2(stacks: MutableList<MutableList<Char>>) {
      stacks[to].addAll(stacks[from].takeLast(count))
      stacks[from] = stacks[from].dropLast(count).toMutableList()
    }

    companion object {
      private val PATTERN = "move (\\d+) from (\\d+) to (\\d+)".toRegex()
      fun parse(str: String): Command {
        val res = PATTERN.matchEntire(str)!!
        return Command(
          count = res.groupValues[1].toInt(),
          from = res.groupValues[2].toInt() - 1,
          to = res.groupValues[3].toInt() - 1
        )
      }
    }
  }
}