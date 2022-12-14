package com.jackmw.aoc2021

import com.jackmw.Utils.Companion.readFileAsLines
import java.util.PriorityQueue

fun main() {
  part1("/day15/part1-test.txt")
  part1("/day15/part1-input.txt")
  
  part2("/day15/part1-test.txt")
  part2("/day15/part1-input.txt")

}


private fun part1(fileName: String) {
  val riskMap = RiskMap.parse(readFileAsLines(fileName))
  // println(riskMap)
  val path = riskMap.findLowestRiskPath()
  println("$fileName -> lowest risk path is ${path.totalRisk}")
}

private fun part2(fileName: String) {
  val riskMap = RiskMap.parse(readFileAsLines(fileName)).expand(5, 5)
  // println(riskMap)
  val path = riskMap.findLowestRiskPath()
  println("$fileName -> lowest risk path is ${path.totalRisk}")
}

class Path(val steps: Set<Pair<Int, Int>>, val totalRisk: Int) {
  val lastStep = steps.last()

  fun contains(step: Pair<Int, Int>): Boolean {
    return step in steps
  }
}


class RiskMap(private val risks: Array<Array<Int>>) {
  private val rows = risks.size
  private val columns = risks[0].size
  private val finish = rows - 1 to columns - 1

  override fun toString(): String {
    val stringBuilder = StringBuilder()
    for (row in 0 until rows) {
      for (column in 0 until columns) {
        stringBuilder.append(risks[row][column])
      }
      stringBuilder.append('\n')
    }
    return stringBuilder.toString()
  }

  fun findLowestRiskPath(): Path {
    val paths = PriorityQueue<Path>(compareBy { it.totalRisk })
    val firstPath = Path(setOf(0 to 0), 0)
    paths.add(firstPath)
    val lowestRisks = mutableMapOf<Pair<Int, Int>, Int>()
    val completedPaths = mutableListOf<Path>()
    while (paths.isNotEmpty()) {
      if (paths.size % 10000 == 0) {
        println("JACK_DEBUG checking ${paths.size} paths")
      }
      val currentPath = paths.elementAt(0)
      paths.remove(currentPath)
      val lastStep = currentPath.lastStep
      if (lastStep == finish) {
        completedPaths.add(currentPath)
        println("JACK_DEBUG found ${paths.size} complete path(s), score: ${currentPath.totalRisk}")
        continue
      }
      val nextMoves = getValidNextMovements(lastStep)
      if (nextMoves.isNotEmpty()) {
        for (move in nextMoves) {
          val lowestRiskToReachCell = lowestRisks[move] ?: Int.MAX_VALUE
          val cellRisk = risks[move.first][move.second]
          if (currentPath.totalRisk + cellRisk >= lowestRiskToReachCell) {
            // we already have a path to get here, don't need to check this path again
            continue
          }
          if (currentPath.contains(move)) {
            // the path should not have the same move twice
            continue
          }
          val newPath = Path(currentPath.steps + move, currentPath.totalRisk + cellRisk)
          paths.add(newPath)
          lowestRisks[move] = newPath.totalRisk
        }
      }
    }
    return completedPaths.minByOrNull { it.totalRisk }!!
  }

  private fun getValidNextMovements(position: Pair<Int, Int>): Set<Pair<Int, Int>> {
    return NEXT_MOVE.asSequence()
      .map { (dRow, dColumn) -> position.first + dRow to position.second + dColumn }
      .filter { (row, column) -> row in 0 until rows && column in 0 until columns }
      .toSet()
  }

  fun expand(horizontal: Int, vertical: Int): RiskMap {
    val newMap = Array(size = rows * vertical) {
      Array(size = columns * horizontal) { 0 }
    }
    for (row in newMap.indices) {
      val newMapRow = newMap[row]
      for (column in newMapRow.indices) {
        val risk = risks[row % rows][column % columns] + (row / rows) + (column / columns)
        newMap[row][column] = if (risk > 9) {
          risk - 9
        } else {
          risk
        }
      }
    }
    return RiskMap(newMap)
  }

  companion object {
    val NEXT_MOVE = setOf(
      1 to 0, // down 1 row
      -1 to 0, // up 1 row
      0 to -1, // move left
      0 to 1, // move right
    )

    fun parse(lines: List<String>): RiskMap {
      val array = lines.map { line ->
        line.toCharArray().map { char -> char.digitToInt() }.toTypedArray()
      }.toTypedArray()
      return RiskMap(array)
    }
  }
}