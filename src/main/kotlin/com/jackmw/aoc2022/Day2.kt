package com.jackmw.aoc2022

import com.jackmw.aoc2021.Utils

fun main(args: Array<String>) {
  part1("/2022/day2/part1-test.txt")
  part1("/2022/day2/part1-input.txt" )

  part2("/2022/day2/part1-test.txt")
  part2("/2022/day2/part1-input.txt" )
}

private fun part1(fileName: String) {
  val guide = parseFile(fileName)
  val score = calculateGuideScore1(guide)
  println("Part1: $fileName -> $score")
}

private fun part2(fileName: String) {
  val guide = parseFile(fileName)
  val score = calculateGuideScore2(guide)
  println("Part2: $fileName -> $score")
}

enum class RPS(
  val inputSymbol: Char,
  val outputSymbol: Char,
  val scoreValue: Int,
) {
  ROCK('A', 'X', 1),
  PAPER('B','Y', 2),
  SCISSORS('C', 'Z', 3),
  ;

  fun getScore(input: RPS): Int {
    var diff = this.ordinal - input.ordinal
    if (diff < 0) diff += 3
    return when (diff) {
      0 -> 3
      1 -> 6
      else -> 0
    }
  }

  fun getOutputForResult(result: Char): RPS {
    val diff = when(result) {
      'Z' -> 1
      'Y' -> 0
      'X' -> 2
      else -> throw IllegalArgumentException("Unknown result: $result")
    }
    val index = (ordinal + diff) % 3
    return RPS.values()[index]
  }

  companion object {
    fun parseFrom(char: Char): RPS {
      return RPS.values().singleOrNull { it.inputSymbol == char }
        ?: RPS.values().singleOrNull { it.outputSymbol == char }
        ?: throw IllegalArgumentException("Unknown symbol: $char")
    }
  }
}

private fun calculateGuideScore1(guide: List<Pair<Char, Char>>): Int {
  return guide.sumOf { (q, a) ->
    val input = RPS.parseFrom(q)
    val output = RPS.parseFrom(a)

    output.getScore(input) + output.scoreValue
  }
}

private fun calculateGuideScore2(guide: List<Pair<Char, Char>>): Int {
  return guide.sumOf { (a, b) ->
    val input = RPS.parseFrom(a)
    val output = input.getOutputForResult(b)

    output.getScore(input) + output.scoreValue
  }
}

private fun parseFile(fileName: String): List<Pair<Char, Char>> {
  val lines = Utils.readFileAsLines(fileName)
  return lines.map { line ->
    val pieces = line.split(' ')
    pieces[0][0] to pieces[1][0]
  }
}