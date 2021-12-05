package com.jackmw.aoc

import com.jackmw.aoc.Utils.Companion.readFileAsLines

fun main() {
  part1("/day5/part1-test.txt")
  part1("/day5/part1-input.txt")

  part2("/day5/part1-test.txt")
  part2("/day5/part1-input.txt")
}

private fun part1(fileName: String) {
  val ventLines = VentLine.parseVentLines(readFileAsLines(fileName)).filter { it.isVertical() || it.isHorizontal() }
  val ventMap = VentMap(ventLines)
  ventMap.printMap()
  println("hotspots: ${ventMap.countHotspots(2)}")
}

private fun part2(fileName: String) {
  val ventLines = VentLine.parseVentLines(readFileAsLines(fileName))
  val ventMap = VentMap(ventLines)
  ventMap.printMap()
  println("hotspots: ${ventMap.countHotspots(2)}")
}

data class VentLine(
  private val fromPoint: Point,
  private val toPoint: Point,
) {
  fun isHorizontal(): Boolean {
    return fromPoint.x == toPoint.x
  }

  fun isVertical(): Boolean {
    return fromPoint.y == toPoint.y
  }

  override fun toString(): String {
    return "$fromPoint -> $toPoint"
  }

  fun getPoints(): List<Point> {
    val points = mutableListOf<Point>()
    val minX = minOf(fromPoint.x, toPoint.x)
    val minY = minOf(fromPoint.y, toPoint.y)
    val maxX = maxOf(fromPoint.x, toPoint.x)
    val maxY = maxOf(fromPoint.y, toPoint.y)
    val pointCount = maxOf(maxX - minX, maxY - minY) + 1
    val deltaX = if (maxX == minX) 0 else (toPoint.x - fromPoint.x) / (maxX - minX)
    val deltaY = if (minY == maxY) 0 else (toPoint.y - fromPoint.y) / (maxY - minY)
    for (i in 0 until pointCount) {
      points.add(Point(fromPoint.x + i * deltaX, fromPoint.y + i * deltaY))
    }
    return points
  }

  companion object {
    fun parseVentLines(inputs: List<String>): List<VentLine> {
      return inputs.mapNotNull { parseVentLine(it) }
    }

    private fun parseVentLine(input: String): VentLine? {
      val indexOfArrow = input.indexOf("->")
      val point1 = parsePoint(input.substring(0, indexOfArrow)) ?: return null
      val point2 = parsePoint(input.substring(indexOfArrow + 2)) ?: return null
      return VentLine(point1, point2)
    }

    private fun parsePoint(input: String): Point? {
      val pieces = input.trim().split(",").map { it.trim() }.filter { it.isNotEmpty() }
      if (pieces.size != 2) return null
      val x = pieces[0].toIntOrNull() ?: return null
      val y = pieces[1].toIntOrNull() ?: return null
      return Point(x, y)
    }
  }
}

data class Point(
  val x: Int,
  val y: Int,
) {
  override fun toString(): String {
    return "$x,$y"
  }
}

class VentMap(ventLines: List<VentLine>) {
  private val heatMap: Map<Point, Int>
  private val width: Int
  private val height: Int

  init {
    val points = ventLines.flatMap { it.getPoints() }
    val map = mutableMapOf<Point, Int>()
    points.forEach { point -> map.merge(point, 1, Int::plus) }
    heatMap = map.toMap()
    width = points.maxOf { it.x } + 1
    height = points.maxOf { it.y } + 1
  }

  fun printMap() {
    if (width > 20) {
      println("Map too large")
      return
    }
    for (y in 0 until height) {
      for (x in 0 until width) {
        val value = heatMap[Point(x, y)]
        print("${value ?: '.'} ")
      }
      println()
    }
  }

  fun countHotspots(minHeat: Int): Int {
    return heatMap.values.count { it >= minHeat }
  }
}