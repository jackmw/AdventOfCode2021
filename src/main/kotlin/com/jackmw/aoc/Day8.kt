package com.jackmw.aoc

import com.jackmw.aoc.Utils.Companion.readFileAsLines

fun main() {
//  part1("/day8/part1-threeTimes.txt")
//  part1("/day8/part1-negate.txt")

  part2("/day8/part1-test.txt")
  part2("/day8/part1-input.txt")
}

private fun part1(fileName: String) {
  val inputs = readFileAsLines(fileName).mapNotNull { Input.parse(it) }
  val uniqueCount = inputs.sumOf {
    it.numbers.count { number ->
      number.length == 2
          || number.length == 4
          || number.length == 3
          || number.length == 7
    }
  }
  println("$fileName ${inputs.size}-> $uniqueCount")
}


private fun part2(fileName: String) {
  val inputs = readFileAsLines(fileName).mapNotNull { Input.parse(it) }
  val allPermutations = allPermutations(('a'..'g').toSet())
  val displayOutputs = mutableListOf<Int>()
  for(input in inputs) {
    val solver = Solver(input.numbers, input.signals)
    solver.bruteForce(allPermutations)
    if (solver.isSolved()) {
      val displayOutput =  input.numbers.map {
        val number = solver.getNumber(it)
        println("$it -> $number")
        number
      }.reduce { acc, i -> acc * 10 + i }
      displayOutputs.add(displayOutput)
      println("Solved! $displayOutput" )
    } else {
      println("failed")
    }
  }
  println("sum of $displayOutputs ${displayOutputs.sum()}")

}

private data class Input(
  val signals: List<String>,
  val numbers: List<String>,
) {
  companion object {
    fun parse(line: String): Input? {
      val pieces = line.split('|')
      if (pieces.size != 2) return null
      val signals = pieces[0].split(' ').filter { it.trim().isNotEmpty() }
      if (signals.size != 10) return null
      val numbers = pieces[1].split(' ').filter { it.trim().isNotEmpty() }
      if (numbers.size != 4) return null
      return Input(signals = signals, numbers = numbers)
    }
  }
}

