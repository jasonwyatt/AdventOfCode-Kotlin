package us.jwf.aoc.parsing

interface Function<T> : ExpressionToken<T> {
  /** Evaluates the [Function] given the stack in postfix orientation. May mutate the stack. */
  fun evaluate(valueStack: MutableList<Value<T>>)
}
