package com.jackmw.aoc2021

import com.jackmw.Utils.Companion.readFileAsLines
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) {
  part1("/day6/part1-test.txt")
  part2("/day6/part1-test.txt")
  val start = System.nanoTime()
  part2("/day6/part1-input.txt")
  val duration = System.nanoTime() - start
  println("duration ${duration}ns ${TimeUnit.NANOSECONDS.toMillis(duration)}ms")
}

private fun part1(fileName: String) {
  val initialCondition = readFileAsLines(fileName).first()
    .split(",")
    .mapNotNull { it.toIntOrNull() }
  val model = FishPopulationModel()
  val state = model.modelPopulation(initialCondition, days = 80)
  println("$fileName -> after 80 days: ${state.size}")
}

private fun part2(fileName: String) {
  val initialCondition = readFileAsLines(fileName).first()
    .split(",")
    .mapNotNull { it.toIntOrNull() }
  val model = FishPopulationModel()
  val population = model.modelPopulationCount(initialCondition, 256)
  println("$fileName -> after 256 days: $population")
}

class FishPopulationModel {
  companion object {
    const val WARMUP_TIME = 2
    const val BAKING_TIME = 7
  }

  private val cache: MutableMap<Pair<Int, Int>, Long> = mutableMapOf()

  fun modelPopulation(
    initialConditions: List<Int>,
    days: Int,
    coolOff: Int = WARMUP_TIME,
    interval: Int = BAKING_TIME,
  ): List<Int> {
    var state: List<Int> = initialConditions
    for (day in 1..days) {
      state = modelGrowthOneDay(initialCondition = state, coolOff = coolOff, interval = interval)
    }
    return state
  }

  fun modelPopulationCount(
    initialConditions: List<Int>,
    days: Int,
    coolOff: Int = WARMUP_TIME,
    interval: Int = BAKING_TIME,
  ): Long {
    return initialConditions.sumOf { getPopulation(initial = it, days = days, interval = interval, coolOff = coolOff) }
  }

  // fun(0, 18) = fun(6, 17) + fun(8, 17)
  // fun(6, 17) = fun(5, 16) = fun(4, 15) = fun(3, 13) = fun(2, 12) = fun(1, 11) = fun(0, 10) = fun(7, 9) + fun(8, 9)
  // fun(7, 9) = fun(6, 8) = fun(5, 7) = fun(4, 6) = fun(3, 5) = fun(2, 4) = fun(1, 3) = fun(0, 2)
  // fun(0, 1) = fun(6, 0) + fun(8, 0)
  private fun getPopulation(initial: Int, days: Int, coolOff: Int = WARMUP_TIME, interval: Int = BAKING_TIME): Long {
    if (days == 0) {
      // println("f($initial, 0) = 1")
      return 1
    }
    val cacheKey = Pair(initial, days)
    val cached = cache[cacheKey]
    if (cached != null) return cached

    val population = if (initial == 0) {
      // println("f($initial, $days) = f(${interval - 1}, ${days - 1}) + f(${interval + coolOff - 1}, ${days - 1})")
      getPopulation(interval - 1, days - 1, coolOff, interval) +
          getPopulation(interval + coolOff - 1,days - 1, coolOff,interval)
    } else {
      // println("f($initial, $days) = f(${interval - 1}, ${days - 1})")
      getPopulation(initial - 1, days - 1, coolOff, interval)
    }

    cache[cacheKey] = population

    // println("f($initial, $days) = $population")
    return population
  }

  private fun modelGrowthOneDay(initialCondition: List<Int>, coolOff: Int = 2, interval: Int = 7): List<Int> {
    val nextState = mutableListOf<Int>()
    var newFishCount = 0
    for (fish in initialCondition) {
      if (fish > 0) {
        nextState.add(fish - 1)
      } else {
        newFishCount++
        nextState.add(interval - 1)
      }
    }
    repeat(newFishCount) { nextState.add(interval + coolOff - 1) }
    return nextState
  }
}