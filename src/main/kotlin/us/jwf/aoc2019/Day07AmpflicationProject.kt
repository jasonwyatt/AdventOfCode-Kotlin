package us.jwf.aoc2019

import java.io.Reader
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import us.jwf.aoc.Day
import us.jwf.aoc.combinatorics.permute
import us.jwf.aoc.toIntFlow

@OptIn(ExperimentalCoroutinesApi::class)
class Day07AmpflicationProject : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    val program = input.toIntFlow(",").toList().toIntArray()
    val computers = List(5) { Computer(program) }

    return setOf(0,1,2,3,4).permute().fold(-1L) { acc, settings ->
      computers.forEach(Computer::reset)
      println("Trying: $settings")
      var output = 0L
      settings.zip(computers).forEach { (setting, computer) ->
        output = computer.calculateFor(setting, output.toInt())
      }
      maxOf(acc, output)
    }.toInt()
  }

  override suspend fun executePart2(input: Reader): Int = coroutineScope {
    val program = input.toIntFlow(",").toList().toIntArray()
    val computers = List(5) { index -> Computer(program).also { it.name = "Computer #$index" } }

    setOf(5,6,7,8,9).permute().fold(-1) { acc, settings ->
      computers.forEach(Computer::reset)
      val inputs = List(5) { Channel<Long>(Channel.BUFFERED) }

      val outputs = inputs.zip(computers)
        .map { (inputChannel, computer) ->
          computer.execute(input = inputChannel)
        }
      launch {
        // Send our initial phase inputs.
        settings.zip(inputs).forEachIndexed { index, (setting, inputChannel) ->
          inputChannel.send(setting.toLong())
          if (index == 0) inputChannel.send(0)
        }
      }

      val lastOutput = mutableMapOf<Int, Int>()
      val jobs = outputs.mapIndexed { index, output ->
        launch {
          output.collect {
            lastOutput[index] = it.toInt()
            inputs[(index + 1) % inputs.size].send(it)
          }
        }
      }
      jobs.joinAll()
      maxOf(acc, lastOutput[4] ?: acc)
    }
  }

  suspend fun Computer.calculateFor(phaseSetting: Int, inputSignal: Int): Long = coroutineScope {
    val inputChannel = Channel<Long>()
    launch {
      inputChannel.send(phaseSetting.toLong())
      inputChannel.send(inputSignal.toLong())
    }
    execute(input = inputChannel).toList().first()
  }
}