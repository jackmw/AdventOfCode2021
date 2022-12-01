package com.jackmw.aoc2021

fun main() {
  part1(Target(xRange = IntRange(20, 30), yRange = IntRange(-10, -5)))
  part1(Target(xRange = IntRange(119, 176), yRange = IntRange(-141, -84)))

  part2(Target(xRange = IntRange(20, 30), yRange = IntRange(-10, -5)))
  part2(Target(xRange = IntRange(119, 176), yRange = IntRange(-141, -84)))
}

private fun part1(target: Target) {
  val trajectory = Trajectory()
  val initialPosition = Point(0, 0)
  val (maxInitial, maxY) = trajectory.guessInitialVelocityWithMaxHeight(initialPosition, target)
  println("initial velcocity $maxInitial will produce max height $maxY")
}

private fun part2(target: Target) {
  val trajectory = Trajectory()
  val initialPosition = Point(0, 0)
  val velocities = trajectory.findAllHittingInitialVelocities(initialPosition, target)
  println("${velocities.size} initial velocities will hit the target")
}


data class Target(
  val xRange: IntRange,
  val yRange: IntRange,
)

class Trajectory {
  fun findAllHittingInitialVelocities(initialPosition: Point, target: Target): List<Point> {
    val initialVelocities = mutableListOf<Point>()
    for (xVelocity in 0..target.xRange.last) {
      for (yVelocity in target.yRange.first * 10 ..target.yRange.first * -10) {
        val initialVelocity = Point(xVelocity, yVelocity)
        // println("Trying initial velocity $initialVelocity target=$target")
        val (_, time, canHitTarget) = canHitTarget(initialPosition, initialVelocity, target)
        if (canHitTarget) {
          // println("$initialVelocity can hit target! at t=$time")
          initialVelocities.add(initialVelocity)
        } else {
          //println("initial velocity $initialVelocity overshoots at t=$time")
        }
      }
    }
    // println(initialVelocities)
    return initialVelocities
  }

  fun guessInitialVelocityWithMaxHeight(initialPosition: Point, target: Target): Pair<Point, Int> {
    // println("Trying to hit target $target from $initialPosition")

    var maxInitial: Point = initialPosition
    var maxY = 0
    for (xVelocity in 0..target.xRange.last) {
      for (yVelocity in 0..target.yRange.count() * 5) {
        val initialVelocity = Point(xVelocity, yVelocity)
        val (positions, time, canHitTarget) = canHitTarget(initialPosition, initialVelocity, target)
        if (canHitTarget) {
          val maxHeight = positions!!.maxOf { it.value.y }
          if (maxHeight > maxY) {
            maxInitial = initialVelocity
            maxY = maxHeight
          }
        }
      }
    }
    return maxInitial to maxY
  }

  private fun canHitTarget(initialPosition: Point, initialVelocity: Point, target: Target): Triple<Map<Int, Point>?, Int, Boolean> {
    var t = 0
    val positions: MutableMap<Int, Point> = mutableMapOf(t to initialPosition)
    var vX = initialVelocity.x
    var vY = initialVelocity.y
    var lastX = initialPosition.x
    var lastY = initialPosition.y
    while (true) {
      t++
      val newX = lastX + vX
      val newY = lastY + vY
      vX = (vX - 1).coerceAtLeast(0)
      vY -= 1
      val position = Point(newX, newY)
      positions[t] = position
      lastX = newX
      lastY = newY
      //println("initial velocity $initialVelocity position($t)=$position")
      if (hitTarget(position, target)) {
        //println("initial velocity $initialVelocity hits at t=$t position=$position")
        return Triple(positions, t ,true)
      } else if (overshotTarget(position, target)) {
        // println("initial velocity $initialVelocity overshoots at t=$t position=$position")
        return Triple(null , t, false)
      } else {
        t++
      }
    }
  }

  private fun overshotTarget(position: Point, target: Target): Boolean {
    if (position.x > target.xRange.last) {
      return true
    }
    if (position.y < target.yRange.first) {
      return true
    }
    return false
  }

  private fun hitTarget(position: Point, target: Target): Boolean {
    return position.x in target.xRange && position.y in target.yRange
  }
}
