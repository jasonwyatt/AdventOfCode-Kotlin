package us.jwf.aoc.parsing

interface Expression<T> {
  fun evaluate(): T
}