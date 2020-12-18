package us.jwf.aoc.parsing

data class Value<T>(val data: T) : ExpressionToken<T>