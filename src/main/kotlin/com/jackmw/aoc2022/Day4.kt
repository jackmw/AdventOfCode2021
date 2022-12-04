package com.jackmw.aoc2022

import com.jackmw.aoc2021.Utils

fun main(args: Array<String>) {
  part1("/2022/day4/part1-test.txt")
  part1("/2022/day4/part1-input.txt")

  part2("/2022/day4/part1-test.txt")
  part2("/2022/day4/part1-input.txt")
}

private fun part1(fileName: String) {
  val rangePairs = parseFile(fileName)
  val overlappedPairsCount = rangePairs.count { (range1, range2) ->
    range1.containsEntirely(range2) || range2.containsEntirely(range1)
  }
  println("Part1: $fileName -> $overlappedPairsCount")
}

private fun part2(fileName: String) {
  val rangePairs = parseFile(fileName)
  val overlappedPairsCount = rangePairs.count { (range1, range2) ->
    range1.overlaps(range2)
  }
  println("Part2: $fileName -> $overlappedPairsCount")
}

private fun parseFile(fileName: String): List<Pair<IntRange, IntRange>> {
  val lines = Utils.readFileAsLines(fileName)
  return lines.map { line ->
    val range1 = line.substringBefore(',')
    val range2 = line.substringAfter(',')
    range1.toIntRange() to range2.toIntRange()
  }
}

private fun String.toIntRange(): IntRange {
  val start = substringBefore('-').toInt()
  val end = substringAfter('-').toInt()
  return IntRange(start = start, endInclusive = end)
}

private fun IntRange.containsEntirely(another: IntRange): Boolean {
  return contains(another.first) && contains(another.last)
}

private fun IntRange.overlaps(another: IntRange): Boolean {
  return !(first > another.last || last < another.first)
}