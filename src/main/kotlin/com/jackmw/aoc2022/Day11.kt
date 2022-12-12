package com.jackmw.aoc2022

import com.jackmw.aoc2021.Utils

fun main(args: Array<String>) {
  val monkeyBusiness = MonkeyBusiness()
//  monkeyBusiness.part1("/2022/day11/part1-test.txt", worryReducer = 3, rounds = 20)
//   monkeyBusiness.part1("/2022/day11/part1-input.txt", worryReducer = 3, rounds = 20)
//  monkeyBusiness.part2("/2022/day11/part1-test.txt", worryReducer = 3, rounds = 20)
//  monkeyBusiness.part2("/2022/day11/part1-input.txt", worryReducer = 3, rounds = 20)
//  monkeyBusiness.part2("/2022/day11/part1-input.txt", worryReducer = 3, rounds = 20)
//  monkeyBusiness.part2("/2022/day11/part1-test.txt", worryReducer = 1, rounds = 1)
//  monkeyBusiness.part2("/2022/day11/part1-test.txt", worryReducer = 1, rounds = 20)
//  monkeyBusiness.part1("/2022/day11/part1-test.txt", worryReducer = 1, rounds = 1)
  monkeyBusiness.part2("/2022/day11/part1-test.txt", worryReducer = 1, rounds = 20)
 monkeyBusiness.part2("/2022/day11/part1-test.txt", worryReducer = 1, rounds = 1000)
//  monkeyBusiness.part2("/2022/day11/part1-test.txt", worryReducer = 1, rounds = 10000)
   monkeyBusiness.part2("/2022/day11/part1-input.txt", worryReducer = 1, rounds = 10000)
}

class MonkeyBusiness {
  interface Item {
    fun intDivideBy(divisor: Int)
    fun multiplyBy(multiplier: Int)
    fun add(adder: Int)
    fun isDivisibleBy(divisor: Int): Boolean
    fun square()
    fun evaluate(): Int
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

    override fun evaluate(): Int {
      return currentValue
    }

    override fun toString(): String {
      return "$currentValue"
    }
  }

  companion object {
    val primeFactors = mutableSetOf(
 2,   3,   5,   7,  11,  13,  17,  19,  23

,  29,  31,  37,  41,  43,  47,  53,  59,  61

,  67,  71,  73,  79,  83,  89,  97, 101, 103

,  107, 109, 113, 127, 131, 137, 139, 149, 151

,  157, 163, 167, 173, 179, 181, 191, 193, 197

,  199, 211, 223, 227, 229, 233, 239, 241, 251

,  257, 263, 269, 271, 277, 281, 283, 293, 307

,  311, 313, 317, 331, 337, 347, 349, 353, 359

,  367, 373, 379, 383, 389, 397, 401, 409, 419

,  421, 431, 433, 439, 443, 449, 457, 461, 463

,  467, 479, 487, 491, 499, 503, 509, 521, 523

,  541, 547, 557, 563, 569, 571, 577, 587, 593

,  599, 601, 607, 613, 617, 619, 631, 641, 643

,  647, 653, 659, 661, 673, 677, 683, 691, 701

,  709, 719, 727, 733, 739, 743, 751, 757, 761

,  769, 773, 787, 797, 809, 811, 821, 823, 827

,  829, 839, 853, 857, 859, 863, 877, 881, 883

,  887, 907, 911, 919, 929, 937, 941, 947, 953

,  967, 971, 977, 983, 991, 997,1009,1013,1019

,  1021,1031,1033,1039,1049,1051,1061,1063,1069

,  1087,1091,1093,1097,1103,1109,1117,1123,1129

,  1151,1153,1163,1171,1181,1187,1193,1201,1213

,  1217,1223,1229,1231,1237,1249,1259,1277,1279

,  1283,1289,1291,1297,1301,1303,1307,1319,1321

,  1327,1361,1367,1373,1381,1399,1409,1423,1427

,  1429,1433,1439,1447,1451,1453,1459,1471,1481

,  1483,1487,1489,1493,1499,1511,1523,1531,1543

,  1549,1553,1559,1567,1571,1579,1583,1597,1601

,  1607,1609,1613,1619,1621,1627,1637,1657,1663

,  1667,1669,1693,1697,1699,1709,1721,1723,1733

,  1741,1747,1753,1759,1777,1783,1787,1789,1801

,  1811,1823,1831,1847,1861,1867,1871,1873,1877

,  1879,1889,1901,1907,1913,1931,1933,1949,1951

,  1973,1979,1987,1993,1997,1999,2003,2011,2017

,  2027,2029,2039,2053,2063,2069,2081,2083,2087

,  2089,2099,2111,2113,2129,2131,2137,2141,2143

,  2153,2161,2179,2203,2207,2213,2221,2237,2239

,  2243,2251,2267,2269,2273,2281,2287,2293,2297

,  2309,2311,2333,2339,2341,2347,2351,2357,2371

,  2377,2381,2383,2389,2393,2399,2411,2417,2423

,  2437,2441,2447,2459,2467,2473,2477,2503,2521

,  2531,2539,2543,2549,2551,2557,2579,2591,2593

,  2609,2617,2621,2633,2647,2657,2659,2663,2671

,  2677,2683,2687,2689,2693,2699,2707,2711,2713

,  2719,2729,2731,2741,2749,2753,2767,2777,2789

,  2791,2797,2801,2803,2819,2833,2837,2843,2851

,  2857,2861,2879,2887,2897,2903,2909,2917,2927

,  2939,2953,2957,2963,2969,2971,2999,3001,3011

,  3019,3023,3037,3041,3049,3061,3067,3079,3083

,  3089, 3109,4643,
      11083, 15749, 15773,22669, 23623, 33569,55949, 159011,
      225961,477029,
    )
  }

