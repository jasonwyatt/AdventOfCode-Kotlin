package us.jwf.aoc2022

import java.io.Reader
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import us.jwf.aoc.Day
import us.jwf.aoc.toIntFlow
import us.jwf.aoc.toLineFlow

class Day01CalorieCounting : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    val totalsByElf = mutableMapOf<Int, Int>()
    var max = Int.MIN_VALUE
    var elf = 0
    input.toLineFlow()
      .collect {
        if (it.isBlank()) {
          max = maxOf(max, totalsByElf[elf] ?: 0)
          elf++
        } else {
          totalsByElf[elf] = (totalsByElf[elf] ?: 0) + it.toInt()
        }
      }
    return max
  }

  override suspend fun executePart2(input: Reader): Int {
    val totalsByElf = mutableMapOf<Int, Int>()
    var elf = 0
    input.toLineFlow()
      .collect {
        if (it.isBlank()) {
          elf++
        } else {
          totalsByElf[elf] = (totalsByElf[elf] ?: 0) + it.toInt()
        }
      }
    return totalsByElf.values.sortedDescending().take(3).sum()
  }
}