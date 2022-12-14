package com.jackmw.aoc2021

import com.jackmw.Utils.Companion.readFileAsLines

fun main() {
  part1("/day12/part1-test1.txt")
  part1("/day12/part1-test2.txt")
  part1("/day12/part1-test3.txt")
  part1("/day12/part1-input.txt")

  part2("/day12/part1-test1.txt")
  part2("/day12/part1-test2.txt")
  part2("/day12/part1-test3.txt")
  part2("/day12/part1-input.txt")
}

private fun part1(fileName: String) {
  val caveMap = CaveMap.parse(readFileAsLines(fileName))
  val paths = caveMap.findCompletePaths(allowRevisit = false)
  println("$fileName -> found ${paths.size} paths through this system.")
}

private fun part2(fileName: String) {
  val caveMap = CaveMap.parse(readFileAsLines(fileName))
  val paths = caveMap.findCompletePaths(allowRevisit = true)
  println("$fileName -> found ${paths.size} paths through this system.")
}

private data class Cave(
  val name: String,
  val neighbours: MutableSet<Cave> = mutableSetOf()
    ) {
  val isBigCave = name.uppercase() == name

  override fun hashCode(): Int {
    return name.hashCode()
  }

  override fun toString(): String {
    return name
  }

  override fun equals(other: Any?): Boolean {
    return name == other
  }
}

private data class CavePath(private val caves: List<Cave>) {
  val start: Cave = caves.first()

  val end: Cave = caves.last()

  val visitedSmallCaves: Set<String> = caves.asSequence()
       .filter { !it.isBigCave }
       .map { it.name}
       .toSet()

  val visitedTwiceSmallCave: String? = caves.asSequence()
      .filter { !it.isBigCave }
      .map { it.name }
      .groupingBy { it }.eachCount()
      .filterValues { it == 2 }
      .firstNotNullOfOrNull { it.key }

  fun visitCave(cave: Cave): CavePath {
    if (cave.name in visitedSmallCaves && visitedTwiceSmallCave != null) {
      throw IllegalArgumentException()
    }
    return CavePath(caves + cave)
  }

  override fun toString(): String {
    return caves.joinToString(",") { it.name }
  }

  fun getVisitableCaves(allowRevisit: Boolean) : List<Cave> {
    if (end.name == "end") {
      return emptyList()
    }
    return end.neighbours
      .filter { cave ->
        when {
          cave.name == "start" -> false
          cave.name == visitedTwiceSmallCave -> false
          cave.name !in visitedSmallCaves -> true
          allowRevisit && cave.name in visitedSmallCaves && visitedTwiceSmallCave == null -> true
          else -> false
        }
      }

  }

  fun foundEnd(): Boolean {
    return end.name == "end"
  }
}

private class CaveMap(private val caves: Set<Cave>) {
  val start: Cave = caves.single { it.name == "start" }
  val end: Cave = caves.single { it.name == "end" }

  fun findCompletePaths(allowRevisit: Boolean): List<CavePath> {
    val completePaths = mutableListOf<CavePath>()
    val cavePath = CavePath(caves = listOf(start))
    val paths = mutableListOf<CavePath>()
    paths.add(cavePath)
    while (paths.isNotEmpty()) {
      val currentPath = paths.removeAt(0)
      if (currentPath.foundEnd()) {
        completePaths.add(currentPath)
      }
      val nextCaves = currentPath.getVisitableCaves(allowRevisit = allowRevisit)
      // println("currentPath: $currentPath visitedTwiceCave: ${currentPath.visitedTwiceSmallCave} next caves: $nextCaves")
      for (nextCave in nextCaves) {
        val newPath = currentPath.visitCave(nextCave)
        paths.add(newPath)
      }
    }
    return completePaths
  }

  companion object {
    fun parse(lines: List<String>): CaveMap {
      val caveMap = mutableMapOf<String, Cave>()
      for (line in lines) {
        val from = line.substringBefore('-')
        val to = line.substringAfter('-')
        val fromCave = caveMap.computeIfAbsent(from) { Cave(it) }
        val toCave = caveMap.computeIfAbsent(to) { Cave(it) }
        fromCave.neighbours.add(toCave)
        toCave.neighbours.add(fromCave)
      }
      return CaveMap(caveMap.values.toSet())
    }
  }
}
