package com.jackmw.aoc2022

import com.jackmw.Utils
import kotlin.math.min
import kotlin.math.sign

fun main(args: Array<String>) {
  val day = Day18()
  day.part1("/2022/day18/part1-test.txt")
  day.part1("/2022/day18/part1-input.txt")

  day.part2("/2022/day18/part1-test.txt")
  day.part2("/2022/day18/part1-input.txt")
}

private class Day18 {
  internal fun part1(fileName: String) {
    val coords = parseFile(fileName)
    val totalSurfaceArea = countOpenSurfaces(coords.toSet())
    println("Part 1: $fileName, the total surface area is $totalSurfaceArea.")
  }

  internal fun part2(fileName: String) {
    val coords = parseFile(fileName)
    val totalSurfaceArea = countExteriorSurfaces(coords.toSet())
    println("Part 2: $fileName, the total surface area is $totalSurfaceArea.")
  }

  private fun countOpenSurfaces(coords: Set<Coord3D>): Int {
    return coords.sumOf { coord -> coord.neighbours.count { neighbour -> neighbour !in coords } }
  }

  private fun countExteriorSurfaces(coords: Set<Coord3D>): Int {
    val xRange = coords.minOf { it.x } - 1..coords.maxOf { it.x } + 1
    val yRange = coords.minOf { it.y } - 1..coords.maxOf { it.y } + 1
    val zRange = coords.minOf { it.z } - 1..coords.maxOf { it.z } + 1

    val queue = mutableListOf<Coord3D>()
    val checked = mutableSetOf<Coord3D>()
    var sidesFound = 0

    queue.add(Coord3D(xRange.first, yRange.first, zRange.first))
    while (queue.isNotEmpty()) {
      val current = queue.removeFirst()
      if (current in checked) {
        continue
      }
      val viableNeighbours = current.neighbours
        .asSequence()
        .filter { it.x in xRange && it.y in yRange && it.z in zRange }
        .toSet()
      for (neighbour in viableNeighbours) {
        checked.add(current)
        if (neighbour in coords) {
          sidesFound++
        } else {
          queue.add(neighbour)
        }
      }
    }
    return sidesFound
  }

  private data class Coord3D(
    val x: Int,
    val y: Int,
    val z: Int,
  ) {
    operator fun plus(other: Coord3D): Coord3D {
      return Coord3D(x = x + other.x, y = y + other.y, z = z + other.z)
    }

    val neighbours: Set<Coord3D>
      get() = NEIGHBOUR_OFFSETS.map { it + this }.toSet()


    companion object {
      private val NEIGHBOUR_OFFSETS = setOf(
      Coord3D(1, 0, 0),
        Coord3D(-1, 0, 0),
        Coord3D(0, 1, 0),
        Coord3D(0, -1, 0),
        Coord3D(0, 0, 1),
        Coord3D(0, 0,-1),
      )
    }
  }

  private fun parseFile(fileName: String): List<Coord3D> {
    return Utils.readFileAsLines(fileName)
      .map { line ->
        val pieces = line.split(',')
        Coord3D(x = pieces[0].toInt(), y = pieces[1].toInt(), z = pieces[2].toInt())
      }
  }
}
