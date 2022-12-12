package com.jackmw.aoc2022

import com.jackmw.aoc2021.Utils
import java.math.BigInteger

fun main(args: Array<String>) {
  val monkeyBusiness = MonkeyBusiness()
  monkeyBusiness.part1("/2022/day11/part1-test.txt", worryReducer = 3, rounds = 20)
  monkeyBusiness.part1("/2022/day11/part1-input.txt", worryReducer = 3, rounds = 20)
  monkeyBusiness.part2("/2022/day11/part1-test.txt", worryReducer = 1, rounds = 1)
  monkeyBusiness.part2("/2022/day11/part1-test.txt", worryReducer = 1, rounds = 20)
//  monkeyBusiness.part1("/2022/day11/part1-test.txt", worryReducer = 1, rounds = 1)
//  monkeyBusiness.part1("/2022/day11/part1-test.txt", worryReducer = 1, rounds = 20)
 monkeyBusiness.part1("/2022/day11/part1-test.txt", worryReducer = 1, rounds = 1000)
  monkeyBusiness.part1("/2022/day11/part1-test.txt", worryReducer = 1, rounds = 10000)
   monkeyBusiness.part1("/2022/day11/part1-input.txt", worryReducer = 1, rounds = 10000)
}

class MonkeyBusiness {
  interface Item {
    fun intDivideBy(divisor: Int)
    fun multiplyBy(multiplier: Int)
    fun add(adder: Int)
    fun isDivisibleBy(divisor: Int): Boolean
    fun square()
  }

  class SmallItem(initialValue: Int): Item {
    private var currentValue: Int = initialValue
    override fun intDivideBy(divisor: Int) {
      currentValue /= divisor
    }

    override fun multiplyBy(multiplier: Int) {
      currentValue *= multiplier
    }

    override fun add(adder: Int) {
      currentValue += adder
    }

    override fun isDivisibleBy(divisor: Int): Boolean {
      return currentValue % divisor == 0
    }

    override fun square() {
      currentValue *= currentValue
    }
  }

  companion object {
    val primeFactors = setOf(2, 3, 5, 7, 11, 13, 17, 19, 23, 37, 79, 97)
  }

  class PrimeFactorTerm(initialValue: Int): Item {
    private val factors: MutableMap<Int, Int> = mutableMapOf()
    init {
      val tempFactors = reduceToPrimeFactors(initialValue)
      factors += tempFactors
    }

    override fun intDivideBy(divisor: Int) {
      throw UnsupportedOperationException()
    }

    override fun multiplyBy(multiplier: Int) {
      val multiplierFactors = reduceToPrimeFactors(multiplier)
      for ((factor, times) in multiplierFactors) {
        factors.compute(factor) { _, existingValue -> times + (existingValue ?: 0) }
      }
    }

    override fun add(adder: Int) {
      throw UnsupportedOperationException()
    }

    override fun isDivisibleBy(divisor: Int): Boolean {
      // val divisorFactors = reduceToPrimeFactors(divisor)
      return divisor in factors && factors[divisor]!! > 0
    }

    override fun square() {
      for ( (factor, _) in factors) {
        factors[factor] = factors[factor]!! + 1
      }
    }

    override fun toString(): String {
      val sb = StringBuilder()
      factors.asSequence()
        .filter { it.value > 0 }
        .forEach { (factor, times) ->
          if (sb.isNotEmpty()) {
            sb.append(" x ")
          }
          sb.append("$factor^$times")
        }
      return sb.toString()
    }

    companion object {
      private fun reduceToPrimeFactors(value: Int): Map<Int, Int> {
        var current = value
        val factors = mutableMapOf<Int, Int>()
        for (primeFactor in primeFactors) {
          while (current % primeFactor == 0) {
            factors.compute(primeFactor) { _, existingValue -> 1 + (existingValue ?: 0) }
            current /= primeFactor
          }
        }
        if (current != 1) {
          throw IllegalStateException("Value cannot be written as prime factors: $value")
        }
        return factors
      }
    }
  }


  class BigItem(initialValue: Int): Item {
    private val terms: MutableList<PrimeFactorTerm> = mutableListOf()
    init {
      terms.add(PrimeFactorTerm(initialValue))
    }

    override fun intDivideBy(divisor: Int) {
      throw UnsupportedOperationException()
    }

    override fun multiplyBy(multiplier: Int) {
      for (term in terms) {
        term.multiplyBy(multiplier)
      }
    }

    override fun add(adder: Int) {
      terms.add(PrimeFactorTerm(adder))
    }

    override fun isDivisibleBy(divisor: Int): Boolean {
      return terms.all { it.isDivisibleBy(divisor) }
    }

    override fun square() {
      if (terms.size > 1) {
        throw UnsupportedOperationException("squaring ${terms.size} terms.")
      }
      for (term in terms) {
        term.square()
      }
    }

