package us.jwf.aoc2019

import java.io.Reader
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.toList
import us.jwf.aoc.Day
import us.jwf.aoc.toLongFlow

class Day09SensorBoost : Day<Long, Long> {
  override suspend fun executePart1(input: Reader): Long {
    val program = input.toLongFlow(",").toList()
    val computer = Computer(program.toLongArray())
    val inputChannel = Channel<Long>(1)
    inputChannel.send(1)
    return computer.execute(input = inputChannel).toList().last()
  }

  override suspend fun executePart2(input: Reader): Long {
    val program = input.toLongFlow(",").toList()
    val computer = Computer(program.toLongArray())
    val inputChannel = Channel<Long>(1)
    inputChannel.send(2)
    return computer.execute(input = inputChannel).toList().last()
  }
}