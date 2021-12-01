import Utils.Companion.readFileAsLines

class IncCounter {
  fun countFromFile(fileName: String): Int {
    val inputs = readFileAsLines(fileName)
      .mapNotNull { it.toIntOrNull() }
    return countFromInput(inputs)
  }

  fun countFromInput(inputs: List<Int>): Int {
    return inputs.zipWithNext { a, b -> b > a }.count { it }
  }
}