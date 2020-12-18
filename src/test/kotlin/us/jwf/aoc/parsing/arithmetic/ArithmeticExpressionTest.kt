package us.jwf.aoc.parsing.arithmetic

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
internal class ArithmeticExpressionTest {
  @Test
  fun evalSingleValue() {
    assertThat(ArithmeticExpression("1.5").evaluate()).isEqualTo(1.5)
    assertThat(ArithmeticExpression("-1.5").evaluate()).isEqualTo(-1.5)
  }

  @Test
  fun add() {
    assertThat(ArithmeticExpression("1 + 2").evaluate()).isEqualTo(3.0)
  }

  @Test
  fun sub() {
    assertThat(ArithmeticExpression("1 - 2").evaluate()).isEqualTo(-1.0)
  }

  @Test
  fun mul() {
    assertThat(ArithmeticExpression("1 * 2").evaluate()).isEqualTo(2.0)
  }

  @Test
  fun div() {
    assertThat(ArithmeticExpression("1 / 2").evaluate()).isEqualTo(0.5)
  }

  @Test
  fun pow() {
    assertThat(ArithmeticExpression("2 ^ 3").evaluate()).isEqualTo(8.0)
  }

  @Test
  fun groups() {
    assertThat(ArithmeticExpression("(1 + 2) * (3 / 4) ^ (2 + 1)").evaluate())
      .isEqualTo(1.265625)
  }

  @Test
  fun precedence() {
    assertThat(ArithmeticExpression("1 + 2 * 3 / 4").evaluate()).isEqualTo(2.5)
  }
}