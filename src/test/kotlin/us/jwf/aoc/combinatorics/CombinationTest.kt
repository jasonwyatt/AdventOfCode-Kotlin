package us.jwf.aoc.combinatorics

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CombinationTest {
  @Test
  fun choose_3_0(): Unit = runBlocking {
    val combinations = setOf(1,2,3).choose(0).toList()
    assertThat(combinations).containsExactly(setOf<Int>())
  }

  @Test
  fun choose_3_1(): Unit = runBlocking {
    val combinations = setOf(1,2,3).choose(1).toList()
    assertThat(combinations).containsExactly(
      setOf(1),
      setOf(2),
      setOf(3),
    )
  }

  @Test
  fun choose_3_2(): Unit = runBlocking {
    val combinations = setOf(1,2,3).choose(2).toList()
    assertThat(combinations).containsExactly(
      setOf(1, 2),
      setOf(1, 3),
      setOf(2, 3),
    )
  }

  @Test
  fun choose_3_3(): Unit = runBlocking {
    val combinations = setOf(1,2,3).choose(3).toList()
    assertThat(combinations).containsExactly(
      setOf(1, 2, 3),
    )
  }

  @Test
  fun choose_a_bunch(): Unit = runBlocking {
    val combinations = "abcdefghijklmnopqrstuvwxyz".toList().toSet()
      .choose(5)
      .count()
    assertThat(combinations).isEqualTo(65780)
  }
}