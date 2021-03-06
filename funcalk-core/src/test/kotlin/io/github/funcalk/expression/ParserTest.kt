package io.github.funcalk.expression

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.Double.Companion.NaN
import kotlin.Double.Companion.POSITIVE_INFINITY
import kotlin.test.assertNotNull

internal class ParserTest {
  @Test
  fun `parse a floating-point number`() {
    val parser = Parser("1.1")

    val expression = assertNotNull(parser.parse())

    assertThat(expression).isInstanceOf(Number::class.java)
    assertThat(expression.calculate()).isEqualTo(1.1)
  }

  @Test
  fun `parse empty input`() {
    val parser = Parser("")

    assertThrows<IllegalArgumentException> { parser.parse() }
  }

  @Test
  fun `parse input with an extra token`() {
    val parser = Parser("1 2")

    assertThrows<IllegalArgumentException> { parser.parse() }
  }

  @Test
  fun `parse a sum`() {
    val parser = Parser("   2    +   3    ")

    val expression = parser.parse()

    assertThat(expression).isEqualTo(Plus(Number(2.0), Number(3.0)))
  }

  @Test
  fun `parse a sub`() {
    val parser = Parser(" 6  -    2    -   3    ")

    val expression = parser.parse()

    assertThat(expression).isEqualTo(Minus(Minus(Number(6.0), Number(2.0)), Number(3.0)))
  }

  @Test
  fun `parse a mult`() {
    val parser = Parser(" 6  *    2    *   3    ")

    val expression = parser.parse()

    assertThat(expression).isEqualTo(Mult(Mult(Number(6.0), Number(2.0)), Number(3.0)))
  }

  @Test
  fun `parse a div`() {
    val parser = Parser("6 / 2 / 3")

    val expression = parser.parse()

    assertThat(expression).isEqualTo(Div(Div(Number(6.0), Number(2.0)), Number(3.0)))
  }

  @Test
  fun `parse a unary minus`() {
    val parser = Parser("-6 + 2")

    val expression = parser.parse()

    assertThat(expression).isEqualTo(Plus(UnaryMinus(Number(6.0)), Number(2.0)))
  }

  @Test
  fun `parse a unary plus`() {
    val parser = Parser("+6 + 2")

    val expression = parser.parse()

    assertThat(expression).isEqualTo(Plus(UnaryPlus(Number(6.0)), Number(2.0)))
  }

  @Test
  fun `operators priority with parentheses`() {
    val parser = Parser("(4 + 2) * 2")

    val expression = parser.parse()

    assertThat(expression).isEqualTo(Mult(Plus(Number(4.0), Number(2.0)), Number(2.0)))
  }

  @Test
  fun `parse input without closing parenthesis`() {
    val parser = Parser("(1 + 2")

    assertThrows<IllegalArgumentException> { parser.parse() }
  }

  @Test
  fun `operators priority`() {
    val parser = Parser("4 + -8 * +6 - 2 / -3")

    val expression = parser.parse()

    assertThat(expression).isEqualTo(
      Minus(
        Plus(
          Number(4.0),
          Mult(UnaryMinus(Number(8.0)), UnaryPlus(Number(6.0)))
        ),
        Div(Number(2.0), UnaryMinus(Number(3.0)))
      )
    )
  }

  @Test
  fun `pi should return pi value`() {
    val parser = Parser("pi")

    val expression = parser.parse()

    assertThat(expression).isEqualTo(Number(Math.PI))
  }

  @Test
  fun `PI should return pi value`() {
    val parser = Parser("PI")

    val expression = parser.parse()

    assertThat(expression).isEqualTo(Number(Math.PI))
  }

  @Test
  fun `e should return e value`() {
    val parser = Parser("e")

    val expression = parser.parse()

    assertThat(expression).isEqualTo(Number(Math.E))
  }

  @Test
  fun `E should return e value`() {
    val parser = Parser("E")

    val expression = parser.parse()

    assertThat(expression).isEqualTo(Number(Math.E))
  }

  @Test
  fun `parse a power`() {
    val parser = Parser("6^2")

    val expression = parser.parse()

    assertThat(expression).isEqualTo(Power(Number(6.0), Number(2.0)))
  }

  @Test
  fun `parse variable`() {
    val parser = Parser("x^2")

    val expression = parser.parse()

    assertThat(expression).isEqualTo(Power(Var("x"), Number(2.0)))
  }

  @Test
  fun `positive inf value`() {
    val parser = Parser("inf")

    val expression = parser.parse()

    assertThat(expression).isEqualTo(Number(POSITIVE_INFINITY))
  }

  @Test
  fun `NaN value`() {
    val parser = Parser("nan")

    val expression = parser.parse()

    assertThat(expression).isEqualTo(Number(NaN))
  }

  @Test
  fun `function call`() {
    val parser = Parser("sqrt(4)")

    val expression = parser.parse()

    assertThat(expression).isEqualTo(FunCall(Math::sqrt, Number(4.0)))
  }

  @Test
  fun `function call without closing parenthesis`() {
    val parser = Parser("sqrt(4")

    val e = assertThrows<IllegalArgumentException> { parser.parse() }
    assertThat(e.message?.lowercase()).contains("expected", ")")
  }

  @Test
  fun `function call with argument as expression`() {
    val parser = Parser("sqrt(2+2)")

    val expression = parser.parse()

    assertThat(expression).isEqualTo(FunCall(Math::sqrt, Plus(Number(2.0), Number(2.0))))
  }

  @Test
  fun `function call as part of expression`() {
    val parser = Parser("2 + sqrt(4)")

    val expression = parser.parse()

    assertThat(expression).isEqualTo(Plus(Number(2.0), FunCall(Math::sqrt, Number(4.0))))
  }

  @Test
  fun `function call with unknown function`() {
    val parser = Parser("unknown(4)")

    val e = assertThrows<IllegalArgumentException> { parser.parse() }
    assertThat(e.message?.lowercase()).contains("unknown")
  }
}