package com.jackmw.aoc2021

import com.jackmw.aoc2021.Utils.Companion.readFileAsLines

fun main() {
//  val parseTests = listOf(
//    "[[1,2],3]",
//    "[9,[8,7]]",
//    "[[1,9],[8,5]]",
//    "[[[[1,2],[3,4]],[[5,6],[7,8]]],9]",
//    "[[[9,[3,8]],[[0,9],6]],[[[3,7],[4,9]],3]]",
//    "[[[[1,3],[5,3]],[[1,3],[8,7]]],[[[4,9],[6,9]],[[8,2],[7,3]]]]"
//  )
//  parseTests
//    .asSequence()
//    .map { it to SnailNumber.parse(it) }
//    .forEach { println("${it.first} parses to \n${it.second}\n") }
//
//  val reduceTests = listOf(
//    "[[[[[9,8],1],2],3],4]" to "[[[[0,9],2],3],4]",
//    "[7,[6,[5,[4,[3,2]]]]]" to "[7,[6,[5,[7,0]]]]",
//    "[[6,[5,[4,[3,2]]]],1]" to "[[6,[5,[7,0]]],3]",
//    "[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]" to "[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]",
//    "[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]" to "[[3,[2,[8,0]]],[9,[5,[7,0]]]]",
//  )
//  reduceTests
//    .asSequence()
//    .map { SnailNumber.parse(it.first) to it.second }
//    .forEach {
//      val before = "${it.first}"
//      it.first.reduce()
//      val after = "${it.first}"
//      println("$before reduces to $after expecting ${it.second} success=${it.second == after}")
//    }

  val sumTests = listOf(
    listOf("[[[[4,3],4],4],[7,[[8,4],9]]]", "[1,1]") to "[[[[0,7],4],[[7,8],[6,0]]],[8,1]]",
    listOf("[1,1]", "[2,2]", "[3,3]", "[4,4]") to "[[[[1,1],[2,2]],[3,3]],[4,4]]",
    listOf("[1,1]", "[2,2]", "[3,3]", "[4,4]", "[5,5]") to "[[[[3,0],[5,3]],[4,4]],[5,5]]",
    listOf("[1,1]", "[2,2]", "[3,3]", "[4,4]", "[5,5]", "[6,6]") to "[[[[5,0],[7,4]],[5,5]],[6,6]]",
    listOf(
      "[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]",
      "[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]",
      "[[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]",
      "[[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]",
      "[7,[5,[[3,8],[1,4]]]]",
      "[[2,[2,2]],[8,[8,1]]]",
      "[2,9]",
      "[1,[[[9,3],9],[[9,0],[0,7]]]]",
      "[[[5,[7,4]],7],1]",
      "[[[[4,2],2],6],[8,7]]"
    ) to "[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]"
  )
  sumTests.forEach { (parts, expectedSum) ->
    val partsString = parts.joinToString(separator = ",", prefix = "sum(", postfix = ")")
    // println("summing $partsString")

    val actualSum = parts.map { SnailNumber.parse(it) }
      .reduce { acc, snailNumber ->
//        println("\t$acc")
//        println("+\t$snailNumber")
        val sum = acc + snailNumber
//        println("=\t$sum\n")
        sum
      }
    val passed = expectedSum == actualSum.toString()
    println("$actualSum expected=$expectedSum passed=$passed\n")
  }

  val magnitudeTests = listOf(
    "[9,1]" to 29,
    "[1,9]" to 21,
    "[[9,1],[1,9]]" to 129,
    "[[1,2],[[3,4],5]]" to 143,
    "[[[[0,7],4],[[7,8],[6,0]]],[8,1]]" to 1384,
    "[[[[1,1],[2,2]],[3,3]],[4,4]]" to 445,
    "[[[[3,0],[5,3]],[4,4]],[5,5]]" to 791,
    "[[[[5,0],[7,4]],[5,5]],[6,6]]" to 1137,
    "[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]" to 3488
  )
  magnitudeTests.forEach { (input, expected)  ->
    val sn = SnailNumber.parse(input)
    println("${sn.getMangitude()} expect $expected")
  }
  part1("/day18/part1-test.txt")
  part1("/day18/part1-input.txt")

  part2("/day18/part1-test.txt")
  part2("/day18/part1-input.txt")
}


private fun part1(fileName: String) {
  val inputs = readFileAsLines(fileName)

  val actualSum = inputs.map { SnailNumber.parse(it) }
    .reduce { acc, snailNumber -> acc + snailNumber }
  println("$fileName -> sum: $actualSum magnitude: ${actualSum.getMangitude()}\n")
}

private fun part2(fileName: String) {
  val inputs = readFileAsLines(fileName)
  var maxA: SnailNumber? = null
  var maxB: SnailNumber? = null
  var maximumSum: SnailNumber? = null
  var maximumMagnitude: Long = 0
  for (a in inputs) {
    for (b in inputs) {
      if (a === b) {
        continue
      }
      val sn1 = SnailNumber.parse(a)
      val sn2 = SnailNumber.parse(b)
      val sum = sn1 + sn2
      val mag = sum.getMangitude()
      if (mag > maximumMagnitude) {
        maximumMagnitude = mag
        maximumSum = sum
        maxA = sn1
        maxB = sn2
      }
    }
  }
  println("$fileName maximum magnitude is $maximumMagnitude. This is the magnitude of $maxA + $maxB which reduces to $maximumSum")
}

