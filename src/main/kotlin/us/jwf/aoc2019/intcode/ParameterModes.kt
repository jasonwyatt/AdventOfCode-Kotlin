package us.jwf.aoc2019.intcode

import us.jwf.aoc2019.InstructionState

data class ParameterModes(val first: Int, val second: Int, val third: Int) {
  fun getFirstParamValue(state: InstructionState): Long = getParameter(state, 1, first)
  fun getFirstParamLocation(state: InstructionState, avoidImmediateMode: Boolean = false): Long =
    getParameterLocation(state, 1, first, true)

  fun getSecondParamValue(state: InstructionState): Long = getParameter(state, 2, second)

  fun getThirdParamLocation(state: InstructionState, avoidImmediateMode: Boolean = false): Long =
    getParameterLocation(state, 3, third, avoidImmediateMode)

  private fun getParameter(state: InstructionState, paramIndex: Int, mode: Int): Long {
    return state.memory[getParameterLocation(state, paramIndex, mode).toInt()]
  }

  private fun getParameterLocation(
      state: InstructionState,
      paramIndex: Int,
      mode: Int,
      avoidImmediateMode: Boolean = false
  ): Long {
    return when (mode) {
      0 -> state.memory[state.ptr + paramIndex]
      1 -> if (!avoidImmediateMode) {
        state.ptr + paramIndex.toLong()
      } else {
        state.memory[state.ptr + paramIndex]
      }
      2 -> state.memory[state.ptr + paramIndex] + state.relativeOffset
      else -> throw IllegalArgumentException("Unsupported parameter mode: $mode")
    }
  }
}