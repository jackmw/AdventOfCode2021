package com.jackmw.aoc.day1

import com.jackmw.aoc.LineWorker

class Counter : LineWorker<Int, Int>() {
  private fun countWithWindow(inputs: List<Int>, windowSize: Int): Int {
    if (inputs.size < (windowSize + 1)) {
      return 0
    }
    val windows = mutableListOf<Int>()
    for (i in (windowSize - 1) until inputs.size) {
      var sum = 0
      for (j in 0 until windowSize) {
        sum += inputs[i - j]
      }
      windows.add(sum)
    }
    return windows.zipWithNext{ a, b -> b > a }.count { it }
  }

  override fun parseLine(line: String): Int? {
    return line.toIntOrNull()
  }

  override fun part1(inputs: List<Int>): Int {
    return inputs.zipWithNext { a, b -> b > a }.count { it }
  }

  override fun part2(inputs: List<Int>): Int {
    return countWithWindow(inputs, 3)
  }
}