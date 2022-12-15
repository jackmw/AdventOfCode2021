package com.jackmw.aoc2022

import com.jackmw.Utils
import com.jackmw.aoc2021.clamp
import java.util.Stack
import kotlin.math.absoluteValue

fun main(args: Array<String>) {
  val day = Day15()
  day.part1("/2022/day15/part1-test.txt", drawMap= true, line = 10)
  day.part1("/2022/day15/part1-input.txt",  drawMap= false, line = 2_000_000, onlyDrawBeaconForLine = true)

  day.part2(
    fileName = "/2022/day15/part1-test.txt",
    drawMap = true,
    xRange = 0..20,
    yRange = 0..20,
  )
  day.part2(
    fileName = "/2022/day15/part1-input.txt",
    drawMap = false,
    xRange = 0..4_000_000,
    yRange = 0..4_000_000,
  )
}

class Day15 {
  internal fun part1(fileName: String, drawMap: Boolean = true, line: Int, onlyDrawBeaconForLine: Boolean = false) {
    val sensorAndBeacons = parseFile(fileName)
    val gameMap = GameMap(sensorAndBeacons)
    if (drawMap) {
      gameMap.printMap()
    }
    println("Finished parsing map, there are ${sensorAndBeacons.size} sensors.")
    gameMap.drawBeaconCoverages(line = if (onlyDrawBeaconForLine) line else null)
    println("Finished drawing coverages")

    if (drawMap) {
      println("\nWith coverage")
      gameMap.printMap()
      println()
    }

    val knownNegatives = gameMap.count('#', line)
    println("Part 1: $fileName in the row where y=$line, there are $knownNegatives positions where a beacon cannot be present.")
  }

  internal fun part2(fileName: String, drawMap: Boolean = true, xRange: IntRange, yRange: IntRange = xRange) {
    val sensorAndBeacons = parseFile(fileName)
    val gameMap = GameMap(sensorAndBeacons)
    if (drawMap) {
      gameMap.printMap()
    }
    val stressBeacon = gameMap.findStressBeacon(xRange = xRange, yRange = yRange)
    val tuningFrequency = stressBeacon?.let { it.x * 4_000_000L + it.y }
    println("Part 2: $fileName found stress beacon at ($stressBeacon) tuning frequency=$tuningFrequency within xRange=$xRange, yRange=$yRange")
  }

  internal class GameMap(private val sensorAndBeacons: List<SensorAndBeacon>) {
    private val map = mutableMapOf<Coordinate, Char>()

    init {
      for ((sensor, beacon) in sensorAndBeacons) {
        map[sensor] = 'S'
        map[beacon] = 'B'
      }
    }

    fun drawBeaconCoverages(line: Int? = null) {
      for (sensorAndBeacon in sensorAndBeacons) {
        if (line == null || sensorAndBeacon.coverageAffects(line)) {
          sensorAndBeacon.drawCoverageMap(map, onlyY = line)
        } else {
          println("Skipping beacon coverage for beacon(${sensorAndBeacon.beaconLocation}) since it is doesn't affect y=$line")
        }
      }
    }

    fun printMap() {
      val minX = map.keys.minOf { it.x }
      val maxX = map.keys.maxOf { it.x }
      val minY = map.keys.minOf { it.y }
      val maxY = map.keys.maxOf { it.y }
      for (y in minY..maxY) {
        for (x in minX..maxX) {
          print(map[Coordinate(x, y)] ?: '.')
        }
        println()
      }
    }

    fun count(c: Char, y: Int): Int {
      return map.count { it.key.y == y && it.value == c }
    }

    fun findStressBeacon(xRange: IntRange, yRange: IntRange): Coordinate? {
      val relevantSensors = sensorAndBeacons.filter { it.coversArea(xRange, yRange) }
      println("JACK_DEBUG looking at ${relevantSensors.size} relevant sensors.")
      for (y in yRange) {
        val xStart: Int? = maybeGetSecondIntRangeStart(relevantSensors.mapNotNull { it.getCoverageOnY(y, xRange) })
        if (xStart != null) {
          return Coordinate(x = xStart - 1, y = y)
        }
      }
      return null
    }

