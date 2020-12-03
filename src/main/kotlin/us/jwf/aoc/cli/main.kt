package us.jwf.aoc.cli

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.optional
import kotlinx.cli.required
import kotlinx.coroutines.runBlocking
import us.jwf.aoc.AdventOfCode
import us.jwf.aoc.fetchInput
import us.jwf.aoc2020.AdventOfCode2020

val aocByYear = mapOf<Int, AdventOfCode>(
  2020 to AdventOfCode2020()
)

fun main(args: Array<String>): Unit = runBlocking {
  val parser = ArgParser("AdventOfCode")
  val year by parser.option(
    ArgType.Int,
    shortName = "y",
    fullName = "year",
    description = "Year of AoC"
  ).default(2020)
  val day by parser.option(
    ArgType.Int,
    shortName = "d",
    fullName = "day",
    description = "Day of the Advent (1 - 25)"
  ).required()
  val part by parser.option(
    ArgType.Int,
    shortName = "p",
    fullName = "part",
    description = "Part of the day (1 or 2)"
  ).required()
  val inputFile by parser.argument(
    ArgType.String,
    description = "Location of input file. If left unspecified, input will be read from stdin"
  ).optional()

  parser.parse(args)

  val aoc = requireNotNull(aocByYear[year]) { "Year: $year not implemented" }
  require(day in 1..25) { "-d / --day must be between 1 and 25 inclusive" }
  require(part == 1 || part == 2) { "-p / --part must be either 1 or 2" }
  inputFile.fetchInput(year, day).use { aoc.printResult(day, part, it) }
}