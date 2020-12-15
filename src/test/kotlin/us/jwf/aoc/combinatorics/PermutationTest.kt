package us.jwf.aoc.combinatorics

import com.google.common.truth.Truth.assertThat
import kotlin.streams.toList
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PermutationTest {
  @Test
  fun permute(): Unit = runBlocking {
    val permutations = setOf(0,1,2).permute().toList()
    assertThat(permutations).containsExactly(
      listOf(0, 1, 2),
      listOf(0, 2, 1),
      listOf(1, 0, 2),
      listOf(1, 2, 0),
      listOf(2, 0, 1),
      listOf(2, 1, 0)
    )
  }

  @Test
  fun permute_shorter(): Unit = runBlocking {
    val permutations = setOf(0,1,2).permute(2).toList()
    assertThat(permutations).containsExactly(
      listOf(0, 1),
      listOf(0, 2),
      listOf(1, 0),
      listOf(1, 2),
      listOf(2, 0),
      listOf(2, 1)
    )
  }

  @Test
  fun permute_shortest(): Unit = runBlocking {
    val permutations = setOf(0,1,2).permute(1).toList()
    assertThat(permutations).containsExactly(
      listOf(0),
      listOf(1),
      listOf(2)
    )
  }

  @Test
  fun permute_long(): Unit = runBlocking {
    val permutations = "abcdefghijklmnopqrstuvwxyz".chars().toList().toSet().permute(5).count()
    assertThat(permutations).isEqualTo(26 * 25 * 24 * 23 * 22)
  }
}