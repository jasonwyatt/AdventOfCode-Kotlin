@file:Suppress("EXPERIMENTAL_API_USAGE")

package us.jwf.aoc2019

import java.util.LinkedList
import java.util.Queue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

/**
 * Intcode computer.
 */
class Computer(private val initialMemory: IntArray) {
  private var instructionPointer: Int = 0
  private val runningMemory: IntArray = IntArray(initialMemory.size) { initialMemory[it] }

  var name: String = "Computer"

  fun memAt(position: Int): Int = runningMemory[position]

  /**
   * Executes the program with the given [arguments], where each [Pair] represents a position and a
   * value of the argument. Returns the value of position 0.
   *
   * For example:
   *
   * ```kotlin
   * computer.execute(1 to 5)
   * ```
   *
   * This would set the memory at position 1 to a value of 5 before execution.
   */
  suspend fun execute(
    vararg arguments: Pair<Int, Int>,
    input: Channel<Int> = Channel()
  ): Flow<Int> = channelFlow {
    arguments.forEach { (pos, value) -> runningMemory[pos] = value }

    do {
      val op = Op.getForValue(runningMemory[instructionPointer]) ?: break
      val state = InstructionState(name, instructionPointer, runningMemory, input, this@channelFlow)
      val result = op.execute(state)
      when {
        result.isSuccess -> instructionPointer = result.getOrThrow()
        else -> result.exceptionOrNull()
          ?.takeIf { it !is ComputerError.ProgramFinished }
          ?.let { throw it }
      }
    } while (result.isSuccess)
  }

  /**
   * Resets the computer's memory to the [initialMemory] state, and resets the [instructionPointer]
   * to position 0.
   */
  fun reset() {
    initialMemory.copyInto(runningMemory)
    instructionPointer = 0
  }
}

private interface ComputerError {
  class ProgramFinished : ComputerError, Exception()
}

private data class InstructionState(
  val computerName: String,
  val ptr: Int,
  val memory: IntArray,
  val i: ReceiveChannel<Int>,
  val o: ProducerScope<Int>
)

private enum class Op(
  val execute: suspend (state: InstructionState) -> Result<Int>
) {
  Add(
    execute = { (_, ptr, mem, _, _) ->
      val (arg1Mode, arg2Mode, _) = getParameterMode(mem[ptr])
      val arg1 = if (arg1Mode == 0) mem[mem[ptr + 1]] else mem[ptr + 1]
      val arg2 = if (arg2Mode == 0) mem[mem[ptr + 2]] else mem[ptr + 2]
      val outPos = mem[ptr + 3]
      mem[outPos] = arg1 + arg2
      Result.success(ptr + 4)
    }
  ),
  Multiply(
    execute = { (_, ptr, mem, _, _) ->
      val (arg1Mode, arg2Mode, _) = getParameterMode(mem[ptr])
      val arg1 = if (arg1Mode == 0) mem[mem[ptr + 1]] else mem[ptr + 1]
      val arg2 = if (arg2Mode == 0) mem[mem[ptr + 2]] else mem[ptr + 2]
      val outPos = mem[ptr + 3]
      mem[outPos] = arg1 * arg2
      Result.success(ptr + 4)
    }
  ),
  Input(
    execute = { (_, ptr, mem, input, _) ->
      val outPos = mem[ptr + 1]
      mem[outPos] = input.receive()
      Result.success(ptr + 2)
    }
  ),
  Output(
    execute = { (_, ptr, mem, _, output) ->
      val (arg1Mode, _, _) = getParameterMode(mem[ptr])
      val arg1 = if (arg1Mode == 0) mem[mem[ptr + 1]] else mem[ptr + 1]
      output.send(arg1)
      Result.success(ptr + 2)
    }
  ),
  JumpIfTrue(
    execute = { (_, ptr, mem, _, _) ->
      val (arg1Mode, arg2Mode, _) = getParameterMode(mem[ptr])
      val arg1 = if (arg1Mode == 0) mem[mem[ptr + 1]] else mem[ptr + 1]
      val arg2 = if (arg2Mode == 0) mem[mem[ptr + 2]] else mem[ptr + 2]
      Result.success(
        if (arg1 != 0) arg2 else ptr + 3
      )
    }
  ),
  JumpIfFalse(
    execute = { (_, ptr, mem, _, _) ->
      val (arg1Mode, arg2Mode, _) = getParameterMode(mem[ptr])
      val arg1 = if (arg1Mode == 0) mem[mem[ptr + 1]] else mem[ptr + 1]
      val arg2 = if (arg2Mode == 0) mem[mem[ptr + 2]] else mem[ptr + 2]
      Result.success(
        if (arg1 == 0) arg2 else ptr + 3
      )
    }
  ),
  LessThan(
    execute = { (_, ptr, mem, _, _) ->
      val (arg1Mode, arg2Mode, _) = getParameterMode(mem[ptr])
      val arg1 = if (arg1Mode == 0) mem[mem[ptr + 1]] else mem[ptr + 1]
      val arg2 = if (arg2Mode == 0) mem[mem[ptr + 2]] else mem[ptr + 2]
      val outPos = mem[ptr + 3]
      mem[outPos] = if (arg1 < arg2) 1 else 0
      Result.success(ptr + 4)
    }
  ),
  Equals(
    execute = { (_, ptr, mem, _, _) ->
      val (arg1Mode, arg2Mode, _) = getParameterMode(mem[ptr])
      val arg1 = if (arg1Mode == 0) mem[mem[ptr + 1]] else mem[ptr + 1]
      val arg2 = if (arg2Mode == 0) mem[mem[ptr + 2]] else mem[ptr + 2]
      val outPos = mem[ptr + 3]
      mem[outPos] = if (arg1 == arg2) 1 else 0
      Result.success(ptr + 4)
    }
  ),
  Exit(
    execute = { (_, _, _, _, _) -> Result.failure(ComputerError.ProgramFinished()) }
  );

  companion object {
    fun getForValue(intCode: Int): Op? = when (intCode % 100) {
      1 -> Add
      2 -> Multiply
      3 -> Input
      4 -> Output
      5 -> JumpIfTrue
      6 -> JumpIfFalse
      7 -> LessThan
      8 -> Equals
      99 -> Exit
      else -> null
    }

    fun getParameterMode(intCode: Int): Triple<Int, Int, Int> =
      Triple(intCode / 100 % 10, intCode / 1000 % 10, intCode / 10000 % 10)
  }
}
