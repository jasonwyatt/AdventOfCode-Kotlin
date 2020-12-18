package us.jwf.aoc.parsing.arithmetic

import kotlin.reflect.KClass
import us.jwf.aoc.parsing.expression.ExpressionToken
import us.jwf.aoc.parsing.expression.Group
import us.jwf.aoc.parsing.expression.InfixExpression
import us.jwf.aoc.parsing.expression.Value

class ArithmeticExpression<T : Number>(
  private val type: KClass<T>,
  private val raw: String
) : InfixExpression<T>(parse(type, raw)) {
  override fun toString(): String = "ArithmeticExpression<${type.simpleName}>($raw)"

  companion object {
    private val TOKEN_PATTERN = Regex("(\\()|(\\))|(-?[0-9]+(\\.[0-9]+)?)|([-+*/^])")

    @Suppress("UNCHECKED_CAST")
    private fun <T : Number> parse(type: KClass<T>, raw: String): List<ExpressionToken<T>> {
      return TOKEN_PATTERN.findAll(raw)
        .map {
          when {
            it.groupValues[1].isNotEmpty() -> Group.Open()
            it.groupValues[2].isNotEmpty() -> Group.Close()
            it.groupValues[3].isNotEmpty() -> {
              val data: T = when (type) {
                Int::class -> it.groupValues[3].toInt(10)
                Long::class -> it.groupValues[3].toLong(10)
                Float::class -> it.groupValues[3].toFloat()
                Double::class -> it.groupValues[3].toDouble()
                Char::class -> it.groupValues[3].toInt(10).toChar()
                Byte::class -> it.groupValues[3].toByte(10)
                Short::class -> it.groupValues[3].toShort(10)
                else -> throw IllegalArgumentException("Unsupported value type: $type")
              } as T
              Value(data)
            }
            it.groupValues[5].isNotEmpty() -> ArithmeticOperation.fromToken(it.groupValues[5])
            else -> throw IllegalArgumentException("Bad format")
          }
        }
        .toList()
    }
  }
}

inline fun <reified T : Number> ArithmeticExpression(raw: String): ArithmeticExpression<T> {
  return ArithmeticExpression(T::class, raw)
}