    private fun maybeGetSecondIntRangeStart(ranges: List<IntRange>): Int? {
      val deltas = ranges.flatMap { listOf(it.first to true, it.last to false) }.sortedWith { p1, p2 ->
        if (p1.first == p2.first) {
          if (p1.second) -1 else 1
        } else {
          p1.first - p2.first
        }
      }
      var numberOfStarts = 0
      var numberOfEnds = 0
      var stopAtNextNumber = false
      for ((number, isStart) in deltas) {
        if (stopAtNextNumber) {
          return number
        }
        if (isStart) numberOfStarts++ else numberOfEnds++
        if (numberOfStarts > 0 && numberOfStarts == numberOfEnds) {
          stopAtNextNumber = true
        }
      }
      return null
    }
  }

  internal data class Coordinate(
    val x: Int,
    val y: Int,
  ) {
    fun manhattanDistanceTo(another: Coordinate): Int {
      return (x - another.x).absoluteValue + (y - another.y).absoluteValue
    }

    override fun toString(): String {
      return "$x, $y"
    }
  }

  internal data class SensorAndBeacon(
    val sensorLocation: Coordinate,
    val beaconLocation: Coordinate,
  ) {
    private val manhattanDistance = sensorLocation.manhattanDistanceTo(beaconLocation)

    fun drawCoverageMap(map: MutableMap<Coordinate, Char>, onlyY: Int? = null) {
      println("The manhattan distance for sensor($sensorLocation) to beacon($beaconLocation) is $manhattanDistance")
      val yRange = if (onlyY != null)  onlyY .. onlyY else coverageYRange
      for (y in yRange) {
        for (x in coverageXRange) {
          val coordinate = Coordinate(x = x, y = y)
          if (coordinate.manhattanDistanceTo(sensorLocation) <= manhattanDistance) {
            if (coordinate !in map) {
              map[coordinate] = '#'
            }
          }
        }
      }
    }

    fun coverageAffects(line: Int): Boolean {
      return line in sensorLocation.y - manhattanDistance..sensorLocation.y + manhattanDistance
    }

    fun covers(coordinate: Coordinate): Boolean {
      return coordinate.manhattanDistanceTo(sensorLocation) <= manhattanDistance
    }

    fun coversArea(xRange: IntRange, yRange: IntRange): Boolean {
      if (coverageXRange.last < xRange.first) {
        return false
      }
      if (coverageXRange.first > xRange.last) {
        return false
      }
      if (coverageYRange.last < yRange.first) {
        return false
      }
      if (coverageYRange.first > yRange.last) {
        return false
      }
      return true
    }

    fun getCoverageOnY(y: Int, xRange: IntRange): IntRange? {
      if (y !in coverageYRange) {
        return null
      }

      val perSide = manhattanDistance - (sensorLocation.y - y).absoluteValue
      return xRange.clamp((sensorLocation.x - perSide) .. (sensorLocation.x + perSide))
    }

    private val coverageXRange = sensorLocation.x - manhattanDistance .. sensorLocation.x + manhattanDistance
    private val coverageYRange = sensorLocation.y - manhattanDistance .. sensorLocation.y + manhattanDistance
  }

  private fun parseFile(fileName: String): List<SensorAndBeacon> {
    val pattern = """Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)""".toRegex()
    return Utils.readFileAsLines(fileName)
      .map { line ->
        val match = pattern.find(line) ?: throw IllegalArgumentException("no match $line")
        val sensorCoordinate = Coordinate(match.groupValues[1].toInt(), match.groupValues[2].toInt())
        val beaconCoordinate = Coordinate(match.groupValues[3].toInt(), match.groupValues[4].toInt())
        SensorAndBeacon(sensorLocation = sensorCoordinate, beaconLocation = beaconCoordinate)
      }
  }
}
