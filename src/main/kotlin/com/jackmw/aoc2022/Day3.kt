package com.jackmw.aoc2022

import com.jackmw.aoc2021.Utils

fun main(args: Array<String>) {
  part1("/2022.day3/part1-test.txt")
  part1("/2022.day3/part1-input.txt")

  part2("/2022.day3/part1-test.txt")
  part2("/2022.day3/part1-input.txt" )
}

private fun part1(fileName: String) {
  val backpacks = parseFile(fileName)
  val sum = backpacks.sumOf { (firstCompartment, secondCompartment) ->
    calculateRepackPriority(firstCompartment, secondCompartment)
  }
  println("Part1: $fileName -> $sum")
}

private fun part2(fileName: String) {
  val backpacks = parseFile(fileName).map { it.first + it.second }
  var index = 0
  var sum = 0
  while(index < backpacks.size) {
    sum += calculateBadgePriority(backpacks, index)
    index += 3
  }
  println("Part2: $fileName -> $sum")
}

private fun calculateRepackPriority(firstCompartment: Set<Char>, secondCompartment: Set<Char>): Int {
  val wrongItem = firstCompartment.intersect(secondCompartment).single()
  val priority = wrongItem.getPriority()
  println("wrongItem=$wrongItem priority=$priority")
  return priority
}

private fun calculateBadgePriority(backpacks: List<Set<Char>>, index: Int): Int {
  val firstBackpack = backpacks[index]
  val secondBackpack = backpacks[index+1]
  val thirdBackpack = backpacks[index+2]
  val badge = firstBackpack.intersect(secondBackpack).intersect(thirdBackpack).single()
  val priority = badge.getPriority()
  println("badge=$badge priority=$priority")
  return priority
}

private fun Char.getPriority(): Int {
  return when (this) {
    in 'a'..'z' ->  this - 'a' + 1
    else ->  this - 'A' + 27
  }
}

private fun parseFile(fileName: String): List<Pair<Set<Char>, Set<Char>>> {
  val lines = Utils.readFileAsLines(fileName)
  return lines.map { line ->
    val length = line.length / 2
    line.take(length).toSet() to line.takeLast(length).toSet()
  }
}