  class PrimeFactorTerm(initialValue: Int): Item, Cloneable {
    private val factors: MutableMap<Int, Int> = mutableMapOf()
    init {
      val tempFactors = reduceToPrimeFactors(initialValue)
      factors += tempFactors
    }

    public override fun clone(): PrimeFactorTerm {
      val copy = PrimeFactorTerm(2)
      copy.factors += this.factors
      return copy
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

    fun multiplyBy(multiplier: PrimeFactorTerm) {
      for ((factor, times) in multiplier.factors) {
        factors.compute(factor) { _, existingValue -> times + (existingValue ?: 0) }
      }
    }

    override fun add(adder: Int) {
      throw UnsupportedOperationException()
    }

    override fun isDivisibleBy(divisor: Int): Boolean {
      // val divisorFactors = reduceToPrimeFactors(divisor)
      val divisible = divisor in factors && factors[divisor]!! > 0
//      if (divisible) {
//        println("JACK_DEBUG: ${printFactors(factors)} IS DIVISIBLE by $divisor")
//      } else {
//        println("JACK_DEBUG: ${printFactors(factors)} IS NOT DIVISIBLE by $divisor")
//      }
      return divisible
    }

    override fun square() {
      for ( (factor, times) in factors) {
        factors[factor] = times + times
      }
    }

    override fun toString(): String {
      return printFactors(factors)
    }

    override fun evaluate(): Int {
      return factors
        .asSequence()
        .map { (key, times) -> key.toBigInteger().pow(times).toInt() }
        .reduce { acc, i -> acc * i }
    }

    companion object {
      private fun reduceToPrimeFactors(value: Int): Map<Int, Int> {
        val factors = mutableMapOf<Int, Int>()
        if (value in primeFactors) {
          factors[value] = 1
        } else {
          var current = value
          for (primeFactor in primeFactors) {
            if (primeFactor <= current) {
              while (current % primeFactor == 0) {
                factors.compute(primeFactor) { _, existingValue -> 1 + (existingValue ?: 0) }
                current /= primeFactor
              }
            }
          }
          if (current != 1) {
            if (current.toBigInteger().isProbablePrime(10)) {
              println("JACK_DEBUG adding $current to prime factors")
              primeFactors.add(current)
            } else {
              throw IllegalStateException("Value cannot be written as prime factors: $value, remainder=$current")
            }
          }
        }
        // println("DEBUG_JACK: $value is equal to ${printFactors(factors)}")
        return factors
      }

      private fun printFactors(factors: Map<Int, Int>, useExponent: Boolean = true): String {
        val sb = StringBuilder()
        factors.asSequence()
          .filter { it.value > 0 }
          .forEach { (factor, times) ->
            if (sb.isNotEmpty()) {
              sb.append(" x ")
            }
            if (useExponent) {
              if (times == 1) {
                sb.append("$factor")
              } else {
                sb.append("$factor^$times")
              }
            } else {
              sb.append("$factor")
              repeat(times - 1) {
                sb.append(" x $factor")
              }
            }
          }
        return sb.toString()
      }
    }
  }