private class Solver(
  private val hints: List<String>,
  private val outputs: List<String>,
) {
  private val possibilities = mutableMapOf<Char, Set<Char>>()
  // solutions should be bad char -> real char
  private val solution = mutableMapOf<Char, Char>()

  init {
    ALLCHARS.forEach { possibilities[it] = ALLCHARS }
  }

  fun bruteForce(allSolutions: Collection<List<Char>>) {
    val tests = this.outputs + this.hints
    var counter = 0
    val possibleSolution = allSolutions.firstOrNull { solution ->
      counter++
      validateSolution(tests, solution)
    }
    if (possibleSolution != null) {
      println("brute force successful after $counter solutions tested")
      ('a'..'g').forEachIndexed { index, char ->
        solution[char] = possibleSolution[index]
      }
    } else {
      println("brute failed after $counter solutions tested")
    }
  }

  private fun validateSolution(tests: List<String>, solution: List<Char>): Boolean {
    val solutionMap = ('a'..'g').mapIndexed { index, char ->
      char to solution[index]
    }.toMap()
    return tests.all {
      val isValid = isValidNumber(it, solutionMap)
      if (!isValid) {
        //println("$it ${String(mapWithSolution(it, solutionMap).toCharArray())} is not a valid number using $solutionMap")
      }
      isValid
    }
  }

  fun mapWithSolution(test: String, solution: Map<Char, Char> = this.solution): Set<Char> {
    return test.asSequence()
      .map { char -> solution[char]!! }
      .toSet()
  }

  private fun isValidNumber(test: String, solution: Map<Char, Char>): Boolean {
    val translated = mapWithSolution(test, solution)
    return SEGMENTS.values.any { segments ->
      segments.size == translated.size && segments.containsAll(translated)
    }

  }

  fun solve() {
    val clues = hints + outputs
    val cluesByEasy = clues.groupBy { getNumberForClue(it) != null }
    val easyClues = cluesByEasy[true] ?: emptyList()
    val hardClues = cluesByEasy[false] ?: emptyList()
    println("applying easy clues: $easyClues")
    easyClues.forEach { clue ->
      val number = getNumberForClue(clue)
      if (number != null) {
        handleNumberForClue(number, clue)
      }
    }
    debug()
    println("guessing with solutions...")
    clues.forEach { clue ->
      val knownSegments = getKnownSegmentsInClue(clue)
      if (knownSegments.isNotEmpty()) {
        val numberWithKnownSegments = getPossibleNumbersWithSegments(knownSegments, clue.length)
        if (numberWithKnownSegments.size == 1) {
          handleNumberForClue(numberWithKnownSegments.first(), clue)
        }
      }
    }

    debug()
  }



  private fun getPossibleNumbersWithSegments(segments: Set<Char>, segmentCount: Int): List<Int> {
    return SEGMENTS.filter { (_, segments) -> segments.size == segmentCount && segments.containsAll(segments) }
      .map { it.key }
  }
  private fun handleNumberForClue(number: Int, clue: String) {
    val chars = clue.toCharArray().toSet()
    val segments = SEGMENTS[number]!!
    segments.forEach { limitPossibility(it, chars) }
    (ALLCHARS - segments).forEach { removePossibility(it, chars)  }
  }

  private fun getKnownSegmentsInClue(clue: String): Set<Char> {
    return clue.asSequence()
      .map { badChar -> solution[badChar] }
      .filterNotNull()
      .toSet()
  }

  private fun expandSolutions() {}


  private fun getNumberForClue(clue: String): Int? {
    return when (clue.length) {
      2 -> 1
      3 -> 7
      4 -> 4
      5 -> null // can be either 2, 3, 5
      6 -> null // can be either 0, 6, 9
      7 -> 8
      else -> null
    }
  }

  private fun guessWithSolution(clue: String, possibleNumbers: Set<Int>): Int? {
    return null
  }


  private fun addClue(clue: String): Boolean {
    val chars = clue.toCharArray().toSet()

    val numbersWithSegments = SEGMENTS.filter { it.value.size == chars.size }
    if (numbersWithSegments.size == 1) {
      val keys = numbersWithSegments.values.first()
      keys.forEach { limitPossibility(it, chars) }
      (ALLCHARS - keys).forEach { removePossibility(it, chars)  }
    } else if (chars.size == 5) {
      // if the clue contains segment b, then it is 5
      // else if the clue contains e then it is 2
      // else it is 3
      if (solution['b'] != null && solution['b'] in chars) {
        val keys = SEGMENTS[5]!!
        keys.forEach { limitPossibility(it, chars) }
        (ALLCHARS - keys).forEach { removePossibility(it, chars)  }
      } else if (solution['e'] != null && solution['e'] in chars) {
        val keys = SEGMENTS[2]!!
        keys.forEach { limitPossibility(it, chars) }
        (ALLCHARS - keys).forEach { removePossibility(it, chars)  }
      } else if (solution['g'] != null && solution['g'] in chars) {
        val keys = SEGMENTS[2]!!
        keys.forEach { limitPossibility(it, chars) }
        (ALLCHARS - keys).forEach { removePossibility(it, chars)  }
      }
      // numbersWithSegments.forEach { (t, u) -> println("$chars -> $t -> $u") }
    } else if (chars.size == 6) {
      if (solution['d'] != null && solution['d'] in chars) {
        val keys = SEGMENTS[6]!!
        keys.forEach { limitPossibility(it, chars) }
        (ALLCHARS - keys).forEach { removePossibility(it, chars)  }
      } else if (solution['c'] != null && solution['c'] in chars) {
        val keys = SEGMENTS[0]!!
        keys.forEach { limitPossibility(it, chars) }
        (ALLCHARS - keys).forEach { removePossibility(it, chars)  }
      } else if (solution['d'] != null && solution['d'] in chars) {
        val keys = SEGMENTS[6]!!
        keys.forEach { limitPossibility(it, chars) }
        (ALLCHARS - keys).forEach { removePossibility(it, chars)  }
      }
    }
    return isSolved()
  }

  private fun removePossibility(char: Char, chars: Set<Char>) {
    val currentPossibilities = possibilities[char]!!
    if (currentPossibilities.size == 1) {
      return
    }
    val newPossibilities = possibilities[char]!! - chars
    possibilities[char] = newPossibilities
    if (newPossibilities.size == 1) {
      setSolution(badChar =  newPossibilities.first(), realChar = char)
      println("Found solution $char = ${newPossibilities.first()} (real)")
      ALLCHARS.filter { it != char }.forEach { removePossibility(it, newPossibilities) }
    }
  }

  private fun setSolution(badChar: Char, realChar: Char) {
    solution[badChar] = realChar
  }

  private fun limitPossibility(char: Char, possibleChars: Set<Char>) {
    removePossibility(char, ALLCHARS - possibleChars)
  }

  fun isSolved(): Boolean {
    return solution.size == 7
  }

  fun debug() {
    possibilities.forEach { (char, chars) -> println("$char -> $chars") }
  }

  fun getNumber(input: String): Int {
    val mapped = mapWithSolution(input, solution)
    return SEGMENTS.entries.first { it.value.size == mapped.size && it.value.containsAll(mapped) }
      .key
  }

  companion object {
    val ALLCHARS = ('a'..'g').toSet()
    val SEGMENTS = mapOf(
      1 to setOf('c', 'f'),
      7 to setOf('a', 'c', 'f'),
      4 to setOf('b', 'c', 'd', 'f'),

      2 to setOf('a', 'c', 'd', 'e', 'g'),
      3 to setOf('a', 'c', 'd', 'f', 'g'),
      5 to setOf('a', 'b', 'd', 'f', 'g'),

      0 to setOf('a', 'b', 'c', 'e', 'f', 'g'),
      6 to setOf('a', 'b', 'd', 'e', 'f', 'g'),
      9 to setOf('a', 'b', 'c', 'd', 'f', 'g'),

      8 to setOf('a', 'b', 'c', 'd', 'e', 'f', 'g'),
    )
  }
  // 1, 4, 7, 8 has unique number of segments
}


fun <T> allPermutations(set: Set<T>): Set<List<T>> {
  if (set.isEmpty()) return emptySet()
  return _allPermutations(set.toList())
}

private fun <T> _allPermutations(list: List<T>): Set<List<T>> {
  if (list.isEmpty()) return setOf(emptyList())

  val result: MutableSet<List<T>> = mutableSetOf()
  for (i in list.indices) {
    _allPermutations(list - list[i]).forEach{
        item -> result.add(item + list[i])
    }
  }
  return result
}