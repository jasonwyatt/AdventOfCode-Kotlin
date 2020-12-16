package us.jwf.aoc2020

import java.io.Reader
import us.jwf.aoc.Day
import us.jwf.aoc.product

/**
 * Day 16 of AoC 2020
 */
class Day16TicketTranslation : Day<Int, Long> {
  /**
   * As you're walking to yet another connecting flight, you realize that one of the legs of your
   * re-routed trip coming up is on a high-speed train. However, the train ticket you were given is
   * in a language you don't understand. You should probably figure out what it says before you get
   * to the train station after the next flight.
   *
   * Unfortunately, you can't actually read the words on the ticket. You can, however, read the
   * numbers, and so you figure out the fields these tickets must have and the valid ranges for
   * values in those fields.
   *
   * You collect the rules for ticket fields, the numbers on your ticket, and the numbers on other
   * nearby tickets for the same train service (via the airport security cameras) together into a
   * single document you can reference (your puzzle input).
   *
   * The rules for ticket fields specify a list of fields that exist somewhere on the ticket and
   * the valid ranges of values for each field. For example, a rule like class: 1-3 or 5-7 means
   * that one of the fields in every ticket is named class and can be any value in the ranges 1-3
   * or 5-7 (inclusive, such that 3 and 5 are both valid in this field, but 4 is not).
   *
   * Each ticket is represented by a single line of comma-separated values. The values are the
   * numbers on the ticket in the order they appear; every ticket has the same format. For
   * example, consider this ticket:
   *
   * ```
   * .--------------------------------------------------------.
   * | ????: 101    ?????: 102   ??????????: 103     ???: 104 |
   * |                                                        |
   * | ??: 301  ??: 302             ???????: 303      ??????? |
   * | ??: 401  ??: 402           ???? ????: 403    ????????? |
   * '--------------------------------------------------------'
   * ```
   *
   * Here, ? represents text in a language you don't understand. This ticket might be represented
   * as 101,102,103,104,301,302,303,401,402,403; of course, the actual train tickets you're looking
   * at are much more complicated. In any case, you've extracted just the numbers in such a way
   * that the first number is always the same specific field, the second number is always a
   * different specific field, and so on - you just don't know what each position actually means!
   *
   * Start by determining which tickets are completely invalid; these are tickets that contain
   * values which aren't valid for any field. Ignore your ticket for now.
   *
   * For example, suppose you have the following notes:
   *
   * ```
   * class: 1-3 or 5-7
   * row: 6-11 or 33-44
   * seat: 13-40 or 45-50
   *
   * your ticket:
   * 7,1,14
   *
   * nearby tickets:
   * 7,3,47
   * 40,4,50
   * 55,2,20
   * 38,6,12
   * ```
   *
   * It doesn't matter which position corresponds to which field; you can identify invalid nearby
   * tickets by considering only whether tickets contain values that are not valid for any field.
   * In this example, the values on the first nearby ticket are all valid for at least one field.
   * This is not true of the other three nearby tickets: the values 4, 55, and 12 are are not valid
   * for any field. Adding together all of the invalid values produces your ticket scanning error
   * rate: 4 + 55 + 12 = 71.
   *
   * Consider the validity of the nearby tickets you scanned. What is your ticket scanning error
   * rate?
   */
  override suspend fun executePart1(input: Reader): Int {
    var state = 0
    val rules = mutableSetOf<Rule>()
    val lines = input.readLines()
    var invalidFields = 0
    lines.forEach { line ->
      when {
        line == "your ticket:" -> state = 1
        line == "nearby tickets:" -> state = 2
        line.isNotEmpty() -> {
          if (state == 0) {
            rules += Rule.parse(line)
          } else if (state == 2) {
            line.split(",").map(String::toInt)
              .filter { rules.none { rule -> rule.check(it) } }
              .forEach { invalidFields += it }
          }
        }
      }
    }
    return invalidFields
  }

