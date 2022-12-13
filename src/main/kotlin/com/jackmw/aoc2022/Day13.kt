package com.jackmw.aoc2022

import com.jackmw.aoc2021.Utils
import java.lang.StringBuilder
import java.util.Stack

fun main(args: Array<String>) {
  val day = Day13()
//   println("[1,[2,[3,[4,[5,6,7]]]],8,9]".getList())
  day.part1("/2022/day13/part1-test.txt")
  day.part1("/2022/day13/part1-input.txt" )

  day.part2("/2022/day13/part1-test.txt")
 day.part2("/2022/day13/part1-input.txt" )
}

class Day13 {
  internal fun part1(fileName: String) {
    val pairsOfPackets = parseFile(fileName)
    val inOrderPairIndices = mutableListOf<Int>()
    for ((index, pair) in pairsOfPackets.withIndex()) {
      println("== Pair ${index + 1} ==")
      val left = pair.first
      val right = pair.second
      if (isInOrder(left, right, prefix = " -")!!) {
        inOrderPairIndices.add(index)
      }
    }
    println("Part 1: $fileName -> sum of indices: ${inOrderPairIndices.sumOf { it + 1 }}")
  }

  internal fun part2(fileName: String) {
    val allPackets = (parseFile(fileName).flatMap { listOf(it.first, it.second) } + listOf("[[2]]", "[[6]]"))
      .sortedWith { o1, o2 ->
        val isInOrder = isInOrder(o1, o2, " -")!!
        if (isInOrder) {
          -1
        } else {
          1
        }
      }
    val keyIndex1 = allPackets.indexOf("[[2]]") + 1
    val keyIndex2 = allPackets.indexOf("[[6]]") + 1
    println("Part 2: $fileName -> decoder key : $keyIndex1 x $keyIndex2 = ${keyIndex1 * keyIndex2}")
  }

  private fun isInOrder(left: String, right: String, prefix: String): Boolean? {
    println("$prefix Compare $left vs $right")
    if (left.isList() && right.isList()) {
      return isInOrder(left.getList(), right.getList(), prefix = "  $prefix")
    } else if (left.isNumber() && right.isNumber()) {
      val leftInt = left.toInt()
      val rightInt = right.toInt()
      return if (leftInt == rightInt) null else leftInt < rightInt
    } else if (left.isNumber() && right.isList()) {
      val convertedLeft = left.asList()
      println("  $prefix Mixed types; convert left to $convertedLeft and retry comparison")
      return isInOrder(convertedLeft, right, prefix = "  $prefix")
    } else if (left.isList() && right.isNumber()) {
      val convertedRight = right.asList()
      println("  $prefix Mixed types; convert right to $convertedRight and retry comparison")
      return isInOrder(left,convertedRight, prefix = "  $prefix")
    }
    return null
  }

  private fun isInOrder(leftList: List<String>, rightList: List<String>, prefix: String): Boolean? {
    var index = 0
    while(true) {
      val leftElement = leftList.elementAtOrNull(index)
      val rightElement = rightList.elementAtOrNull(index)
      if (leftElement == null && rightElement == null) {
        return null
      } else if (leftElement == null) {
        println("$prefix Left side ran out of items, so inputs are in the right order")
        return true
      } else if (rightElement == null) {
        println("$prefix Right side ran out of items, so inputs are not in the right order")
        return false
      } else {
        val isInOrder = isInOrder(leftElement, rightElement, prefix = "  $prefix")
        if (isInOrder != null) {
          if (isInOrder) {
            println("    $prefix Left side is smaller, so the inputs are in the right order")
          } else {
            println("    $prefix Right side is smaller, so inputs are not in the right order")
          }
          return isInOrder
        } else {
          index++
        }
      }
    }
  }

  private fun parseFile(fileName: String): List<Pair<String, String>> {
    return Utils.readFileAsLines(fileName).asSequence()
      .filter { it.isNotEmpty() }
      .chunked(2)
      .map { it.first() to it.last() }
      .toList()
  }
}

private fun String.asList(): String {
  return "[$this]"
}

private fun String.isList(): Boolean {
  return startsWith('[') && endsWith(']')
}
private fun String.getList(): List<String> {
  val remaining = removeSurrounding("[", "]")
  val elements = mutableListOf<String>()
  val stack = Stack<Char>()
  var depth = 0
  for (char in remaining) {
    if (char == ',' && depth == 0) {
      val chars = mutableListOf<Char>()
      while(stack.isNotEmpty()) {
        chars.add(0, stack.pop())
      }
      elements.add(String(chars.toCharArray()))
    } else if (char == ']') {
      depth--
      stack.push(char)
    } else if (char == '[') {
      depth++
      stack.push(char)
    }else {
      stack.push(char)
    }
  }
  if (stack.isNotEmpty()) {
    val chars = mutableListOf<Char>()
    while(stack.isNotEmpty()) {
      chars.add(0, stack.pop())
    }
    elements.add(String(chars.toCharArray()))
  }
  return elements
}

private fun String.isNumber(): Boolean {
  return toIntOrNull() != null
}