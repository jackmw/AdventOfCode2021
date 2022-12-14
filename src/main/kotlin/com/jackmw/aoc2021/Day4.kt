package com.jackmw.aoc2021

import com.jackmw.Utils.Companion.readFileAsLines
import kotlin.properties.Delegates

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
  private var boardSize by Delegates.notNull<Int>()

  constructor(numbers: List<List<Int>>, boardSize:Int = 5) : this() {
    this.boardSize = boardSize
    assert(numbers.size == boardSize)
    for( i in 0 until boardSize) {
      assert(numbers[i].size == boardSize)
    }
    for (row in 0 until boardSize) {
      for (column in 0 until boardSize) {
        val number = numbers[row][column]
        numbersMap[number] = row to column
        unmarkedNumbers.add(number)
      }
    }
  }

  fun markNumber(number: Int): Boolean {
    numbersMap[number] ?: return false
    unmarkedNumbers.remove(number)
    markedNumbers.add(number)
    return true
  }

  fun wins(): Pair<Int?, Int?>? {
    if (markedNumbers.size < boardSize) {
      return null
    }
    val markedPositions = markedNumbers.map { numbersMap[it]!! }
    for (row in 0 until boardSize) {
      val rowWins = markedPositions.filter { it.first == row }.size == boardSize
      if (rowWins) {
        return row to null
      }
    }
    for (column in 0 until boardSize) {
      val columnWins =  markedPositions.filter { it.second == column }.size == boardSize
      if (columnWins) {
        return null to column
      }
    }
    return null
  }

  fun sumOfUnmarked() : Int {
    return unmarkedNumbers.sum()
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
          println("Board $index wins: ${winToString(wins)}")
          return board.sumOfUnmarked() to calledNumber
        }
      }
    }
    return null
  }

  private fun winToString(win: Pair<Int?, Int?>?): String? {
    return when {
      win == null -> null
      win.first != null -> "row ${win.first}"
      win.second != null -> "column ${win.second}"
      else -> ""
    }
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
    println("Board ${winningBoardIndices.last()} wins: ${winToString(lastWin)}")
    return lastWin
  }

  companion object {
    fun parse(lines: List<String>, boardSize: Int = 5): BingoGame {
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
        if (boardLine.size == boardSize) {
          pendingBoardLines.add(boardLine)
          if (pendingBoardLines.size == boardSize) {
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

