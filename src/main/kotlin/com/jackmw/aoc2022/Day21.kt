package com.jackmw.aoc2022

import com.jackmw.Utils

fun main(args: Array<String>) {
  val day = Day21()
  day.part1("/2022/day21/part1-test.txt")
  day.part1("/2022/day21/part1-input.txt")


  day.part2("/2022/day21/part1-test.txt")
  day.part2("/2022/day21/part1-input.txt")
}

internal class Day21 {
  internal fun part1(fileName: String) {
    val mathOperations = parseFile(fileName)
    val rootAnswer = mathOperations.evaluate("root")
    knownAnswers.clear()
    println("Day21 part1 fileName=$fileName rootAnswer=$rootAnswer")
  }

  internal fun part2(fileName: String) {
    val mathOperations = parseFile(fileName)
    val humnAnswer = mathOperations.solveForHumn()
    knownVectors.clear()
    println("Day21 part2 fileName=$fileName humn=$humnAnswer")
  }

  private val knownAnswers: MutableMap<String, Long> = mutableMapOf()
  private val knownVectors: MutableMap<String, Binomial> = mutableMapOf()

  // y = ax + b
  internal data class Fraction(
    val top: Long,
    val bottom: Long,
  ) {
    val isZero = top == 0L

    private fun gcd(n1: Long, n2: Long): Long {
      if(n1 == 0L) return n2
      return gcd(n2 % n1, n1)
    }

    override fun toString(): String {
      return when {
        top == 0L -> "0"
        bottom == 1L -> "$top"
        else -> "$top/$bottom"
      }
    }

    operator fun plus(other: Fraction): Fraction {
      val newTop = this.top * other.bottom + other.top * this.bottom
      val newBottom = this.bottom * other.bottom
      val gcd = gcd(newTop, newBottom)
      if (gcd > 1) {
        println("JACK_DEBUG reducing $newTop/$newBottom = ${newTop/gcd}/${newBottom/gcd}")
      }
      return Fraction(top = newTop / gcd, bottom = newBottom / gcd)
    }

    operator fun minus(other: Fraction): Fraction {
      val newTop = this.top * other.bottom - other.top * this.bottom
      val newBottom = this.bottom * other.bottom
      val gcd = gcd(newTop, newBottom)
      if (gcd > 1) {
        println("JACK_DEBUG reducing $newTop/$newBottom = ${newTop/gcd}/${newBottom/gcd}")
      }
      return Fraction(top = newTop / gcd, bottom = newBottom / gcd)
    }

    operator fun times(other: Fraction): Fraction {
      val newTop = this.top * other.top
      val newBottom = this.bottom * other.bottom
      val gcd = gcd(newTop, newBottom)
      if (gcd > 1) {
        println("JACK_DEBUG reducing $newTop/$newBottom = ${newTop/gcd}/${newBottom/gcd}")
      }
      return Fraction(top = newTop / gcd, bottom = newBottom / gcd)
    }

    operator fun div(other: Fraction): Fraction {
      val newTop = this.top * other.bottom
      val newBottom = this.bottom * other.top
      val gcd = gcd(newTop, newBottom)
      if (gcd > 1) {
        println("JACK_DEBUG reducing $newTop/$newBottom = ${newTop/gcd}/${newBottom/gcd}")
      }
      return Fraction(top = newTop / gcd, bottom = newBottom / gcd)
    }
  }

