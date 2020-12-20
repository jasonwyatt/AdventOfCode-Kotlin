package us.jwf.aoc2020

import java.io.Reader
import kotlinx.coroutines.flow.collect
import us.jwf.aoc.Day
import us.jwf.aoc.toLineFlow

/**
 * Day 19 of AoC 2020
 */
class Day19MonsterMessages : Day<Int, Int> {
  /**
   * You land in an airport surrounded by dense forest. As you walk to your high-speed train, the
   * Elves at the Mythical Information Bureau contact you again. They think their satellite has
   * collected an image of a sea monster! Unfortunately, the connection to the satellite is having
   * problems, and many of the messages sent back from the satellite have been corrupted.
   *
   * They sent you a list of the rules valid messages should obey and a list of received messages
   * they've collected so far (your puzzle input).
   *
   * The rules for valid messages (the top part of your puzzle input) are numbered and build upon
   * each other. For example:
   *
   * ```
   * 0: 1 2
   * 1: "a"
   * 2: 1 3 | 3 1
   * 3: "b"
   * ```
   *
   * Some rules, like 3: "b", simply match a single character (in this case, b).
   *
   * The remaining rules list the sub-rules that must be followed; for example, the rule 0: 1 2
   * means that to match rule 0, the text being checked must match rule 1, and the text after the
   * part that matched rule 1 must then match rule 2.
   *
   * Some of the rules have multiple lists of sub-rules separated by a pipe (|). This means that at
   * least one list of sub-rules must match. (The ones that match might be different each time the
   * rule is encountered.) For example, the rule 2: 1 3 | 3 1 means that to match rule 2, the text
   * being checked must match rule 1 followed by rule 3 or it must match rule 3 followed by rule 1.
   *
   * Fortunately, there are no loops in the rules, so the list of possible matches will be finite.
   * Since rule 1 matches a and rule 3 matches b, rule 2 matches either ab or ba. Therefore, rule
   * 0 matches aab or aba.
   *
   * Here's a more interesting example:
   *
   * ```
   * 0: 4 1 5
   * 1: 2 3 | 3 2
   * 2: 4 4 | 5 5
   * 3: 4 5 | 5 4
   * 4: "a"
   * 5: "b"
   * ```
   *
   * Here, because rule 4 matches a and rule 5 matches b, rule 2 matches two letters that are the
   * same (aa or bb), and rule 3 matches two letters that are different (ab or ba).
   *
   * Since rule 1 matches rules 2 and 3 once each in either order, it must match two pairs of
   * letters, one pair with matching letters and one pair with different letters. This leaves
   * eight possibilities: aaab, aaba, bbab, bbba, abaa, abbb, baaa, or babb.
   *
   * Rule 0, therefore, matches a (rule 4), then any of the eight options from rule 1, then b (rule
   * 5): aaaabb, aaabab, abbabb, abbbab, aabaab, aabbbb, abaaab, or ababbb.
   *
   * The received messages (the bottom part of your puzzle input) need to be checked against the
   * rules so you can determine which are valid and which are corrupted. Including the rules and
   * the messages together, this might look like:
   *
   * ```
   * 0: 4 1 5
   * 1: 2 3 | 3 2
   * 2: 4 4 | 5 5
   * 3: 4 5 | 5 4
   * 4: "a"
   * 5: "b"
   *
   * ababbb
   * bababa
   * abbbab
   * aaabbb
   * aaaabbb
   * ```
   *
   * Your goal is to determine the number of messages that completely match rule 0. In the above
   * example, ababbb and abbbab match, but bababa, aaabbb, and aaaabbb do not, producing the answer
   * 2. The whole message must match all of rule 0; there can't be extra unmatched characters in
   * the message. (For example, aaaabbb might appear to match rule 0 above, but it has an extra
   * unmatched b on the end.)
   *
   * How many messages completely match rule 0?
   */
  override suspend fun executePart1(input: Reader): Int {
    val rules = mutableMapOf<Int, Rule>()
    val tests = mutableListOf<String>()
    input.toLineFlow().collect {
      val rule = Rule.parse(it)
      if (rule != null) {
        rules[rule.id] = rule
      } else if (it.isNotBlank()) {
        tests.add(it)
      }
    }
    return tests.count {
      rules[0]?.matches(it, 0, rules)?.any { matchLen -> matchLen == it.length } == true
    }
  }

