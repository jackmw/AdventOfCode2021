package com.jackmw.aoc2021

import com.jackmw.aoc2021.Utils.Companion.readFileAsLines

fun main() {
  part1("/day11/part1-test.txt", 10)
  part1("/day11/part1-test.txt", 100)
  part1("/day11/part1-input.txt", 100)

  part2("/day11/part1-test.txt")
  part2("/day11/part1-input.txt")
}

private fun part1(fileName: String, steps: Int) {
  val map = DumboMap.parseMap(readFileAsLines(fileName))
  val flashes = map.simulateSteps(steps)
  println("$fileName -> There are $flashes flashes after $steps steps.")
}

private fun part2(fileName: String) {
  val map = DumboMap.parseMap(readFileAsLines(fileName))
  var step = 0
  while (!map.hasSynchronizedFlash()) {
    map.step()
    step++
  }
  println("$fileName -> After $step steps, there is synchronized flashing.")
}

private data class DumboMap(
  private val map: Array<Array<DumboOctopus>>,
  private val rows: Int,
  private val columns: Int,
) {
  init {
    for (row in 0 until rows) {
      for (column in 0 until columns) {
        map[row][column].setNeighbours(getNeighbours(row, column))
      }
    }
  }

  private fun getNeighbours(row: Int, column: Int): List<DumboOctopus> {
    return RELATIVE_NEIGHBOURS.mapNotNull { (dRow, dColumn) ->
      val newRow = (row + dRow).takeIf { it in 0 until rows } ?: return@mapNotNull null
      val newColumn = (column + dColumn).takeIf { it in 0 until columns } ?: return@mapNotNull null
      map[newRow][newColumn]
    }
  }

  fun debug() {
    for (row in 0 until rows) {
      for (column in 0 until columns) {
        print(map[row][column].energyLevel)
      }
      println()
    }
  }

  fun simulateSteps(steps: Int): Int {
    var flashes = 0
    for (step in 1 .. steps) {
      flashes += step()
    }
    return flashes
  }

  fun step(): Int {
    for (row in 0 until rows) {
      for (column in 0 until columns) {
        map[row][column].energyLevel++
      }
    }

    for (row in 0 until rows) {
      for (column in 0 until columns) {
        val octopus = map[row][column]
        octopus.maybeFlash()
      }
    }

    var flashes = 0
    for (row in 0 until rows) {
      for (column in 0 until columns) {
        val octopus = map[row][column]
        if (octopus.flashed) {
          flashes++
          octopus.resetIfFlashed()
        }
      }
    }
    return flashes
  }

  override fun hashCode(): Int {
    return map.contentDeepHashCode()
  }

  fun hasSynchronizedFlash(): Boolean {
    for (row in 0 until rows) {
      for (column in 0 until columns) {
        val octopus = map[row][column]
        if (octopus.energyLevel > 0) {
          return false
        }
      }
    }
    return true
  }

  companion object {
    private val RELATIVE_NEIGHBOURS = listOf(
      -1 to -1, -1 to 0, -1 to 1,
      0 to -1,            0 to 1,
      1 to -1,  1 to 0,  1 to 1,
    )
    fun parseMap(lines: List<String>): DumboMap {
      return DumboMap(map = lines.map { parseLine(it) }.toTypedArray(), rows = lines.size, columns = lines[0].length)
    }

    private fun parseLine(line: String): Array<DumboOctopus> {
      return line.toCharArray()
        .map { DumboOctopus(energyLevel = it.digitToInt()) }
        .toTypedArray()
    }
  }
}

private data class DumboOctopus(
  var energyLevel: Int,
  var flashed: Boolean = false,
  val neighbours: MutableList<DumboOctopus> = mutableListOf(),
) {
  fun maybeFlash() {
    if (!flashed && energyLevel > 9) {
      flashed = true
      neighbours.forEach { it.onFlashed() }
    }
  }

  fun onFlashed() {
    energyLevel++
    maybeFlash()
  }

  fun setNeighbours(newNeighbours: List<DumboOctopus>) {
    if(neighbours.isNotEmpty()) {
      throw IllegalStateException()
    } else {
      neighbours.addAll(newNeighbours)
    }
  }

  fun resetIfFlashed() {
    if(flashed) {
      flashed = false
      energyLevel = 0
    }
  }
}