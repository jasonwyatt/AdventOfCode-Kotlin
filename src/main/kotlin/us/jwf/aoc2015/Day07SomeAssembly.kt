package us.jwf.aoc2015

import java.io.Reader
import us.jwf.aoc.Day

/**
 * AoC 2015 - Day 7
 */
class Day07SomeAssembly : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    val emulator = Emulator()

    input.readLines()
      .forEach { emulator.addWire(Wire.parse(it)) }

    return emulator.getWire("a")(emulator).toInt()
  }

  override suspend fun executePart2(input: Reader): Int {
    val emulator = Emulator()

    input.readLines()
      .forEach { emulator.addWire(Wire.parse(it)) }

    val aValue = emulator.getWire("a")(emulator)

    emulator.addWire(Wire("b", "faked") { aValue })
    emulator.clearCache()

    return emulator.getWire("a")(emulator).toInt()
  }

  class Emulator {
    private val wires = mutableMapOf<String, Wire>()

    fun getWire(name: String): Wire = wires[name]!!
    fun addWire(wire: Wire) = wires.put(wire.name, wire)
    fun clearCache() = wires.values.forEach { it.clearCache() }

    override fun toString(): String {
      return "$wires"
    }
  }

  class Wire(val name: String, val raw: String, val value: (Emulator) -> UShort) {
    private var cache: UShort? = null
    operator fun invoke(emulator: Emulator): UShort {
      cache?.let { return it }
      val calculated = value(emulator)
      cache = calculated
      return calculated
    }

    fun clearCache() {
      cache = null
    }

    override fun toString(): String {
      return raw
    }

    companion object {
      val OPERAND = "([a-z]+)|(\\d+)"
      val RAW = "$OPERAND".toRegex()
      val AND = "($OPERAND) AND ($OPERAND)".toRegex()
      val OR = "($OPERAND) OR ($OPERAND)".toRegex()
      val NOT = "NOT ($OPERAND)".toRegex()
      val LSHIFT = "($OPERAND) LSHIFT ($OPERAND)".toRegex()
      val RSHIFT = "($OPERAND) RSHIFT ($OPERAND)".toRegex()

      fun parse(line: String): Wire {
        val (input, name) = line.split(" -> ")

        val valueFun: (Emulator) -> UShort = when {
          input.matches(RAW) -> {
            val match = RAW.matchEntire(input)!!
            { emu ->
              if (match.groupValues[1].isNotEmpty()) {
                emu.getWire(match.groupValues[1])(emu)
              } else input.toUShort()
            }
          }
          input.matches(AND) -> {
            val match = AND.matchEntire(input)!!
            { emu ->
              val lhs = if (match.groupValues[2].isNotEmpty()) {
                emu.getWire(match.groupValues[2])(emu)
              } else {
                match.groupValues[3].toUShort()
              }
              val rhs = if (match.groupValues[5].isNotEmpty()) {
                emu.getWire(match.groupValues[5])(emu)
              } else {
                match.groupValues[6].toUShort()
              }
              lhs and rhs
            }
          }
          input.matches(OR) -> {
            val match = OR.matchEntire(input)!!
            { emu ->
              val lhs = if (match.groupValues[2].isNotEmpty()) {
                emu.getWire(match.groupValues[2])(emu)
              } else {
                match.groupValues[3].toUShort()
              }
              val rhs = if (match.groupValues[5].isNotEmpty()) {
                emu.getWire(match.groupValues[5])(emu)
              } else {
                match.groupValues[6].toUShort()
              }
              lhs or rhs
            }
          }
          input.matches(NOT) -> {
            val match = NOT.matchEntire(input)!!
            { emu ->
              val value = if (match.groupValues[2].isNotEmpty()) {
                emu.getWire(match.groupValues[2])(emu)
              } else {
                match.groupValues[3].toUShort()
              }
              value.inv()
            }
          }
          input.matches(LSHIFT) -> {
            val match = LSHIFT.matchEntire(input)!!
            { emu ->
              val value = if (match.groupValues[2].isNotEmpty()) {
                emu.getWire(match.groupValues[2])(emu)
              } else {
                match.groupValues[3].toUShort()
              }
              val shift = if (match.groupValues[5].isNotEmpty()) {
                emu.getWire(match.groupValues[5])(emu).toInt()
              } else {
                match.groupValues[6].toInt()
              }
              (value.toInt() shl shift).toUShort()
            }
          }
          input.matches(RSHIFT) -> {
            val match = RSHIFT.matchEntire(input)!!
            { emu ->
              val value = if (match.groupValues[2].isNotEmpty()) {
                emu.getWire(match.groupValues[2])(emu)
              } else {
                match.groupValues[3].toUShort()
              }
              val shift = if (match.groupValues[5].isNotEmpty()) {
                emu.getWire(match.groupValues[5])(emu).toInt()
              } else {
                match.groupValues[6].toInt()
              }
              (value.toInt() shr shift)
                .toUShort()
            }
          }
          else -> throw IllegalArgumentException("BAD: $input")
        }
        return Wire(name, input, valueFun)
      }
    }
  }
}