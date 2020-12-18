package us.jwf.aoc.parsing.arithmetic

import kotlin.math.pow
import us.jwf.aoc.parsing.ExpressionToken
import us.jwf.aoc.parsing.Group
import us.jwf.aoc.parsing.InfixExpression
import us.jwf.aoc.parsing.Operator
import us.jwf.aoc.parsing.Value

class ArithmeticExpression(val raw: String) : InfixExpression<Double>(parse(raw)) {
  override fun toString(): String = "ArithmeticExpression($raw)"

  private sealed class Op : Operator<Double> {
    object Add : Op() {
      override val precedence = 1
      override val associativity = Operator.Associativity.LEFT
      override fun evaluate(valueStack: MutableList<Value<Double>>) {
        val rhs = valueStack.removeLast()
        val lhs = valueStack.removeLast()
        valueStack.add(Value(lhs.data + rhs.data))
      }
    }

    object Sub : Op() {
      override val precedence = 1
      override val associativity = Operator.Associativity.LEFT
      override fun evaluate(valueStack: MutableList<Value<Double>>) {
        val rhs = valueStack.removeLast()
        val lhs = valueStack.removeLast()
        valueStack.add(Value(lhs.data - rhs.data))
      }
    }

    object Mul : Op() {
      override val precedence = 2
      override val associativity = Operator.Associativity.LEFT
      override fun evaluate(valueStack: MutableList<Value<Double>>) {
        val rhs = valueStack.removeLast()
        val lhs = valueStack.removeLast()
        valueStack.add(Value(lhs.data * rhs.data))
      }
    }

    object Div : Op() {
      override val precedence = 2
      override val associativity = Operator.Associativity.LEFT
      override fun evaluate(valueStack: MutableList<Value<Double>>) {
        val rhs = valueStack.removeLast()
        val lhs = valueStack.removeLast()
        valueStack.add(Value(lhs.data / rhs.data))
      }
    }

    object Pow : Op() {
      override val precedence = 3
      override val associativity = Operator.Associativity.RIGHT
      override fun evaluate(valueStack: MutableList<Value<Double>>) {
        val rhs = valueStack.removeLast()
        val lhs = valueStack.removeLast()
        valueStack.add(Value(lhs.data.pow(rhs.data)))
      }
    }
  }

  companion object {
    private val TOKEN_PATTERN = Regex("(\\()|(\\))|(-?[0-9]+(\\.[0-9]+)?)|([-+*/^])")
    private fun parse(raw: String): List<ExpressionToken<Double>> {
      return TOKEN_PATTERN.findAll(raw)
        .map {
          when {
            it.groupValues[1].isNotEmpty() -> Group.Open()
            it.groupValues[2].isNotEmpty() -> Group.Close()
            it.groupValues[3].isNotEmpty() -> Value(it.groupValues[3].toDouble())
            it.groupValues[5].isNotEmpty() -> when (it.groupValues[5]) {
              "+" -> Op.Add
              "-" -> Op.Sub
              "*" -> Op.Mul
              "/" -> Op.Div
              "^" -> Op.Pow
              else -> throw IllegalArgumentException("Bad operator")
            }
            else -> throw IllegalArgumentException("Bad format")
          }
        }
        .toList()
    }
  }
}