package us.jwf.aoc.combinatorics

import java.util.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

@ExperimentalCoroutinesApi
fun <T> Set<T>.permute(length: Int = this.size): Flow<List<T>> = channelFlow {
  nextStep(listOf(), this@permute, length)
}

@ExperimentalCoroutinesApi
suspend fun <T> ProducerScope<List<T>>.nextStep(soFar: List<T>, available: Set<T>, target: Int) {
  if (target == 0) {
    send(soFar)
    return
  }
  available.map {
    nextStep(soFar + it, available - it, target - 1)
  }
}
