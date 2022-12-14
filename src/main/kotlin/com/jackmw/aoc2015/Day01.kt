package com.jackmw.aoc2015

import com.jackmw.Utils

fun main(args: Array<String>) {
  val day = Day01()
  day.part1("/2015/day01/part1-test.txt")
  day.part1("/2015/day01/part1-input.txt" )

  day.part2("/2015/day01/part1-test.txt")
  day.part2("/2015/day01/part1-input.txt" )
}

class Day01 {
  internal fun part1(fileName: String) {
    val directions = parseFile(fileName)
    val charCount = directions.groupingBy { it }.eachCount()
    val floor = (charCount['('] ?: 0) - (charCount[')'] ?: 0)
    println("Part1 fileName=$fileName floor=$floor")
  }

  internal fun part2(fileName: String) {
    val directions = parseFile(fileName)
    var currentFloor = 0
    for ((index, direction) in directions.withIndex()) {
      val diff = when (direction) {
        '(' -> 1
        ')' -> -1
        else -> throw IllegalArgumentException("bad direction $direction")
      }
      currentFloor += diff
      if (currentFloor < 0) {
        println("Index=$index")
        break
      }
    }
  }

  private fun parseFile(fileName: String): String {
    val lines = Utils.readFileAsLines(fileName)
    return lines.first()
  }
}