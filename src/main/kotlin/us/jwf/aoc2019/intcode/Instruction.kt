package us.jwf.aoc2019.intcode

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

sealed class Instruction(val modes: ParameterModes) {
  abstract suspend fun execute(state: State): Result

  class Add(modes: ParameterModes) : Instruction(modes) {
    override suspend fun execute(state: State): Result {
      TODO("Not yet implemented")
    }
  }

  class Mul(modes: ParameterModes) : Instruction(modes) {
    override suspend fun execute(state: State): Result {
      TODO("Not yet implemented")
    }
  }

  class In(modes: ParameterModes) : Instruction(modes) {
    override suspend fun execute(state: State): Result {
      TODO("Not yet implemented")
    }
  }

  class Out(modes: ParameterModes) : Instruction(modes) {
    override suspend fun execute(state: State): Result {
      TODO("Not yet implemented")
    }
  }

  class Jt(modes: ParameterModes) : Instruction(modes) {
    override suspend fun execute(state: State): Result {
      TODO("Not yet implemented")
    }
  }

  class Jf(modes: ParameterModes) : Instruction(modes) {
    override suspend fun execute(state: State): Result {
      TODO("Not yet implemented")
    }
  }

  class Lt(modes: ParameterModes) : Instruction(modes) {
    override suspend fun execute(state: State): Result {
      TODO("Not yet implemented")
    }
  }

  class Eq(modes: ParameterModes) : Instruction(modes) {
    override suspend fun execute(state: State): Result {
      TODO("Not yet implemented")
    }
  }

  class Arb(modes: ParameterModes) : Instruction(modes) {
    override suspend fun execute(state: State): Result {
      TODO("Not yet implemented")
    }
  }

  class Exit(modes: ParameterModes) : Instruction(modes) {
    override suspend fun execute(state: State): Result =
      Result.Error(ComputerError.ProgramFinished())
  }

  class State(
    private val ptr: Int,
    val memory: Memory,
    private val input: ReceiveChannel<Long>,
    private val output: SendChannel<Long>,
  ) {
    suspend fun readInput(): Long = input.receive()
    suspend fun writeOutput(value: Long) = output.send(value)
  }

  sealed class Result {
    data class Error(
      val throwable: Throwable
    ) : Result()

    data class Ok(
      val relativeOffsetDelta: Int,
      val instructionPointerDelta: Int,
      val value: Long?
    ) : Result()
  }

  companion object {
    fun parse(opCode: Int): Instruction {
      val modes = ParameterModes(
        first = opCode / 100 % 10,
        second = opCode / 1000 % 10,
        third = opCode / 10000 % 10
      )

      val instructionCode = opCode % 100
      return when (instructionCode) {
        1 -> Add(modes)
        2 -> Mul(modes)
        3 -> In(modes)
        4 -> Out(modes)
        5 -> Jt(modes)
        6 -> Jf(modes)
        7 -> Lt(modes)
        8 -> Eq(modes)
        9 -> Arb(modes)
        99 -> Exit(modes)
        else -> throw IllegalArgumentException("Unsupported instruction code: $opCode")
      }
    }
  }
}
