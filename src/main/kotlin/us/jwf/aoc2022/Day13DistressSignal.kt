package us.jwf.aoc2022

import java.io.Reader
import java.util.Stack
import us.jwf.aoc.Day
import us.jwf.aoc2022.Day13DistressSignal.Item.Multiple
import us.jwf.aoc2022.Day13DistressSignal.Item.Single

class Day13DistressSignal : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    val lines = input.readLines()
    val pairs = mutableListOf<Pair<Item, Item>>()
    for (i in lines.indices.step(3)) {
      val left = parse(lines[i])
      val right = parse(lines[i + 1])
      pairs.add(left to right)
    }
    return pairs.withIndex()
      .filter { (_, pair) -> pair.first.checkAsLeft(pair.second) == Result.VALID }
      .sumOf { (idx, _) -> idx + 1 }
  }

  override suspend fun executePart2(input: Reader): Int {
    val two = parse("[[2]]")
    val six = parse("[[6]]")
    val items = mutableListOf(two, six)
    input.readLines().forEach {
      if (it.isEmpty()) return@forEach
      items.add(parse(it))
    }
    items.sort()

    return (items.indexOf(two) + 1) * (items.indexOf(six) + 1)
  }

  enum class Result {
    VALID,
    INVALID,
    NOT_SURE
  }

  sealed class Item : Comparable<Item> {
    abstract fun checkAsLeft(right: Item): Result

    override fun compareTo(other: Item): Int = when (checkAsLeft(other)) {
      Result.VALID -> -1
      Result.INVALID -> 1
      Result.NOT_SURE -> 0
    }

    data class Single(val value: Int) : Item() {
      override fun checkAsLeft(right: Item): Result {
        if (right is Single) {
          if (right.value > value) return Result.VALID
          if (right.value < value) return Result.INVALID
          return Result.NOT_SURE
        }
        return Multiple(mutableListOf(this)).checkAsLeft(right)
      }

      override fun toString(): String = "$value"
    }

    data class Multiple(val items: MutableList<Item>) : Item() {
      override fun checkAsLeft(right: Item): Result {
        if (right is Multiple) {
          items.forEachIndexed { i, item ->
            if (i >= right.items.size) return Result.INVALID
            val itemResult = item.checkAsLeft(right.items[i])
            if (itemResult != Result.NOT_SURE) return itemResult
          }
          if (items.size == right.items.size) return Result.NOT_SURE
          return Result.VALID
        }
        return checkAsLeft(Multiple(mutableListOf(right)))
      }

      override fun toString(): String = items.toString()
    }
  }

  sealed class Token {
    object LeftBrace : Token() {
      override fun toString(): String = "["
    }
    object RightBrace : Token() {
      override fun toString(): String = "]"
    }
    object Comma : Token() {
      override fun toString(): String = ","
    }
    data class Value(val value: Int) : Token() {
      override fun toString(): String = "$value"
    }

    companion object {
      private val TOKEN_PATTERN = Regex("(\\[)|(])|(,)|(\\d+)")

      fun tokenize(str: String) : Sequence<Token> = sequence {
        TOKEN_PATTERN.findAll(str).forEach { match ->
          val token = when {
            match.groups[1]?.value?.isNotEmpty() == true -> LeftBrace
            match.groups[2]?.value?.isNotEmpty() == true -> RightBrace
            match.groups[3]?.value?.isNotEmpty() == true -> Comma
            match.groups[4]?.value?.isNotEmpty() == true ->
              Value(match.groups[4]!!.value.toInt(10))
            else -> throw IllegalStateException("Impossible")
          }
          if (token != Comma) {
            yield(token)
          }
        }
      }
    }
  }

  companion object {
    fun parse(line: String): Item {
      val tokens = Token.tokenize(line)
      val stack = Stack<Multiple>()
      tokens.forEach {
        when (it) {
          Token.LeftBrace -> stack.push(Multiple(mutableListOf()))
          is Token.Value -> stack.peek().items.add(Single(it.value))
          Token.RightBrace -> {
            val top = stack.pop()
            if (stack.isEmpty()) {
              // We are done.
              return top
            }

            stack.peek().items.add(top)
          }
          Token.Comma -> throw IllegalArgumentException("Comma not expected")
        }
      }
      throw IllegalArgumentException("Poor format for string: $line")
    }
  }
}