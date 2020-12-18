package us.jwf.aoc.parsing.expression

open class InfixExpression<T>(private val tokens: List<ExpressionToken<T>>) : Expression<T> {
  override fun evaluate(): T {
    val postfixStack = mutableListOf<ExpressionToken<T>>()
    val operatorStack = mutableListOf<ExpressionToken<T>>()

    tokens.forEach {
      when (it) {
        is Value<*> -> postfixStack.add(it)
        is Group.Open<*> -> operatorStack.add(it)
        is Group.Close<*> -> {
          while (operatorStack.last() !is Group.Open) {
            postfixStack.add(operatorStack.removeLast())
          }
          operatorStack.removeLast()
          if (operatorStack.lastOrNull() is Function<*>) {
            postfixStack.add(operatorStack.removeLast())
          }
        }
        is Operator<*> -> {
          while(true) {
            operatorStack.lastOrNull() as? Operator<T> ?: break
            val top = operatorStack.removeLast() as Operator<T>
            if (top.precedence > it.precedence) {
              postfixStack.add(top)
            } else if (top.precedence == it.precedence &&
              it.associativity == Operator.Associativity.LEFT
            ) {
              postfixStack.add(top)
            } else {
              operatorStack.add(top)
              break
            }
          }
          operatorStack.add(it)
        }
      }
    }
    while (operatorStack.isNotEmpty()) postfixStack.add(operatorStack.removeLast())

    val valueStack = mutableListOf<Value<T>>()
    postfixStack.forEach {
      when (it) {
        is Value<T> -> valueStack.add(it)
        is Operator<T> -> it.evaluate(valueStack)
        is Function<T> -> it.evaluate(valueStack)
      }
    }
    return valueStack.last().data
  }
}