  /**
   * Now that you've identified which tickets contain invalid values, discard those tickets
   * entirely. Use the remaining valid tickets to determine which field is which.
   *
   * Using the valid ranges for each field, determine what order the fields appear on the tickets.
   * The order is consistent between all tickets: if seat is the third field, it is the third
   * field on every ticket, including your ticket.
   *
   * For example, suppose you have the following notes:
   *
   * ```
   * class: 0-1 or 4-19
   * row: 0-5 or 8-19
   * seat: 0-13 or 16-19
   *
   * your ticket:
   * 11,12,13
   *
   * nearby tickets:
   * 3,9,18
   * 15,1,5
   * 5,14,9
   * ```
   *
   * Based on the nearby tickets in the above example, the first position must be row, the second
   * position must be class, and the third position must be seat; you can conclude that in your
   * ticket, class is 12, row is 11, and seat is 13.
   *
   * Once you work out which field is which, look for the six fields on your ticket that start
   * with the word departure. What do you get if you multiply those six values together?
   */
  override suspend fun executePart2(input: Reader): Long {
    var state = 0
    val rules = mutableSetOf<Rule>()
    val lines = input.readLines()
    var myTicket: List<Int> = emptyList()
    val validTickets = mutableListOf<List<Int>>()
    lines.forEach { line ->
      when {
        line == "your ticket:" -> state = 1
        line == "nearby tickets:" -> state = 2
        line.isNotEmpty() -> {
          when (state) {
            0 -> rules += Rule.parse(line)
            1 -> myTicket = line.split(",").map(String::toInt)
            2 -> {
              val theirs = line.split(",").map(String::toInt)
              if (!rules.check(theirs)) return@forEach
              validTickets.add(theirs)
            }
          }
        }
      }
    }

    // Start with all rules as possibilities for all fields.
    val rulePossibilities = ArrayList<MutableSet<Rule>>(myTicket.size)
    repeat(myTicket.size) { rulePossibilities.add(rules.toMutableSet()) }

    // Keep going until we've found unique rules for each field.
    val doneFields = mutableSetOf<Int>()
    while (doneFields.size != myTicket.size) {
      // Iterate over the fields.
      myTicket.indices.forEach { field ->
        if (field in doneFields) return@forEach

        // For each candidate rule, if all tickets current field work for that rule, we can keep
        // it. Otherwise, we need to drop it.
        val candidates = rulePossibilities[field]
        val toKeep = mutableSetOf<Rule>()
        candidates.forEach { rule ->
          val allOkay = validTickets.all { ticket -> rule.check(ticket[field]) }
          if (allOkay) toKeep.add(rule)
        }
        rulePossibilities[field] = toKeep

        // If we found our rule for this field, we can remove it as an option from all of the
        // other fields and mark it as done so we can skip it next time around the while loop.
        if (toKeep.size == 1) {
          val ruleAtField = toKeep.first()
          rulePossibilities.forEachIndexed { index, remaining ->
            if (index == field) return@forEachIndexed
            remaining.remove(ruleAtField)
          }
          doneFields.add(field)
        }
      }
    }

    return rulePossibilities.map(MutableSet<Rule>::first).withIndex()
      .filter { (_, rule) -> rule.name.startsWith("departure") }
      .product { (i, _) -> myTicket[i].toLong() }
  }

  data class Rule(val name: String, val rangeOne: IntRange, val rangeTwo: IntRange) {
    fun check(value: Int): Boolean = value in rangeOne || value in rangeTwo

    companion object {
      private val RULE_PATTERN = Regex("([A-Za-z ]+): (([0-9]+)-([0-9]+)) or (([0-9]+)-([0-9]+))")

      fun parse(line: String): Rule {
        val match = requireNotNull(RULE_PATTERN.matchEntire(line)) { line }.groupValues

        return Rule(
          name = match[1],
          rangeOne = match[3].toInt()..match[4].toInt(),
          rangeTwo = match[6].toInt()..match[7].toInt()
        )
      }
    }
  }
}

fun Collection<Day16TicketTranslation.Rule>.check(ticket: List<Int>): Boolean =
  ticket.none { field -> all { rule -> !rule.check(field) } }
