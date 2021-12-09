package com.jackmw.aoc

import com.jackmw.aoc.Utils.Companion.readFileAsLines

fun main() {
  part1("/day9/part1-test.txt")
  part1("/day9/part1-input.txt")


  part2("/day9/part1-test.txt")
  part2("/day9/part1-input.txt")
}

private fun part1(fileName: String) {
  val lavaMap = LavaMap.parse(readFileAsLines(fileName))
  val lowPointValues = lavaMap.findLowPointValues()
  val riskLevel = lowPointValues.sumOf { it + 1 }
  println("$fileName risk-level $riskLevel")
}

private fun part2(fileName: String) {
  val lavaMap = LavaMap.parse(readFileAsLines(fileName))
  val basins = lavaMap.findBasins()
  val top3BasinSizes = basins.map {
    it.size
  }.sortedDescending()
    .subList(0, 3)
  println(top3BasinSizes)
  val product = top3BasinSizes.reduce { acc, size -> acc * size }
  println("$fileName top3 basin-size product $product")
}

class LavaMap(
  private val heightMap: Array<IntArray>,
  private val rows: Int = heightMap.size,
  private val columns: Int = heightMap[0].size) {

  fun findBasins(): List<Set<Pair<Int, Int>>> {
    return findLowPoints()
      .map { findPointsThatFlowTo(it.first, it.second) }
  }

  private fun findPointsThatFlowTo(row: Int, column: Int): Set<Pair<Int, Int>> {
    val points = mutableSetOf<Pair<Int, Int>>()
    val point = Pair(row, column)
    points.add(point)
    val localValue = heightMap[row][column]
    val neighourCoords = getNeighbourCoordinates(row, column)
      .filter { heightMap[it.first][it.second] > localValue && heightMap[it.first][it.second] != 9 } - points
    if (neighourCoords.isEmpty()) {
      return emptySet()
    }
    points.addAll(neighourCoords)
    neighourCoords.forEach {
      points.addAll(findPointsThatFlowTo(it.first, it.second))
    }
    return points
  }

  private fun findLowPoints(): List<Pair<Int, Int>> {
    val lowPoints = mutableListOf<Pair<Int, Int>>()
    for(row in 0 until rows) {
      for (column in 0 until columns) {
        if (isLowPoint(row, column)) {
          lowPoints.add(Pair(row, column))
        }
      }
    }
    return lowPoints
  }

  fun findLowPointValues(): List<Int> {
    val lowPointValues = mutableListOf<Int>()
    for(row in 0 until rows) {
      for (column in 0 until columns) {
        val lowPointValue = getValueIfLowPoint(row, column) ?: continue
        lowPointValues.add(lowPointValue)
      }
    }
    return lowPointValues
  }
  private fun isLowPoint(row: Int, column: Int): Boolean {
    val neighbourCoordinates = getNeighbourCoordinates(row, column)
    val localValue =heightMap[row][column]
    return neighbourCoordinates.all { (r, c) ->
      heightMap[r][c] > localValue
    }
  }

  private fun getValueIfLowPoint(row: Int, column: Int): Int? {
    val neighbourCoordinates = getNeighbourCoordinates(row, column)
    val localValue =heightMap[row][column]
    val isLowPoint = neighbourCoordinates.all { (r, c) ->
      heightMap[r][c] > localValue
    }
    return if (isLowPoint) {
      localValue
    } else {
      null
    }
  }

  private fun getNeighbourCoordinates(row: Int, column: Int): List<Pair<Int, Int>> {
    return listOfNotNull(
      getIfValid(row - 1, column),
      getIfValid(row + 1, column),
      getIfValid(row, column + 1),
      getIfValid(row, column - 1),
    )
  }

  private fun getIfValid(row: Int, column: Int): Pair<Int, Int>? {
    return if (isValidRow(row) && isValidColumn(column)) {
      Pair(row, column)
    } else {
      null
    }
  }

  private fun isValidRow(row: Int): Boolean {
    return row in 0 until rows
  }

  private fun isValidColumn(column: Int): Boolean {
    return column in 0 until columns
  }

  companion object {
    fun parse(lines: List<String>): LavaMap {
      val heightMap = lines.map {
        it.toCharArray().map { char ->
          char - '0'
        }.toIntArray()
      }.toTypedArray()
      return LavaMap(heightMap)
    }
  }
}