  internal data class Binomial(
    val a: Fraction,
    val b: Fraction,
  ) {
    override fun toString(): String {
      return if (!a.isZero && !b.isZero ) {
        "($a)x + $b"
      } else if (a.isZero && b.isZero) {
        "0"
      } else if (!a.isZero) {
        "($a)x"
      } else {
        "$b"
      }
    }

    operator fun plus(other: Binomial): Binomial {
      // println("JACK_DEBUG $this + $other: a=${this.a * other.aDivisor + other.a * this.aDivisor}, aDivisor=${this.aDivisor * other.aDivisor}, b=${this.b + other.b}")
      return Binomial(a = this.a + other.a, b = this.b + other.b)
    }

    operator fun minus(other: Binomial): Binomial {
      return Binomial(a = this.a - other.a, b = this.b - other.b)
    }

    operator fun times(other: Binomial): Binomial {
      if (!this.a.isZero && !other.a.isZero) {
        throw IllegalStateException("didn't want to use polynomial")
      }
      return if (this.a.isZero && other.a.isZero) {
        Binomial(a = this.a, b = this.b * other.b)
      } else if (!this.a.isZero) {
        // (this.a * x + this.b)  x (other.b)
        Binomial(a = this.a * other.b, b = this.b * other.b)
      } else {
        // ( this.b)  x (other.a * x + other.b)
        Binomial(a = other.a * this.b, b = this.b * other.b)
      }
    }

    operator fun div(other: Binomial): Binomial {
      if (!this.a.isZero && !other.a.isZero) {
        throw IllegalStateException("didn't want to use polynomial")
      }
      return if (this.a.isZero && other.a.isZero) {
        // (this. b) / (other.b)
        Binomial(a = this.a, b = this.b / other.b)
      } else if (!this.a.isZero) {
        // (this.a * x + this.b)  / (other.b)
        Binomial(a = this.a / other.b, b = this.b / other.b)
      } else {
        // ( this.b)  / (other.a * x + other.b)
       throw IllegalStateException("didn't want to use polynomial, inverse")
      }
    }
  }

  private fun Map<String, MathOperation>.solveForHumn(): Binomial {
    val root = get("root")!!
    val rootLeft = evaluateHumn(root.left!!)
    val rootRight = evaluateHumn(root.right!!)
    println("$rootLeft = $rootRight ")
    return Binomial(a = Fraction(0, bottom = 1), b = Fraction(0, bottom = 1))
  }

  private fun Map<String, MathOperation>.evaluateHumn(monkeyName: String): Binomial {
    val knownVector = knownVectors[monkeyName]
    if (knownVector != null) {
      return knownVector
    }
    if (monkeyName == "humn") {
      val answer = Binomial(a = Fraction(top = 1, bottom = 1), b = Fraction(top = 0, bottom = 1))
      knownVectors["humn"] = answer
      return answer
    }
    val operation = get(monkeyName)!!

    val answer = if (operation.intValue != null) {
      Binomial(a = Fraction(0, bottom = 1), b = Fraction(top = operation.intValue.toLong(), bottom = 1))
    } else {
      val left = operation.left!!
      val right = operation.right!!
      val evaluatedLeft = evaluateHumn(left)
      val evaluatedRight = evaluateHumn(right)
      val operator = operation.operator!!
      val result = when (operator) {
        '+' -> evaluatedLeft + evaluatedRight
        '-' -> evaluatedLeft - evaluatedRight
        '*' -> evaluatedLeft * evaluatedRight
        '/' -> evaluatedLeft / evaluatedRight
        else -> throw IllegalArgumentException("Unknown operator $operator in operation $operation")
      }
      println("($evaluatedLeft) $operator ($evaluatedRight) = $result")
      result
    }
    knownVectors[monkeyName] = answer
    return answer
  }

  private fun Map<String, MathOperation>.evaluate(monkeyName: String): Long {
    val knownAnswer = knownAnswers[monkeyName]
    if (knownAnswer != null) {
      return knownAnswer
    }
    val operation = get(monkeyName)!!

    val answer = if (operation.intValue != null) {
      operation.intValue.toLong()
    } else {
      val left = operation.left!!
      val right = operation.right!!
      when(val operator = operation.operator!!) {
        '+' -> evaluate(left) + evaluate(right)
        '-' -> evaluate(left) - evaluate(right)
        '*' -> evaluate(left) * evaluate(right)
        '/' -> evaluate(left) / evaluate(right)
        else -> throw IllegalArgumentException("Unknown operator $operator in operation $operation")
      }
    }
    knownAnswers[monkeyName] = answer
    return answer
  }

  internal data class MathOperation(val operation: String) {
    val intValue: Int? = operation.toIntOrNull()
    val left: String?
    val right: String?
    val operator: Char?

    init {
      operator = operation.substringAfter(" ").substringBefore(" ").first()
      left = operation.substringBefore(operator).trim()
      right = operation.substringAfter(operator).trim()
    }
  }

  companion object {
    fun parseFile(fileName: String): Map<String, MathOperation> {
      return Utils.readFileAsLines(fileName)
        .associate { line ->
          val monkeyName = line.substringBefore(":")
          val operation = line.substringAfter(": ")
          monkeyName to MathOperation(operation)
        }
    }
  }

}
