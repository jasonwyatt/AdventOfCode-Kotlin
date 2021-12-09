package us.jwf.aoc2015

import java.io.Reader
import us.jwf.aoc.Day

/**
 * AoC 2015 - Day 11
 */
class Day11CorporatePolicy : Day<String, String> {
  override suspend fun executePart1(input: Reader): String {
    var password = "hxbxwxba"
    do {
      password = password.increment()
    } while (!password.isValid())
    return password
  }

  override suspend fun executePart2(input: Reader): String {
    var password = "hxbxxyzz"
    do {
      password = password.increment()
    } while (!password.isValid())
    return password
  }

  fun String.increment(): String {
    var i = length - 1
    val newString = StringBuilder()
    var done = false
    while (i >= 0) {
      if (done) {
        newString.insert(0, this[i])
        i--
        continue
      }

      val newChar = this[i] + 1
      if (this[i] + 1 > 'z') {
        newString.insert(0, 'a')
      } else {
        newString.insert(0, newChar)
        done = true
      }
      i--
    }
    return newString.toString()
  }

  fun String.isValid(): Boolean {
    return hasStraight() && !hasIllegal() && hasPairs()
  }

  fun String.hasStraight(): Boolean {
    var i = 2
    while (i < length) {
      if (this[i] - this[i - 1] == 1 && this[i - 1] - this[i - 2] == 1)  return true
      i++
    }
    return false
  }

  fun String.hasIllegal(): Boolean {
    var i = 0
    while (i < length) {
      if (this[i] == 'i' || this[i] == 'o' || this[i] == 'l') return true
      i++
    }
    return false
  }

  fun String.hasPairs(): Boolean {
    var firstPair: Char = '_'
    var i = 0
    while (i < length - 1) {
      if (this[i] == this[i+1]) {
        if (firstPair == '_') {
          firstPair = this[i]
        } else if (firstPair != this[i]) {
          return true
        }
        i += 2
      } else {
        i++
      }
    }
    return false
  }
}