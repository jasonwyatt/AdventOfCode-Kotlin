package us.jwf.aoc.parsing.expression

sealed class Group<T> : ExpressionToken<T> {
  class Open<T>() : Group<T>()
  class Close<T>() : Group<T>()
}
