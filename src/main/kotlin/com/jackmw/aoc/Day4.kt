package com.jackmw.aoc

import com.jackmw.aoc.Utils.Companion.readFileAsLines

fun main(args: Array<String>) {
  part1("/day4/part1-test.txt")
  part1("/day4/part1-input.txt")

  part2("/day4/part1-test.txt")
  part2("/day4/part1-input.txt")
}

private fun part1(fileName: String) {
  val bingoGame = BingoGame.parse(readFileAsLines(fileName))
  println("Found ${bingoGame.boards.size} and ${bingoGame.calledNumbers.size} numbers.")
  val win = bingoGame.findFirstWinningBoard()
  if (win == null) {
    println("No winning boards")
  } else {
    println("Winning score is ${win.first} x ${win.second} = ${win.first * win.second}")
  }
}

private fun part2(fileName: String) {
  val bingoGame = BingoGame.parse(readFileAsLines(fileName))
  println("Found ${bingoGame.boards.size} and ${bingoGame.calledNumbers.size} numbers.")
  val win = bingoGame.findLastWinningBoard()
  if (win == null) {
    println("No winning boards")
  } else {
    println("Winning score is ${win.first} x ${win.second} = ${win.first * win.second}")
  }
}

private class BingoBoard() {
  private val numbersMap: MutableMap<Int, Pair<Int, Int>> = mutableMapOf()
  private val markedNumbers: MutableSet<Int> = mutableSetOf()
  private val unmarkedNumbers: MutableSet<Int> = mutableSetOf()
  private val marks: Array<BooleanArray> = arrayOf(
    BooleanArray(5), BooleanArray(5), BooleanArray(5),BooleanArray(5),BooleanArray(5),
  )

  constructor(numbers: List<List<Int>>) : this() {
    assert(numbers.size == 5)
    for( i in 0 until 5) {
      assert(numbers[i].size == 5)
    }
    for (row in 0 until 5) {
      for (column in 0 until 5) {
        val number = numbers[row][column]
        numbersMap[number] = row to column
        unmarkedNumbers.add(number)
      }
    }
  }

  fun markNumber(number: Int): Boolean {
    val position = numbersMap[number] ?: return false
    unmarkedNumbers.remove(number)
    markedNumbers.add(number)
    marks[position.first][position.second] = true
    return true
  }

  fun wins(): Pair<Int?, Int?>? {
    if (markedNumbers.size < 5) {
      return null
    }
    for (row in 0 until 5) {
      var rowWins = true
      for (column in 0 until 5) {
        rowWins = rowWins && marks[row][column]
      }
      if (rowWins) {
        return row to null
      }
    }
    for (column in 0 until 5) {
      var columnWins = true
      for (row in 0 until 5) {
        columnWins = columnWins && marks[row][column]
      }
      if (columnWins) {
        return null to column
      }
    }
    return null
  }

  fun sumOfUnmarked() : Int {
    return unmarkedNumbers.sum()
  }

  fun getRowSum(row: Int): Int {
    return numbersMap.filter { it.value.first == row }
    .map { it.key }
    .sum()
  }

  fun getColumnSum(column: Int): Int {
    return numbersMap.filter { it.value.second == column }
      .map { it.key }
      .sum()
  }
}

private data class BingoGame(
  val boards: List<BingoBoard>,
  val calledNumbers: List<Int>,
) {
  fun findFirstWinningBoard(): Pair<Int, Int>? {
    for (calledNumber in calledNumbers) {
      boards.forEachIndexed { index, board ->
        val marked = board.markNumber(calledNumber)
        if (marked) {
          val wins = board.wins() ?: return@forEachIndexed
          println("Board $index wins: $wins")
          return board.sumOfUnmarked() to calledNumber
        }
      }
    }
    return null
  }

  fun findLastWinningBoard(): Pair<Int, Int>? {
    val winningBoardIndices = mutableSetOf<Int>()
    var lastWin: Pair<Int, Int>? = null
    for (calledNumber in calledNumbers) {
      boards.forEachIndexed { index, board ->
        val marked = board.markNumber(calledNumber)
        if (marked) {
          val wins = board.wins()
          if (wins != null && !winningBoardIndices.contains(index)) {
            winningBoardIndices.add(index)
            lastWin = board.sumOfUnmarked() to calledNumber
          }
        }
      }
    }
    println("Board ${winningBoardIndices.last()} wins: $lastWin")
    return lastWin
  }

  companion object {
    fun parse(lines: List<String>): BingoGame {
      var numbers: List<Int>? = null
      val pendingBoardLines: MutableList<List<Int>> = mutableListOf()
      val boards: MutableList<BingoBoard> = mutableListOf()
      for (line in lines) {
        if (line.trim().isEmpty()) {
          continue
        }
        val pieces = line.trim().split(',')
        if (pieces.size > 1) {
          numbers = pieces.mapNotNull { it.toIntOrNull() }
        }
        val boardLine = line.trim().split(' ').mapNotNull { it.toIntOrNull() }
        if (boardLine.size == 5) {
          pendingBoardLines.add(boardLine)
          if (pendingBoardLines.size == 5) {
            boards.add(BingoBoard(pendingBoardLines))
            pendingBoardLines.clear()
          }
        }
      }
      if (numbers != null && boards.isNotEmpty()) {
        return BingoGame(boards, numbers)
      } else {
        throw IllegalArgumentException("Invalid input")
      }
    }
  }
}

