package com.jackmw.aoc2022

import com.jackmw.Utils
import java.util.LinkedList
import kotlin.math.ceil

fun main(args: Array<String>) {
  val day = Day19()
//  day.part1("/2022/day19/part1-test.txt", 12)
  day.part1("/2022/day19/part1-test.txt", 24)
//  day.part1("/2022/day19/part1-test.txt", 24)
  // day.part1("/2022/day19/part1-input.txt")
}

internal class Day19 {

  enum class Resource {
    ORE,
    CLAY,
    OBSIDIAN,
    GEODE,
  }

  internal fun part1(fileName: String, time: Int = 24) {
    val blueprints = parseFile(fileName)
    var mostGeodes = 0
    var bestBlueprint: Blueprint = blueprints.first()
    for (blueprint in blueprints) {
      val run = Run(blueprint)
      val geodes = run.getMostNumberOfGeodes(RunState(oreRobots = 1, remainingTime = time))
      println("blueprint ${blueprint.blueprintNumber} can makes $geodes geodes in $time minutes.")

      if (geodes > mostGeodes) {
        mostGeodes = geodes
        bestBlueprint = blueprint
      }
    }
    println("Part 1: the most geodes we can harvest is $mostGeodes, using blueprint ${bestBlueprint.blueprintNumber}")
  }

  internal data class RunState(
  val ores: Int  = 0,
  val clays: Int = 0,
  val obsidians: Int = 0,
  val geodes: Int = 0,
  val oreRobots: Int = 0,
  val clayRobots: Int = 0,
  val obsidianRobots: Int = 0,
  val geodeRobots: Int =0,
    val remainingTime: Int,
  )

  internal data class Run(
    val blueprint: Blueprint,
  ) {

    fun getMostNumberOfGeodes(initialState: RunState): Int {
      val runStates = mutableListOf(initialState)
      val processedStates = LinkedList<RunState>()
      var mostGeodes = 0
      var stateCount = 0
      while (runStates.isNotEmpty()) {
        val currentState = runStates.removeFirst()
        mostGeodes = maxOf(currentState.geodes, mostGeodes)
        stateCount++
        if (stateCount % 10_000 == 0) {
          println("JACK_DEBUG Blueprint ${blueprint.blueprintNumber}: current max geode is $mostGeodes after processing $stateCount states, ${runStates.size} states remaining.")
        }

        val futureStates = currentState.getFutureRunStates()
        for (futureRunState in futureStates) {
          if (futureRunState !in processedStates) {
            runStates.add(futureRunState)
            processedStates.add(futureRunState)
          }
        }
      }
      return mostGeodes
    }

    private fun RunState.getFutureRunStates(): List<RunState> {
      return Resource.values().mapNotNull { robotType ->
        getRunStateIfBuildNow(robotType) ?: saveResourceForNewRobot(robotType)
      }
    }

    private fun RunState.getRunStateIfBuildNow(robotType: Resource): RunState? {
      // Don't need to build robots on the last turn, it won't change our final resource count.
      if (remainingTime <= 1) {
        return null
      }
      val robotCost = blueprint.costs[robotType]!!
      if (ores < robotCost.ores) {
        return null
      }
      if (clays < robotCost.clays) {
        return null
      }
      if (obsidians < robotCost.obsidians) {
        return null
      }
      return RunState(
        ores = ores - robotCost.ores + oreRobots,
        clays = clays - robotCost.clays + clayRobots,
        obsidians = obsidians - robotCost.obsidians + obsidianRobots,
        geodes = geodes + geodeRobots,
        oreRobots = oreRobots + (if (robotType == Resource.ORE) 1 else 0),
        clayRobots = clayRobots + (if (robotType == Resource.CLAY) 1 else 0),
        obsidianRobots = obsidianRobots + (if (robotType == Resource.OBSIDIAN) 1 else 0),
        geodeRobots = geodeRobots + (if (robotType == Resource.GEODE) 1 else 0),
        remainingTime = remainingTime - 1,
      )
    }

    private fun RunState.saveResourceForNewRobot(robotType: Resource): RunState? {
      val robotCost = blueprint.costs[robotType]!!
      if (robotCost.ores > 0 && oreRobots == 0) {
        return null
      }
      if (robotCost.clays > 0 && clayRobots == 0) {
        return null
      }
      if (robotCost.obsidians > 0 && obsidianRobots == 0) {
        return null
      }
      val saveForDays = maxOf(
        ((robotCost.obsidians - obsidians) * 1.0 / obsidianRobots + 0.5).toInt(),
        ((robotCost.clays - clays) * 1.0 / clayRobots + 0.5).toInt(),
        ((robotCost.ores - ores) * 1.0 / oreRobots + 0.5).toInt(),
      )
      // save for N days and then 1 day to build
      if (saveForDays + 1 >= remainingTime) {
        return null
      }
      return copy(
        ores = ores + oreRobots * saveForDays,
        clays = clays + clayRobots * saveForDays,
        obsidians = obsidians + obsidianRobots * saveForDays,
        geodes = geodes + geodeRobots * saveForDays,
        remainingTime = remainingTime - saveForDays,
      )
    }
  }

  internal fun part2(fileName: String) {
  }

  internal data class Blueprint(
    val blueprintNumber:Int,
    val costs: Map<Resource, RobotCost>,
  )
  
  private fun parseFile(fileName: String): List<Blueprint> {
    return Utils.readFileAsLines(fileName)
      .map { parseBlueprint(it) }
  }

  private fun parseBlueprint(line: String): Blueprint {
    val blueprintNumber = line.substringBefore(":").substringAfter("Blueprint ").toInt()
    val robotCosts = parseRobotCosts(line.substringAfter(": "))
    return Blueprint(blueprintNumber, robotCosts)
  }

  data class RobotCost(
    val ores: Int,
    val clays: Int = 0,
    val obsidians: Int = 0,
  )

  private fun parseRobotCosts(line: String): Map<Resource, RobotCost> {
    val map = mutableMapOf<Resource, RobotCost>()
    val oreRobotMatchResult = oreRobotCost.find(line)!!
    map[Resource.ORE] = RobotCost(ores = oreRobotMatchResult.groupValues[1].toInt())
    val clayRobotMatchResult = clayRobotCost.find(line)!!
    map[Resource.CLAY] = RobotCost(ores = clayRobotMatchResult.groupValues[1].toInt())
    val obsidianRobotMatchResult = obsidianRobotCost.find(line)!!
    map[Resource.OBSIDIAN] = RobotCost(
      ores = obsidianRobotMatchResult.groupValues[1].toInt(),
      clays= obsidianRobotMatchResult.groupValues[2].toInt(),
    )
    val geodeRobotMatchResult = geodeRobotCost.find(line)!!
    map[Resource.GEODE] = RobotCost(
      ores =geodeRobotMatchResult.groupValues[1].toInt(),
      obsidians = geodeRobotMatchResult.groupValues[2].toInt(),
    )
    return map
  }

  companion object {
    val oreRobotCost = """Each ore robot costs (\d+) ore""".toRegex()
    val clayRobotCost = """Each clay robot costs (\d+) ore""".toRegex()
    val obsidianRobotCost = """Each obsidian robot costs (\d+) ore and (\d+) clay""".toRegex()
    val geodeRobotCost = """Each geode robot costs (\d+) ore and (\d+) obsidian""".toRegex()
  }
}
