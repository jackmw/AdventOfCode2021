package com.jackmw

abstract class LineWorker<T, R> {
  private fun getInputs(fileName: String): List<T> {
    return Utils.readFileAsLines(fileName).mapNotNull { parseLine(it) }
  }
  fun part1(fileName: String): R {
    return part1(getInputs(fileName))
  }

  fun part2(fileName: String): R {
    return part2(getInputs(fileName))
  }

  abstract fun parseLine(line: String): T?
  abstract fun part1(inputs: List<T>): R
  abstract fun part2(inputs: List<T>): R
}