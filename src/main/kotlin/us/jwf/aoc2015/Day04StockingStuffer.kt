package us.jwf.aoc2015

import java.io.Reader
import java.math.BigInteger
import java.security.MessageDigest
import us.jwf.aoc.Day

class Day04StockingStuffer : Day<Long, Long> {
  val md = MessageDigest.getInstance("MD5")

  fun md5(input:String, test: Long): String {
    md.reset()
    return BigInteger(1, md.digest("$input$test".toByteArray()))
      .toString(16)
      .padStart(32, '0')
  }

  override suspend fun executePart1(input: Reader): Long {
    var i = 0L
    while (true) {
      val hash = md5("bgvyzdsv", i)
      if (hash.startsWith("00000")) return i
      i++
    }
  }

  override suspend fun executePart2(input: Reader): Long {
    var i = 0L
    while (true) {
      val hash = md5("bgvyzdsv", i)
      if (hash.startsWith("000000")) return i
      i++
    }
  }

}