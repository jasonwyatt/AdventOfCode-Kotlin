package us.jwf.aoc2020

import java.io.Reader
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.reduce
import us.jwf.aoc.Day
import us.jwf.aoc.parsing.Group
import us.jwf.aoc.parsing.InfixExpression
import us.jwf.aoc.parsing.Operator
import us.jwf.aoc.parsing.Value
import us.jwf.aoc.toLineFlow

/**
 * Day 18 of AoC 2020.
 */
class Day18OperationOrder : Day<Long, Long> {
  /**
   * As you look out the window and notice a heavily-forested continent slowly appear over the
   * horizon, you are interrupted by the child sitting next to you. They're curious if you could
   * help them with their math homework.
   *
   * Unfortunately, it seems like this "math" follows different rules than you remember.
   *
   * The homework (your puzzle input) consists of a series of expressions that consist of addition
   * (+), multiplication (*), and parentheses ((...)). Just like normal math, parentheses indicate
   * that the expression inside must be evaluated before it can be used by the surrounding
   * expression. Addition still finds the sum of the numbers on both sides of the operator, and
   * multiplication still finds the product.
   *
   * However, the rules of operator precedence have changed. Rather than evaluating multiplication
   * before addition, the operators have the same precedence, and are evaluated left-to-right
   * regardless of the order in which they appear.
   *
   * For example, the steps to evaluate the expression 1 + 2 * 3 + 4 * 5 + 6 are as follows:
   *
   * ```
   * 1 + 2 * 3 + 4 * 5 + 6
   *   3   * 3 + 4 * 5 + 6
   *       9   + 4 * 5 + 6
   *          13   * 5 + 6
   *              65   + 6
   *                   71
   * ```
   *
   * Parentheses can override this order; for example, here is what happens if parentheses are added
   * to form 1 + (2 * 3) + (4 * (5 + 6)):
   *
   * ```
   * 1 + (2 * 3) + (4 * (5 + 6))
   * 1 +    6    + (4 * (5 + 6))
   *      7      + (4 * (5 + 6))
   *      7      + (4 *   11   )
   *      7      +     44
   *             51
   * ```
   *
   * Here are a few more examples:
   *
   * 2 * 3 + (4 * 5) becomes 26.
   * 5 + (8 * 3 + 9 + 3 * 4 * 3) becomes 437.
   * 5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4)) becomes 12240.
   * ((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2 becomes 13632.
   *
   * Before you can help with the homework, you need to understand it yourself. Evaluate the
   * expression on each line of the homework; what is the sum of the resulting values?
   */
  override suspend fun executePart1(input: Reader): Long {
    return input.toLineFlow().map { line ->
      val tokens = tokenize(line, phase = 1)
      InfixExpression(tokens).evaluate()
    }.reduce { accumulator, value -> accumulator + value }
  }

  /**
   * You manage to answer the child's questions and they finish part 1 of their homework, but get
   * stuck when they reach the next section: advanced math.
   *
   * Now, addition and multiplication have different precedence levels, but they're not the ones
   * you're familiar with. Instead, addition is evaluated before multiplication.
   *
   * For example, the steps to evaluate the expression 1 + 2 * 3 + 4 * 5 + 6 are now as follows:
   *
   * ```
   * 1 + 2 * 3 + 4 * 5 + 6
   *   3   * 3 + 4 * 5 + 6
   *   3   *   7   * 5 + 6
   *   3   *   7   *  11
   *      21       *  11
   *              231
   * ```
   *
   * Here are the other examples from above:
   *
   * 1 + (2 * 3) + (4 * (5 + 6)) still becomes 51.
   * 2 * 3 + (4 * 5) becomes 46.
   * 5 + (8 * 3 + 9 + 3 * 4 * 3) becomes 1445.
   * 5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4)) becomes 669060.
   * ((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2 becomes 23340.
   *
   * What do you get if you add up the results of evaluating the homework problems using these new
   * rules?
   */
  override suspend fun executePart2(input: Reader): Long {
    return input.toLineFlow().map { line ->
      val tokens = tokenize(line, phase = 2)
      InfixExpression(tokens).evaluate()
    }.reduce { accumulator, value -> accumulator + value }
  }

  sealed class Op : Operator<Long> {
    object AddOne : Op() {
      override val precedence = 1
      override val associativity = Operator.Associativity.LEFT
      override fun evaluate(valueStack: MutableList<Value<Long>>) {
        valueStack.add(Value(valueStack.removeLast().data + valueStack.removeLast().data))
      }
    }

    object AddTwo : Op() {
      override val precedence = 2
      override val associativity = Operator.Associativity.LEFT
      override fun evaluate(valueStack: MutableList<Value<Long>>) {
        valueStack.add(Value(valueStack.removeLast().data + valueStack.removeLast().data))
      }
    }

    object Mul : Op() {
      override val precedence = 1
      override val associativity = Operator.Associativity.LEFT
      override fun evaluate(valueStack: MutableList<Value<Long>>) {
        valueStack.add(Value(valueStack.removeLast().data * valueStack.removeLast().data))
      }
    }

    companion object {
      fun parsePhase1(c: Char): Op {
        return when (c) {
          '+' -> AddOne
          '*' -> Mul
          else -> throw IllegalArgumentException("Oops")
        }
      }

      fun parsePhase2(c: Char): Op {
        return when (c) {
          '+' -> AddTwo
          '*' -> Mul
          else -> throw IllegalArgumentException("Oops")
        }
      }
    }
  }

  companion object {
    private val TOKENS = Regex(pattern = "(\\()|(\\))|(-?[0-9]+)|([+-\\\\*/])")

    fun tokenize(str: String, phase: Int) = TOKENS.findAll(str).map {
      when {
        it.groupValues[1].isNotEmpty() -> Group.Open()
        it.groupValues[2].isNotEmpty() -> Group.Close()
        it.groupValues[3].isNotEmpty() -> Value(it.groupValues[3].toLong(10))
        it.groupValues[4].isNotEmpty() ->  {
          if (phase == 1) {
            Op.parsePhase1(it.groupValues[4][0])
          } else {
            Op.parsePhase2(it.groupValues[4][0])
          }
        }
        else -> throw IllegalArgumentException("Oops")
      }
    }.toList()

  }
}