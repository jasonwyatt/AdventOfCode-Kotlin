package us.jwf.aoc2021

import java.io.Reader
import java.util.LinkedList
import us.jwf.aoc.Day

/**
 * AoC 2021 - Day 10
 */
class Day10SyntaxScoring : Day<Int, Long> {
  override suspend fun executePart1(input: Reader): Int {
    return input.readLines()
      .sumOf { it.findSyntaxErrorScore() }
  }

  override suspend fun executePart2(input: Reader): Long {
    return input.readLines()
      .mapNotNull { it.findCompletionScore() }
      .filter { it != 0L }
      .sorted()
      .let { it[it.size / 2] }
  }

  fun String.findCompletionScore(): Long? {
    val stack = LinkedList<Char>()
    var i = 0
    do {
      when (val c = this[i]) {
        '(', '[', '{', '<' -> stack.push(c)
        ')' -> {
          val top = stack.pop()
          if (top != '(') return null
        }
        ']' -> {
          val top = stack.pop()
          if (top != '[') return null
        }
        '}' -> {
          val top = stack.pop()
          if (top != '{') return null
        }
        '>' -> {
          val top = stack.pop()
          if (top != '<') return null
        }
      }
      i++
    } while (stack.isNotEmpty() && i < length)

    return stack
      .fold(0L) { acc, char ->
        (acc * 5) + when (char) {
          '(' -> 1
          '[' -> 2
          '{' -> 3
          '<' -> 4
          else -> 0
        }
      }
  }

  fun String.findSyntaxErrorScore(): Int {
    val stack = LinkedList<Char>()
    var i = 0
    do {
      when (val c = this[i]) {
        '(', '[', '{', '<' -> stack.push(c)
        ')' -> {
          val top = stack.pop()
          if (top != '(') return score(c)
        }
        ']' -> {
          val top = stack.pop()
          if (top != '[') return score(c)
        }
        '}' -> {
          val top = stack.pop()
          if (top != '{') return score(c)
        }
        '>' -> {
          val top = stack.pop()
          if (top != '<') return score(c)
        }
      }
      i++
    } while (stack.isNotEmpty() && i < length)
    return 0
  }

  fun score(char: Char): Int = when(char) {
    ')' -> 3
    ']' -> 57
    '}' -> 1197
    '>' -> 25137
    else -> 0
  }
}