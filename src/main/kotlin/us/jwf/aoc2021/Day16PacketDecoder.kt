package us.jwf.aoc2021

import java.io.Reader
import us.jwf.aoc.Day

/**
 * AoC 2021 - Day 16
 */
class Day16PacketDecoder : Day<Int, ULong> {
  override suspend fun executePart1(input: Reader): Int {
    val (packetTree, _) = Packet.parse(input.readLines()[0].hexToBinary(), 0)

    fun addUpVersions(packet: Packet): Int =
      when (packet) {
        is Packet.Literal -> packet.version
        is Packet.Operator -> packet.version + packet.subPackets.sumOf { addUpVersions(it) }
      }

    return addUpVersions(packetTree)
  }

  override suspend fun executePart2(input: Reader): ULong {
    return Packet.parse(input.readLines()[0].hexToBinary(), 0).first.eval()
  }

  fun String.hexToBinary(): String {
    val result = StringBuilder()
    forEach {
      result.append(
        when (it) {
          '0' -> "0000"
          '1' -> "0001"
          '2' -> "0010"
          '3' -> "0011"
          '4' -> "0100"
          '5' -> "0101"
          '6' -> "0110"
          '7' -> "0111"
          '8' -> "1000"
          '9' -> "1001"
          'A' -> "1010"
          'B' -> "1011"
          'C' -> "1100"
          'D' -> "1101"
          'E' -> "1110"
          'F' -> "1111"
          else -> ""
        }
      )
    }
    return result.toString()
  }

  sealed class Packet(val version: Int, val typeId: Int) {
    abstract fun eval(): ULong

    class Literal(version: Int, val value: ULong) : Packet(version, 4) {
      override fun eval(): ULong = value

      companion object {
        fun parse(source: String, version: Int, offset: Int): Pair<Literal, Int> {
          val numberBytes = StringBuilder()
          var soFar = offset
          do {
            val first = source[soFar]
            soFar++
            numberBytes.append(source.substring(soFar, soFar + 4))
            soFar += 4
          } while (first != '0')
          val value = numberBytes.toString().toULong(2)
          return Literal(version, value) to soFar
        }
      }
    }

    class Operator(version: Int, typeId: Int, val subPackets: List<Packet>) : Packet(version, typeId) {
      override fun eval(): ULong {
        return when (typeId) {
          0 -> subPackets.sumOf { it.eval() }
          1 -> subPackets.fold(1.toULong()) { acc, packet -> acc * packet.eval() }
          2 -> subPackets.minOf { it.eval() }
          3 -> subPackets.maxOf { it.eval() }
          5 -> {
            if (subPackets[0].eval() > subPackets[1].eval()) 1.toULong() else 0.toULong()
          }
          6 -> {
            if (subPackets[0].eval() < subPackets[1].eval()) 1.toULong() else 0.toULong()
          }
          7 -> {
            if (subPackets[0].eval() == subPackets[1].eval()) 1.toULong() else 0.toULong()
          }
          else -> throw IllegalArgumentException("bad type")
        }
      }

      companion object {
        fun parse(source: String, version: Int, typeId: Int, offset: Int): Pair<Operator, Int> {
          var soFar = offset
          val lengthType = source[soFar].digitToInt(2)
          soFar++

          return when (lengthType) {
            0 -> {
              val totalLengthBits = source.substring(soFar, soFar + 15).toInt(2)
              soFar += 15
              val subPacketStart = soFar
              val subpackets = mutableListOf<Packet>()
              while (soFar < subPacketStart + totalLengthBits) {
                val (subPacket, newOffset) = Packet.parse(source, soFar)
                soFar = newOffset
                subpackets.add(subPacket)
              }
              Operator(version, typeId, subpackets) to soFar
            }
            1 -> {
              val numSubpackets = source.substring(soFar, soFar + 11).toInt(2)
              soFar += 11
              val subpackets = mutableListOf<Packet>()
              repeat(numSubpackets) {
                val (subPacket, newOffset) = Packet.parse(source, soFar)
                soFar = newOffset
                subpackets.add(subPacket)
              }
              Operator(version, typeId, subpackets) to soFar
            }
            else -> throw IllegalArgumentException("Inconceivable!")
          }
        }
      }
    }

    companion object {
      fun parse(source: String, offset: Int): Pair<Packet, Int> {
        var soFar = offset
        val versionBits = source.substring(soFar, soFar + 3)
        soFar += 3
        val typeBits = source.substring(soFar, soFar + 3)
        soFar += 3

        val version = versionBits.toInt(2)
        val type = typeBits.toInt(2)
        return when (type) {
          4 -> Literal.parse(source, version, soFar)
          else -> Operator.parse(source, version, type, soFar)
        }
      }
    }
  }
}