package com.jackmw.aoc2021

import com.jackmw.Utils.Companion.readFileAsLines

fun main() {
  val negate = AluProgram.parse("/day24/part1-negate.txt")
  negate.printAndRun(listOf(1))

  val threeTimes = AluProgram.parse("/day24/part1-threeTimes.txt")
  threeTimes.printAndRun(listOf(1, 4))
  threeTimes.printAndRun(listOf(2, 6))

  val toBits = AluProgram.parse("/day24/part1-tobits.txt")
  toBits.printAndRun(listOf(8))
  toBits.printAndRun(listOf(7))

  val monad = AluProgram.parse("/day24/part1-input.txt")
  monad.printAndRun("13579246899999".map { it.digitToInt() })

  println("Part 1 - finding the largest valid input")
  val possibles = listOf(
    9..9,
    9.. 9,
    9 downTo 1,
    5..5,
    9..9,
    9..9,
    9..9,
    3..3,
    4..4,
    9 downTo 1,
    5..5,
    8..8,
    9..9,
    7..7,
  )
  val modelNumber = 9995_99934_95897
  for (input2 in 9 downTo 1) {
  for (input3 in 9 downTo 1) {
    for (input9 in 9 downTo 1) {
      var numberToTry = replaceIndex(modelNumber, 2, input2)
      numberToTry = replaceIndex(numberToTry, 3, input3)
      numberToTry = replaceIndex(numberToTry, 9, input9)
      val output = monad.run(numberToTry.toString().map { it.digitToInt() })
      val zValue = output.single { it.fieldName == 'z' }.value
      println("Testing model number $modelNumber z=$zValue")
      if (zValue == 0L) {
        println("$modelNumber is the largest valid model number.")
        break
      }
    }
  }
  }
//  for (modelNumber in 9995_99934_95897 downTo 9915_99934_15897) {
//    if (modelNumber.toString().any { it == '0' }) {
//      continue
//    }
//    if (!checkIsValid(modelNumber, possibles)) {
//      /// println("Model number $modelNumber is not within allowed rules.")
//      continue
//    }
//    val output = monad.run(modelNumber.toString().map { it.digitToInt() })
//    val zValue = output.single { it.fieldName == 'z'}.value
//    println("Testing model number $modelNumber z=$zValue")
//    if (zValue == 0L) {
//      println("$modelNumber is the largest valid model number.")
//      break
//    }
//  }

}

fun replaceIndex(modelNumber: Long, index: Int, replacement: Int): Long {
  val chars = modelNumber.toString().toCharArray()
  chars[index] = "$replacement"[0]
  return String(chars).toLong()
}

fun checkIsValid(modelNumber: Long, possibles: List<IntProgression>): Boolean {
  val chars = modelNumber.toString().toCharArray()

  for (index in chars.indices) {
      val range = possibles[index]
      val digit = chars[index].digitToInt()
      if (digit !in range) {
        return false
      }
  }
  return true
}

fun part1sucks() {
  // 10	12
  // 10	10
  // 12	8
  // 11	4
  // 0	3
  // 15	10
  // 13	6
  // -12	13
  // -15	8
  // -15	1
  // -4	7
  // 10	6
  // -5	9
  // -12	9
}

data class Variable(
  val fieldName: Char,
  var value: Long,
) {
  override fun toString(): String {
    return "$fieldName=$value"
  }
}

data class VariableOrInteger(
  val variable: Variable?,
  val int: Int?,
) {
  val value: Long
    get() = variable?.value ?: int!!.toLong()
}

data class IntermediateInstruction(
  val type: InstructionType,
  val aName: Char,
  val bName: Char?,
  val bValue: Int?,
) {
  enum class InstructionType(
    val instructionSymbol: String,
    val operation: (Variable, VariableOrInteger) -> Unit,
  ) {
    INP("inp", { a, b -> a.value = b.int!!.toLong() }),
    ADD("add", { a, b -> a.value += b.value }),
    MUL("mul", { a, b -> a.value *= b.value }),
    DIV("div", { a, b -> a.value /= b.value }),
    MOD("mod", { a, b -> a.value %= b.value }),
    EQL("eql", { a, b -> a.value = if (a.value == b.value) 1 else 0 }),
  }

  companion object {
    fun parse(lines: List<String>): List<IntermediateInstruction> {
      val instructions: MutableList<IntermediateInstruction> = mutableListOf()
      for (line in lines) {
        val pieces = line.split(" ")
        if (pieces.size < 2) throw IllegalArgumentException("\"$line\" is invalid")
        val type = InstructionType.values().single { it.instructionSymbol == pieces[0] }
        val aName = pieces[1][0]
        val (bName, bValue) = if (type == InstructionType.INP) {
          null to null
        } else {
          val intValue = pieces[2].toIntOrNull()
          val variableName = if (intValue == null) {
            pieces[2][0]
          } else {
            null
          }
          variableName to intValue
        }
        instructions.add(IntermediateInstruction(type, aName, bName = bName, bValue = bValue))
      }
      return instructions
    }
  }
}

class AluProgram(
  private val name: String,
  private val rawInstructions: List<IntermediateInstruction>
) {
  private val inputCount = rawInstructions.count { it.type == IntermediateInstruction.InstructionType.INP }

  fun run(inputs: List<Int> = listOf()): Collection<Variable> {
    if (inputs.size < inputCount) throw IllegalArgumentException("Requires $inputCount input(s), received ${inputs.size}")
    val variables = setOf('w', 'x', 'y', 'z').associateWith { Variable(it, 0L) }
    run(variables, inputs)
    return variables.values
  }

  private fun run(variables: Map<Char, Variable>, inputs: List<Int>) {
    var inputIndex = 0
    for (instruction in rawInstructions) {
      val operandOne = variables[instruction.aName]!!
      val operandTwo = if (instruction.type == IntermediateInstruction.InstructionType.INP) {
        VariableOrInteger(int = inputs[inputIndex++], variable = null)
      } else {
        VariableOrInteger(variable = variables[instruction.bName], int = instruction.bValue)
      }
      instruction.type.operation.invoke(operandOne, operandTwo)
    }
  }

  fun printAndRun(inputs: List<Int> = listOf()): Collection<Variable> {
    val vars = run(inputs)
    println("$name($inputs) = $vars")
    return vars
  }

  companion object {
    fun parse(fileName: String): AluProgram {
      return AluProgram(fileName, IntermediateInstruction.parse(readFileAsLines(fileName)))
    }
  }
}