package us.jwf.aoc2020

import java.io.Reader
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import us.jwf.aoc.Day
import us.jwf.aoc.toIntFlow

/**
 * Day One of AoC 2020.
 */
class DayOne : Day<Int, Int> {
  /**
   * After saving Christmas five years in a row, you've decided to take a vacation at a nice
   * resort on a tropical island. Surely, Christmas will go on without you.
   *
   * The tropical island has its own currency and is entirely cash-only. The gold coins used
   * there have a little picture of a starfish; the locals just call them stars. None of the
   * currency exchanges seem to have heard of them, but somehow, you'll need to find fifty of
   * these coins by the time you arrive so you can pay the deposit on your room.
   *
   * To save your vacation, you need to get all fifty stars by December 25th.
   *
   * Collect stars by solving puzzles. Two puzzles will be made available on each day in the
   * Advent calendar; the second puzzle is unlocked when you complete the first. Each puzzle
   * grants one star. Good luck!
   *
   * Before you leave, the Elves in accounting just need you to fix your expense report (your
   * puzzle input); apparently, something isn't quite adding up.
   *
   * Specifically, they need you to find the two entries that sum to 2020 and then multiply
   * those two numbers together.
   *
   * For example, suppose your expense report contained the following:
   *   1721
   *   979
   *   366
   *   299
   *   675
   *   1456
   *
   * In this list, the two entries that sum to 2020 are 1721 and 299. Multiplying them together
   * produces 1721 * 299 = 514579, so the correct answer is 514579.
   *
   * Of course, your expense report is much larger. Find the two entries that sum to 2020; what
   * do you get if you multiply them together?
   */
  override suspend fun executePart1(input: Reader): Int {
    val lookup = mutableSetOf<Int>()
    val firstWithMatch = input.toIntFlow("\n")
      .filter {
        val exists = (2020 - it) in lookup
        if (!exists) lookup.add(it)
        exists
      }.first()
    return firstWithMatch * (2020 - firstWithMatch)
  }

  /**
   * The Elves in accounting are thankful for your help; one of them even offers you a starfish
   * coin they had left over from a past vacation. They offer you a second one if you can find
   * three numbers in your expense report that meet the same criteria.
   *
   * Using the above example again, the three entries that sum to 2020 are 979, 366, and 675.
   * Multiplying them together produces the answer, 241861950.
   *
   * In your expense report, what is the product of the three entries that sum to 2020?
   */
  override suspend fun executePart2(input: Reader): Int {
    val sorted = input.toIntFlow("\n").toList().sorted()

    fun findProductOfThree(head: Int, tail: Int): Int? {
      // If we have crossed, there is no answer.
      if (head >= tail - 1) return null

      val remainder = 2020 - sorted[head] - sorted[tail]

      // If the remainder is less than our head, make the tail smaller, because there's no
      // way we could find something to match by moving the head forward any more.
      if (remainder < sorted[head]) return findProductOfThree(head, tail - 1)

      // Find where our third value lies.
      val location =
        sorted.binarySearch(
          fromIndex = head + 1,
          toIndex = tail - 1
        ) { it.compareTo(remainder) }

      // If the third value is valid, we're done.
      if (location > head) return sorted[head] * sorted[tail] * sorted[location]

      // If the third value is invalid, try moving the tail or the head.
      return findProductOfThree(head + 1, tail) ?: findProductOfThree(head, tail - 1)
    }

    return findProductOfThree(0, sorted.size - 1) ?: -1
  }
}