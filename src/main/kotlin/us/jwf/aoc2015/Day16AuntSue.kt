package us.jwf.aoc2015

import java.io.Reader
import us.jwf.aoc.Day

/**
 * AoC 2015 - Day 16
 */
class Day16AuntSue : Day<Int, Int> {
  override suspend fun executePart1(input: Reader): Int {
    return input.readLines().map { SueStats.parse(it) }.find { it.isTarget() }!!.id
  }

  override suspend fun executePart2(input: Reader): Int {
    return input.readLines().map { SueStats.parse(it) }.find { it.isTarget2() }!!.id
  }

  data class SueStats(
    val id: Int,
    val children: Int?,
    val cats: Int?,
    val samoyeds: Int?,
    val pomeranians: Int?,
    val akitas: Int?,
    val vizslas: Int?,
    val goldfish: Int?,
    val trees: Int?,
    val cars: Int?,
    val perfumes: Int?
  ) {
    fun isTarget(): Boolean {
      return (children?.let { it == TARGET.children } ?: true) &&
        (cats?.let { it == TARGET.cats } ?: true) &&
        (samoyeds?.let { it == TARGET.samoyeds } ?: true) &&
        (pomeranians?.let { it == TARGET.pomeranians } ?: true) &&
        (akitas?.let { it == TARGET.akitas } ?: true) &&
        (vizslas?.let { it == TARGET.vizslas } ?: true) &&
        (goldfish?.let { it == TARGET.goldfish } ?: true) &&
        (trees?.let { it == TARGET.trees } ?: true) &&
        (cars?.let { it == TARGET.cars } ?: true) &&
        (perfumes?.let { it == TARGET.perfumes } ?: true)
    }

    fun isTarget2(): Boolean {
      return (children?.let { it == TARGET.children } ?: true) &&
        (cats?.let { it > TARGET.cats!! } ?: true) &&
        (samoyeds?.let { it == TARGET.samoyeds } ?: true) &&
        (pomeranians?.let { it < TARGET.pomeranians!! } ?: true) &&
        (akitas?.let { it == TARGET.akitas } ?: true) &&
        (vizslas?.let { it == TARGET.vizslas } ?: true) &&
        (goldfish?.let { it < TARGET.goldfish!! } ?: true) &&
        (trees?.let { it > TARGET.trees!! } ?: true) &&
        (cars?.let { it == TARGET.cars } ?: true) &&
        (perfumes?.let { it == TARGET.perfumes } ?: true)
    }

    companion object {
      val TARGET = SueStats(
        id = -1,
        children = 3,
        cats = 7,
        samoyeds = 2,
        pomeranians = 3,
        akitas = 0,
        vizslas = 0,
        goldfish = 5,
        trees = 3,
        cars = 2,
        perfumes = 1
      )
      val NAME_PATTERN = "Sue (\\d+)".toRegex()

      fun parse(line: String): SueStats {
        val (namePart, statsPart) = line.split(": ", limit = 2)

        val id = NAME_PATTERN.matchEntire(namePart)!!.groupValues[1].toInt()
        val rawStats =
          statsPart.split(", ")
            .map { it.split(": ") }
            .associate { it.first() to it.last().toInt() }

        return SueStats(
          id,
          rawStats["children"],
          rawStats["cats"],
          rawStats["samoyeds"],
          rawStats["pomeranians"],
          rawStats["akitas"],
          rawStats["vizslas"],
          rawStats["goldfish"],
          rawStats["trees"],
          rawStats["cars"],
          rawStats["perfumes"]
        )
      }
    }
  }
}