package us.jwf.aoc.parsing.expression

data class Value<T>(val data: T) : ExpressionToken<T>