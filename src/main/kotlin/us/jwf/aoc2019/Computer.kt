package us.jwf.aoc2019

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
  fun execute(vararg arguments: Pair<Int, Int>): Int {
    arguments.forEach { (pos, value) -> runningMemory[pos] = value }

    do {
      val op = Op.getForValue(runningMemory[instructionPointer]) ?: break
      val result = op.execute(instructionPointer, runningMemory)
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
  val execute: (ptr: Int, memory: IntArray) -> Result<Int>
) {
  Add(
    execute = { ptr, mem ->
      val arg1 = mem[mem[ptr + 1]]
      val arg2 = mem[mem[ptr + 2]]
      val outPos = mem[ptr + 3]
      mem[outPos] = arg1 + arg2
      Result.success(ptr + 4)
    }
  ),
  Multiply(
    execute = { ptr, mem ->
      val arg1 = mem[mem[ptr + 1]]
      val arg2 = mem[mem[ptr + 2]]
      val outPos = mem[ptr + 3]
      mem[outPos] = arg1 * arg2
      Result.success(ptr + 4)
    }
  ),
  Exit(
    execute = { _, _ -> Result.failure(ComputerError.ProgramFinished()) }
  );

  companion object {
    fun getForValue(intCode: Int): Op? = when (intCode) {
      1 -> Add
      2 -> Multiply
      99 -> Exit
      else -> null
    }
  }
}
