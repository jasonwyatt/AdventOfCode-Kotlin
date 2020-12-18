package us.jwf.aoc.parsing.arithmetic

import us.jwf.aoc.parsing.expression.Operator
import us.jwf.aoc.parsing.expression.Value
import kotlin.math.pow

@Suppress("UNCHECKED_CAST")
sealed class ArithmeticOperation<T : Number> : Operator<T> {
  class Add<T : Number> : ArithmeticOperation<T>() {
    override val precedence = 1
    override val associativity = Operator.Associativity.LEFT
    override fun evaluate(valueStack: MutableList<Value<T>>) {
      val rhs = valueStack.removeLast()
      val lhs = valueStack.removeLast()
      val newData = when (val rhsData = rhs.data) {
        is Int -> lhs.data as Int + rhsData
        is Long -> lhs.data as Long + rhsData
        is Float -> lhs.data as Float + rhsData
        is Double -> lhs.data as Double + rhsData
        else -> throw IllegalArgumentException("Invalid value type in stack: $rhsData")
      } as T
      valueStack.add(Value(newData))
    }
  }

  class Sub<T : Number> : ArithmeticOperation<T>() {
    override val precedence = 1
    override val associativity = Operator.Associativity.LEFT
    override fun evaluate(valueStack: MutableList<Value<T>>) {
      val rhs = valueStack.removeLast()
      val lhs = valueStack.removeLast()
      val newData = when (val rhsData = rhs.data) {
        is Int -> lhs.data as Int - rhsData
        is Long -> lhs.data as Long - rhsData
        is Float -> lhs.data as Float - rhsData
        is Double -> lhs.data as Double - rhsData
        else -> throw IllegalArgumentException("Invalid value type in stack: $rhsData")
      } as T
      valueStack.add(Value(newData))
    }
  }

  class Mul<T : Number> : ArithmeticOperation<T>() {
    override val precedence = 2
    override val associativity = Operator.Associativity.LEFT
    override fun evaluate(valueStack: MutableList<Value<T>>) {
      val rhs = valueStack.removeLast()
      val lhs = valueStack.removeLast()
      val newData = when (val rhsData = rhs.data) {
        is Int -> lhs.data as Int * rhsData
        is Long -> lhs.data as Long * rhsData
        is Float -> lhs.data as Float * rhsData
        is Double -> lhs.data as Double * rhsData
        else -> throw IllegalArgumentException("Invalid value type in stack: $rhsData")
      } as T
      valueStack.add(Value(newData))
    }
  }

  class Div<T : Number> : ArithmeticOperation<T>() {
    override val precedence = 2
    override val associativity = Operator.Associativity.LEFT
    override fun evaluate(valueStack: MutableList<Value<T>>) {
      val rhs = valueStack.removeLast()
      val lhs = valueStack.removeLast()
      val newData = when (val rhsData = rhs.data) {
        is Int -> lhs.data as Int / rhsData
        is Long -> lhs.data as Long / rhsData
        is Float -> lhs.data as Float / rhsData
        is Double -> lhs.data as Double / rhsData
        else -> throw IllegalArgumentException("Invalid value type in stack: $rhsData")
      } as T
      valueStack.add(Value(newData))
    }
  }

  class Pow<T : Number> : ArithmeticOperation<T>() {
    override val precedence = 3
    override val associativity = Operator.Associativity.RIGHT
    override fun evaluate(valueStack: MutableList<Value<T>>) {
      val rhs = valueStack.removeLast()
      val lhs = valueStack.removeLast()
      val newData: T = when (val rhsData = rhs.data) {
        is Int -> (lhs.data as Int).pow(rhsData)
        is Long -> (lhs.data as Long).pow(rhsData)
        is Float -> (lhs.data as Float).pow(rhsData)
        is Double -> (lhs.data as Double).pow(rhsData)
        else -> throw IllegalArgumentException("Invalid value type in stack: $rhsData")
      } as T
      valueStack.add(Value(newData))
    }
  }

  companion object {
    fun <T : Number> fromToken(rawToken: String): ArithmeticOperation<T> {
      return when (rawToken) {
        "+" -> Add()
        "-" -> Sub()
        "*" -> Mul()
        "/" -> Div()
        "^" -> Pow()
        else -> throw IllegalArgumentException("Invalid ArithmeticOperation token: $rawToken")
      }
    }
  }
}

private fun Int.pow(rhs: Int): Int {
  if (rhs < 0) return 0
  if (rhs == 0) return 1
  if (rhs == 1) return this
  val halfPow = this.pow(rhs / 2)
  if (rhs % 2 == 1) return this * halfPow * halfPow
  return halfPow * halfPow
}

private fun Long.pow(rhs: Long): Long {
  if (rhs < 0) return 0
  if (rhs == 0L) return 1
  if (rhs == 1L) return this
  val halfPow = this.pow(rhs / 2L)
  if (rhs % 2 == 1L) return this * halfPow * halfPow
  return halfPow * halfPow
}
