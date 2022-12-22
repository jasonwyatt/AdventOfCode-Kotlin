package us.jwf.aoc2022

import java.io.Reader
import java.math.BigInteger
import us.jwf.aoc.Day

class Day11MonkeyInTheMiddle : Day<Long, Long> {
  override suspend fun executePart1(input: Reader): Long {
    val monkeys = ProdMonkeys()
    repeat(20) { monkeys.doRound() }
    val sorted = monkeys.monkeys.sortedBy { -it.inspections }
    return sorted[0].inspections * sorted[1].inspections
  }

  override suspend fun executePart2(input: Reader): Long {
    val monkeys = ProdMonkeys()
    val divisor = monkeys.partTwoDivisor
    repeat(10000) {
      monkeys.doRound { worry -> worry % divisor }
      if (it != 0 && it % 1000 == 0) println("Round $it done")
    }
    val sorted = monkeys.monkeys.sortedBy { -it.inspections }
    monkeys.monkeys.forEach { println(it) }
    return sorted[0].inspections * sorted[1].inspections
  }

  interface MonkeyManager {
    val monkeys: List<Monkey>

    val partTwoDivisor: Int
      get() = monkeys.fold(1) { d, monkey -> d * monkey.testDivisor }

    fun doRound(postOp: (BigInteger) -> BigInteger = { it / 3 }) {
      monkeys.forEach { it.takeTurn(postOp) }
    }
    fun getMonkey(idx: Int) = monkeys[idx]
  }

  class ProdMonkeys : MonkeyManager {
    override val monkeys: List<Monkey> =
      listOf(
        Monkey(0, listOf(57, 58), { it * 19 }, 7) {
          getMonkey(if (it) 2 else 3)
        },
        Monkey(1, listOf(66, 52, 59, 79, 94, 73), { it + 1 }, 19) {
          getMonkey(if (it) 4 else 6)
        },
        Monkey(2, listOf(80), { it + 6 }, 5) {
          getMonkey(if (it) 7 else 5)
        },
        Monkey(3, listOf(82, 81, 68, 66, 71, 83, 75, 97), { it + 5 }, 11) {
          getMonkey(if (it) 5 else 2)
        },
        Monkey(4, listOf(55, 52, 67, 70, 69, 94, 90), { it * it }, 17) {
          getMonkey(if (it) 0 else 3)
        },
        Monkey(5, listOf(69, 85, 89, 91), { it + 7 }, 13) {
          getMonkey(if (it) 1 else 7)
        },
        Monkey(6, listOf(75, 53, 73, 52, 75), { it * 7 }, 2) {
          getMonkey(if (it) 0 else 4)
        },
        Monkey(7, listOf(94, 60, 79), { it + 2 }, 3) {
          getMonkey(if (it) 1 else 6)
        }
      )
  }

  class DemoMonkeys : MonkeyManager {
    override val monkeys: List<Monkey> =
      listOf(
        Monkey(0, listOf(79, 98), { it * 19 }, 23) {
          getMonkey(if (it) 2 else 3)
        },
        Monkey(1, listOf(54, 65, 75, 74), { it + 6 }, 19) {
          getMonkey(if (it) 2 else 0)
        },
        Monkey(2, listOf(79, 60, 97), { it * it }, 13) {
          getMonkey(if (it) 1 else 3)
        },
        Monkey(3, listOf(74), { it + 3 }, 17) {
          getMonkey(if (it) 0 else 1)
        },
      )
  }

  class Monkey(
    val id: Int,
    startingItems: List<Int>,
    private val operation: (BigInteger) -> BigInteger,
    val testDivisor: Int,
    private val testResult: (Boolean) -> Monkey,
  ) {
    var inspections = 0L

    private val items =
      startingItems.map { bigInt(it) }.toMutableList()

    fun takeTurn(postEvalOperation: (BigInteger) -> BigInteger) {
      items.forEach { item ->
        inspections++

        val newItem = postEvalOperation(operation(item))
        val isDivisible = newItem.divisibleBy(testDivisor)
        testResult(isDivisible).catch(newItem)
      }
      items.clear()
    }

    fun catch(item: BigInteger) {
      items.add(item)
    }

    override fun toString(): String = "Monkey($id, $items, inspections = $inspections)"
  }

  companion object {
    private val cache = mutableMapOf<Int, BigInteger>()
    fun bigInt(small: Int): BigInteger =
      cache[small] ?: small.toBigInteger().also { cache[small] = it }

    fun BigInteger.divisibleBy(divisor: Int): Boolean = this % bigInt(divisor) == BigInteger.ZERO

    operator fun BigInteger.plus(other: Int): BigInteger =
      this + bigInt(other)

    operator fun BigInteger.times(other: Int): BigInteger =
      this * bigInt(other)

    operator fun BigInteger.div(other: Int): BigInteger =
      this / bigInt(other)

    operator fun BigInteger.rem(other: Int): BigInteger =
      this % bigInt(other)
  }
}