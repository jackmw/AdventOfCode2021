import Utils.Companion.readFileAsLines

class WindowCounter() {
  fun part1(fileName: String): Int {
    return countPart1(readFileAsLines(fileName).mapNotNull { it.toIntOrNull() })
  }

  fun part2(fileName: String, windowSize: Int = 3): Int {
    return countWithWindow(readFileAsLines(fileName), windowSize)
  }

  private fun countPart1(inputs: List<Int>): Int {
    return inputs.zipWithNext { a, b -> b > a }.count { it }
  }

  private fun countWithWindow(lines: List<String>, windowSize: Int): Int {
    val inputs = lines.mapNotNull { it.toIntOrNull() }
    if (inputs.size < (windowSize + 1)) {
      return 0
    }
    val windows = mutableListOf<Int>()
    for (i in (windowSize - 1) until inputs.size) {
      var sum = 0
      for (j in 0 until windowSize) {
        sum += inputs[i - j]
      }
      windows.add(sum)
    }
    return windows.zipWithNext{ a, b -> b > a }.count { it }
  }
}