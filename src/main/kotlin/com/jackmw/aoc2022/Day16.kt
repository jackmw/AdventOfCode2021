package com.jackmw.aoc2022

import com.jackmw.Utils

fun main(args: Array<String>) {
  val day = Day16()
  day.part1("/2022/day16/part1-test.txt")
}

class Day16 {
  internal fun part1(fileName: String) {
    val valves = parseFile(fileName)
    for (valve in valves) {
      println(valve)
    }
    val gameMap = GameMap(valves)
    val bestRoute = gameMap.findBestRoute(steps = 30, startAt = "AA")
    println(bestRoute)
    gameMap.printRoute(bestRoute, 30)
  }

  internal data class Edge(
    val fromValve: Valve,
    val toValve: Valve,
  ) {
    val isOpening = fromValve == toValve

    override fun toString(): String {
      return if (isOpening) {
         "Openning ${fromValve.name}"
      } else {
        "${fromValve.name} to ${toValve.name}"
      }
    }
  }

  internal class GameMap(private val valves: List<Valve>) {
    private val valveMap = valves.associateBy { it.name }

    private fun getEdges(valve: Valve): List<Edge> {
      val edges = mutableListOf<Edge>()
      if (valve.flowRate > 0) {
        edges.add(Edge(valveMap[valve.name]!!, valveMap[valve.name]!!))
      }
      valve.neighbours.mapTo(edges) { neighbourValve -> Edge(valveMap[valve.name]!!, valveMap[neighbourValve]!!) }
      return edges
    }

    fun findBestRoute(steps: Int, startAt: String="AA"): List<Edge> {
      val startingEdges = getEdges(valveMap[startAt]!!)
      var bestScore: Long = 0
      var bestSteps: List<Edge> = listOf()
      for (startingEdge in startingEdges) {
        val (score, routeStep) = findBestRoute(steps, startingEdge)
        if (score < bestScore) {
          bestScore = score
          bestSteps = routeStep
        }
      }
      return bestSteps
    }

    private fun findBestRoute(steps: Int, startingEdge: Edge): Pair<Long, List<Edge>> {
      var remainingSteps = steps
      val distance = mutableMapOf<Edge, Long>()
      val previous = mutableMapOf<Edge, Edge?>()
      val unvisited = mutableSetOf<Edge>()
      val visited = mutableSetOf<Edge>()
      val score = mutableMapOf<Edge, Long>()

      val edges = valves.flatMap { getEdges(it) }.toSet()
      for (edge in edges) {
        distance[edge] = Long.MAX_VALUE
        previous[edge] = null
        unvisited.add(edge)
      }
      val opennedValves = mutableMapOf<Valve, Int>()
      distance[startingEdge] = 0

      while (unvisited.isNotEmpty()) {
        val currentEdge = unvisited.minBy { distance[it]!! }
        unvisited.remove(currentEdge)
        val distanceToMe = distance[currentEdge]!!
        if (currentEdge.isOpening) {
          opennedValves[currentEdge.fromValve] = remainingSteps - 1
        }
        score[currentEdge] = calculateScore(opennedValves)
        remainingSteps--

        val possibleEdges = getEdges(currentEdge.toValve).intersect(unvisited)
        for (edge in possibleEdges) {
          val distanceToNeighbour = distance[edge]!!
          val newDistance = distanceToMe + 1
          if (newDistance < distanceToNeighbour) {
            distance[edge] = newDistance
            previous[edge] = currentEdge
          }
        }
        1 + 1
      }
      val lastEdge = score.maxBy { it.value }
      val lastStep = lastEdge.key
      val allSteps = mutableListOf(lastStep)
      var step: Edge? = previous[lastStep]
      while (step != null) {
        allSteps.add(0, step)
        step = previous[step]
      }
      return lastEdge.value to allSteps
    }

    private fun calculateScore(opennedValves: Map<Valve, Int>): Long {
      return opennedValves.asSequence()
        .map { (valve, openSteps) -> valve.flowRate * openSteps }
        .sum()
    }

    fun printRoute(edges: List<Edge>, steps: Int) {
      val openValves = mutableSetOf<Valve>()
      var currentEdge: Edge?
      var totalPressureReleased: Long = 0
      for (step in 1 .. steps) {
        println("== Minute $step ==")
        if (openValves.isEmpty()) {
          println("No valves are open.")
        } else {
          val pressureReleased = openValves.sumOf { it.flowRate }
          totalPressureReleased += pressureReleased
          if (openValves.size == 1) {
            println("Valve ${openValves.single().name} is open, releasing $pressureReleased pressure.")

          } else {
            val valveNames = openValves.joinToString(", ") { it.name }
            println("Valves $valveNames are open, releasing $pressureReleased pressure.")
          }
        }
        currentEdge = edges.getOrNull(step - 1)
        if (currentEdge != null) {
          if (currentEdge.isOpening) {
            println("You open valve ${currentEdge.fromValve.name}.")
            openValves.add(currentEdge.fromValve)
          } else {
            println("You move to valve ${currentEdge.toValve.name}.")
          }
        }
        println()
      }
      println("Released $totalPressureReleased in $steps minutes.")
    }
  }



  internal fun part2(fileName: String) {
  }


  internal data class Valve(
    val name: String,
    val flowRate: Long,
    val neighbours: Set<String>,
  ) {
    override fun toString(): String {
      return "Valve $name has flow rate=$flowRate; tunnels lead to valves ${neighbours.joinToString(", ")}"
    }
  }

  private fun parseFile(fileName: String): List<Valve> {
    val pattern = """Valve ([A-Z]+) has flow rate=(\d+); tunnels? leads? to valves? (.*)""".toRegex()
    return Utils.readFileAsLines(fileName)
      .map { line ->
        val match = pattern.find(line) ?: throw IllegalArgumentException("no match $line")
        val roomName = match.groupValues[1]
        val flowRate = match.groupValues[2].toLong()
        val neighbours = match.groupValues[3].trim().split(',').map { it.trim() }.toSet()
        Valve(name = roomName, flowRate = flowRate, neighbours = neighbours)
      }
  }
}