data class SnailNumber(
  var number: Int? = null,
  var leftSnail: SnailNumber? = null,
  var rightSnail: SnailNumber? = null,
  var _parent: SnailNumber? = null,
) {
  init {
    ensureParent()
  }

  private fun isLeft(): Boolean {
    return _parent?.leftSnail === this
  }

  private fun isRight(): Boolean {
    return _parent?.rightSnail === this
  }

  private fun ensureParent() {
    leftSnail?.setParent(this)
    rightSnail?.setParent(this)
  }

  private fun getDepth(): Int {
    var depth = 0
    var curr: SnailNumber? = this
    while (curr != null) {
      curr = curr._parent
      depth++
    }
    return depth
  }

  private fun setParent(parent: SnailNumber) {
    this._parent = parent
  }

  fun getMangitude(): Long {
    if (number != null) {
      return number!!.toLong()
    }
    return 3 * leftSnail!!.getMangitude() + 2 * rightSnail!!.getMangitude()
  }


  operator fun plus(b: SnailNumber): SnailNumber {
    val left = this.copy()
    val right = b.copy()
    val sum = SnailNumber(leftSnail = left, rightSnail = right)
    sum.reduce()
    return sum
  }

  override fun toString(): String {
    return if (number != null) {
      "$number"
    } else {
      "[$leftSnail,$rightSnail]"
    }
  }

  override fun equals(other: Any?): Boolean {
    return if (other is SnailNumber) {
      this.toString() == other.toString()
    } else {
      super.equals(other)
    }
  }

  fun reduce() {
   // println("start form: $this\n")
    while (true) {
     // println("Reducing $this")
      val explodable = findExplodable()
     // println("Found explodable $explodable")
        if (explodable != null) {
          explodable.explode()
          continue
        }
      val splittable = findSplittable()
     // println("Found splittable $splittable")
      if (splittable != null) {
        splittable.split()
        continue
      }
      break
    }
   //  println("final form: $this\n")
  }

  private fun explode() {
    val leftNeighbour = findLeftNeighbour()
    val rightNeighbour = findRightNeighbour()
  //  println("leftNeighbour=$leftNeighbour rightNeighbour=$rightNeighbour")
    if (leftNeighbour != null) {
      leftNeighbour.number = leftNeighbour.number!! + leftSnail!!.number!!
    }
    if (rightNeighbour != null) {
      rightNeighbour.number = rightNeighbour.number!! + rightSnail!!.number!!
    }
    leftSnail = null
    rightSnail = null
    number = 0
  }

  private fun findLeftNeighbour(): SnailNumber? {
    if (isRight()) {
    return getRightMostBasicSnail(_parent!!.leftSnail!!)
  }
    var curr: SnailNumber? = this
    while (curr?.isLeft() == true) {
      curr = curr._parent
    }
    if (curr?._parent == null) {
      return null
    }
    return getRightMostBasicSnail(curr!!._parent!!.leftSnail!!)
  }

  private fun findRightNeighbour(): SnailNumber? {
    if (isLeft()) {
      return getLeftMostBasicSnail(_parent!!.rightSnail!!)
    }
    var curr: SnailNumber? = this
    while (curr?.isRight() == true) {
      curr = curr._parent
    }
    if (curr?._parent == null) {
      return null
    }
    return getLeftMostBasicSnail(curr!!._parent!!.rightSnail!!)
  }


  private fun findExplodable(): SnailNumber? {
    if (getDepth() > 4 && leftSnail?.number != null && rightSnail?.number != null) {
      return this
    }
    return leftSnail?.findExplodable() ?: rightSnail?.findExplodable()
  }

  private fun findSplittable(): SnailNumber? {
    if (number != null && number!! >= 10) {
      return this
    }
    return leftSnail?.findSplittable() ?: rightSnail?.findSplittable()
  }

  private fun split() {
    val oldNumber = number
    if (oldNumber == null || oldNumber < 10) {
      return
    }
    leftSnail = SnailNumber(number = oldNumber / 2)
    rightSnail = SnailNumber(number = (oldNumber + 1) / 2)
    number = null
    ensureParent()
  }

  companion object {
    fun parse(line: String): SnailNumber {
      val parsed = mutableListOf<SnailNumber>()
      var currSnail: SnailNumber? = null
      for (char in line) {
        if (char == '[') {
          if (currSnail == null) {
            currSnail = SnailNumber()
            parsed.add(currSnail)
          } else {
            if (currSnail.leftSnail == null) {
              currSnail.leftSnail = SnailNumber()
              currSnail.leftSnail!!.setParent(currSnail)
              currSnail = currSnail.leftSnail
            } else if (currSnail.rightSnail == null) {
              currSnail.rightSnail = SnailNumber()
              currSnail.rightSnail!!.setParent(currSnail)
              currSnail = currSnail.rightSnail
            } else {
              throw IllegalStateException()
            }
          }
        } else if (char.isDigit()) {
         val number = char.digitToInt()
          if (currSnail!!.leftSnail == null) {
            currSnail.leftSnail = SnailNumber(number = number)
            currSnail.leftSnail!!.setParent(currSnail)
          } else if (currSnail!!.rightSnail == null) {
            currSnail.rightSnail = SnailNumber(number = number)
            currSnail.rightSnail!!.setParent(currSnail)
          } else {
            throw IllegalStateException()
          }
        } else if (char == ',') {
          // probably don't have to do anything
        } else if (char == ' ') {
          // probably don't have to do anything
        } else if (char == ']') {
          currSnail = currSnail?._parent
        }
      }
      return parsed.first()
    }

    fun getRightMostBasicSnail(snailNumber: SnailNumber): SnailNumber {
      var curr: SnailNumber? = snailNumber
      while (curr?.number == null) {
        curr = curr?.rightSnail
      }
      return curr
    }

    fun getLeftMostBasicSnail(snailNumber: SnailNumber): SnailNumber {
      var curr: SnailNumber? = snailNumber
      while (curr?.number == null) {
        curr = curr?.leftSnail
      }
      return curr
    }
  }
}
