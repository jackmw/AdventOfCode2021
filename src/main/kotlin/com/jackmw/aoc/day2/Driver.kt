package com.jackmw.aoc.day2

import com.jackmw.aoc.LineWorker

class Driver : LineWorker<Driver.Instruction, Pair<Int, Int>>() {
  data class Instruction(
    val forward: Int = 0,
    val down: Int = 0,
  )

  override fun parseLine(line: String): Instruction? {
    val pieces = line.split(" ").filter { it.isNotBlank() }
    if (pieces.size != 2) {
      return null
    }
    val direction = pieces[0]
    if (direction != "forward" && direction != "down" && direction != "up") {
      return null
    }
    val movement = pieces[1].toIntOrNull() ?: return null

    return when (direction) {
      "forward" -> Instruction(forward = movement)
      "down" -> Instruction(down = movement)
      "up" -> Instruction(down = -movement)
      else -> null
    }
  }

  override fun part1(inputs: List<Instruction>): Pair<Int, Int> {
    var horizontal = 0
    var vertical = 0
    for (instruction in inputs) {
      horizontal += instruction.forward
      vertical = (vertical + instruction.down).coerceAtLeast(0)
    }
    return horizontal to vertical
  }

  override fun part2(inputs: List<Instruction>): Pair<Int, Int> {
    var aim = 0
    var horizontal = 0
    var vertical = 0
    for (instruction in inputs) {
      horizontal += instruction.forward
      aim += instruction.down
      vertical += aim * instruction.forward
      vertical = vertical.coerceAtLeast(0)
    }
    return horizontal to vertical
  }
}