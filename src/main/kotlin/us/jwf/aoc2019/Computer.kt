@file:Suppress("EXPERIMENTAL_API_USAGE")

package us.jwf.aoc2019

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

/**
 * Intcode computer.
 */
class Computer(private val initialMemory: LongArray) {
  private var instructionPointer: Int = 0
  var relativeOffset: Int = 0
  val runningMemory: LongArray =
    LongArray(50000) {
      if (it < initialMemory.size) {
        initialMemory[it]
      } else 0
    }

  var name: String = "Computer"

  constructor(initialMemory: IntArray) :
    this(LongArray(initialMemory.size) { initialMemory[it].toLong() })

  fun memAt(position: Int): Long = runningMemory[position]

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
    input: Channel<Long> = Channel()
  ): Flow<Long> = channelFlow {
    arguments.forEach { (pos, value) -> runningMemory[pos] = value.toLong() }

    do {
      val op = Op.getForValue(runningMemory[instructionPointer]) ?: break
      val state = InstructionState(
        computer = this@Computer,
        ptr = instructionPointer,
        memory = runningMemory,
        relativeOffset = relativeOffset,
        i = input,
        o = this@channelFlow
      )
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
    runningMemory.indices.forEach { runningMemory[it] = 0L }
    initialMemory.copyInto(runningMemory)
    instructionPointer = 0
    relativeOffset = 0
  }
}

private interface ComputerError {
  class ProgramFinished : ComputerError, Exception()
}

private data class InstructionState(
  val computer: Computer,
  val ptr: Int,
  val memory: LongArray,
  val relativeOffset: Int,
  val i: ReceiveChannel<Long>,
  val o: ProducerScope<Long>
)

private enum class Op(
  val execute: suspend (state: InstructionState) -> Result<Int>
) {
  Add(
    execute = { state ->
      val mode = getParameterMode(state)
      val arg1 = mode.getFirstParamValue(state)
      val arg2 = mode.getSecondParam(state)
      val outPos = state.memory[state.ptr + 3]
      state.memory[outPos.toInt()] = arg1 + arg2
      Result.success(state.ptr + 4)
    }
  ),
  Multiply(
    execute = { state ->
      val mode = getParameterMode(state)
      val arg1 = mode.getFirstParamValue(state)
      val arg2 = mode.getSecondParam(state)
      val outPos = state.memory[state.ptr + 3]
      state.memory[outPos.toInt()] = arg1 * arg2
      Result.success(state.ptr + 4)
    }
  ),
  Input(
    execute = { state ->
      val mode = getParameterMode(state)
      val outPos = mode.getFirstParamLocation(state)
      state.memory[outPos.toInt()] = state.i.receive()
      Result.success(state.ptr + 2)
    }
  ),
  Output(
    execute = { state ->
      val mode = getParameterMode(state)
      val arg1 = mode.getFirstParamValue(state)
      state.o.send(arg1)
      Result.success(state.ptr + 2)
    }
  ),
  JumpIfTrue(
    execute = { state ->
      val mode = getParameterMode(state)
      val arg1 = mode.getFirstParamValue(state)
      val arg2 = mode.getSecondParam(state)
      Result.success(
        if (arg1 != 0L) arg2.toInt() else state.ptr + 3
      )
    }
  ),
  JumpIfFalse(
    execute = { state ->
      val mode = getParameterMode(state)
      val arg1 = mode.getFirstParamValue(state)
      val arg2 = mode.getSecondParam(state)
      Result.success(
        if (arg1 == 0L) arg2.toInt() else state.ptr + 3
      )
    }
  ),
  LessThan(
    execute = { state ->
      val mode = getParameterMode(state)
      val arg1 = mode.getFirstParamValue(state)
      val arg2 = mode.getSecondParam(state)
      val outPos = state.memory[state.ptr + 3]
      state.memory[outPos.toInt()] = if (arg1 < arg2) 1 else 0
      Result.success(state.ptr + 4)
    }
  ),
  Equals(
    execute = { state ->
      val mode = getParameterMode(state)
      val arg1 = mode.getFirstParamValue(state)
      val arg2 = mode.getSecondParam(state)
      val outPos = state.memory[state.ptr + 3]
      state.memory[outPos.toInt()] = if (arg1 == arg2) 1 else 0
      Result.success(state.ptr + 4)
    }
  ),
  AdjustRelativeBase(
    execute = { state ->
      val mode = getParameterMode(state)
      state.computer.relativeOffset += mode.getFirstParamValue(state).toInt()
      Result.success(state.ptr + 2)
    }
  ),
  Exit(
    execute = { state -> Result.failure(ComputerError.ProgramFinished()) }
  );

  companion object {
    fun getForValue(intCode: Long): Op? = when (intCode % 100) {
      1L -> Add
      2L -> Multiply
      3L -> Input
      4L -> Output
      5L -> JumpIfTrue
      6L -> JumpIfFalse
      7L -> LessThan
      8L -> Equals
      9L -> AdjustRelativeBase
      99L -> Exit
      else -> null
    }

    private fun getParameterMode(state: InstructionState): ParameterModes {
      val intCode = state.memory[state.ptr]
      return ParameterModes(intCode / 100 % 10, intCode / 1000 % 10, intCode / 10000 % 10)
    }
  }
}

private data class ParameterModes(val first: Long, val second: Long, val third: Long) {
  fun getFirstParamValue(state: InstructionState): Long = getParameter(state, 1, first)
  fun getFirstParamLocation(state: InstructionState): Long = getParameterLocation(state, 1, first)

  fun getSecondParam(state: InstructionState): Long = getParameter(state, 2, second)

  fun getThirdParam(state: InstructionState): Long = getParameter(state, 3, third)

  private fun getParameter(state: InstructionState, paramIndex: Int, mode: Long): Long {
    return when (mode) {
      0L -> state.memory[state.memory[state.ptr + paramIndex].toInt()]
      1L -> state.memory[state.ptr + paramIndex]
      2L -> state.memory[(state.memory[state.ptr + paramIndex] + state.relativeOffset).toInt()]
      else -> throw IllegalArgumentException("Unsupported parameter mode: $mode")
    }
  }

  private fun getParameterLocation(state: InstructionState, paramIndex: Int, mode: Long): Long {
    return when (mode) {
      0L -> state.memory[state.ptr + paramIndex]
      1L -> state.ptr + paramIndex.toLong()
      2L -> state.memory[state.ptr + paramIndex] + state.relativeOffset
      else -> throw IllegalArgumentException("Unsupported parameter mode: $mode")
    }
  }
}
