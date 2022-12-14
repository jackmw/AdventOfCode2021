package com.jackmw.aoc2022

import com.jackmw.Utils
import java.util.Stack

fun main(args: Array<String>) {
  val day = Day14()
  day.part1("/2022/day14/part1-test.txt")
  day.part1("/2022/day14/part1-input.txt")

  day.part2("/2022/day14/part1-test.txt")
  day.part2("/2022/day14/part1-input.txt")
}

class Day14 {
  internal fun part1(fileName: String) {
    val rockFormations = parseFile(fileName)
    val gameMap = GameMap(rockFormations)
    gameMap.printMap()
    val sands = mutableListOf<Pair<Int, Int>>()
    while(true) {
      val sandLocation = gameMap.dropSand(500 to 0)
      if (sandLocation != null) {
        sands.add(sandLocation)
      } else {
        break
      }
    }
    gameMap.printMap()
    println("Part 1: After dropping ${sands.size} it comes to a stop.")
  }

  internal fun part2(fileName: String) {
    val rockFormations = parseFile(fileName)
    val gameMap = GameMap(rockFormations, floorModifier = 2)
    gameMap.printMap()
    val sands = mutableListOf<Pair<Int, Int>>()
    val ingress = 500 to 0
    while(gameMap.isOpen(ingress)) {
      val sandLocation = gameMap.dropSand(ingress)
      if (sandLocation != null) {
        sands.add(sandLocation)
      } else {
        break
      }
    }
    gameMap.printMap()
    println("Part 2: After dropping ${sands.size} the ingress is blocked.")
  }

  internal class GameMap(rockFormations: List<RockFormation>, floorModifier: Int? = null) {
    private val rockAndSandMap = mutableMapOf<Pair<Int, Int>, Char>()
    private val columns = mutableMapOf<Int, MutableList<Int>>()
    private val floor: Int?

    init {
      for (rockFormation in rockFormations) {
        for ((from, to) in rockFormation.paths.zipWithNext()) {
          val minX = minOf(from.first, to.first)
          val maxX = maxOf(from.first, to.first)
          val minY = minOf(from.second, to.second)
          val maxY = maxOf(from.second, to.second)
          for (x in minX .. maxX) {
            for (y in minY .. maxY) {
              rockAndSandMap[x to y] = '#'
              if (x !in columns) {
                columns[x] = mutableListOf()
              }
              columns[x]!!.add(y)
            }
          }
        }
      }
      floor = if(floorModifier == null) { null } else {
        rockAndSandMap.keys.maxOf { it.second } + floorModifier
      }
    }

    fun isOpen(point: Pair<Int, Int>): Boolean {
      return rockAndSandMap[point] == null
    }

    fun dropSand(at: Pair<Int, Int>): Pair<Int, Int>? {
      var current: Pair<Int, Int>? = at
      while (true) {
        if (current == null) {
          return null
        }
        current = if (canFallDown(current)) {
          fallDown(current)
        } else if (canFallLeft(current)) {
          (current.first - 1) to (current.second + 1)
        } else if (canFallRight(current)) {
          (current.first +1) to (current.second + 1)
        } else {
          break
        }
      }
      if (current != null) {
        if (rockAndSandMap[current] == null) {
          rockAndSandMap[current] = 'o'
          if (columns[current.first] == null) {
            columns[current.first] = mutableListOf()
          }
          columns[current.first]!!.add(current.second)
        } else {
          println("JACK_DEBUG: oops $current is already occupied.")
        }
      }
      return current
    }

    private fun canFallDown(current: Pair<Int, Int>): Boolean {
      if (floor != null && current.second + 1 == floor) {
        return false
      }
      return rockAndSandMap[current.first to current.second + 1] == null
    }

    private fun fallDown(current: Pair<Int, Int>): Pair<Int, Int>? {
      if (floor == null) {
        val column = columns[current.first] ?: return null
        val highestPointInColumn = column.filter { it > current.second }.minOrNull() ?: return null
        return Pair(current.first, highestPointInColumn - 1)
      } else {
        val column = columns[current.first]
        return if (column == null) {
          // fall to the floor
          (current.first to floor - 1)
        } else {
          val highestPointInColumn = column.filter { it > current.second }.minOrNull()
          if (highestPointInColumn == null) {
            (current.first to floor - 1)
          } else {
            (current.first to highestPointInColumn - 1)
          }
        }
      }
    }

    private fun canFallLeft(current: Pair<Int, Int>): Boolean {
      if (floor != null && current.second + 1 == floor) {
        return false
      }
      return rockAndSandMap[current.first - 1 to current.second + 1] == null
    }

    private fun canFallRight(current: Pair<Int, Int>): Boolean {
      if (floor != null && current.second + 1 == floor) {
        return false
      }
      return rockAndSandMap[current.first + 1 to current.second + 1] == null
    }

    fun printMap() {
      val minX = rockAndSandMap.keys.minOf { it.first }
      val maxX = rockAndSandMap.keys.maxOf { it.first }
      val minY = rockAndSandMap.keys.minOf { it.second }
      val maxY = rockAndSandMap.keys.maxOf { it.second }
      for (y in minY .. maxY) {
        for (x in minX .. maxX) {
          print(rockAndSandMap[x to y] ?: '.')
        }
        println()
      }
    }
  }

  internal data class RockFormation(
    val paths: List<Pair<Int, Int>>,
  )

  private fun parseFile(fileName: String): List<RockFormation> {
    return Utils.readFileAsLines(fileName)
      .map { line ->
        val pairs = line.split(" -> ").map { pair ->
          val x = pair.substringBefore(',').toInt()
          val y = pair.substringAfter(',').toInt()
          Pair(x, y)
        }
        RockFormation(paths = pairs)
      }
  }
}