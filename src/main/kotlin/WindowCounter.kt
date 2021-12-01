import Utils.Companion.readFileAsLines

class WindowCounter {

  fun countFromFile(fileName: String): Int {
    return countFromLines(readFileAsLines(fileName))
  }
  private fun countFromLines(lines: List<String>): Int {
    val inputs = lines.mapNotNull { it.toIntOrNull() }
    if (inputs.size < 4) {
      return 0
    }
    val windows = mutableListOf<Int>()
    for (i in 2 until inputs.size) {
      windows.add(element = inputs[i] + inputs[i-1] + inputs[i-2])
    }
    return windows.zipWithNext{ a, b -> b > a }.count { it }
  }

  data class Reading(
    val depth: Int,
    val windowIds: List<String>,
  )

}