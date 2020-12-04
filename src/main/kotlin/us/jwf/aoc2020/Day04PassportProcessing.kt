package us.jwf.aoc2020

import java.io.Reader
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import us.jwf.aoc.Day
import us.jwf.aoc.isIntInRange
import us.jwf.aoc.toMatchFlow

/**
 * Day 4 of AoC 2020.
 */
class Day04PassportProcessing : Day<Int, Int> {
  /**
   * You arrive at the airport only to realize that you grabbed your North Pole Credentials instead
   * of your passport. While these documents are extremely similar, North Pole Credentials aren't
   * issued by a country and therefore aren't actually valid documentation for travel in most of the
   * world.
   *
   * It seems like you're not the only one having problems, though; a very long line has formed for
   * the automatic passport scanners, and the delay could upset your travel itinerary.
   *
   * Due to some questionable network security, you realize you might be able to solve both of these
   * problems at the same time.
   *
   * The automatic passport scanners are slow because they're having trouble detecting which
   * passports have all required fields. The expected fields are as follows:
   *
   * ```
   * byr (Birth Year)
   * iyr (Issue Year)
   * eyr (Expiration Year)
   * hgt (Height)
   * hcl (Hair Color)
   * ecl (Eye Color)
   * pid (Passport ID)
   * cid (Country ID)
   * ```
   *
   * Passport data is validated in batch files (your puzzle input). Each passport is represented as
   * a sequence of key:value pairs separated by spaces or newlines. Passports are separated by blank
   * lines.
   *
   * Here is an example batch file containing four passports:
   *
   * ```
   * ecl:gry pid:860033327 eyr:2020 hcl:#fffffd
   * byr:1937 iyr:2017 cid:147 hgt:183cm
   *
   * iyr:2013 ecl:amb cid:350 eyr:2023 pid:028048884
   * hcl:#cfa07d byr:1929
   *
   * hcl:#ae17e1 iyr:2013
   * eyr:2024
   * ecl:brn pid:760753108 byr:1931
   * hgt:179cm
   *
   * hcl:#cfa07d eyr:2025 pid:166559648
   * iyr:2011 ecl:brn hgt:59in
   * ```
   *
   * The first passport is valid - all eight fields are present. The second passport is invalid -
   * it is missing hgt (the Height field).
   *
   * The third passport is interesting; the only missing field is cid, so it looks like data from
   * North Pole Credentials, not a passport at all! Surely, nobody would mind if you made the system
   * temporarily ignore missing cid fields. Treat this "passport" as valid.
   *
   * The fourth passport is missing two fields, cid and byr. Missing cid is fine, but missing any
   * other field is not, so this passport is invalid.
   *
   * According to the above rules, your improved system would report 2 valid passports.
   *
   * Count the number of valid passports - those that have all required fields. Treat cid as
   * optional. In your batch file, how many passports are valid?
   */
  override suspend fun executePart1(input: Reader): Int {
    return input.toMatchFlow(Regex("(.+\n?)*", RegexOption.MULTILINE), "\n\n")
      .map {
        it[0].toFields()
      }
      .filter { it.isValid() }
      .count()
  }

  /**
   * The line is moving more quickly now, but you overhear airport security talking about how
   * passports with invalid data are getting through. Better add some data validation, quick!
   *
   * You can continue to ignore the cid field, but each other field has strict rules about what
   * values are valid for automatic validation:
   *
   * ```
   * byr (Birth Year) - four digits; at least 1920 and at most 2002.
   * iyr (Issue Year) - four digits; at least 2010 and at most 2020.
   * eyr (Expiration Year) - four digits; at least 2020 and at most 2030.
   * hgt (Height) - a number followed by either cm or in:
   *    If cm, the number must be at least 150 and at most 193.
   *    If in, the number must be at least 59 and at most 76.
   * hcl (Hair Color) - a # followed by exactly six characters 0-9 or a-f.
   * ecl (Eye Color) - exactly one of: amb blu brn gry grn hzl oth.
   * pid (Passport ID) - a nine-digit number, including leading zeroes.
   * cid (Country ID) - ignored, missing or not.
   * ```
   *
   * Your job is to count the passports where all required fields are both present and valid
   * according to the above rules.
   *
   * Count the number of valid passports - those that have all required fields and valid values.
   * Continue to treat cid as optional. In your batch file, how many passports are valid?
   */
  override suspend fun executePart2(input: Reader): Int {
    return input.toMatchFlow(Regex("(.+\n?)*", RegexOption.MULTILINE), "\n\n")
      .map {
        it[0].toFields()
      }
      .filter { it.isValid(checkValues = true) }
      .count()
  }

  private fun String.toFields(): List<Field> {
    return split(Regex("\\s+"))
      .map {
        val parts = it.split(":")
        Field(parts[0], parts[1])
      }
  }
}

/** Represents a Passport Field for the elves. */
private data class Field(val fieldName: String, val fieldValue: String) {
  /** Whether or not the field is valid. */
  fun isValid(): Boolean = when(fieldName) {
    "byr" -> fieldValue.isIntInRange(1920..2002)
    "iyr" -> fieldValue.isIntInRange(2010..2020)
    "eyr" -> fieldValue.isIntInRange(2020..2030)
    "hgt" -> {
      when (fieldValue.substring(fieldValue.length - 2)) {
        "cm" -> fieldValue.substring(0, fieldValue.length - 2).isIntInRange(150..193)
        "in" -> fieldValue.substring(0, fieldValue.length - 2).isIntInRange(59..76)
        else -> false
      }
    }
    "hcl" -> COLOR_PATTERN.matches(fieldValue)
    "ecl" -> fieldValue in EYE_COLORS
    "pid" -> fieldValue.length == 9 && fieldValue.toIntOrNull(10) != null
    else -> true
  }

  companion object {
    private val COLOR_PATTERN = Regex("#[a-fA-F0-9]{6}")
    private val EYE_COLORS = setOf("amb","blu","brn","gry","grn","hzl","oth")
  }
}

/**
 * Validates whether or not the receiving list of [Field] is an acceptable passport.
 *
 * When [checkValues] is false, we only look for the fields. When [checkValues] is true, we
 * validate the field values themselves.
 */
private fun List<Field>.isValid(checkValues: Boolean = false): Boolean {
  val fieldNames = mutableSetOf("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid")
  forEach {
    if (!checkValues || it.isValid()) {
      fieldNames.remove(it.fieldName)
    }
  }
  return fieldNames.isEmpty()
}
