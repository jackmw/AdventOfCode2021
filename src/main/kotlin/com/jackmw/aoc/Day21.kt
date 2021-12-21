package com.jackmw.aoc

import java.math.BigInteger

fun main() {
  val game = DiceGame()
  game.playWithPracticeDice(4, 8)
  game.playWithPracticeDice(4, 2)

  part2(4, 8)
  part2(4, 2)
}

fun part2(p1Start: Int, p2Start: Int) {
  val (p1, p2) = DiracDiceGame.playWithStartingPosition2(p1Start, p2Start)
  println("p1start=$p1Start and p2Start=$p2Start Player 1 wins in $p1 universes and player 2 wins in $p2 universes")
  println(maxOf(p1, p2))
}

data class DiracDiceGame(
  val p1Position: Int,
  val p2Position: Int,
  val p1Score: Int,
  val p2Score: Int,
  val isP1Turn: Boolean = true,
) {
  companion object {
    fun playWithStartingPosition2(p1Position: Int, p2Position: Int, gamePoint: Int = 21): Pair<BigInteger, BigInteger> {
      var p1Wins = BigInteger.valueOf(0)
      var p2Wins = BigInteger.valueOf(0)
      // Game state to count map
      val initialState = DiracDiceGame(
        p1Position = p1Position,
        p2Position = p2Position,
        p1Score = 0,
        p2Score = 0,
        isP1Turn = true,
      )
      // Game state to count map
      val games = mutableMapOf(initialState to BigInteger.valueOf(1))
      // each turn there are the following rolls
      // 	1,1,1 -> 3	2,1,1 -> 4	3,1,1 -> 5
      //	1,1,2 -> 4	2,1,2 -> 5	3,1,2 -> 6
      //	1,1,3 -> 5	2,1,3 -> 6	3,1,3 -> 7
      //	1,2,1 -> 4	2,2,1 -> 5	3,2,1 -> 6
      //	1,2,2 -> 5	2,2,2 -> 6	3,2,2 -> 7
      //	1,2,3 -> 6	2,2,3 -> 7	3,2,3 -> 8
      //	1,3,1 -> 5	2,3,1 -> 6	3,3,1 -> 7
      //	1,3,2 -> 6	2,3,2 -> 7	3,3,2 -> 8
      //	1,3,3 -> 7	2,3,3 -> 8	3,3,3 -> 9
      // that means each turn the following outcomes are possible
      // 3: 1 time  4: 3 times 5: 6 times  6: 7 times
      // 7: 6 times 8: 3 times 9: 1 time
      val newMovesMap = mapOf(3 to 1, 4 to 3, 5 to 6, 6 to 7, 7 to 6, 8 to 3, 9 to 1)
        .map { (key, value) -> key to value.toBigInteger() }
        .toMap()
      while (games.isNotEmpty()) {
        val (game, gameCount) = games.entries.first()
        games.remove(game)
        if (game.p1Score >= gamePoint) {
          p1Wins += gameCount
          continue
        } else if (game.p2Score >= gamePoint) {
          p2Wins += gameCount
          continue
        }
        if (game.isP1Turn) {
          for ((movement, moveCount) in newMovesMap) {
            games.compute(game.p1Move(movement)) { _, value -> (value ?: BigInteger.ZERO) + moveCount * gameCount }
          }
        } else {
          for ((movement, moveCount) in newMovesMap) {
            games.compute(game.p2Move(movement)) { _, value -> (value ?: BigInteger.ZERO) + moveCount * gameCount }
          }
        }
      }
      return p1Wins to p2Wins
    }
//    fun playWithStartingPosition(p1Position: Int, p2Position: Int, gamePoint: Int = 21): Pair<Long, Long> {
//      var p1Wins = 0L
//      var p2Wins = 0L
//      val games = mutableListOf(DiracDiceGame(p1Position = p1Position, p2Position = p2Position, p1Score = 0, p2Score = 0))
//      var lastReported = 0
//      while (games.isNotEmpty()) {
//        if (games.size / 1000000 > lastReported) {
//          println("temp p1Wins=$p1Wins p2Wins=$p2Wins games=${games.size}")
//          lastReported++
//        }
//        val game = games.removeAt(0)
//        println(game)
//        if (game.p1Score >= gamePoint) {
//          println("temp p1Wins=$p1Wins p2Wins=$p2Wins games=${games.size}")
//          p1Wins++
//          continue
//        } else if (game.p2Score >= gamePoint) {
//          println("temp p1Wins=$p1Wins p2Wins=$p2Wins games=${games.size}")
//          p2Wins++
//          continue
//        }
//        if (game.isP1Turn) {
//          // p1 roll dice
//          games.add(game.p1RollDice(1))
//          games.add(game.p1RollDice(2))
//          games.add(game.p1RollDice(3))
//        } else {
//          // p2 roll dice
//          games.add(game.p2RollDice(1))
//          games.add(game.p2RollDice(2))
//          games.add(game.p2RollDice(3))
//        }
//      }
//      return p1Wins to p2Wins
//    }
  }

  private fun p1Move(movement: Int): DiracDiceGame {
    if (!isP1Turn) throw IllegalStateException()
    var newPosition = p1Position + movement
    if (newPosition > 10) newPosition -= 10
    return copy(
      p1Position = newPosition,
      isP1Turn = false,
      p1Score = p1Score + newPosition,
    )
  }

  private fun p2Move(movement: Int): DiracDiceGame {
    if (isP1Turn) throw IllegalStateException()
    var newPosition = p2Position + movement
    if (newPosition > 10) newPosition -= 10
    return copy(
      p2Position = newPosition,
      isP1Turn = true,
      p2Score = p2Score + newPosition,
    )
  }
//
//  private fun p1RollDice(diceRoll: Int): DiracDiceGame {
//    if (!isP1Turn) throw IllegalStateException()
//    if (rolls.size == 3) throw IllegalStateException()
//    val newRolls = rolls + diceRoll
//    return if (newRolls.size == 3) {
//      // move player 1
//      var newPosition = p1Position + newRolls.sum()
//      while (newPosition > 10) {
//        newPosition -= 10
//      }
//      // change to p2
//      this.copy(
//        p1Position = newPosition,
//        p1Score = p1Score + newPosition,
//        isP1Turn = false,
//        rolls = listOf(),allRolls = allRolls + diceRoll, p1Positions = p1Positions + p1Position
//      )
//    } else {
//      this.copy(rolls = newRolls, allRolls = allRolls + diceRoll, p1Positions = p1Positions + p1Position)
//    }
//  }
//
//
//  private fun p2RollDice(diceRoll: Int): DiracDiceGame {
//    if (isP1Turn) throw IllegalStateException()
//    if (rolls.size == 3) throw IllegalStateException()
//    val newRolls = rolls + diceRoll
//    return if (newRolls.size == 3) {
//      // move player 2
//      var newPosition = p2Position + newRolls.sum()
//      while (newPosition > 10) {
//        newPosition -= 10
//      }
//      // change to p1
//      this.copy(
//        p2Position = newPosition,
//        p2Score = p2Score + newPosition,
//        isP1Turn = true,
//        rolls = listOf(), allRolls = allRolls + diceRoll, p2Positions = p2Positions + p2Position
//      )
//    } else {
//      this.copy(rolls = newRolls, allRolls = allRolls + diceRoll, p2Positions = p2Positions + p2Position)
//    }
//  }
}

