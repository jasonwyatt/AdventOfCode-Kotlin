package us.jwf.aoc.parsing.arithmetic

import us.jwf.aoc.parsing.expression.Operator
import us.jwf.aoc.parsing.expression.Value
import kotlin.math.pow

sealed class ArithmeticOperation : Operator<Double> {
  object Add : ArithmeticOperation() {
    override val precedence = 1
    override val associativity = Operator.Associativity.LEFT
    override fun evaluate(valueStack: MutableList<Value<Double>>) {
      val rhs = valueStack.removeLast()
      val lhs = valueStack.removeLast()
      valueStack.add(Value(lhs.data + rhs.data))
    }
  }

  object Sub : ArithmeticOperation() {
    override val precedence = 1
    override val associativity = Operator.Associativity.LEFT
    override fun evaluate(valueStack: MutableList<Value<Double>>) {
      val rhs = valueStack.removeLast()
      val lhs = valueStack.removeLast()
      valueStack.add(Value(lhs.data - rhs.data))
    }
  }

  object Mul : ArithmeticOperation() {
    override val precedence = 2
    override val associativity = Operator.Associativity.LEFT
    override fun evaluate(valueStack: MutableList<Value<Double>>) {
      val rhs = valueStack.removeLast()
      val lhs = valueStack.removeLast()
      valueStack.add(Value(lhs.data * rhs.data))
    }
  }

  object Div : ArithmeticOperation() {
    override val precedence = 2
    override val associativity = Operator.Associativity.LEFT
    override fun evaluate(valueStack: MutableList<Value<Double>>) {
      val rhs = valueStack.removeLast()
      val lhs = valueStack.removeLast()
      valueStack.add(Value(lhs.data / rhs.data))
    }
  }

  object Pow : ArithmeticOperation() {
    override val precedence = 3
    override val associativity = Operator.Associativity.RIGHT
    override fun evaluate(valueStack: MutableList<Value<Double>>) {
      val rhs = valueStack.removeLast()
      val lhs = valueStack.removeLast()
      valueStack.add(Value(lhs.data.pow(rhs.data)))
    }
  }

  companion object {
    fun fromToken(rawToken: String): ArithmeticOperation {
      return when (rawToken) {
        "+" -> Add
        "-" -> Sub
        "*" -> Mul
        "/" -> Div
        "^" -> Pow
        else -> throw IllegalArgumentException("Invalid ArithmeticOperation token: $rawToken")
      }
    }
  }
}