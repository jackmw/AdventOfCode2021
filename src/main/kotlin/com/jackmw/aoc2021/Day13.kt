package com.jackmw.aoc2021

import com.jackmw.aoc2021.Utils.Companion.readFileAsLines
import kotlin.math.max

fun main() {
  part1("/day13/part1-test1.txt")
//  part1("/day13/part1-negate.txt")

  part2("/day13/part1-test1.txt")
  part2("/day13/part1-input.txt")
}

private fun part1(fileName: String) {
  val folding = Folding.parse(readFileAsLines(fileName))
  println(folding)
  val afterOneFold = folding.makeFirstFold()
  println(afterOneFold.countActive())
  println(afterOneFold)
}

private fun part2(fileName: String) {
  var folding = Folding.parse(readFileAsLines(fileName))
  while(folding.hasMoreFolds()) {
    folding = folding.makeFirstFold()
    println(folding.countActive())
  }
  println(folding)
}

private class Folding(
  private val map: Map<Point, Boolean>,
  private val folds: List<Pair<Int, Int>>,
) {
  private val rows = map.keys.maxOf { it.y } + 1
  private val columns =  map.keys.maxOf { it.x } + 1

  fun hasMoreFolds(): Boolean {
    return folds.isNotEmpty()
  }

  fun makeFirstFold(): Folding {
    val (x, y) = folds.first()
    val newMap = if (x > -1) {
      makeXFold(x)
    } else {
      makeYFold(y)
    }
    return Folding(newMap, folds.drop(1))
  }

  fun countActive(): Int {
    return map.count { it.value }
  }

  override fun toString(): String {
    val sb = StringBuilder()
    for (r in 0 until rows) {
      for (c in 0 until columns) {
        val char = if (map[Point(x = c, y = r)] == true) '#' else '.'
        sb.append(char)
      }
      sb.append('\n')
    }
    return sb.toString()
  }

  fun makeXFold(x: Int): Map<Point, Boolean> {
    val columnsAfterFold = max(x, columns - x) - 1
    val xShift = (columns - x - x - 1).coerceAtLeast(0)
    println("x = $x xShift=$xShift columnsBeforeFolding=$columns columnsAfterFolding=$columnsAfterFold")

    val newMap = mutableMapOf<Point, Boolean>()
    for (r in 0 until rows) {
      for (c in 0 until columnsAfterFold) {
        val cFromLeft = x - 1 - c
        val cFromRight = x + 1 + c
        newMap[Point(y = r, x = cFromLeft + xShift)] = map[Point(y = r,x = cFromLeft)] ?: false || map[Point(y = r, x = cFromRight)] ?: false
        //println("newMap[$r, ${cFromLeft + xShift}] = map[$r, $cFromLeft] + map[$r, $cFromRight]")
      }
    }
    return newMap
  }

  private fun makeYFold(y: Int): Map<Point, Boolean> {
    val rowsAfterFolding = max(y, rows - y) - 1
    val yShift = (rows - y - y - 1).coerceAtLeast(0)
    println("y = $y yShift=$yShift rowsBeforeFolding=$rows rowsAfterFolding=$rowsAfterFolding")

    val newMap = mutableMapOf<Point, Boolean>()
    for (r in 0 until rowsAfterFolding) {
      for (c in 0 until columns) {
        val rFromTop = y - 1 - r
        val rFromBottom = y + 1 + r
        newMap[Point(y = rFromTop + yShift, x = c)] = map[Point(y = rFromTop, x = c)] ?: false || map[Point(y = rFromBottom, x = c)] ?: false
        //println("newMap[${rFromTop + yShift}, $c] = map[$rFromTop, $c] + map[$rFromBottom, $c]")
      }
    }
    return newMap
  }

  companion object {
    fun parse(lines: List<String>): Folding {
      val groups = lines
        .filter { it.isNotBlank() }
        .groupBy { it.startsWith("fold along") }

      val map = groups[false]!!.map { line ->
        val x = line.substringBefore(",").toInt()
        val y = line.substringAfter(",").toInt()
        Point(x, y)
      }.associateWith { true }

      val foldingPoints = groups[true]!!.mapNotNull { line ->
        if (line.startsWith("fold along y=")) {
          val y = line.substringAfter("fold along y=").toInt()
          Pair(-1, y)
        } else if (line.startsWith("fold along x=")) {
          val x = line.substringAfter("fold along x=").toInt()
          Pair(x, -1)
        } else {
          null
        }
      }

      return Folding(map, foldingPoints)
    }
  }
}