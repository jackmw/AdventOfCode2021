package com.jackmw.aoc2022

import com.jackmw.Utils

fun main(args: Array<String>) {
  val day = Day10()
  day.part1("/2022/day10/part1-test.txt")
  day.part1("/2022/day10/part1-input.txt" )

  day.part2("/2022/day10/part1-test.txt")
  day.part2("/2022/day10/part1-input.txt" )
}

class Day10 {
  internal fun part1(fileName: String) {
    val device = Device()
    val operations = parseFile(fileName)
    for (operation in operations) {
      device.execute(operation)
    }
    val signalStrength = device.getSignalStrength()
    println("Part1 fileName=$fileName signalStrength=$signalStrength")
  }

  internal fun part2(fileName: String) {
    val device = Device()
    val operations = parseFile(fileName)
    for (operation in operations) {
      device.execute(operation)
    }
    device.drawScreen()
  }

  enum class Command(val cycles: Int) {
    NOOP(1),
    ADDX(2),
    ;
  }

  class Device(private val screenWidth: Int = 40) {
    private var x = 1
    private var cycle = 1
    private val commandHistory = mutableMapOf<Int, Int>()

    fun execute(operation: Operation) {
      repeat(operation.command.cycles - 1) {i ->
        commandHistory[cycle + i + 1] = x
      }
      x += operation.value
      cycle += operation.command.cycles
      commandHistory[cycle] = x
    }

    private fun getXAt(cycleNumber: Int): Int {
      if (cycleNumber <= 1) {
        return 1
      } else if (cycleNumber >= commandHistory.size) {
        return commandHistory[cycle]!!
      }
      return commandHistory[cycleNumber] ?: throw IllegalStateException("unknown value for cycleNumber=$cycleNumber")
    }

    fun getSignalStrength(): Long {
      var sum = 0L
      for (i in (20 .. commandHistory.size step 40)) {
        println("JACK_DEBUG cycleNumber=$i strength=${getXAt(i)}")
        sum += i * getXAt(i)
      }
      return sum
    }

    fun drawScreen() {
      for (cycle in 1 .. 240) {
        val pixelPosition = cycle - 1
        if (pixelPosition > 0 && pixelPosition % screenWidth == 0) {
          println()
        }
        val spriteMiddle = getXAt(cycle)
        // println("JACK_DEBUG pixelPosition = $pixelPosition, spriteMIddle=$spriteMiddle")
        val xPosition = pixelPosition % screenWidth
        val pixel = if (xPosition >= spriteMiddle - 1 && xPosition <= spriteMiddle + 1) {
          '#'
        } else {
          '.'
        }
        print(pixel)
      }
      println()
    }
  }

  data class Operation(
    val command: Command,
    val value: Int = 0,
  )

  private fun parseFile(fileName: String): List<Operation> {
    val lines = Utils.readFileAsLines(fileName)
    return lines.map { line ->
      val command = Command.valueOf(line.substringBefore(' ').uppercase())
      val value = line.substringAfter(' ', "0").toInt()
      Operation(command, value)
    }
  }
}