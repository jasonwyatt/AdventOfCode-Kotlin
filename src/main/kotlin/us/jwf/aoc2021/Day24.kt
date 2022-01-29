package us.jwf.aoc2021

import java.io.Reader
import us.jwf.aoc.Day
import us.jwf.aoc2021.Day24.Field.Register
import us.jwf.aoc2021.Day24.Instruction.Input

/**
 * AoC 2021 - Day 24
 */
class Day24 : Day<Long, Int> {
  override suspend fun executePart1(input: Reader): Long {
    val instructions = input.readLines().map { Instruction.parse(it) }

    val cache = mutableMapOf<Machine, List<Int>?>()
    // Try each
    var i = 0
    fun recurse(machine: Machine, current: Int, soFar: List<Int>, instructions: List<Instruction>): List<Int>? {
      val args = machine.copy()
      if (args in cache.keys) {
        val value = cache[args]
        //println("Cache hit: $args = $value = $soFar (size: ${cache.size})")
        return value
      }
      if (args.inputBufferPos >= 14) {
        if (i % 100000 == 0) println("End: $machine - $soFar (cache size: ${cache.size})")
        i++
        if (machine.z == 0) {
          cache[args] = soFar.subList(0, 14)
          return soFar.subList(0, 14)
        }
        cache[args] = null
        return null
      }

      // "Handle" the input.
      var nextMachine = args.copy(
        w = current,
        inputBufferPos = args.inputBufferPos + 1,
        instructionPos = args.instructionPos + 1
      )
      // read to the next position
      while (nextMachine.instructionPos in instructions.indices) {
        val instruction = instructions[nextMachine.instructionPos]
        if (instruction is Input) break
        nextMachine = instructions[nextMachine.instructionPos].execute(nextMachine)
      }

      if (nextMachine.instructionPos == Int.MAX_VALUE) {
        cache[args] = null
        return null
      }

      fun dive(): List<Int>? {
        (1..9).forEach {
          val solution = recurse(nextMachine.copy(), it, soFar + it, instructions)
          if (solution != null) return@dive solution
        }
        return@dive null
      }
      return dive().also { cache[nextMachine] = it }
    }

    (1..9).forEach {
      recurse(Machine(), it, listOf(it), instructions)?.let { println(it.joinToString("")); return 5 }
    }
    return 0
  }

  override suspend fun executePart2(input: Reader): Int {
    TODO("Not yet implemented")
  }

  class Machine(
    val w: Int = 0,
    val x: Int = 0,
    val y: Int = 0,
    val z: Int = 0,
    val inputBufferPos: Int = 0,
    val instructionPos: Int = 0,
  ) {
    fun copy(w: Int = this.w, x: Int = this.x, y: Int = this.y, z: Int = this.z, inputBufferPos: Int = this.inputBufferPos, instructionPos: Int = this.instructionPos): Machine {
      return Machine(w,x, y, z, inputBufferPos, instructionPos)
    }

    override fun toString(): String = "Machine($w, $x, $y, $z, $inputBufferPos)"

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is Machine) return false

      if (w != other.w) return false
      if (x != other.x) return false
      if (y != other.y) return false
      if (z != other.z) return false
      if (inputBufferPos != other.inputBufferPos) return false
      if (instructionPos != other.instructionPos) return false

