package com.jackmw.aoc

import com.jackmw.aoc.Utils.Companion.readFileAsLines
import kotlin.math.max
import kotlin.math.min

fun main() {
  fun part1(fileName: String) {
    val instructions = CubeInstruction.parse(readFileAsLines(fileName))
    val cubeStart = CubeStart()
//    for (instruction in instructions) {
//      if (instruction.turnOn) {
//        cubeStart.turnOnCubes(instruction.xRange, instruction.yRange, instruction.zRange)
//      } else {
//        cubeStart.turnOffCubes(instruction.xRange, instruction.yRange, instruction.zRange)
//      }
//    }
//    println("$fileName -> ${cubeStart.onCubeCount} cubes are active after instruction.")
    val sum = CubeRange.simplifyInstructions(instructions)
    println("$fileName -> $sum cubes are active after instruction.")
  }

  part1("/day22/part1-test.txt")
  // part1("/day22/part1-input.txt")
}

data class Point3D(
  val x: Int,
  val y: Int,
  val z: Int,
)

data class CubeRange(
  val xRange: IntRange,
  val yRange: IntRange,
  val zRange: IntRange,
) {
  val size: Long = 1L * (xRange.last- xRange.first) * (yRange.last - yRange.first) * (zRange.last - zRange.first)

  companion object {
    fun simplifyInstructions(instructions: List<CubeInstruction>): Long {
      val onCubeRanges = mutableSetOf<CubeRange>()
      val offCubeRanges = mutableSetOf<CubeRange>()
      var totalOnCubes = 0L
      for (instruction in instructions) {
        val instructionCube = instruction.getClampedCube() ?: continue
        val overlapsWithOnCubes = onCubeRanges.mapNotNull { it.getOverlap(instructionCube) }
        val overlapsWithOffCubes = offCubeRanges.mapNotNull { it.getOverlap(instructionCube) }
        if (instruction.turnOn) {
          // handle on cubes
          onCubeRanges.add(instructionCube)
          val newOnCubes = (instructionCube.size - (overlapsWithOnCubes.sumOf { it.size } - overlapsWithOffCubes.sumOf { it.size })).coerceAtLeast(0)
          totalOnCubes += newOnCubes
        } else {
          offCubeRanges.add(instructionCube)
          val newOffCubes = (overlapsWithOnCubes.sumOf { it.size } - overlapsWithOffCubes.sumOf { it.size }).coerceAtLeast(0)
          totalOnCubes -= newOffCubes

//          totalOffCubes += overlapsWithOnCubes.sumOf { it.size }
//          totalOffCubes -= overlapsWithOffCubes.sumOf { it.size }
        }

        println("Tracking ${onCubeRanges.size} total on $totalOnCubes")
      }
      return totalOnCubes
    }
  }

  private fun getOverlap(cubeRange: CubeRange): CubeRange? {
    val xRange = getOverlap(this.xRange, cubeRange.xRange) ?: return null
    val yRange = getOverlap(this.yRange, cubeRange.yRange) ?: return null
    val zRange = getOverlap(this.zRange, cubeRange.zRange) ?: return null
    return CubeRange(xRange, yRange, zRange)
  }

  private inline fun getOverlap(rangeA: IntRange, rangeB: IntRange): IntRange? {
    val start = max(rangeA.first, rangeB.first)
    val end = min(rangeA.last, rangeB.last)
    return if (end > start) {
       IntRange(start, end)
    } else {
      null
    }
  }
}


data class CubeInstruction(
  val xRange: IntRange,
  val yRange: IntRange,
  val zRange: IntRange,
  val turnOn: Boolean,
) {
  val cubeRange: CubeRange = CubeRange(xRange, yRange, zRange)

  fun getClampedCube(): CubeRange? {
    val xRange = xRange.clamp(CLAMP) ?: return null
    val yRange = yRange.clamp(CLAMP) ?: return null
    val zRange = zRange.clamp(CLAMP) ?: return null
    return CubeRange(xRange, yRange, zRange)
  }

  companion object {
    val CLAMP = -50..50
    fun parse(lines: List<String>): List<CubeInstruction> {
      return lines.map { parse(it) }
    }

    private fun parse(line: String): CubeInstruction {
      val onOff = line.substringBefore(" ") == "on"
      val coords = line.substringAfter(" ").split(',').map { it.substringAfter("=") }
      return CubeInstruction(
        xRange = IntRange.parse(coords[0]),
        yRange = IntRange.parse(coords[1]),
        zRange = IntRange.parse(coords[2]),
        turnOn = onOff,
      )
    }
  }
}

class CubeStart {
  private val onCubes: MutableSet<Point3D> = mutableSetOf()

  fun turnOnCubes(xRange: IntRange, yRange: IntRange, zRange: IntRange, clamp: IntRange = IntRange(-50, 50)) {
    for (x in xRange.clamp(clamp)!!) {
      for (y in yRange.clamp(clamp)!!) {
        for (z in zRange.clamp(clamp)!!) {
          val point = Point3D(x, y, z)
          onCubes.add(point)
        }
      }
    }
  }

  fun turnOffCubes(xRange: IntRange, yRange: IntRange, zRange: IntRange) {
    for (x in xRange) {
      for (y in yRange) {
        for (z in zRange) {
          val point = Point3D(x, y, z)
          onCubes.remove(point)
        }
      }
    }
  }

  val onCubeCount: Int
    get() = onCubes.size
}

private fun IntRange.Companion.parse(s: String, delimiter: String = ".."): IntRange {
  val start = s.substringBefore(delimiter).toInt()
  val end = s.substringAfter(delimiter).toInt()
  return IntRange(start, end)
}

fun IntRange.clamp(clamp: IntRange): IntRange? {
  val newStart = max(this.first, clamp.first)
  val newEnd = min(this.last, clamp.last)
  if (newEnd < newStart) return null
  return IntRange(newStart, newEnd)
}