  class BigItem(initialValue: Int): Item {
    private val terms: MutableList<PrimeFactorTerm> = mutableListOf()
    init {
      terms.add(PrimeFactorTerm(initialValue))
    }

    override fun intDivideBy(divisor: Int) {
      val evaluate = evaluate()
      val newValue = evaluate / divisor
      // println("JACK_DEBUG INTDIV $evaluate / $divisor = $newValue")
      terms.clear()
      terms.add(PrimeFactorTerm(newValue))
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
      val divisible = terms.all { it.isDivisibleBy(divisor) }
//      if (divisible) {
//        println("JACK_DEBUG: ${toString()} IS DIVISIBLE by $divisor")
//      } else {
//        println("JACK_DEBUG: ${toString()} IS NOT DIVISIBLE by $divisor")
//      }
      return divisible
    }

    override fun square() {
      val before = toString()
      if (terms.size > 1) {
        if (terms.size == 2) {
          val aSquared = terms.first().clone()
          aSquared.square()
          val bSquared = terms.last().clone()
          bSquared.square()
          val twoAb = terms.first().clone()
          twoAb.multiplyBy(2)
          twoAb.multiplyBy(terms.last().clone())
          terms.clear()
          terms.add(aSquared)
          terms.add(bSquared)
          terms.add(twoAb)
        } else {
          println("JACK_DEBUG: Squaring ${terms.size} is not supported")
          // throw UnsupportedOperationException("Squaring ${terms.size} is not supported")
        }
      } else {
        for (term in terms) {
          term.square()
        }
      }
    }

    override fun evaluate(): Int {
      return terms.sumOf { it.evaluate() }
    }

    override fun toString(): String {
      return terms.joinToString(separator = " + ")
    }
  }

  class Monkey(
    val name: Int,
    private val items: MutableList<Item>,
    var inspectionCount: Long = 0,
    private val operation: (Item) -> Unit,
    private val test: (Item) -> Boolean,
    private val trueMonkeyName: Int,
    private val falseMonkeyName: Int,
  ) {
    fun inspectAndThrowAllItems(monkeys: Map<Int, Monkey>, worryReducer: Int, print: Boolean = false): String {
      val sb = StringBuilder()
      while(items.isNotEmpty()) {
        val item = items.removeAt(0)
        // val originalItem = item.evaluate()
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
        monkeys[receiverName]!!.items.add(item)
//        if (print) {
//          sb.append("JACK_DEBUG: $originalItem becomes -> ${item.evaluate()} and passes to $receiverName\n")
//        }
      }
      return sb.toString()
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
//    val monkeysWithSmallItems = parseFile(fileName) { SmallItem(it) }
//      .sortedBy { it.name }
//      .associateBy { it.name }

    val monkeysWithBigItems = parseFile(fileName) { BigItem(it) }
      .sortedBy { it.name }
      .associateBy { it.name }

    repeat(rounds) {
      for ((_, monkey) in monkeysWithBigItems) {
        val t2 = monkey.inspectAndThrowAllItems(monkeysWithBigItems, worryReducer = worryReducer, print = true)
//        println("int: \n$t1")
//        println("new: \n$t2")
//        if (t1 != t2) {
//          throw IllegalStateException("omg")
//        }
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
    println("Part2: rounds=$rounds, worryReducer=$worryReducer, $fileName -> monkeyBusinessLevel: $monkeyBusinessLevel")
  }


}