      return true
    }

    override fun hashCode(): Int {
      var result = w
      result = 31 * result + x
      result = 31 * result + y
      result = 31 * result + z
      result = 31 * result + inputBufferPos
      result = 31 * result + instructionPos
      return result
    }
  }

  sealed interface Field {
    sealed interface Register : Field
    object W : Register
    object X : Register
    object Y : Register
    object Z : Register
    data class Literal(val value: Int) : Field
  }

  sealed interface Instruction {
    fun execute(machine: Machine): Machine

    data class Input(val target: Register) : Instruction {
      override fun execute(machine: Machine): Machine {
        /*
        val value = machine.inputBuffer[machine.inputBufferPos++]
        when (target) {
          Field.W -> machine.w = value
          Field.X -> machine.x = value
          Field.Y -> machine.y = value
          Field.Z -> machine.z = value
        }
         */
        return machine
      }
    }

    data class Add(val a: Register, val b: Field) : Instruction {
      override fun execute(machine: Machine): Machine {
        val rhs = when (b) {
          is Field.Literal -> b.value
          is Register -> when (b) {
            Field.W -> machine.w
            Field.X -> machine.x
            Field.Y -> machine.y
            Field.Z -> machine.z
          }
        }

        return when (a) {
          Field.W -> machine.copy(w = machine.w + rhs, instructionPos = machine.instructionPos + 1)
          Field.X -> machine.copy(x = machine.x + rhs, instructionPos = machine.instructionPos + 1)
          Field.Y -> machine.copy(y = machine.y + rhs, instructionPos = machine.instructionPos + 1)
          Field.Z -> machine.copy(z = machine.z + rhs, instructionPos = machine.instructionPos + 1)
        }
      }
    }

    data class Mul(val a: Register, val b: Field) : Instruction {
      override fun execute(machine: Machine): Machine {
        val rhs = when (b) {
          is Field.Literal -> b.value
          is Register -> when (b) {
            Field.W -> machine.w
            Field.X -> machine.x
            Field.Y -> machine.y
            Field.Z -> machine.z
          }
        }

        return when (a) {
          Field.W -> machine.copy(w = machine.w * rhs, instructionPos = machine.instructionPos + 1)
          Field.X -> machine.copy(x = machine.x * rhs, instructionPos = machine.instructionPos + 1)
          Field.Y -> machine.copy(y = machine.y * rhs, instructionPos = machine.instructionPos + 1)
          Field.Z -> machine.copy(z = machine.z * rhs, instructionPos = machine.instructionPos + 1)
        }
      }
    }

    data class Div(val a: Register, val b: Field) : Instruction {
      override fun execute(machine: Machine): Machine {
        val rhs = when (b) {
          is Field.Literal -> b.value
          is Register -> when (b) {
            Field.W -> machine.w
            Field.X -> machine.x
            Field.Y -> machine.y
            Field.Z -> machine.z
          }
        }

        return when (a) {
          Field.W -> machine.copy(w = machine.w / rhs, instructionPos = machine.instructionPos + 1)
          Field.X -> machine.copy(x = machine.x / rhs, instructionPos = machine.instructionPos + 1)
          Field.Y -> machine.copy(y = machine.y / rhs, instructionPos = machine.instructionPos + 1)
          Field.Z -> machine.copy(z = machine.z / rhs, instructionPos = machine.instructionPos + 1)
        }
      }
    }

    data class Mod(val a: Register, val b: Field) : Instruction {
      override fun execute(machine: Machine): Machine {
        val rhs = when (b) {
          is Field.Literal -> b.value
          is Register -> when (b) {
            Field.W -> machine.w
            Field.X -> machine.x
            Field.Y -> machine.y
            Field.Z -> machine.z
          }
        }

        val lhs = when (a) {
          Field.W -> machine.w
          Field.X -> machine.x
          Field.Y -> machine.y
          Field.Z -> machine.z
        }

        if (lhs < 0 || rhs <= 0) {
          //return machine.copy(instructionPos = Int.MAX_VALUE)
        }

        return when (a) {
          Field.W -> machine.copy(w = lhs % rhs, instructionPos = machine.instructionPos + 1)
          Field.X -> machine.copy(x = lhs % rhs, instructionPos = machine.instructionPos + 1)
          Field.Y -> machine.copy(y = lhs % rhs, instructionPos = machine.instructionPos + 1)
          Field.Z -> machine.copy(z = lhs % rhs, instructionPos = machine.instructionPos + 1)
        }
      }
    }

    data class Eql(val a: Register, val b: Field) : Instruction {
      override fun execute(machine: Machine): Machine {
        val rhs = when (b) {
          is Field.Literal -> b.value
          is Register -> when (b) {
            Field.W -> machine.w
            Field.X -> machine.x
            Field.Y -> machine.y
            Field.Z -> machine.z
          }
        }

        return when (a) {
          Field.W -> machine.copy(w = if (machine.w == rhs) 1 else 0, instructionPos = machine.instructionPos + 1)
          Field.X -> machine.copy(x = if (machine.x == rhs) 1 else 0, instructionPos = machine.instructionPos + 1)
          Field.Y -> machine.copy(y = if (machine.y == rhs) 1 else 0, instructionPos = machine.instructionPos + 1)
          Field.Z -> machine.copy(z = if (machine.z == rhs) 1 else 0, instructionPos = machine.instructionPos + 1)
        }
      }
    }

    companion object {
      val pattern = "(inp|add|mul|div|mod|eql) ([wxyz])( (([wxyz])|([-]*\\d+)))?".toRegex()
      fun parse(line: String): Instruction {
        val match = pattern.matchEntire(line)!!.groupValues

        val target = when(match[2]) {
          "w" -> Field.W
          "x" -> Field.X
          "y" -> Field.Y
          "z" -> Field.Z
          else -> throw IllegalArgumentException()
        }

        val rhs = if (match[5].isNotEmpty()) {
          when (match[5]) {
            "w" -> Field.W
            "x" -> Field.X
            "y" -> Field.Y
            "z" -> Field.Z
            else -> throw IllegalArgumentException()
          }
        } else if (match[6].isNotEmpty()) {
          Field.Literal(match[6].toInt())
        } else null

        return when (match[1]) {
          "inp" -> Input(target)
          "add" -> Add(target, rhs!!)
          "mul" -> Mul(target, rhs!!)
          "div" -> Div(target, rhs!!)
          "mod" -> Mod(target, rhs!!)
          "eql" -> Eql(target, rhs!!)
          else -> throw IllegalStateException()
        }
      }
    }
  }
}
/*
w = i[0]
x = 0
y = 0

 */