class DiceGame {
  fun playWithPracticeDice(p1Start:Int, p2Start: Int) {
    var p1Score = 0
    var p2Score = 0
    var p1Position = p1Start
    var p2Position = p2Start
    val dice = PracticeDice()
    while (p1Score < 1000 && p2Score < 1000) {
      val p1Moves = dice.rollDice() + dice.rollDice() + dice.rollDice()
      p1Position += p1Moves
      while (p1Position > 10) {
        p1Position -= 10
      }
      // println("player 1 roll sum $p1Moves and moves to $p1Position")
      p1Score += p1Position
      if (p1Score >= 1000) {
        println("player 1 wins with score $p1Score")
        break
      }
      val p2Moves = dice.rollDice() + dice.rollDice() + dice.rollDice()
      p2Position += p2Moves
      while (p2Position > 10) {
        p2Position -= 10
      }
      p2Score += p2Position
      // println("player 2 roll sum $p2Moves and moves to $p2Position")
      if (p2Score >= 1000) {
        println("player 2 wins with score $p2Score")
        break
      }
    }
    println("player 2 score $p2Score roll count ${dice.getRollCount()}")
  }

}

class PracticeDice {
  private var rollCount = 0
  private var nextRoll = 1

  fun rollDice(): Int {
    val roll = nextRoll
    rollCount++
    nextRoll++
    if (nextRoll > 100) {
      nextRoll = 1
    }
    return roll
  }

  fun getRollCount(): Int {
    return rollCount
  }
}
