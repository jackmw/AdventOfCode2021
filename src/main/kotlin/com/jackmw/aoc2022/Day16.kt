package com.jackmw.aoc2022

import com.jackmw.Utils
import kotlin.math.sign

fun main(args: Array<String>) {
  val day = Day16()
  day.part1("/2022/day16/part1-test.txt")
  day.part1("/2022/day16/part1-input.txt")

  day.part2("/2022/day16/part1-test.txt")
  day.part2("/2022/day16/part1-input.txt")
}

internal class Day16 {
  internal fun part1(fileName: String) {
    val valves = parseFile(fileName)
    val gameMap = GameMap(valves)
    // val bestRoute = gameMap.findBestPathForPressureDfs(30, "AA")
    val mostPressureReleased = gameMap.findPaths("AA", 30)
    println("most pressure released is $mostPressureReleased")
  }

  internal fun part2(fileName: String) {
    val valves = parseFile(fileName)
    val gameMap = GameMap(valves)
    val mostPressureReleased = gameMap.findPaths2("AA", elephantFrom = "AA", 30)
    println("part2: most pressure released is $mostPressureReleased")
  }

  internal data class Edge(
    val fromValve: Valve,
    val toValve: Valve,
    val fromIsOpen: Boolean = false,
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
    private val knownPaths = mutableMapOf<Pair<String, String>, List<String>>()
    private val operableValves = valves.filter { it.flowRate > 0 }.map { it.name }.toSet()

    private fun <T> Set<T>.allPairs(): List<Pair<T, T>> {
      val permutations = mutableListOf<Pair<T, T>>()
      for (first in this) {
        for (second in this) {
          if (first != second) {
            permutations.add(Pair(first, second))
          }
        }
      }
      return permutations
    }
    fun findPaths2(
      myFrom: String?,
      elephantFrom: String?,
      totalSteps: Int,
      myVisited: Set<String> = setOf(),
      elephantVisited: Set<String> = setOf(),
      myStepsSoFar: Int = 0,
      elephantStepsSoFar: Int = 0,
      releasedPressure: Long = 0,
    ): Long {
      if (myFrom == null && elephantFrom == null) {
        throw IllegalStateException("don't know how we got here..")
      }
      else if (myFrom == null && elephantFrom != null) {
        return findPaths(
          elephantFrom,
          totalSteps = totalSteps,
          visited = elephantVisited + myVisited,
          stepsSoFar = elephantStepsSoFar,
          releasedPressure = releasedPressure,
        )
      } else if (myFrom != null && elephantFrom == null) {
        return findPaths(
          myFrom,
          totalSteps = totalSteps,
          visited = elephantVisited + myVisited,
          stepsSoFar = myStepsSoFar,
          releasedPressure = releasedPressure,
        )
      }
      // println("Generating and evaluating open order for ${operableValves.size} operable valves.")
      val nextValves = operableValves - myVisited - elephantVisited
      return nextValves.allPairs().asSequence()
        // +1 is the cost of openning the next valve
        .filter { (myNext, elephantNext) ->
          myStepsSoFar + getSteps(myFrom!!, myNext) + 1 < totalSteps ||
              elephantStepsSoFar + getSteps(elephantFrom!!, elephantNext) + 1 < totalSteps
        }
        .maxOfOrNull { (myNext, elephantNext) ->
          val myStepCountToOpenNextValve = myStepsSoFar + 1 + getSteps(myFrom!!, myNext)
          val elephantStepCountToOpenNextValve = elephantStepsSoFar + 1 + getSteps(elephantFrom!!, elephantNext)
          val myNextFrom = if (myStepCountToOpenNextValve < totalSteps) myNext else null
          val elephantNextFrom = if (elephantStepCountToOpenNextValve < totalSteps) elephantNext else null
          findPaths2(
            myFrom = myNextFrom,
            elephantFrom = elephantNextFrom,
            totalSteps = totalSteps,
            myVisited = myVisited + setOfNotNull(myNextFrom),
            elephantVisited = elephantVisited + setOfNotNull(elephantNextFrom),
            myStepsSoFar = myStepCountToOpenNextValve,
            elephantStepsSoFar = elephantStepCountToOpenNextValve,
            releasedPressure = releasedPressure +
                (myNextFrom?.let { valveMap[it]!!.flowRate } ?: 0L) * (totalSteps - myStepCountToOpenNextValve) +
                (elephantNextFrom?.let { valveMap[it]!!.flowRate } ?: 0L)* (totalSteps - elephantStepCountToOpenNextValve)
          )
        } ?: releasedPressure
    }

    fun findPaths(
      from: String,
      totalSteps: Int,
      visited: Set<String> = setOf(),
      stepsSoFar: Int = 0,
      releasedPressure: Long = 0,
    ): Long {
      // println("Generating and evaluating open order for ${operableValves.size} operable valves.")
      val nextValves = operableValves - visited
      return nextValves.asSequence()
        // +1 is the cost of openning the next valve
        .filter { stepsSoFar + getSteps(from, it) + 1 < totalSteps }
        .maxOfOrNull { nextValve ->
          val stepCountToOpenNextValve = stepsSoFar + 1 + getSteps(from, nextValve)
          findPaths(
            from = nextValve,
            totalSteps = totalSteps,
            visited = visited + nextValve,
            stepsSoFar = stepCountToOpenNextValve,
            releasedPressure = releasedPressure + (totalSteps - stepCountToOpenNextValve) * valveMap[nextValve]!!.flowRate
          )
        } ?: releasedPressure
    }

    private fun scoreOpenOrder(valveOpenOrder: List<String>, totalSteps: Int, startingAt: String): Pair<Long, List<String>> {
      val completePath = mutableListOf<String>()
      var lastValve: String = startingAt
      var releasedPressure = 0L
      completePath.add(startingAt)
      for(valve in valveOpenOrder) {
        val pathToValve = getPath(lastValve, valve).drop(1)
        if (completePath.size + pathToValve.size + 1 <= totalSteps) {
          completePath.addAll(pathToValve)
          completePath.add(valve)
          releasedPressure += valveMap[valve]!!.flowRate * (totalSteps - completePath.size + 1)
        } else {
          break
        }
        lastValve = valve
      }
      return releasedPressure to completePath
    }

    private fun getEdges(valve: Valve): List<Edge> {
      val edges = mutableListOf<Edge>()
      if (valve.flowRate > 0) {
        edges.add(Edge(valveMap[valve.name]!!, valveMap[valve.name]!!))
      }
      valve.neighbours.mapTo(edges) { neighbourValve -> Edge(valveMap[valve.name]!!, valveMap[neighbourValve]!!) }
      return edges
    }

    private fun getVisitCount(valve: Valve): Int {
      return (if (valve.flowRate > 0) 1 else 0) + valve.neighbours.size
    }

    private fun buildPath(start: String, end: String, previous: Map<String, String?>): List<String> {
      val valves = mutableListOf<String>()
      valves.add(end)
      var valve: String? = previous[end]
      while (valve != null) {
        valves.add(0, valve)
        if (valve == start) {
          break
        }
        valve = previous[valve]
      }
      return valves
    }

    private fun getSteps(start: String, end: String): Int {
      if (start == end) return 0
      val knownPath = knownPaths[start to end]
      if (knownPath != null) {
        return knownPath.size - 1
      }
      val shortestPath = findShortestPath(start, end)
      return if (shortestPath.isEmpty()) {
        throw IllegalStateException("No path from $start to $end.")
      } else {
        knownPaths[start to end] = shortestPath
        shortestPath.size - 1
      }
    }

    private fun getPath(start: String, end: String): List<String> {
      if (start == end) return listOf()
      val knownPath = knownPaths[start to end]
      if (knownPath != null) {
        return knownPath
      }
      val shortestPath = findShortestPath(start, end)
      return if (shortestPath.isEmpty()) {
        listOf()
      } else {
        knownPaths[start to end] = shortestPath
        shortestPath
      }
    }

    private fun findShortestPath(start: String, end: String): List<String> {
      val distance = mutableMapOf<String, Int>()
      val previous = mutableMapOf<String, String?>()

      val unvisited = mutableSetOf<String>()
      for (valve in valves.map { it.name }) {
        distance[valve] = Int.MAX_VALUE
        previous[valve] = null
        unvisited.add(valve)
      }
      distance[start] = 0

      while (unvisited.isNotEmpty()) {
        val valve = unvisited.minBy { distance[it]!! }
        val distanceToMe = distance[valve]!!
        unvisited.remove(valve)

        if (valve == end) {
          return buildPath(start, end, previous)
        }

        val neighbours = valveMap[valve]!!.neighbours
        for (neighbour in neighbours) {
          if (neighbour in unvisited) {
            val newDistance = distanceToMe + 1
            val distanceToNeighbour = distance[neighbour]!!
            if (newDistance < distanceToNeighbour) {
              distance[neighbour] = newDistance
              previous[neighbour] = valve
            }
          }
        }
      }
      return listOf()
    }

    fun findBestRouteBruteForce(steps: Int = 30, startingAt: String = "AA"): List<Edge> {
      val maxVisitCount = valves.associate { it.name to getVisitCount(it) }
      val unexplored = getEdges(valve = valveMap[startingAt]!!)
        .map { listOf(it) }
        .toMutableList()
      val paths: MutableList<List<Edge>> = mutableListOf()
      println("Exploring paths")
      while (unexplored.isNotEmpty()) {
        val path = unexplored.removeAt(0)
        val nextEdges = getEdges(path.last().toValve)
        var pathIsComplete = true
        val pathVisitCount = getVisitedCount(path)
        for (edge in nextEdges) {
          if (pathVisitCount[edge.toValve.name] == maxVisitCount[edge.toValve.name]) {
            continue
          }
          val newPath = path + edge
          unexplored.add(newPath)
          pathIsComplete = false
        }
        if (pathIsComplete) {
          paths.add(path)
        }
      }
      println("Scoring ${paths.size} paths")
      var bestScore: Long = 0
      var bestPath: List<Edge> = listOf()
      for (path in paths) {
        val score = scorePath(steps, path)
        if (score > bestScore) {
          bestScore = score
          bestPath = path
        }
      }
      println("Found best path with score $bestScore")
      return bestPath
    }

    private fun getVisitedCount(path: List<Edge>): Map<String, Int> {
      return path.groupingBy { it.toValve.name }.eachCount()
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

    companion object {
      private fun scorePath(steps: Int, path: List<Edge>): Long {
        var remainingSteps = steps
        var releasedPressure = 0L
        for (edge in path) {
          if (edge.isOpening) {
            releasedPressure += (remainingSteps - 1) * edge.toValve.flowRate
          }
          remainingSteps--
        }
        return releasedPressure
      }
    }
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
