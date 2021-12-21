package us.jwf.aoc2015

import java.io.Reader
import us.jwf.aoc.Day

/**
 * AoC 2015 - Day 23
 */
class Day23TuringLock : Day<UInt, UInt> {
  override suspend fun executePart1(input: Reader): UInt {
    val computer = Computer()
    val instructions = input.readLines().map { Instruction.parse(it) }

    while (computer.programCounter in instructions.indices) {
      instructions[computer.programCounter].operate(computer)
    }

    return computer.registerB
  }

  override suspend fun executePart2(input: Reader): UInt {
    val computer = Computer(registerA = 1u)
    val instructions = input.readLines().map { Instruction.parse(it) }

    while (computer.programCounter in instructions.indices) {
      instructions[computer.programCounter].operate(computer)
    }

    return computer.registerB
  }

  data class Computer(
    var programCounter: Int = 0,
    var registerA: UInt = 0u,
    var registerB: UInt = 0u
  )

  sealed class Instruction {
    abstract fun operate(computer: Computer)

    object HalfA : Instruction() {
      override fun operate(computer: Computer) {
        computer.registerA = computer.registerA shr 1
        computer.programCounter++
      }
    }

    object HalfB : Instruction() {
      override fun operate(computer: Computer) {
        computer.registerB = computer.registerB shr 1
        computer.programCounter++
      }
    }

    object TripleA : Instruction() {
      override fun operate(computer: Computer) {
        computer.registerA = computer.registerA * 3u
        computer.programCounter++
      }
    }

    object TripleB : Instruction() {
      override fun operate(computer: Computer) {
        computer.registerB = computer.registerB * 3u
        computer.programCounter++
      }
    }

    object IncA : Instruction() {
      override fun operate(computer: Computer) {
        computer.registerA = computer.registerA + 1u
        computer.programCounter++
      }
    }

    object IncB : Instruction() {
      override fun operate(computer: Computer) {
        computer.registerB = computer.registerB + 1u
        computer.programCounter++
      }
    }

    data class Jump(val offset: Int) : Instruction() {
      override fun operate(computer: Computer) {
        computer.programCounter += offset
      }
    }

    data class JumpIfEvenA(val offset: Int) : Instruction() {
      override fun operate(computer: Computer) {
        if (computer.registerA and 1u == 0u) {
          computer.programCounter += offset
        } else {
          computer.programCounter++
        }
      }
    }

    data class JumpIfEvenB(val offset: Int) : Instruction() {
      override fun operate(computer: Computer) {
        if (computer.registerB and 1u == 0u) {
          computer.programCounter += offset
        } else {
          computer.programCounter++
        }
      }
    }

    data class JumpIfOneA(val offset: Int) : Instruction() {
      override fun operate(computer: Computer) {
        if (computer.registerA == 1u) {
          computer.programCounter += offset
        } else {
          computer.programCounter++
        }
      }
    }

    data class JumpIfOneB(val offset: Int) : Instruction() {
      override fun operate(computer: Computer) {
        if (computer.registerB == 1u) {
          computer.programCounter += offset
        } else {
          computer.programCounter++
        }
      }
    }

    companion object {
      fun parse(line: String): Instruction {
        return when (line) {
          "hlf a" -> HalfA
          "hlf b" -> HalfB
          "tpl a" -> TripleA
          "tpl b" -> TripleB
          "inc a" -> IncA
          "inc b" -> IncB
          else -> {
            val parts = line.split(" ")
            if (parts[0] == "jmp") {
              Jump(parts[1].toInt())
            } else if (parts[0] == "jie") {
              if (parts[1] == "a,") {
                JumpIfEvenA(parts[2].toInt())
              } else {
                JumpIfEvenB(parts[2].toInt())
              }
            } else if (parts[0] == "jio") {
              if (parts[1] == "a,") {
                JumpIfOneA(parts[2].toInt())
              } else {
                JumpIfOneB(parts[2].toInt())
              }
            } else throw IllegalArgumentException()
          }
        }
      }
    }
  }
}
