package us.jwf.aoc2019

import java.io.Reader
import java.util.LinkedList
import javafx.application.Application.launch
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import us.jwf.aoc.Day
import us.jwf.aoc.toIntFlow

class Day05SunnyAsteroids : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int = coroutineScope {
    val intCode = input.toIntFlow(",\\s*").toList().toIntArray()
    val computer = Computer(intCode)
    val inputChannel = Channel<Long>()
    launch {
      inputChannel.send(1)
    }
    val output = computer.execute(input = inputChannel)
    output.toList().last().toInt()
  }

  override suspend fun executePart2(input: Reader): Int = coroutineScope {
    val intCode = input.toIntFlow(",\\s*").toList().toIntArray()
    val computer = Computer(intCode)
    val inputChannel = Channel<Long>()
    launch {
      inputChannel.send(5)
    }
    val output = computer.execute(input = inputChannel)
    output.toList().last().toInt()
  }
}