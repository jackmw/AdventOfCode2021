package com.jackmw.aoc2022

import com.jackmw.Utils

fun main(args: Array<String>) {
  val day = Day12()
  day.part1("/2022/day12/part1-test.txt")
  day.part1("/2022/day12/part1-input.txt")

  day.part2("/2022/day12/part1-test.txt")
  day.part2("/2022/day12/part1-input.txt" )
}

class Day12 {
  internal fun part1(fileName: String) {
    val map = parseFile(fileName)
    val completedPath = findPath(map, map.start)
    if (completedPath != null) {
      println("Path found: ${completedPath.size}")
      map.printWithPath(completedPath)
    } else {
      println("Path not found")
    }
  }

  internal fun part2(fileName: String) {
    val map = parseFile(fileName)
    val possibleStarts = map.cells.filter { it.height == 'a' }.toSet() - map.start
    var shortestPath: Path? = null
    for ((index, start) in possibleStarts.withIndex()) {
      print("Exploring possible start $start ${index + 1}/${possibleStarts.size} ")
      val completedPath = findPath(map, start)
      if (completedPath != null) {
        println("Found path ${completedPath.size}")
        if (shortestPath == null || completedPath.size < shortestPath.size) {
          shortestPath = completedPath
        }
      } else {
        println("no path")
      }
    }
    println("Part 2: the shorted path is of length ${shortestPath!!.size - 1} and it starts at ${shortestPath!!.start}")
  }

  private fun findPath(map: GameMap, startAt: Cell = map.start): Path? {
    val distance = mutableMapOf<Coordinate, Int>()
    val previous = mutableMapOf<Cell, Cell?>()

    val unvisited = mutableSetOf<Coordinate>()
    for (cell in map.cells) {
      distance[cell.coord] = Int.MAX_VALUE
      previous[cell] = null
      unvisited.add(cell.coord)
    }
    distance[startAt.coord] = 0

    while (unvisited.isNotEmpty()) {
      val currentCoord = unvisited.filter { distance[it] != Int.MAX_VALUE }.minByOrNull { distance[it]!! }
      if (currentCoord == null) {
        // println("out of options...")
        break
      }
      val distanceToMe = distance[currentCoord]!!
      unvisited.remove(currentCoord)
      val current = map.getCell(currentCoord)

      val neighbours = map.getViableNeighbours(current)
      for (neighbour in neighbours) {
        if (neighbour.coord in unvisited) {
          val newDistance = distanceToMe + 1
          val distanceToNeighbour = distance[neighbour.coord]!!
          if (distanceToNeighbour == Int.MAX_VALUE || newDistance < distanceToNeighbour) {
       //     println("setting distance for ${neighbour.coord} to $newDistance")
            distance[neighbour.coord] = newDistance
            previous[neighbour] = current
          }
        }
      }
    }

    if (map.end.coord !in unvisited) {
      // println("Found the end!")
      val cells = mutableListOf<Cell>()
      cells.add(map.end)
      var cell: Cell? = previous[map.end]
      while (cell != null) {
        cells.add(0, cell)
        cell = previous[cell]
      }
      val path = Path()
      for (cellPath in cells) {
        path.addCell(cellPath)
      }
      return path
    } else {
      return null
    }
  }

  private fun findPathBFS(map: GameMap): Path? {
    val paths = mutableListOf<Path>()
    paths.add(Path(cells = mutableListOf(map.start)))
    while (paths.isNotEmpty()) {
      val currentPath = paths.removeAt(0)
      val viableNeighbours = map.getViableNeighbours(currentPath.lastStep)
      for (nextCell in viableNeighbours) {
        if (!currentPath.hasBeenTo(nextCell.coord)) {
          val newPath = currentPath.clone()
          newPath.addCell(nextCell)
          paths.add(newPath)
          if (newPath.isComplete()) {
            return newPath
          }
        }
      }
    }
    return null
  }

  internal data class Coordinate(
    val row: Int,
    val column: Int,
  ) {
  }

  internal data class Cell(
    val coord: Coordinate,
    val height: Char,
  ) {
    init {
      if (height !in 'a' .. 'z') {
        throw IllegalArgumentException("bad height $height")
      }
    }
  }

