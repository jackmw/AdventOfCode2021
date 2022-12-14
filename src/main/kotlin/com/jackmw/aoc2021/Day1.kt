package com.jackmw.aoc2021

import com.jackmw.LineWorker

fun main(args: Array<String>) {
  part1("/day1/part1-test.txt")
  part1("/day1/part1-input.txt" )

  part2("/day1/part2-test.txt")
  part2("/day1/part2-input.txt")
}

private fun part1(fileName: String) {
  println("$fileName -> ${ Counter().part1(fileName)}")
}

private fun part2(fileName: String) {
  println("$fileName -> ${ Counter().part2(fileName)}")
}

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