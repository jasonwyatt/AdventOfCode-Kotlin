package us.jwf.aoc.parsing

sealed class Group<T> : ExpressionToken<T> {
  class Open<T>() : Group<T>()
  class Close<T>() : Group<T>()
}
