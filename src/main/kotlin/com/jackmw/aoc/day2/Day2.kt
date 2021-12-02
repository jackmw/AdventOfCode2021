package com.jackmw.aoc

import com.jackmw.aoc.day2.Driver

fun main(args: Array<String>) {
  part1("day2/part1-test.txt")
  part1("day2/part1-input.txt")

  part2("day2/part1-test.txt")
  part2("day2/part1-input.txt")
}

private fun part1(fileName: String) {
  val (horizontal, vertical) = Driver().part1(fileName)
  println("$fileName -> ($horizontal, $vertical) = ${horizontal * vertical}")
}

private fun part2(fileName: String) {
  val (horizontal, vertical) = Driver().part2(fileName)
  println("$fileName -> ($horizontal, $vertical) = ${horizontal * vertical}")
}
