package com.jackmw.aoc

import com.jackmw.aoc.Utils.Companion.readFileAsLines
import kotlin.math.abs

fun main() {
  part1("/day7/part1-test.txt")
  part1("/day7/part1-input.txt")

  part2("/day7/part1-test.txt")
  part2("/day7/part1-input.txt")
}

private fun part1(fileName: String) {
  val positions = readFileAsLines(fileName)
    .first()
    .split(",")
    .mapNotNull { it.toIntOrNull() }
  val (idealPosition, fuelCost) = findIdealPosition(positions) {a, b -> abs(a-b).toLong()}
  println("$fileName idealPosition $idealPosition, $fuelCost")
}

private fun part2(fileName: String) {
  val positions = readFileAsLines(fileName)
    .first()
    .split(",")
    .mapNotNull { it.toIntOrNull() }
  val (idealPosition, fuelCost) = findIdealPosition(positions) { a, b -> summation(abs(a - b)) }
  println("$fileName idealPosition $idealPosition, $fuelCost")
}

private fun summation(n:Int):Long {
  return n.toLong() * (n+1) / 2
}

fun findIdealPosition(positions: List<Int> ,fuelCostCalculator :(Int, Int) -> Long): Pair<Int, Long> {
  val maxPosition = positions.maxOf { it }
  val costs = (0 .. maxPosition).map { targetPosition ->
    val fuelCost = sumFuelCost(positions, targetPosition, fuelCostCalculator)
    targetPosition to fuelCost
  }
  costs.sortedBy { it.second }.forEach {println("${it.first} -> ${it.second}")}

  return costs.minByOrNull { it.second }!!
}

fun sumFuelCost(positions: List<Int>, target: Int, fuelCostCalculator :(Int, Int) -> Long): Long {
  return positions.sumOf { from -> fuelCostCalculator(from, target) }
}