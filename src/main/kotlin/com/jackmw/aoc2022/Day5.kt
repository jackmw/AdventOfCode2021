package com.jackmw.aoc2022

import com.jackmw.Utils
import java.util.Stack

fun main(args: Array<String>) {
  part1("/2022/day5/part1-test.txt")
  part1("/2022/day5/part1-input.txt")

  part2("/2022/day5/part1-test.txt")
  part2("/2022/day5/part1-input.txt")

}

private fun part1(fileName: String) {
  val (stacks, instructions) = parseFile(fileName)
  printStacks(stacks)
  for (instruction in instructions) {
    // println(instruction)
    repeat(instruction.count) {
      // println("Action ${it+1} of ${instruction.count}")
      val fromStack = stacks[instruction.from]!!
      val toStack = stacks[instruction.to]!!
      if (fromStack.isEmpty()) {
        println("JACK_DEBUG cannot execute instruction $instruction")
      }
      val item = fromStack.pop()
      toStack.push(item)
      // printStacks(stacks)
    }
  }
  printStacks(stacks)
  val answer = stacks.values.map { it.peek() }.joinToString("")
  println("part1 $fileName: $answer")
}

private fun part2(fileName: String) {
  val (stacks, instructions) = parseFile(fileName)
  printStacks(stacks)
  for (instruction in instructions) {
    // println(instruction)

    val fromStack = stacks[instruction.from]!!
    val toStack = stacks[instruction.to]!!

    val items = mutableListOf<Char>()
    repeat(instruction.count) {
      val item = fromStack.pop()
      items.add(item)
    }
    for (item in items.reversed()) {
      toStack.push(item)
    }
  }
  val answer = stacks.values.map { it.peek() }.joinToString("")
  printStacks(stacks)
  println("part2 $fileName: $answer")
}

private fun printStacks(stacks: Map<Int, Stack<Char>>) {
  val lines = mutableListOf<StringBuilder>()
  val tallest = stacks.maxOf { (_, stack) -> stack.size }
  repeat(tallest + 1) {
    lines.add(StringBuilder())
  }
  stacks.map { (stackName, stack) ->
    var lineCount = 0
    repeat(tallest - stack.size) {
      lines[lineCount].append("    ")
      lineCount++
    }
    for (char in stack.reversed()) {
      lines[lineCount].append("[$char] ")
      lineCount++
    }
    lines[lineCount].append(" $stackName  ")
  }
  for (line in lines) {
    println(line)
  }
}

data class Instruction(
  val count: Int,
  val from: Int,
  val to: Int,
) {
  override fun toString(): String {
    return "move $count from $from to $to"
  }
}

private fun parseFile(fileName: String): Pair<Map<Int, Stack<Char>>, List<Instruction>> {
  val lines = Utils.readFileAsLines(fileName)
  val instructions = mutableListOf<Instruction>()
  val stacksTopToBottom = mutableMapOf<Int, MutableList<Char>>()
  lines.forEach { line ->
    if (line.startsWith("move")) {
      instructions.add(parseInstruction(line))
    } else if (line.isNotBlank()) {
      parseStacks(stacksTopToBottom, line)
    }
  }
  val stacks = mutableMapOf<Int, Stack<Char>>()
    stacksTopToBottom.map { (stackName, stackContent) ->
      val stack: Stack<Char> = Stack<Char>()
      stackContent.reversed().forEach { stack.push(it) }
      stacks[stackName] = stack
    }
  return stacks.toSortedMap() to instructions
}

fun parseStacks(stacksTopToBottom: MutableMap<Int, MutableList<Char>>, line: String) {
  var index = 0
  var stackName = 1
  while (index < line.length) {
    if (line[index] == '[') {
      if (stacksTopToBottom[stackName] == null) {
        stacksTopToBottom[stackName] = mutableListOf()
      }
      stacksTopToBottom[stackName]!!.add(line[index+1])
    }
    index += 4
    stackName++
  }
}

fun parseInstruction(line: String): Instruction {
  val pattern = """move (\d+) from (\d+) to (\d+)""".toRegex()
  val matches = pattern.findAll(line)
  return matches.map{match ->
  Instruction(
    count = match.groupValues[1].toInt(),
    from = match.groupValues[2].toInt(),
    to = match.groupValues[3].toInt(),
  )
  }.single()
}


private fun String.toIntRange(): IntRange {
  val start = substringBefore('-').toInt()
  val end = substringAfter('-').toInt()
  return IntRange(start = start, endInclusive = end)
}
