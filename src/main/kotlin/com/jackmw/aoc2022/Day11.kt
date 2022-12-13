package com.jackmw.aoc2022

import com.jackmw.aoc2021.Utils

fun main(args: Array<String>) {
  val monkeyBusiness = MonkeyBusiness()
  monkeyBusiness.part1("/2022/day11/part1-test.txt", worryReducer = 3, rounds = 20)
  monkeyBusiness.part1("/2022/day11/part1-input.txt", worryReducer = 3, rounds = 20)

  monkeyBusiness.part2("/2022/day11/part1-test.txt", rounds = 20)
  for (rounds in 1000 .. 10000 step 1000) {
    monkeyBusiness.part2("/2022/day11/part1-test.txt", rounds = rounds)
  }
  monkeyBusiness.part2("/2022/day11/part1-input.txt", rounds = 10000)
}

class MonkeyBusiness {
  interface Item {
    fun intOperation(op: (Long) -> Long)
    fun multiplyBy(multiplier: Long)
    fun add(adder: Long)
    fun isDivisibleBy(divisor: Long): Boolean
    fun square()
  }

  class SmallItem(initialValue: Long): Item {
    private var currentValue: Long = initialValue

    override fun intOperation(op: (Long) -> Long) {
      currentValue = op(currentValue)
    }

    override fun multiplyBy(multiplier: Long) {
      currentValue *= multiplier
    }

    override fun add(adder: Long) {
      currentValue += adder
    }

    override fun isDivisibleBy(divisor: Long): Boolean {
      return currentValue % divisor == 0L
    }

    override fun square() {
      currentValue *= currentValue
    }

    override fun toString(): String {
      return "$currentValue"
    }
  }

  class Monkey(
    val name: Int,
    private val items: MutableList<Item>,
    var inspectionCount: Long = 0,
    private val operation: (Item) -> Unit,
    val testDivisor: Long,
    private val trueMonkeyName: Int,
    private val falseMonkeyName: Int,
  ) {
    val test: (Item) -> Boolean = { it.isDivisibleBy(testDivisor) }
    fun inspectAndThrowAllItems(monkeys: Map<Int, Monkey>, worryReducer: (Long) -> Long): String {
      val sb = StringBuilder()
      while(items.isNotEmpty()) {
        val item = items.removeAt(0)
        inspectionCount++
        operation.invoke(item)
        item.intOperation(worryReducer)
        val receiverName = if (test.invoke(item)) {
          trueMonkeyName
        } else {
          falseMonkeyName
        }
        monkeys[receiverName]!!.items.add(item)
      }
      return sb.toString()
    }
  }

  private fun <T: Item> parseFile(fileName: String, itemParser: (Long) -> T): List<Monkey> {
    val lines = Utils.readFileAsLines(fileName).filter { it.isNotBlank() }
    val monkeys = mutableListOf<Monkey>()
    for (i in lines.indices step 6) {
      val monkeyName = lines[i].dropLast(1).substringAfter("Monkey ").toInt()
      val startingItems = lines[i+1].substringAfter("Starting items: ")
        .splitToSequence(',')
        .map { itemParser.invoke(it.trim().toLong()) }
        .toMutableList()
      val operation = parseOperation(lines[i+2])
      val testDivisor = parseTestDivisor(lines[i+3])
      val trueMonkeyName = lines[i+4].substringAfter("If true: throw to monkey ").toInt()
      val falseMonkeyName = lines[i+5].substringAfter("If false: throw to monkey ").toInt()
      monkeys.add(
        Monkey(
          name = monkeyName,
          items = startingItems.toMutableList(),
          inspectionCount = 0,
          operation = operation,
          testDivisor = testDivisor,
          trueMonkeyName = trueMonkeyName,
          falseMonkeyName = falseMonkeyName,
        )
      )
    }
    return monkeys
  }

  private fun parseTestDivisor(line: String): Long {
    return line.substringAfter("Test: divisible by ").toLong()
  }

  private fun parseOperation(line: String): (Item) -> Unit {
    val info = line.substringAfter("Operation: new = old ")
    return if (info == "* old") {
      { it.square() }
    } else {
      val operator = info[0]
      val operand = info.drop(2).toLong()
      when (operator) {
        '*' -> { it -> it.multiplyBy(operand) }
        '+' -> { it -> it.add(operand) }
        else -> throw IllegalArgumentException("Unknown operator $operator")
      }
    }
  }

  fun part1(fileName: String, worryReducer: Int, rounds: Int) {
    val monkeys = parseFile(fileName) { SmallItem(it) }
      .sortedBy { it.name }
      .associateBy { it.name }

    repeat(rounds) {
      for ((_, monkey) in monkeys) {
        monkey.inspectAndThrowAllItems(monkeys) { it / 3 }
      }
    }

    for ((_, monkey) in monkeys) {
      println("Monkey ${monkey.name} inspected items ${monkey.inspectionCount} times.")
    }

    val monkeyBusinessLevel = monkeys.values.asSequence()
      .map { it.inspectionCount }
      .sortedDescending()
      .take(2)
      .reduce { a, b -> a * b }
    println("rounds=$rounds, worryReducer=$worryReducer, $fileName -> monkeyBusinessLevel: $monkeyBusinessLevel")
  }

  fun part2(fileName: String, rounds: Int) {
    val monkeysWithBigItems = parseFile(fileName) { SmallItem(it) }
      .sortedBy { it.name }
      .associateBy { it.name }

    val divisorProduct = monkeysWithBigItems.values.map { it.testDivisor }.reduce(Long::times)
    val worryReducer: (Long) -> Long = { it % divisorProduct }
    repeat(rounds) {
      for ((_, monkey) in monkeysWithBigItems) {
        monkey.inspectAndThrowAllItems(monkeysWithBigItems, worryReducer)
      }
    }

    for ((_, monkey) in monkeysWithBigItems) {
      println("Final Part2: Monkey ${monkey.name} inspected items ${monkey.inspectionCount} times.")
    }

    val monkeyBusinessLevel = monkeysWithBigItems.values.asSequence()
      .map { it.inspectionCount }
      .sortedDescending()
      .take(2)
      .reduce { a, b -> a * b }
    println("Part2: rounds=$rounds, $fileName -> monkeyBusinessLevel: $monkeyBusinessLevel")
  }
}



