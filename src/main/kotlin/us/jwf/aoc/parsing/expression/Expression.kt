package us.jwf.aoc.parsing.expression

interface Expression<T> {
  fun evaluate(): T
}