package us.jwf.aoc2019.intcode

interface Memory {
  fun loadFrom(
    source: LongArray,
    sourceStart: Int = 0,
    sourceEnd: Int = source.size,
    localOffset: Long = 0
  )

  operator fun get(position: Long): Long
  operator fun set(position: Long, value: Long)
  fun clear()
}

class HashMapMemory(initialSize: Int = 4096) : Memory {
  private val memoryMap = mutableMapOf<Long, Long>()

  override fun loadFrom(source: LongArray, sourceStart: Int, sourceEnd: Int, localOffset: Long) {
    (sourceStart until sourceEnd).forEach { i ->
      memoryMap[i + localOffset] = source[i]
    }
  }

  override fun get(position: Long): Long = memoryMap[position] ?: 0

  override fun set(position: Long, value: Long) {
    memoryMap[position] = value
  }

  override fun clear() = memoryMap.clear()
}
