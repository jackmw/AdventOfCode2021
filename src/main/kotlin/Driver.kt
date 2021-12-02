import Utils.Companion.readFileAsLines

class Driver {
  fun driveByFile(fileName: String): Pair<Int, Int> {
    val lines = readFileAsLines(fileName)
    return driveByInstructions(lines.mapNotNull { Instruction.parse(it) })
  }

  fun driveByInstructions(instructions: List<Instruction>): Pair<Int, Int> {
    var horizontal = 0
    var vertical = 0
    for (instruction in instructions) {
      horizontal += instruction.forward
      vertical = (vertical + instruction.down).coerceAtLeast(0)
    }
    return horizontal to vertical
  }
  fun driveByFilePart2(fileName: String): Pair<Int, Int> {
    val lines = readFileAsLines(fileName)
    return driveByInstructionsPart2(lines.mapNotNull { Instruction.parse(it) })
  }

  fun driveByInstructionsPart2(instructions: List<Instruction>): Pair<Int, Int> {
    var aim = 0
    var horizontal = 0
    var vertical = 0
    for (instruction in instructions) {
      horizontal += instruction.forward
      aim += instruction.down
      vertical += aim * instruction.forward
      vertical = vertical.coerceAtLeast(0)
    }
    return horizontal to vertical
  }

  data class Instruction(
    val forward: Int = 0,
    val down: Int = 0,
  ) {
    companion object {
      fun parse(line: String): Instruction? {
        val pieces = line.split(" ").filter { it.isNotBlank() }
        if (pieces.size != 2) {
          return null
        }
        val direction = pieces[0]
        if (direction != "forward" && direction != "down" && direction != "up") {
          return null
        }
        val movement = pieces[1].toIntOrNull() ?: return null


        return when (direction) {
          "forward" -> Instruction(forward = movement)
          "down" -> Instruction(down = movement)
          "up" -> Instruction(down = -movement)
          else -> null
        }
      }
    }
  }
}