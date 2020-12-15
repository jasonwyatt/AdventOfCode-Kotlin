package us.jwf.aoc.combinatorics

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

fun <T> Set<T>.choose(num: Int, avoid: Set<T> = emptySet()): Flow<Set<T>> = flow {
  if (num == 0) {
    emit(emptySet())
    return@flow
  }
  if (size == num) {
    emit(this@choose)
    return@flow
  }
  val newAvoid = avoid.toMutableSet()
  forEach { chosen ->
    if (chosen in newAvoid) return@forEach
    newAvoid += chosen
    (this@choose - chosen).choose(num - 1, newAvoid).collect { emit(it + chosen) }
  }
}
