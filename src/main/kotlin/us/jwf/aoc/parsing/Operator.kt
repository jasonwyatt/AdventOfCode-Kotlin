package us.jwf.aoc.parsing

interface Operator<T> : ExpressionToken<T> {
  val precedence: Int
  val associativity: Associativity

  /** Evaluates the operator given the value stack. May mutate the value stack if necessary. */
  fun evaluate(valueStack: MutableList<Value<T>>)

  enum class Associativity {
    LEFT, RIGHT
  }
}