package us.jwf.aoc2015

import java.io.Reader
import us.jwf.aoc.Day

/**
 * AoC 2015 - Day 19
 */
class Day19Medicine : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    val rules = mutableMapOf<Atom, List<Rule>>()
    var doneWithRules = false
    var molecule = emptyList<Atom>()

    input.readLines().forEach { line ->
      if (line.isBlank() || line.isEmpty()) {
        doneWithRules = true
      } else if (doneWithRules) {
        molecule = Atom.parseMolecule(line)
      } else {
        Rule.parse(line).also {
          rules[it.input] = (rules[it.input] ?: emptyList()) + it
        }
      }
    }

    val created = mutableSetOf<List<Atom>>()
    molecule.forEachIndexed { i, atom ->
      val left = if (i > 0) molecule.subList(0, i) else emptyList()
      val right = if (i < molecule.size - 1) molecule.subList(i + 1, molecule.size) else emptyList()

      val rulesToApply = rules[atom] ?: emptyList()
      rulesToApply.forEach { rule ->
        created += left + rule.output + right
      }
    }
    return created.size
  }

  override suspend fun executePart2(input: Reader): Int {
    // A* with LevenshteinDistance.
    // Or: start from long string and try to find e by going backwards....
    TODO("Not yet implemented")
  }

  data class Atom(val symbol: String) {
    companion object {
      private val elementPattern = "[A-Z][a-z]?".toRegex()
      fun parseMolecule(input: String): List<Atom> {
        return elementPattern.findAll(input).map { Atom(it.value) }.toList()
      }
    }
  }

  data class Rule(val input: Atom, val output: List<Atom>) {
    companion object {
      fun parse(input: String): Rule {
        val (lhs, rhs) = input.split(" => ")
        return Rule(Atom(lhs), Atom.parseMolecule(rhs))
      }
    }
  }
}