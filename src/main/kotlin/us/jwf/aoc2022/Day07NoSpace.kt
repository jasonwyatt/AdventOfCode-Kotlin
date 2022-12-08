package us.jwf.aoc2022

import java.io.Reader
import us.jwf.aoc.Day
import us.jwf.aoc2022.Day07NoSpace.FileSystemElement.Directory

class Day07NoSpace : Day<Long, Long> {
  override suspend fun executePart1(input: Reader): Long {
    val fileSystem = parse(input.readLines())

    fun totalSizeOfDirsLessThanLimit(
      directory: Directory,
      quota: Long
    ): Pair<Long, Long> {
      // base case
      if (directory.dirs.isEmpty()) {
        val size = directory.shallowSize
        return if (size <= quota) size to size else 0L to size
      }

      val (sizeLessThan, totalSize) = directory.dirs.values
        .fold(0L to 0L) { acc, dir ->
          val (sizeLessThan, totalSize) = totalSizeOfDirsLessThanLimit(dir, quota)
          (acc.first + sizeLessThan) to (acc.second + totalSize)
        }
      val totalSizeWithFiles = directory.shallowSize + totalSize
      return if (totalSizeWithFiles <= quota) {
        (sizeLessThan + totalSizeWithFiles) to totalSizeWithFiles
      } else {
        sizeLessThan to totalSizeWithFiles
      }
    }
    return totalSizeOfDirsLessThanLimit(fileSystem as Directory, 100000L).first
  }

  override suspend fun executePart2(input: Reader): Long {
    val fileSystem = parse(input.readLines()) as Directory

    val totalSize = fileSystem.size
    val toDelete = 30000000 - (70000000 - totalSize)
    println("toDelete = $toDelete")

    var minOverThreshold = Long.MAX_VALUE
    fun traverse(dir: Directory) {
      if (dir.size >= toDelete) {
        minOverThreshold = minOf(minOverThreshold, dir.size)
      }
      dir.dirs.values.forEach(::traverse)
    }
    traverse(fileSystem)

    return minOverThreshold
  }

  sealed interface FileSystemElement {
    val parent: Directory?
    val size: Long
    fun print(indent: String = "")

    data class Directory(
      override val parent: Directory? = null,
      val name: String,
      val dirs: MutableMap<String, Directory> = mutableMapOf(),
      val files: MutableMap<String, File> = mutableMapOf(),
    ) : FileSystemElement {
      override val size: Long by lazy {
        dirs.values.sumOf { it.size } + shallowSize
      }
      val shallowSize: Long by lazy {
        files.values.sumOf { it.size }
      }

      fun addDir(name: String) {
        dirs[name] = Directory(this, name)
      }

      fun addFile(name: String, size: Long) {
        files[name] = File(this, name, size)
      }

      override fun print(indent: String) {
        println("$indent/$name (total size: $size)")
        val newIndent = "$indent  "
        dirs.values.forEach { it.print(newIndent) }
        files.values.forEach { it.print(newIndent) }
      }
    }

    data class File(
      override val parent: Directory,
      val name: String,
      override val size: Long
    ) : FileSystemElement {
      override fun print(indent: String) {
        println("$indent$name ($size)")
      }
    }
  }

  companion object {
    fun parse(input: List<String>): FileSystemElement {
      val root = Directory(name = "")
      var current = root
      var readingLsOutput = false
      input.forEach { line ->
        when {
          line.startsWith("$ cd") -> {
            readingLsOutput = false
            val targetName = line.substring(5)
            current =
              if (targetName == "/") root
              else if (targetName == "..") current.parent ?: root
              else current.dirs[targetName]!!
          }
          line == "$ ls" -> {
            readingLsOutput = true
          }
          readingLsOutput && line.startsWith("dir") -> {
            current.addDir(line.substring(4))
          }
          readingLsOutput -> {
            val splitLine = line.split(" ")
            val size = splitLine[0].toLong()
            val name = splitLine[1]
            current.addFile(name, size)
          }
        }
      }
      return root
    }
  }
}