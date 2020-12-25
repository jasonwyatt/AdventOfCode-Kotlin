package us.jwf.aoc2020

import java.io.Reader
import java.lang.StringBuilder
import java.util.*
import kotlin.system.measureNanoTime
import us.jwf.aoc.Day

/**
 * Day 23 of AoC 2020
 */
class Day23CrabCups : Day<String, Long> {
  /**
   * The small crab challenges you to a game! The crab is going to mix up some cups, and you have to
   * predict where they'll end up.
   *
   * The cups will be arranged in a circle and labeled clockwise (your puzzle input). For example,
   * if your labeling were 32415, there would be five cups in the circle; going clockwise around the
   * circle from the first cup, the cups would be labeled 3, 2, 4, 1, 5, and then back to 3 again.
   *
   * Before the crab starts, it will designate the first cup in your list as the current cup. The
   * crab is then going to do 100 moves.
   *
   * Each move, the crab does the following actions:
   *
   * * The crab picks up the three cups that are immediately clockwise of the current cup. They are
   *   removed from the circle; cup spacing is adjusted as necessary to maintain the circle.
   * * The crab selects a destination cup: the cup with a label equal to the current cup's label
   *   minus one. If this would select one of the cups that was just picked up, the crab will keep
   *   subtracting one until it finds a cup that wasn't just picked up. If at any point in this
   *   process the value goes below the lowest value on any cup's label, it wraps around to the
   *   highest value on any cup's label instead.
   * * The crab places the cups it just picked up so that they are immediately clockwise of the
   *   destination cup. They keep the same order as when they were picked up.
   * * The crab selects a new current cup: the cup which is immediately clockwise of the current
   *   cup.
   *
   * After the crab is done, what order will the cups be in? Starting after the cup labeled 1,
   * collect the other cups' labels clockwise into a single string with no extra characters; each
   * number except 1 should appear exactly once.
   *
   * Using your labeling, simulate 100 moves. What are the labels on the cups after cup 1?
   */
  override suspend fun executePart1(input: Reader): String {
    // Read 'em in.
    val nodesByValue = mutableMapOf<Int, Node>()
    val nodes = input.readLines()[0]
      .map { c ->
        val value = "$c".toInt(10)
        Node(value, null).also { nodesByValue[value] = it }
      }

    // Link 'em up.
    nodes.forEachIndexed { i, node -> node.next = nodes[(i + 1) % nodes.size] }

    // Play the game.
    playGame(100, nodes[0], nodesByValue)

    // Print 'em out.
    return nodes[0].find(1).joinExceptSelf()
  }

  /**
   * Due to what you can only assume is a mistranslation (you're not exactly fluent in Crab), you
   * are quite surprised when the crab starts arranging many cups in a circle on your raft - one
   * million (1000000) in total.
   *
   * Your labeling is still correct for the first few cups; after that, the remaining cups are just
   * numbered in an increasing fashion starting from the number after the highest number in your
   * list and proceeding one by one until one million is reached. (For example, if your labeling
   * were 54321, the cups would be numbered 5, 4, 3, 2, 1, and then start counting up from 6 until
   * one million is reached.) In this way, every number from one through one million is used exactly
   * once.
   *
   * After discovering where you made the mistake in translating Crab Numbers, you realize the small
   * crab isn't going to do merely 100 moves; the crab is going to do ten million (10000000) moves!
   *
   * The crab is going to hide your stars - one each - under the two cups that will end up
   * immediately clockwise of cup 1. You can have them if you predict what the labels on those
   * cups will be when the crab is finished.
   *
   * Determine which two cups will end up immediately clockwise of cup 1. What do you get if you
   * multiply their labels together?
   */
  override suspend fun executePart2(input: Reader): Long {
    var max = Int.MIN_VALUE
    val nodesByValue = mutableMapOf<Int, Node>()
    // Read 'em in.
    val nodes = input.readLines()[0]
      .map { c ->
        val value = "$c".toInt(10)
        max = maxOf(value, max)
        Node(value, null).also { nodesByValue[value] = it }
      }
      .toMutableList()

    // Add the rest.
    ((max + 1)..1000000).forEach {
      val node = Node(it, null)
      nodesByValue[it] = node
      nodes.add(node)
    }

    // Link 'em up.
    nodes.forEachIndexed { i, node -> node.next = nodes[(i + 1) % nodes.size] }

    // Play the game.
    playGame(10000000, nodes[0], nodesByValue)

    return nodesByValue[1]?.let {
      val next = checkNotNull(it.next)
      val nextNext = checkNotNull(next.next)
      next.value.toLong() * nextNext.value.toLong()
    } ?: 0
  }

  private fun playGame(rounds: Int, startNode: Node, nodesByValue: Map<Int, Node>) {
    val inactiveNodes = BooleanArray(nodesByValue.size + 1)
    val lowest = nodesByValue.keys.minOrNull() ?: return
    val highest = nodesByValue.keys.maxOrNull() ?: return

    var current = startNode
    repeat(rounds) {
      val taken = current.removeThree()
      taken.forEach { inactiveNodes[it.value] = true }

      var nextValue = if (current.value == lowest) highest else current.value - 1
      while (inactiveNodes[nextValue]) {
        nextValue = if (nextValue == lowest) highest else nextValue - 1
      }
      taken.forEach { inactiveNodes[it.value] = false }

      val destination = checkNotNull(nodesByValue[nextValue])
      destination.insertAfter(taken)

      current = checkNotNull(current.next)
    }
  }

  class Node(val value: Int, var next: Node?) {
    override fun equals(other: Any?): Boolean = other is Node && other.value == value
    override fun hashCode(): Int = value
    override fun toString(): String = "Node($value, next = ${next?.value})"

    fun removeThree(): Node {
      val removedHead = checkNotNull(next)
      val tail = removedHead.next?.next
      next = tail?.next
      tail?.next = null
      return removedHead
    }

    fun insertAfter(list: Node) {
      val tail = this.next
      this.next = list

      var current = list
      var next = current.next
      while (next != null) {
        current = next
        next = next.next
      }
      current.next = tail
    }

    fun joinExceptSelf(): String {
      val result = StringBuilder()
      forEach { if (it != this) { result.append(it.value) } }
      return result.toString()
    }

    fun find(value: Int): Node {
      forEach { if (it.value == value) return it }
      throw IllegalArgumentException("Not Found")
    }

    inline fun forEach(block: (Node) -> Unit) {
      var current: Node? = this
      do {
        if (current != null) block(current)
        current = current?.next
      } while (current != null && current != this)
    }
  }
}