package com.jackmw.aoc2022

import com.jackmw.Utils
import kotlin.math.min
import kotlin.math.sign

fun main(args: Array<String>) {
  val day = Day19()
  day.part1("/2022/day19/part1-test.txt")
  day.part1("/2022/day19/part1-input.txt")
}

internal class Day19 {
  internal fun part1(fileName: String) {
    val blueprints = parseFile(fileName)
    getMostNumberOfGeodes(blueprint = blueprints.first(), minutes = 24)
  }

  private fun getMostNumberOfGeodes(
    blueprint: Blueprint,
    minutes: Int,
    startingRobots: Map<Resource, Int> = mapOf(Resource.ORE to 1),
  ): Int {
    val activeRobots: MutableMap<Resource, Int> = mutableMapOf()
    val resources: MutableMap<Resource, Int> = mutableMapOf()
    activeRobots += startingRobots
    for (minute in 1 .. minutes) {
      val newRobot = buildRobots(blueprint.costs)
      resources += activeRobots
      if (newRobot != null) {
        activeRobots.compute(newRobot) { _, existingCount -> 1 + (existingCount ?: 0) }
      }
    }
  }

  private fun harvestResources(
    activeRobots: MutableMap<Resource, Int>,
    resources: MutableMap<Resource, Int>
  ) {
    TODO("Not yet implemented")
  }

  private fun buildRobots(costs: Map<Day19.Resource, Day19.RobotCost>): Resource? {
  }

  internal fun part2(fileName: String) {
  }

  enum class Resource {
    ORE,
    CLAY,
    OBSIDIAN,
    GEODE,
  }

  internal data class Blueprint(
    val blueprintNumber:Int,
    val costs: Map<Resource, RobotCost>,
  )

  internal data class RobotCost(
    val map: Map<Resource, Int>
  ) {
    val ores = map[Resource.ORE] ?: 0
    val clays = map[Resource.CLAY] ?: 0
    val obsidians = map[Resource.OBSIDIAN] ?: 0
  }

  private fun parseFile(fileName: String): List<Blueprint> {
    return Utils.readFileAsLines(fileName)
      .map { parseBlueprint(it) }
  }

  private fun parseBlueprint(line: String): Blueprint {
    val blueprintNumber = line.substringBefore(":").substringAfter("Blueprint ").toInt()
    val robotCosts = parseRobotCosts(line.substringAfter(": "))
    return Blueprint(blueprintNumber, robotCosts)
  }

  private fun parseRobotCosts(line: String): Map<Resource, RobotCost> {
    val map = mutableMapOf<Resource, RobotCost>()
    val oreRobotMatchResult = oreRobotCost.find(line)!!
    map[Resource.ORE] = RobotCost(mapOf(Resource.ORE to oreRobotMatchResult.groupValues[1].toInt()))
    val clayRobotMatchResult = clayRobotCost.find(line)!!
    map[Resource.CLAY] = RobotCost(mapOf(Resource.ORE to clayRobotMatchResult.groupValues[1].toInt()))
    val obsidianRobotMatchResult = obsidianRobotCost.find(line)!!
    map[Resource.OBSIDIAN] = RobotCost(
      mapOf(
        Resource.ORE to obsidianRobotMatchResult.groupValues[1].toInt(),
        Resource.CLAY to obsidianRobotMatchResult.groupValues[2].toInt(),
      )
    )
    val geodeRobotMatchResult = geodeCosts.find(line)!!
    map[Resource.GEODE] = RobotCost(
      mapOf(
        Resource.ORE to geodeRobotMatchResult.groupValues[1].toInt(),
        Resource.OBSIDIAN to obsidianRobotMatchResult.groupValues[2].toInt(),
      )
    )
    return map
  }

  companion object {
    val oreRobotCost = """Each ore robot costs (\d+) ore""".toRegex()
    val clayRobotCost = """Each clay robot costs (\d+) ore""".toRegex()
    val obsidianRobotCost = """Each obsidian robot costs (\d+) ore and (\d+) clay""".toRegex()
    val geodeCosts = """Each geode robot costs (\d+) ore and (\d+) obsidian""".toRegex()
  }
}
