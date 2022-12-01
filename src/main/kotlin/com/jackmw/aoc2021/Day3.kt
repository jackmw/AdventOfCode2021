package com.jackmw.aoc2021

fun main(args: Array<String>) {
  part1("/day3/part1-test.txt")
  part1("/day3/part1-input.txt")

  part2("/day3/part1-test.txt")
  part2("/day3/part1-input.txt")
}

private fun part1(fileName: String) {
  val (gamma, epsilon) = Day3().part1(fileName)
  println("$fileName -> ($gamma, $epsilon) = ${gamma * epsilon}")
}

private fun part2(fileName: String) {
  val (horizontal, vertical) = Day3().part2(fileName)
  println("$fileName -> ($horizontal, $vertical) = ${horizontal * vertical}")
}

class Day3 : LineWorker<String, Pair<Int, Int>>() {
  override fun parseLine(line: String): String {
    return line
  }

  override fun part1(inputs: List<String>): Pair<Int, Int> {
    val bits = inputs.first().length
    val gamma = mutableListOf('0')
    val epsilon = mutableListOf('0')
    for (index in 0 until bits) {
      val ones = inputs.count { it[index] == '1' }
      val zeroes = inputs.size - ones
      if (ones > zeroes) {
        gamma.add('1')
        epsilon.add('0')
      } else {
        gamma.add('0')
        epsilon.add('1')
      }
    }
    return charArrayToInt(gamma) to charArrayToInt(epsilon)
  }

  private fun charArrayToInt(chars: List<Char>): Int {
    return String(chars.toCharArray()).toInt(2)
  }

  override fun part2(inputs: List<String>): Pair<Int, Int> {
    return filterValues(inputs, 0, true) to
        filterValues(inputs, 0, false)
  }

  private fun filterValues(inputs: List<String>, index: Int, mostCommon: Boolean): Int {
    if (inputs.size == 1) {
      return inputs.first().toInt(2)
    }
    if (index == inputs.first().length) {
      return inputs.first().toInt(2)
    }
    val groups = inputs.groupBy { it[index] }
    val ones = groups['1'] ?: emptyList()
    val zeroes = groups['0'] ?: emptyList()
    val nextInputs = if (mostCommon) {
      if (ones.size >= zeroes.size) {
        ones
      } else {
        zeroes
      }
    } else {
      if (zeroes.size <= ones.size) {
        zeroes
      } else {
        ones
      }
    }
    return filterValues(nextInputs, index + 1, mostCommon)
  }
}