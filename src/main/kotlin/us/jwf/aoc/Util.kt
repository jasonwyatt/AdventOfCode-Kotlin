package us.jwf.aoc

import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.io.Reader
import java.util.Scanner
import java.util.regex.Pattern
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Fetches an [InputStreamReader] for the receiving [String?].
 *
 * If the receiver is null, a relative path to `input/[year]/day[day].txt`
 * will be constructed and used.
 */
fun String?.fetchInput(year: Int, day: Int): InputStreamReader {
  val dayNaught = if (day < 10) "0" else ""
  val file = if (this == null) {
    File("input/$year/day$dayNaught$day.txt")
  } else {
    File(this)
  }
  require(file.exists()) { "No such file found at ${file.absolutePath}" }
  return InputStreamReader(FileInputStream(file), Charsets.UTF_8)
}

/**
 * Parses the receiving [Reader] as a [Flow] of [Int]s delimited by the provided [delimiter].
 */
fun Reader.toIntFlow(delimiter: String = "\n"): Flow<Int> = flow {
  val scanner = Scanner(this@toIntFlow)
  scanner.useDelimiter(Pattern.compile(delimiter))

  while (scanner.hasNext()) {
    val token = scanner.next()
    val intVal = token.toInt(10) // scanner.nextInt(10)
    emit(intVal)
  }
}

/**
 * Parses the receiving [Reader] into matched-parts of the provided [pattern], delimited by the
 * provided [delimiter].
 */
fun Reader.toMatchFlow(
  pattern: Regex,
  delimiter: String = "\n"
): Flow<List<String>> = flow {
  val scanner = Scanner(this@toMatchFlow)
  scanner.useDelimiter(delimiter)

  while (scanner.hasNext()) {
    val raw = scanner.next()
    val matchResult = pattern.find(raw) ?: continue
    emit(matchResult.groupValues)
  }
}

fun Reader.toLineFlow(delimiter: String = "\n"): Flow<String> = flow {
  val scanner = Scanner(this@toLineFlow)
  scanner.useDelimiter(delimiter)

  while (scanner.hasNext()) emit(scanner.next())
}

/**
 * Returns whether or not the receiving String is a valid base-10 integer within the provided
 * [range].
 */
fun String.isIntInRange(range: IntRange): Boolean =
  toIntOrNull(10)?.let { it in range } == true