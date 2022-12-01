package com.jackmw.aoc2021

import com.jackmw.aoc2021.Utils.Companion.readFileAsLines

fun main() {
  part2("/day14/part1-test.txt", 10)
  part2("/day14/part1-input.txt", 10)

  part2("/day14/part1-test.txt", 40)
  part2("/day14/part1-input.txt", 40)
}


private fun part2(fileName: String, iterations: Int) {
  val (template, rules) = parse(readFileAsLines(fileName))
  val polymer = FastPolymer()
  polymer.setTemplate(template)
  polymer.generate(rules, iterations)
  polymer.printResult()
}

class FastPolymer {
  private val map: MutableMap<Pair<Char, Char>, Long> = mutableMapOf()
  private val counters: MutableMap<Char, Long> = mutableMapOf()

  fun setTemplate(template: String) {
    template.zipWithNext { a, b -> map.compute(a to b) { _, count -> 1 + (count ?: 0) } }
    template.map { char -> counters.compute(char) { _, count -> 1 + (count ?: 0) } }
  }

  fun printResult() {
    val mostCommon = counters.maxByOrNull { it.value }!!
    val leastCommon = counters.minByOrNull { it.value }!!
    println("Most common $mostCommon - least common $leastCommon: ${mostCommon.value - leastCommon.value}")
  }

  fun generate(rules: Map<Pair<Char, Char>, Char>, iterations: Int) {
    for(iteration in 1..iterations) {
      generate(rules)
      // println("After step $iteration: ${counters.values.sumOf { it }}")
    }
  }

  private fun generate(rules: Map<Pair<Char, Char>, Char>) {
    val newPairs: MutableMap<Pair<Char, Char>, Long> = mutableMapOf()
    map.forEach { (pair, pairCount) ->
      val newChar = rules[pair]
      if (newChar != null) {
        counters.compute(newChar) { _, value -> pairCount + (value ?: 0) }
        newPairs.compute(pair.first to newChar) { _, value -> pairCount + (value ?: 0) }
        newPairs.compute(newChar to pair.second) { _, value -> pairCount + (value ?: 0) }
      }
    }
    map.clear()
    map.putAll(newPairs)
  }
}

private fun parse(lines: List<String>): Pair<String, Map<Pair<Char, Char>, Char>> {
  val template = lines.first()
  val rules = lines.drop(2).associate { line ->
    val pair = line.substringBefore(" -> ")
    val expansion = line.substringAfter(" -> ")[0]
    Pair(pair[0], pair[1]) to expansion
  }
  return template to rules
}
