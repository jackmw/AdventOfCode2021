package com.jackmw.aoc2022

import com.jackmw.Utils

fun main(args: Array<String>) {
  part1("/2022/day8/part1-test.txt")
  part1("/2022/day8/part1-input.txt")

  part2("/2022/day8/part1-test.txt")
  part2("/2022/day8/part1-input.txt")
}

private fun part1(fileName: String) {
  val map = parseFile(fileName)
  val visibleTrees = countInteriorVisibleTrees(map) + countExteriorTrees(map)
  println("part1: $fileName -> visibleTrees: $visibleTrees")
}

private fun part2(fileName: String) {
  val map = parseFile(fileName)
  val highestScenicScore = getHighestScenicScore(map)
  println("part2: $fileName -> highestScenicScore: $highestScenicScore")
}

private fun printMap(mapName: String, map: Array<IntArray>) {
  println("$mapName:")
  for (row in map) {
    for (column in row) {
      print("$column ")
    }
    println()
  }
}

fun copyArray(array: Array<IntArray>): Array<IntArray> {
  val rows = array.size
  val columns = array.first().size
  val copy = Array(rows) { IntArray(columns) }
  for (row in 0 until rows) {
    for (column in 0 until columns) {
      copy[row][column] = array[row][column]
    }
  }
  return copy
}

fun countInteriorVisibleTrees(map: Array<IntArray>): Int {
  val rows = map.size
  val columns = map.first().size

  if (rows <= 2 || columns <= 2) {
    return 0
  }

  val leftVisibility = copyArray(map)
  val rightVisibility = copyArray(map)

  for (row in 1 until rows - 1) {
    for (column in 1 until columns - 1) {
      leftVisibility[row][column] = maxOf(leftVisibility[row][column - 1], map[row][column])
    }
    for (column in columns - 2 downTo 1) {
      rightVisibility[row][column] = maxOf(rightVisibility[row][column + 1], map[row][column])
    }
  }

  val topVisibility = copyArray(map)
  val bottomVisibility = copyArray(map)
  for (column in 1 until columns - 1) {
    for (row in 1 until rows - 1) {
      topVisibility[row][column] = maxOf(topVisibility[row - 1][column], map[row][column])
    }
    for (row in rows - 2 downTo 1) {
      bottomVisibility[row][column] = maxOf(bottomVisibility[row + 1][column], map[row][column])
    }
  }

  var visibleTrees = 0
  for (row in 1 until rows - 1) {
    for (column in 1 until columns - 1) {
      val visibleFromLeft = map[row][column] > leftVisibility[row][column - 1]
      val visibleFromRight= map[row][column] > rightVisibility[row][column + 1]
      val visibleFromTop = map[row][column] > topVisibility[row - 1][column]
      val visibleFromBottom = map[row][column] > bottomVisibility[row + 1][column]
      val visible = visibleFromLeft || visibleFromRight || visibleFromTop || visibleFromBottom
      if (visible) {
        visibleTrees++
      }
    }
  }
  return visibleTrees
}

fun countExteriorTrees(map: Array<IntArray>): Int {
  val rows = map.size
  val columns = map.first().size
  return if (rows <= 2 || columns <= 2) {
    rows * columns
  } else {
    (map.size + map.first().size) * 2 - 4
  }
}

fun getHighestScenicScore(map: Array<IntArray>): Int {
  val rows = map.size
  val columns = map.first().size

  var maxScore = 0
  var maxLocation: Pair<Int, Int> = 0 to 0
  for (row in 1 until rows - 1) {
    for (column in 1 until columns - 1) {
      val topScore =  map.getTopVisibleTrees(row, column)
      val bottomScore = map.getBottomVisibleTrees(row, column)
      val leftScore = map.getLeftVisibleTrees(row, column)
      val rightScore = map.getRightVisibleTrees(row, column)
      val score = topScore * bottomScore * rightScore * leftScore
      println("JACK_DEBUG ($row, $column) has left=$leftScore, right=$rightScore, top=$topScore, bottom=$bottomScore, total=$score")

      if (score > maxScore) {
        maxScore = score
        maxLocation = row to column
      }
    }
  }
  println("JACK_DEBUG $maxLocation has a scenic score of $maxScore")
  return maxScore

}

fun Array<IntArray>.getLeftVisibleTrees(row: Int, column: Int): Int {
  var score = 0
  val height = this[row][column]
  for (c in column - 1 downTo 0) {
      score++
    if (this[row][c] >= height) {
      break
    }
  }
  return score
}

fun Array<IntArray>.getRightVisibleTrees(row: Int, column: Int): Int {
  var score = 0
  val columns = this[0].size
  val height = this[row][column]
  for (c in column + 1 until  columns) {
      score++
    if (this[row][c] >= height) {
      break
    }
  }
  return score
}
fun Array<IntArray>.getTopVisibleTrees(row: Int, column: Int): Int {
  var score = 0
  val height = this[row][column]
  for (r in row - 1 downTo  0) {
      score++

    if (this[r][column] >= height) {
      break
    }
  }
  return score
}

fun Array<IntArray>.getBottomVisibleTrees(row: Int, column: Int): Int {
  var score = 0
  val height = this[row][column]
  for (r in row + 1 until size) {
    score++
    if (this[r][column] >= height) {
      break
    }
  }
  return score
}

private fun parseFile(fileName: String): Array<IntArray> {
  val lines = Utils.readFileAsLines(fileName)
  return lines
    .map { line ->
      line.map { it.digitToInt() }.toIntArray()
    }.toTypedArray()
}
