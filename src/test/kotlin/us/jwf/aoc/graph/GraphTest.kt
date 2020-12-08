package us.jwf.aoc.graph

import com.google.common.truth.Truth.assertThat
import kotlin.test.assertFailsWith
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
internal class GraphTest {
  @Test
  fun findMSTKruskal_linkedList_findsLinkedList() {
    val graph = Graph<String>()
    graph.addEdge("a", "b", 1.0)
    graph.addEdge("b", "c", 1.0)
    graph.addEdge("c", "d", 1.0)
    graph.addEdge("d", "e", 1.0)

    val mst = graph.findMSTKruskal()

    assertThat(mst).isEqualTo(graph)
  }

  @Test
  fun findMSTKruskal_twoNodeTwoEdge_findsShortestEdge() {
    val graph = Graph<String>()
    graph.addEdge("a", "b", 1.0)
    graph.addEdge("a", "b", 2.0)

    val mst = graph.findMSTKruskal()
    val expected = Graph<String>()
    expected.addEdge("a", "b", 1.0)

    assertThat(mst).isEqualTo(expected)
  }

  // Credit: https://stackoverflow.com/a/23279294
  /**
   *      B -- A
   *      |  /  \
   *      | /    \
   *      C       D
   *            / |
   *          /   |
   *         E -- F
   */
  @Test
  fun findMSTKruskal_badCaseFromStackOverflow() {
    val graph = Graph<String>()
    graph.addEdge("a", "b", 1.0)
    graph.addEdge("a", "c", 3.0)
    graph.addEdge("b", "c", 1.0)
    graph.addEdge("a", "d", 3.0)
    graph.addEdge("d", "e", 2.0)
    graph.addEdge("d", "f", 1.0)
    graph.addEdge("f", "e", 1.0)

    val mst = graph.findMSTKruskal()
    val expected = Graph<String>()
    expected.addEdge("a", "b", 1.0)
    expected.addEdge("b", "c", 1.0)
    expected.addEdge("a", "d", 3.0)
    expected.addEdge("d", "f", 1.0)
    expected.addEdge("d", "e", 2.0)

    assertThat(mst).isEqualTo(expected)
  }
}