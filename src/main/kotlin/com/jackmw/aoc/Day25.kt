package com.jackmw.aoc

import com.jackmw.aoc.Utils.Companion.readFileAsLines

fun main(args: Array<String>) {
  // example("/day25/part1-test.txt")
  part1("/day25/part1-test.txt")
  part1("/day25/part1-input.txt")
}

private fun example(fileName: String) {
  val seaCucumberMovements = SeaCucumberMovements.parse(readFileAsLines(fileName))
  println(seaCucumberMovements)
  repeat(5) {
    seaCucumberMovements.move()
    println("After ${it+1} move(s)")
    println(seaCucumberMovements)
    println()
  }
}

private fun part1(fileName: String) {
  val seaCucumberMovements = SeaCucumberMovements.parse(readFileAsLines(fileName))
  println(seaCucumberMovements)
  var moved = 0
  var steps = 0
  do {
    moved = seaCucumberMovements.move()
    if (moved == 0) break
    steps++
  } while (moved > 0)
  println("$fileName part 1 -> stops moving after $steps moves.")
}

private fun part2(fileName: String) {
  println("$fileName -> ${ Counter().part2(fileName)}")
}

data class Cucumber(
  val movesEast: Boolean,
) {

  val movesSouth = !movesEast
}
class SeaCucumberMovements(private val cucumbersMap: MutableMap<Point, Cucumber>) {
  private val rows = cucumbersMap.keys.maxOf { it.y } + 1
  private val columns = cucumbersMap.keys.maxOf { it.x } + 1

  override fun toString(): String {
    val sb = StringBuilder()
    for (y in 0 until rows) {
      for (x in 0 until columns) {
        val cucumber = cucumbersMap[Point(x, y)]
        val char = if (cucumber == null) {
          '.'
        } else if (cucumber.movesEast) {
          '>'
        } else {
          'v'
        }
        sb.append(char)
      }
      sb.append('\n')
    }
    return sb.toString()
  }

  fun move(): Int {
    val tempMap: MutableMap<Point, Cucumber> = mutableMapOf()
    tempMap.putAll(cucumbersMap)
    var moved = 0
    val eastMoveableCucumbers = getEastMoveableCucumbers(tempMap)
    moved += eastMoveableCucumbers.size
    eastMoveableCucumbers.forEach { (currentLocation, cucumber) ->
      val newLocation = getNextEastLocation(currentLocation)
      tempMap.remove(currentLocation)
      tempMap[newLocation] = cucumber
    }
    val southMoveableCucubmers = getSouthMoveableCucumbers(tempMap)
    moved += southMoveableCucubmers.size
    southMoveableCucubmers.forEach { (currentLocation, cucumber) ->
      val newLocation = getNextSouthLocation(currentLocation)
      tempMap.remove(currentLocation)
      tempMap[newLocation] = cucumber
    }
    cucumbersMap.clear()
    cucumbersMap.putAll(tempMap)
    return moved
  }

  private fun getEastMoveableCucumbers(map: MutableMap<Point, Cucumber>): Map<Point, Cucumber> {
    return map.filterValues { it.movesEast }
      .filter { isSpaceAvailableEastward(it.key, map) }
  }

  private fun getSouthMoveableCucumbers(map: MutableMap<Point, Cucumber>): Map<Point, Cucumber> {
    return map.filterValues { it.movesSouth }
      .filter { isSpaceAvailableSouthward(it.key, map) }
  }

  private fun getNextEastLocation(currentLocation: Point): Point {
    var newX = currentLocation.x + 1
    if (newX >= columns) newX -= columns
    return Point(newX, currentLocation.y)
  }

  private fun getNextSouthLocation(currentLocation: Point): Point {
    var newY = currentLocation.y + 1
    if (newY >= rows) newY -= rows
    return Point(currentLocation.x, newY)
  }

  private fun isSpaceAvailableEastward(currentLocation: Point, map: MutableMap<Point, Cucumber>): Boolean {
    return map[getNextEastLocation(currentLocation)] == null
  }

  private fun isSpaceAvailableSouthward(currentLocation: Point, map: MutableMap<Point, Cucumber>): Boolean {
    return map[getNextSouthLocation(currentLocation)] == null
  }

  companion object {
    fun parse(lines: List<String>): SeaCucumberMovements {
      val map: MutableMap<Point, Cucumber> = mutableMapOf()
      lines.forEachIndexed { y, line ->
        line.forEachIndexed { x, char ->
          if (char == 'v') {
            map[Point(x, y)] = Cucumber(false)
          } else if (char == '>') {
            map[Point(x, y)] = Cucumber(true)
          }
        }
      }
      return SeaCucumberMovements(map)
    }
  }
}