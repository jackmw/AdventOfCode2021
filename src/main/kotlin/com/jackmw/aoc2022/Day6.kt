package com.jackmw.aoc2022

import com.jackmw.aoc2021.Utils
import java.util.Stack

fun main(args: Array<String>) {
  part1("/2022/day6/part1-test.txt")
  part1("/2022/day6/part1-input.txt")

  part2("/2022/day6/part1-test.txt")
  part2("/2022/day6/part1-input.txt")

}

private fun part1(fileName: String) {
  val signals = parseFile(fileName)
  for (signal in signals) {
    val startPosition = signal.findStartOfPacket()
    println("start of packet for $signal is at $startPosition, the marker is ${signal.substring(startPosition - 4, startPosition)}")
  }
}

private fun part2(fileName: String) {
  val signals = parseFile(fileName)
  for (signal in signals) {
    val startPosition = signal.findStartOfPacket(marketLength = 14)
    println("start of packet for $signal is at $startPosition, the marker is ${signal.substring(startPosition - 14, startPosition)}")
  }
}

private fun String.findStartOfPacket(marketLength: Int = 4): Int {
  if (length < marketLength) throw IllegalStateException()
  var startIndex = -1
  val mutableSet = mutableSetOf<Char>()
  for (i in marketLength until length) {
    for (j in i -marketLength until i) {
      mutableSet.add(this[j])
    }
    if (mutableSet.size == marketLength) {
      startIndex = i
      break
    }
    mutableSet.clear()
  }
  return startIndex
}


private fun parseFile(fileName: String): List<String> {
  return Utils.readFileAsLines(fileName)
}