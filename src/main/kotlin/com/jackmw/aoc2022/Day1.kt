package com.jackmw.aoc2022

import com.jackmw.Utils

fun main(args: Array<String>) {
  part1("/2022/day1/part1-test.txt")
  part1("/2022/day1/part1-input.txt" )

  part2("/2022/day1/part1-test.txt")
  part2("/2022/day1/part1-input.txt" )
}

private fun part1(fileName: String) {
  val calorieLog = parseFile(fileName)
  val max = calorieLog.maxOf { it.sum() }
  println("Part1: $fileName -> $max")
}

private fun part2(fileName: String) {
  val calorieLog = parseFile(fileName)
  val top3Sum = calorieLog.map { it.sum() }
    .sorted()
    .takeLast(3)
    .sum()
  println("Part2: $fileName -> $top3Sum")
}

private fun parseFile(fileName: String): List<List<Int>> {
  val lines = Utils.readFileAsLines(fileName)
  var currentList: MutableList<Int> = mutableListOf()
  val output: MutableList<List<Int>> = mutableListOf()
  for (line in lines) {
    if (line.isBlank()) {
      output.add(currentList)
      currentList = mutableListOf()
    } else {
      currentList.add(line.toInt())
    }
  }
  if (currentList.isNotEmpty()) {
    output.add(currentList)
  }
  return output
}