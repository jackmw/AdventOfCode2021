package com.jackmw.aoc

import com.jackmw.aoc.Utils.Companion.readFileAsLines

fun main() {
  part1("/day20/part1-test.txt")
  part1("/day20/part1-input.txt")

  part2("/day20/part1-test.txt")
  part2("/day20/part1-input.txt")

}

private fun part1(fileName: String) {
  val (image, algo) = ImageEnhancer.parse(readFileAsLines(fileName))
  val imageEnhancer = ImageEnhancer()
  imageEnhancer.printImage(image)
  println()
  val onceEnhanced = imageEnhancer.enhance(image, algo, paddingActive = false)
  imageEnhancer.printImage(onceEnhanced, padding = 2)
  println()
  val twiceEnhanced = imageEnhancer.enhance(onceEnhanced, algo, paddingActive = true)
  imageEnhancer.printImage(twiceEnhanced, padding = 2)
  println()
  val activePixelCount = twiceEnhanced.values.count { it }
  println("Part 1 $fileName -> active pixels after 2 enhancements: $activePixelCount")
}

private fun part2(fileName: String) {
  val (image, algo) = ImageEnhancer.parse(readFileAsLines(fileName))
  val imageEnhancer = ImageEnhancer()

  val alternatePadding = algo[0] && !algo[511]
  var enhanced: Map<Coord<Int>, Boolean> = image
  var paddingActive = false
  for (enhancement in 1 .. 25) {
    enhanced = imageEnhancer.enhance(enhanced, algo, paddingActive = paddingActive)
    paddingActive = if (alternatePadding) !paddingActive else paddingActive
    enhanced = imageEnhancer.enhance(enhanced, algo, paddingActive = paddingActive)
    paddingActive =if (alternatePadding) !paddingActive else paddingActive
  }
  val activePixelCount = enhanced.values.count { it }
  imageEnhancer.printImage(enhanced, 2)
  println("Part 2 $fileName -> active pixels after 50 enhancements: $activePixelCount")
}


data class Coord<T>(
  val row: T,
  val column: T,
)
private class ImageEnhancer {

  fun enhance(image: Map<Coord<Int>, Boolean>, algorithm: BooleanArray, paddingActive: Boolean = false): Map<Coord<Int>, Boolean> {
    val minRow = image.keys.minOfOrNull { it.row }!!
    val maxRow = image.keys.maxOfOrNull { it.row }!!
    val minColumn = image.keys.minOfOrNull { it.column }!!
    val maxColumn = image.keys.maxOfOrNull { it.column }!!

    val padding = 2
    val enhanced = mutableMapOf<Coord<Int>, Boolean>()
    for (row in minRow - padding .. maxRow + padding) {
      for (column in minColumn - padding..maxColumn + padding) {
        val coord = Coord(row, column)
        val neighbours = getNeighbouringCoords(coord)
        val algoIndex = getAlgoIndex(
          neighbours.map {
            val active = if (it.row < minRow || it.row > maxRow || it.column < minColumn || it.column > maxColumn) {
              paddingActive
            } else {
              image[it] == true
            }
            if (active) '1' else '0'
          }
        )
        val algoValue = algorithm[algoIndex]
        if (algoValue) {
          enhanced[coord] = algoValue
        }
      }
    }
    return enhanced
  }

  fun printImage(image: Map<Coord<Int>, Boolean>, padding: Int = 0) {
    val minRow = image.keys.minOfOrNull { it.row }!! - padding
    val maxRow = image.keys.maxOfOrNull { it.row }!! + padding
    val minColumn = image.keys.minOfOrNull { it.column }!! - padding
    val maxColumn = image.keys.maxOfOrNull { it.column }!! + padding

    for (row in minRow .. maxRow) {
      for (column in minColumn .. maxColumn) {
        val coord = Coord(row, column)
        val active = image[coord] == true
        print(if (active) '█' else ' ')
      }
      println()
    }
  }


  private fun getAlgoIndex(binChars: List<Char>): Int {
    val withZero = binChars.toMutableList()
    withZero.add(0, '0')
    return String(withZero.toCharArray()).toInt(2)
  }

  private fun getNeighbouringCoords(coord: Coord<Int>): List<Coord<Int>> {
    return getNeighbouringCoords(coord.row, coord.column)
  }

  private fun getNeighbouringCoords(row: Int, column: Int): List<Coord<Int>> {
    val coords = mutableListOf<Coord<Int>>()
    for (dRow in -1 .. 1) {
      for (dColumn in -1 .. 1) {
        coords.add(Coord(row = row + dRow, column = column + dColumn))
      }
    }
    return coords
  }

  companion object {
    fun parse(lines: List<String>): Pair<Map<Coord<Int>, Boolean>, BooleanArray> {
      val algorithm = parseAlgorithm(lines.first())
      val image = parseImage(lines.drop(2))
      return image to algorithm
    }

    private fun parseImage(lines: List<String>): Map<Coord<Int>, Boolean> {
      val image = mutableMapOf<Coord<Int>, Boolean>()
      lines.forEachIndexed { row, line ->
        line.forEachIndexed { column, char ->
          if (char == '#') {
            image[Coord(row, column)] = true
          }
        }
      }
      return image
    }

    private fun parseAlgorithm(line: String): BooleanArray {
      return line.map { char ->
        when (char) {
          '.' -> false
          '#' -> true
          else -> throw IllegalArgumentException()
        }
      }.toBooleanArray()
    }
  }
}