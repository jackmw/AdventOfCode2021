package com.jackmw.aoc2022

import com.jackmw.aoc2021.Utils
import kotlin.math.absoluteValue

fun main(args: Array<String>) {
//  part1("/2022/day9/part1-test1.txt")
//  part1("/2022/day9/part1-test2.txt")
//  part1("/2022/day9/part1-input.txt")
//  part2("/2022/day9/part1-test1.txt", ropeLength = 2)
//  part2("/2022/day9/part1-test2.txt", ropeLength = 2)
//  part2("/2022/day9/part1-input.txt", ropeLength = 2)
//  part2("/2022/day9/part1-test1.txt", ropeLength = 2)
//  part2("/2022/day9/part1-test1.txt", ropeLength = 10)
  part2("/2022/day9/part1-test2.txt", ropeLength = 10)
  part2("/2022/day9/part1-input.txt", ropeLength = 10)
}


enum class Direction(val symbol: Char, val xDelta: Int, val yDelta: Int) {
  LEFT('L', -1, 0),
  RIGHT('R', 1, 0),
  UP('U', 0, -1),
  DOWN('D', 0, 1),
  ;

  companion object {
    fun fromSymbol(symbol: Char): Direction {
      return when(symbol) {
        'L' -> LEFT
        'R' -> RIGHT
        'U' -> UP
        'D' -> DOWN
        else -> throw IllegalArgumentException("Unknown direction symbol $symbol")
      }
    }
  }
}
data class Knot(
  val x: Int,
  val y: Int,
  val name: String,
)

private fun part1(fileName: String) {
  val movements = parseFile(fileName)
  val tailPositions = mutableListOf<Knot>()
  var head = Knot(0, 0, "H")
  var tail = Knot(0, 0, "T")
  tailPositions.add(tail)
  for ((direction, steps) in movements) {
    repeat(steps) {
      val (newHead, newTail) = moveFollower(head, tail, direction)
      if (newTail != tail) {
        tailPositions.add(newTail)
      }
      head = newHead
      tail = newTail
    }
  }
  val uniqueTailPositions = tailPositions.toSet()
  println("Part1: $fileName -> tail has moved ${tailPositions.size} and covered ${uniqueTailPositions.size} unique tiles")
}

data class Rope(val knots: List<Knot>) {
  constructor(ropeSize: Int = 2) : this(List(ropeSize) {
    Knot(
      x = 0,
      y = 0,
      name = when (it) {
        0 -> "H"
        1 -> if (ropeSize == 2) "T" else "1"
        else -> "$it"
      }
    )
  })

  val tail: Knot
    get() {
      return knots.last()
    }
  val head: Knot
    get() {
      return knots.first()
    }

  override fun toString(): String {
    val sb = StringBuilder()
    var minX = Int.MAX_VALUE
    var maxX = Int.MIN_VALUE
    var minY = Int.MAX_VALUE
    var maxY = Int.MIN_VALUE
    for (knot in knots) {
      minX = minX.coerceAtMost(knot.x)
      minY = minY.coerceAtMost(knot.y)
      maxX = maxX.coerceAtLeast(knot.x)
      maxY = maxY.coerceAtLeast(knot.y)
    }
    val posToKnot = knots.groupBy { (it.y to it.x) }
    for (row in minY..maxY) {
      for (column in minX .. maxX) {
        val knot = posToKnot[(row to column)]?.first()
        if (knot == null) {
          sb.append(".")
        } else {
          sb.append(knot.name)
        }
      }
      sb.append("\n")
    }
    return sb.toString()
  }
}

private fun part2(fileName: String, ropeLength: Int) {
  val movements = parseFile(fileName)
  val tailPositions = mutableListOf<Knot>()
  var rope = Rope(ropeSize = ropeLength)

  tailPositions.add(rope.tail)
  println("== initial state ==")
  println(rope)
  for ((direction, steps) in movements) {
    println("== $direction $steps ==")
    repeat(steps) {
      val newRope = moveFollower(rope, direction)
      if (newRope.tail != rope.tail) {
        tailPositions.add(newRope.tail)
      }
      rope = newRope
    }
    println(rope)
  }
  val uniqueTailPositions = tailPositions.toSet()
  println("Part2: $fileName, ropeLength=$ropeLength -> tail has moved ${tailPositions.size} and covered ${uniqueTailPositions.size} unique tiles")
}

private fun moveFollower(rope: Rope, direction: Direction): Rope {
  val newKnots = mutableListOf<Knot>()
  newKnots.add(moveHead(rope.head, direction))
  for (i in 1 until rope.knots.size) {
    newKnots.add(getNextKnotPosition(newKnots.last(), rope.knots[i]))
  }
  return Rope(newKnots)
}

private fun getNextKnotPosition(previousKnot: Knot, myKnot: Knot): Knot {
  val xDiff = (previousKnot.x - myKnot.x)
  val yDiff = (previousKnot.y - myKnot.y)
  val newKnot =
    if (xDiff.absoluteValue == 2 && yDiff.absoluteValue == 2) {
      Knot(x = myKnot.x + xDiff / 2, y = myKnot.y + yDiff / 2, myKnot.name)
    } else if (xDiff.absoluteValue == 2) {
    if (yDiff == 0) {
      Knot(x = myKnot.x + xDiff / 2, y = myKnot.y, myKnot.name)
    } else {
      Knot(x = myKnot.x + xDiff / 2, y = previousKnot.y, myKnot.name)
    }
  } else if (yDiff.absoluteValue == 2) {
    if (xDiff == 0) {
      Knot(x = myKnot.x, y = myKnot.y + yDiff / 2, myKnot.name)
    } else {
      Knot(x = previousKnot.x, y = myKnot.y + yDiff / 2, myKnot.name)
    }
  } else {
    myKnot
  }
  // println("prev=$previousKnot, xDiff=$xDiff, yDiff=$yDiff, from $myKnot to $newKnot)")
  return newKnot
}

private fun getNextKnotPosition(previousKnot: Knot, myKnot: Knot, direction: Direction): Knot {
  val xDiff = (previousKnot.x - myKnot.x)
  val yDiff = (previousKnot.y - myKnot.y)
  return if (xDiff.absoluteValue == 2) {
    if (yDiff == 0) {
      Knot(x = myKnot.x + direction.xDelta, y = myKnot.y + direction.yDelta, myKnot.name)
    } else {
      Knot(x = myKnot.x + direction.xDelta, y = previousKnot.y, myKnot.name)
    }
  } else if (yDiff.absoluteValue == 2) {
    if (xDiff == 0) {
      Knot(x = myKnot.x + direction.xDelta, y = myKnot.y + direction.yDelta, myKnot.name)
    } else {
      Knot(x = previousKnot.x, y = myKnot.y + direction.yDelta, myKnot.name)
    }
  } else {
    myKnot
  }
}

private fun moveHead(
  knot: Knot,
  direction: Direction,
): Knot {
  return Knot(x = knot.x + direction.xDelta, y = knot.y + direction.yDelta, knot.name)
}

private fun moveFollower(
  headPosition: Knot,
  tailPosition: Knot,
  direction: Direction
): Pair<Knot, Knot> {
  val newHeadPosition = moveHead(headPosition, direction)
  val newTailPosition = getNextKnotPosition(newHeadPosition, tailPosition, direction)
  return newHeadPosition to newTailPosition
}

private fun parseFile(fileName: String): List<Pair<Direction, Int>> {
  return Utils.readFileAsLines(fileName).map { line ->
    val direction = Direction.fromSymbol(line.first())
    val count = line.substringAfter(' ').toInt()
    direction to count
  }
}
