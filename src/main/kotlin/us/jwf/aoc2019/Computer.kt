package us.jwf.aoc2019

import java.util.LinkedList
import java.util.Queue

/**
 * Intcode computer.
 */
class Computer(private val initialMemory: IntArray) {
  private var instructionPointer: Int = 0
  private val runningMemory: IntArray = IntArray(initialMemory.size) { initialMemory[it] }

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
  fun execute(
    vararg arguments: Pair<Int, Int>,
    input: Queue<Int> = LinkedList(),
    output: MutableList<Int> = LinkedList()
  ): Int {
    arguments.forEach { (pos, value) -> runningMemory[pos] = value }

    do {
      val op = Op.getForValue(runningMemory[instructionPointer]) ?: break
      val result = op.execute(instructionPointer, runningMemory, input, output)
      when {
        result.isSuccess -> instructionPointer = result.getOrThrow()
        else -> result.exceptionOrNull()
          ?.takeIf { it !is ComputerError.ProgramFinished }
          ?.let { throw it }
      }
    } while (result.isSuccess)

    return runningMemory[0]
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

private enum class Op(
  val execute: (ptr: Int, memory: IntArray, i: Queue<Int>, o: MutableList<Int>) -> Result<Int>
) {
  Add(
    execute = { ptr, mem, _, _ ->
      val (arg1Mode, arg2Mode, _) = getParameterMode(mem[ptr])
      val arg1 = if (arg1Mode == 0) mem[mem[ptr + 1]] else mem[ptr + 1]
      val arg2 = if (arg2Mode == 0) mem[mem[ptr + 2]] else mem[ptr + 2]
      val outPos = mem[ptr + 3]
      mem[outPos] = arg1 + arg2
      Result.success(ptr + 4)
    }
  ),
  Multiply(
    execute = { ptr, mem, _, _ ->
      val (arg1Mode, arg2Mode, _) = getParameterMode(mem[ptr])
      val arg1 = if (arg1Mode == 0) mem[mem[ptr + 1]] else mem[ptr + 1]
      val arg2 = if (arg2Mode == 0) mem[mem[ptr + 2]] else mem[ptr + 2]
      val outPos = mem[ptr + 3]
      mem[outPos] = arg1 * arg2
      Result.success(ptr + 4)
    }
  ),
  Input(
    execute = { ptr, mem, input, _ ->
      val outPos = mem[ptr + 1]
      mem[outPos] = input.poll()
      Result.success(ptr + 2)
    }
  ),
  Output(
    execute = { ptr, mem, _, output ->
      val (arg1Mode, _, _) = getParameterMode(mem[ptr])
      val arg1 = if (arg1Mode == 0) mem[mem[ptr + 1]] else mem[ptr + 1]
      output.add(arg1)
      Result.success(ptr + 2)
    }
  ),
  JumpIfTrue(
    execute = { ptr, mem, _, _ ->
      val (arg1Mode, arg2Mode, _) = getParameterMode(mem[ptr])
      val arg1 = if (arg1Mode == 0) mem[mem[ptr + 1]] else mem[ptr + 1]
      val arg2 = if (arg2Mode == 0) mem[mem[ptr + 2]] else mem[ptr + 2]
      Result.success(
        if (arg1 != 0) arg2 else ptr + 3
      )
    }
  ),
  JumpIfFalse(
    execute = { ptr, mem, _, _ ->
      val (arg1Mode, arg2Mode, _) = getParameterMode(mem[ptr])
      val arg1 = if (arg1Mode == 0) mem[mem[ptr + 1]] else mem[ptr + 1]
      val arg2 = if (arg2Mode == 0) mem[mem[ptr + 2]] else mem[ptr + 2]
      Result.success(
        if (arg1 == 0) arg2 else ptr + 3
      )
    }
  ),
  LessThan(
    execute = { ptr, mem, _, _ ->
      val (arg1Mode, arg2Mode, _) = getParameterMode(mem[ptr])
      val arg1 = if (arg1Mode == 0) mem[mem[ptr + 1]] else mem[ptr + 1]
      val arg2 = if (arg2Mode == 0) mem[mem[ptr + 2]] else mem[ptr + 2]
      val outPos = mem[ptr + 3]
      mem[outPos] = if (arg1 < arg2) 1 else 0
      Result.success(ptr + 4)
    }
  ),
  Equals(
    execute = { ptr, mem, _, _ ->
      val (arg1Mode, arg2Mode, _) = getParameterMode(mem[ptr])
      val arg1 = if (arg1Mode == 0) mem[mem[ptr + 1]] else mem[ptr + 1]
      val arg2 = if (arg2Mode == 0) mem[mem[ptr + 2]] else mem[ptr + 2]
      val outPos = mem[ptr + 3]
      mem[outPos] = if (arg1 == arg2) 1 else 0
      Result.success(ptr + 4)
    }
  ),
  Exit(
    execute = { _, _, _, _ -> Result.failure(ComputerError.ProgramFinished()) }
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