  /**
   * As you look over the list of messages, you realize your matching rules aren't quite right. To
   * fix them, completely replace rules 8: 42 and 11: 42 31 with the following:
   *
   * ```
   * 8: 42 | 42 8
   * 11: 42 31 | 42 11 31
   * ```
   *
   * This small change has a big impact: now, the rules do contain loops, and the list of messages
   * they could hypothetically match is infinite. You'll need to determine how these changes affect
   * which messages are valid.
   *
   * Fortunately, many of the rules are unaffected by this change; it might help to start by looking
   * at which rules always match the same set of values and how those rules (especially rules 42
   * and 31) are used by the new versions of rules 8 and 11.
   *
   * (Remember, you only need to handle the rules you have; building a solution that could handle
   * any hypothetical combination of rules would be significantly more difficult.)
   */
  override suspend fun executePart2(input: Reader): Int {
    val rules = mutableMapOf<Int, Rule>()
    val tests = mutableListOf<String>()
    input.toLineFlow().collect {
      val rule = Rule.parse(it)
      if (rule != null) {
        rules[rule.id] = rule
      } else if (it.isNotBlank()) {
        tests.add(it)
      }
    }
    rules[8] = Rule.parse("8: 42 | 42 8")!!
    rules[11] = Rule.parse("11: 42 31 | 42 11 31")!!
    return tests.count {
      rules[0]?.matches(it, 0, rules)?.any { matchLen -> matchLen == it.length } == true
    }
  }

  sealed class Rule(open val id: Int) {
    abstract fun matches(str: String, atIndex: Int, ruleSet: Map<Int, Rule>): List<Int>

    data class Sequence(override val id: Int, val sequence: List<Int>) : Rule(id) {
      override fun matches(str: String, atIndex: Int, ruleSet: Map<Int, Rule>): List<Int> {
        var indices = mutableListOf(atIndex)
        sequence.forEach {
          val newIndices = mutableListOf<Int>()
          indices.forEach byIndex@{ index ->
            val nextIndices = ruleSet[it]?.matches(str, index, ruleSet) ?: return@byIndex
            newIndices.addAll(nextIndices)
          }
          indices = newIndices
        }
        return indices
      }
    }

    data class Or(override val id: Int, val rules: List<Rule>) : Rule(id) {
      override fun matches(str: String, atIndex: Int, ruleSet: Map<Int, Rule>): List<Int> {
        return rules.flatMap { it.matches(str, atIndex, ruleSet) }
      }
    }

    data class Literal(override val id: Int, val literal: Char) : Rule(id) {
      override fun matches(str: String, atIndex: Int, ruleSet: Map<Int, Rule>): List<Int> {
        if (atIndex >= str.length) return emptyList()
        if (str[atIndex] == literal) return listOf(atIndex + 1)
        return emptyList()
      }
    }

    companion object {
      private val OR_SEQUENCE_PATTERN = Regex("([0-9]+): ([ |0-9]+)")
      private val LITERAL_PATTERN = Regex("([0-9]+): \"([ab])\"")

      fun parse(line: String): Rule? {
        OR_SEQUENCE_PATTERN.find(line)?.let {
          val id = it.groupValues[1].toInt(10)
          val rhs = it.groupValues[2]
          val orParts = rhs.split("|").map { part ->
            Sequence(-1, part.split(" ").mapNotNull { ruleId -> ruleId.toIntOrNull(10) })
          }
          return Or(id, orParts)
        }

        LITERAL_PATTERN.find(line)?.let {
          val id = it.groupValues[1].toInt(10)
          val literal = it.groupValues[2][0]
          return Literal(id, literal)
        }

        return null
      }
    }
  }

  companion object {
  }
}