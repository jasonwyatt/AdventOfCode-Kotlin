package us.jwf.aoc.parsing.arithmetic

import us.jwf.aoc.parsing.expression.ExpressionToken
import us.jwf.aoc.parsing.expression.Group
import us.jwf.aoc.parsing.expression.InfixExpression
import us.jwf.aoc.parsing.expression.Value

class ArithmeticExpression(val raw: String) : InfixExpression<Double>(parse(raw)) {
  override fun toString(): String = "ArithmeticExpression($raw)"

  companion object {
    private val TOKEN_PATTERN = Regex("(\\()|(\\))|(-?[0-9]+(\\.[0-9]+)?)|([-+*/^])")
    private fun parse(raw: String): List<ExpressionToken<Double>> {
      return TOKEN_PATTERN.findAll(raw)
        .map {
          when {
            it.groupValues[1].isNotEmpty() -> Group.Open()
            it.groupValues[2].isNotEmpty() -> Group.Close()
            it.groupValues[3].isNotEmpty() -> Value(it.groupValues[3].toDouble())
            it.groupValues[5].isNotEmpty() -> ArithmeticOperation.fromToken(it.groupValues[5])
            else -> throw IllegalArgumentException("Bad format")
          }
        }
        .toList()
    }
  }
}