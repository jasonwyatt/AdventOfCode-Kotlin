package us.jwf.aoc2015

import java.io.Reader
import java.util.LinkedList
import java.util.PriorityQueue
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
    val atomsFromRules = mutableMapOf<Rule, Atom>()
    var doneWithRules = false
    var molecule = emptyList<Atom>()

    input.readLines().forEach { line ->
      if (line.isBlank() || line.isEmpty()) {
        doneWithRules = true
      } else if (doneWithRules) {
        molecule = Atom.parseMolecule(line)
      } else {
        Rule.parse(line).also {
          atomsFromRules[it] = it.input
        }
      }
    }

    val orderedRules = atomsFromRules.keys.sortedBy { -it.output.size }

    val visited = mutableSetOf(molecule)
    val queue =
      PriorityQueue<Pair<List<Atom>, Int>> { a, b -> a.first.size compareTo b.first.size }
        .apply { add(molecule to 0) }
    while (queue.isNotEmpty()) {
      val (currentMolecule, currentSteps) = queue.poll()
      if (currentMolecule == listOf(Atom("e"))) return currentSteps

      orderedRules.forEach { rule ->
        currentMolecule.windowed(rule.output.size)
          .withIndex()
          .forEach inner@{ (i, window) ->
            if (window != rule.output) return@inner

            val left = if (i > 0) currentMolecule.subList(0, i) else emptyList()
            val right = if (i < currentMolecule.size - rule.output.size) {
              currentMolecule.subList(i + rule.output.size, currentMolecule.size)
            } else emptyList()

            val candidate = left + atomsFromRules[rule]!! + right
            if (candidate !in visited && candidate.size <= currentMolecule.size) {
              visited.add(candidate)
              queue.add(candidate to currentSteps + 1)
            }
            if (candidate == listOf(Atom("e"))) return currentSteps + 1
          }
      }
    }
    return 0
  }

  data class Atom(val symbol: String) {
    override fun toString(): String = symbol

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