    override fun toString(): String {
      return terms.joinToString(separator = " + ")
    }
  }

//  class BigItem(private val initialValue: Int): Item {
//    private val lazyMultiplyBy = mutableListOf<Int>()
//    private val divisibleBy = mutableSetOf<Int>()
//    private var currentValue: BigInteger = initialValue.toBigInteger()
//
//    init {
//      add(0)
//    }
//
//    fun isDivisibleBy(divisor: Int): Boolean {
//      if (divisor !in setOf(19, 3, 13, 7, 5, 11, 17, 2, 23)) {
//        throw IllegalStateException("Unknown divisor $divisor")
//      }
//      return divisor in divisibleBy
//    }
//
//    fun multiplyBy(multiplier: Int) {
//      divisibleBy.add(multiplier)
//      lazyMultiplyBy.add(multiplier)
//    }
//
//    private fun applyLazyMultiplies() {
//      var newValue: BigInteger = currentValue
//      for (multiplier in lazyMultiplyBy) {
//        newValue *= multiplier.toBigInteger()
//      }
//      currentValue = newValue
//      lazyMultiplyBy.clear()
//    }
//
//    private fun calculateDivisibleBy() {
//      divisibleBy.clear()
//      val divisors = listOf(19, 3, 13, 7, 5, 11, 17, 2, 23)
//
//      for(divisor in divisors) {
//        val reminder = initialValue % divisor
//        if (reminder == 0) {
//          divisibleBy.add(divisor)
//          continue
//        }
//        val remainder = currentValue % divisor.toBigInteger()
//        if (remainder == BigInteger.ZERO) {
//          divisibleBy.add(divisor)
//        }
//      }
//    }
//
//    fun add(adder: Int) {
//      applyLazyMultiplies()
//      if (adder != 0) {
//        currentValue += adder.toBigInteger()
//      }
//      calculateDivisibleBy()
//    }
//
//    fun square() {
//      if (currentValue == initialValue.toBigInteger()) {
//        lazyMultiplyBy.addAll(lazyMultiplyBy)
//        lazyMultiplyBy.add(initialValue)
//      } else {
//        applyLazyMultiplies()
//        currentValue *= currentValue
//        calculateDivisibleBy()
//      }
//    }
//
//    fun divideBy(divisor: Int) {
//      if (divisor in lazyMultiplyBy) {
//        lazyMultiplyBy.remove(divisor)
//      } else {
//        currentValue /= divisor.toBigInteger()
//      }
//      calculateDivisibleBy()
//    }
//  }

  class Monkey(
    val name: Int,
    private val items: MutableList<Item>,
    var inspectionCount: Long = 0,
    private val operation: (Item) -> Unit,
    private val test: (Item) -> Boolean,
    private val trueMonkeyName: Int,
    private val falseMonkeyName: Int,
  ) {
    fun inspectAndThrowAllItems(monkeys: Map<Int, Monkey>, worryReducer: Int, print: Boolean = false) {
      while(items.isNotEmpty()) {
        val item = items.removeAt(0)
        val originalItem = item.toString()
        if (print) {
          println("JACK_DEBUG: $name inspects $originalItem")
        }
        inspectionCount++
        operation.invoke(item)
        if (worryReducer != 1) {
          item.intDivideBy(worryReducer)
        }
        val receiverName = if (test.invoke(item)) {
          trueMonkeyName
        } else {
          falseMonkeyName
        }
        if (print) {
          println("JACK_DEBUG: $originalItem mutates to $item and passes to $receiverName")
        }
        monkeys[receiverName]!!.items.add(item)
      }
    }
  }

  private fun <T: Item> parseFile(fileName: String, itemParser: (Int) -> T): List<Monkey> {
    val lines = Utils.readFileAsLines(fileName).filter { it.isNotBlank() }
    val monkeys = mutableListOf<Monkey>()
    for (i in lines.indices step 6) {
      val monkeyName = lines[i].dropLast(1).substringAfter("Monkey ").toInt()
      val startingItems = lines[i+1].substringAfter("Starting items: ")
        .splitToSequence(',')
        .map { itemParser.invoke(it.trim().toInt()) }
        .toMutableList()
      val operation = parseOperation(lines[i+2])
      val test = parseTest(lines[i+3])
      val trueMonkeyName = lines[i+4].substringAfter("If true: throw to monkey ").toInt()
      val falseMonkeyName = lines[i+5].substringAfter("If false: throw to monkey ").toInt()
      monkeys.add(
        Monkey(
          name = monkeyName,
          items = startingItems.toMutableList(),
          inspectionCount = 0,
          operation = operation,
          test = test,
          trueMonkeyName = trueMonkeyName,
          falseMonkeyName = falseMonkeyName,
        )
      )
    }
    return monkeys
  }

  private fun parseTest(line: String): (Item) -> Boolean {
    val divisor = line.substringAfter("Test: divisible by ").toInt()
    return { it -> it.isDivisibleBy(divisor) }
  }

  private fun parseOperation(line: String): (Item) -> Unit {
    val info = line.substringAfter("Operation: new = old ")
    return if (info == "* old") {
      { it.square() }
    } else {
      val operator = info[0]
      val operand = info.drop(2).toInt()
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
        monkey.inspectAndThrowAllItems(monkeys, worryReducer = worryReducer)
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

  fun part2(fileName: String, worryReducer: Int, rounds: Int) {
    val monkeys = parseFile(fileName) { BigItem(it) }
      .sortedBy { it.name }
      .associateBy { it.name }

    repeat(rounds) {
      for ((_, monkey) in monkeys) {
        monkey.inspectAndThrowAllItems(monkeys, worryReducer = worryReducer, print = true)
      }
    }

    for ((_, monkey) in monkeys) {
      println("Part2: Monkey ${monkey.name} inspected items ${monkey.inspectionCount} times.")
    }

    val monkeyBusinessLevel = monkeys.values.asSequence()
      .map { it.inspectionCount }
      .sortedDescending()
      .take(2)
      .reduce { a, b -> a * b }
    println("Part2: rounds=$rounds, worryReducer=$worryReducer, $fileName -> monkeyBusinessLevel: $monkeyBusinessLevel")
  }


}