  internal data class GameMap(
    private val map: Map<Coordinate, Cell>,
    val start: Cell,
    val end: Cell,
  ) {
    private val rows = map.keys.maxOf { it.row } + 1
    private val columns =map.keys.maxOf { it.column } + 1

    val cells: Set<Cell>
      get() = map.values.toSet()

    private fun isValidCoordinate(coord: Coordinate): Boolean {
      return coord.row in 0 until rows && coord.column in 0 until columns
    }

    private fun isPathableFrom(from: Coordinate, to: Coordinate): Boolean {
      val fromHeight = map[from]!!.height
      val toHeight = map[to]!!.height
      val diff = fromHeight - toHeight
      return diff >= 0 || diff == - 1
    }

    fun getViableNeighbours(cell: Cell): List<Cell> {
      val coord = cell.coord
      return COORD_DIFF.asSequence()
        .map { (rowDiff, columnDiff) -> Coordinate(row = coord.row + rowDiff, column = coord.column + columnDiff) }
        .filter { isValidCoordinate(it) }
        .filter { isPathableFrom(coord, it) }
        .map { map[it]!! }
        .toList()
    }

    fun printWithPath(path: Path) {
      for (row in 0 until rows) {
        for (column in 0 until columns) {
          val coord = Coordinate(row = row, column = column)
          if (start.coord == coord) {
            print('S')
          } else if (end.coord == coord) {
            print('E')
          } else {
            print(map[coord]!!.height)
          }
        }
        println()
      }

      println()
      for (row in 0 until rows) {
        for (column in 0 until columns) {
          val coord = Coordinate(row = row, column = column)
          val char = if (start.coord == coord) {
            'S'
          } else if (end.coord == coord) {
            'E'
          } else {
            path.getChar(coord) ?: '.'
          }
          print(char)
        }
        println()
      }
    }

    fun getCell(coord: Coordinate): Cell {
      return map[coord]!!
    }

    companion object {
      val COORD_DIFF = listOf(1 to 0, -1 to 0, 0 to 1, 0 to -1)
    }
  }

  internal data class Path(
    val cells: MutableList<Cell> = mutableListOf(),
    private val coords: MutableSet<Coordinate> = mutableSetOf(),
    private val chars: MutableMap<Coordinate, Char> = mutableMapOf(),
  ): Cloneable {
    fun hasBeenTo(coord: Coordinate): Boolean {
      return coord in coords
    }

    fun addCell(cell: Cell) {
      if (cells.isNotEmpty()) {
        val previous = cells.last()
        val char = getPreviousChar(previous.coord, cell.coord)
        chars[previous.coord] = char
      }
      cells.add(cell)
      coords.add(cell.coord)
    }

    private fun getPreviousChar(from: Coordinate, to: Coordinate): Char {
      return if (from.row < to.row) {
        'v'
      } else if (from.row > to.row) {
        '^'
      } else if (from.column < to.column) {
        '>'
      } else {
        '<'
      }
    }

    fun isComplete(): Boolean {
      return lastStep.height == 'z'
    }

    val size: Int
      get() = cells.size

    val start: Cell
      get() = cells.first()

    val lastStep: Cell
      get() = cells.last()

    public override fun clone(): Path {
      val newPath = Path()
      newPath.cells.addAll(cells)
      newPath.coords.addAll(coords)
      newPath.chars += chars
      return newPath
    }

    fun getChar(coord: Coordinate): Char? {
      return chars[coord]
    }
  }


  private fun parseFile(fileName: String): GameMap {
    val lines = Utils.readFileAsLines(fileName)
    val map = mutableMapOf<Coordinate, Cell>()

    var row =0
    var startCell: Cell? = null
    var endCell: Cell? = null
      lines.forEach { line ->
      var column = 0
      for (char in line) {
        val coord = Coordinate(row = row, column = column)
        val height = when (char) {
          'S' -> 'a'
          'E' -> 'z'
          else -> char
        }
        val cell = Cell(coord, height)
        if (char == 'S') {
          startCell = cell
        } else if (char == 'E') {
          endCell = cell
        }
        map[coord] = cell
        column++
      }
      row++
    }
    return GameMap(map = map, start = startCell!!, end = endCell!!)
  }
}