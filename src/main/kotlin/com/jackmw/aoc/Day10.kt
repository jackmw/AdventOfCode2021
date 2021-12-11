package com.jackmw.aoc

import com.jackmw.aoc.Utils.Companion.readFileAsLines

fun main() {
  part1("/day10/part1-test.txt")
  part1("/day10/part1-input.txt")

  part2("/day10/part1-test.txt")
  part2("/day10/part1-input.txt")
}

private val CORRUPTED = mapOf(
  ')' to 3,
  ']' to 57,
  '}' to 1197,
  '>' to 25137,
)

private val INCOMPLETE = mapOf(
  ')' to 1,
  ']' to 2,
  '}' to 3,
  '>' to 4,
)

private fun part1(fileName: String) {
  val lines = readFileAsLines(fileName)
  val chunkParser = ChunkParser()
  val score = lines.map { chunkParser.parseChunks(it) }
    .mapNotNull { it.invalidChar }
    .mapNotNull { CORRUPTED[it] }
    .sum()
  println("score = $score")
}

private fun part2(fileName: String) {
  val lines = readFileAsLines(fileName)
  val chunkParser = ChunkParser()
  val sortedScores = lines.map { chunkParser.parseChunks(it) }
    .filter { it.finishers.isNotEmpty() }
    .map { it.finishers }
    .map { scoreFinisher(it) }
    .sorted()
  println("$fileName score = ${sortedScores[sortedScores.size / 2]}")
}

fun scoreFinisher(finishers: List<Char>): Long {
  var score:Long = 0
  finishers.forEach {
    score = score * 5 + INCOMPLETE[it]!!
  }
  return score
}

class ChunkParser {
  data class ParseResult(
    val chunks: List<Chunk> = emptyList(),
    val invalidChar: Char? = null,
    val finishers: List<Char> = emptyList()
  )
  fun parseChunks(line: String): ParseResult {
    val chunks = mutableListOf<Chunk>()
    var currentChunk: Chunk? = null
    for (char in line.toCharArray()) {
      if (Chunk.isStart(char)) {
        val newChunk = Chunk(start = char, parent = currentChunk)
        currentChunk?.innerChunks?.add(newChunk)
        currentChunk = newChunk
        if (newChunk.parent == null) {
          chunks.add(newChunk)
        }
      } else if (Chunk.isEnd(char)) {
        if (currentChunk == null) {
          return ParseResult(invalidChar = char)
        }
        val expected = Chunk.getEndCharForStartChar(currentChunk.start)
        if (char != expected) {
          return ParseResult(invalidChar = char)
        }
        currentChunk.end = char
        currentChunk = currentChunk.parent
      } else {
        // invalid character
        return ParseResult(invalidChar = char)
      }
    }
    if (currentChunk != null  && currentChunk.end == null) {
      val finishers = finishChunk(currentChunk)
      return ParseResult(chunks = chunks, finishers = finishers)
    }
    return ParseResult(chunks = chunks)
  }

  private fun finishChunk(currentChunk: Chunk): List<Char> {
    val finishers = mutableListOf<Char>()
    if (currentChunk.end != null) {
      throw IllegalStateException()
    }
    val expected = Chunk.getEndCharForStartChar(currentChunk.start)
    finishers.add(expected)
    if (currentChunk.parent != null) {
      finishers.addAll(finishChunk(currentChunk.parent!!))
    }
    return finishers
  }
}

data class Chunk(
  var start: Char,
  var end: Char? = null,
  var parent: Chunk? = null,
  val innerChunks: MutableList<Chunk> = mutableListOf()
) {
  override fun toString(): String {
    val sb = StringBuilder()
    sb.append(start)
    innerChunks.forEach { sb.append(it) }
    end?.let { sb.append(it) }
    return sb.toString()
  }

  companion object {
    fun isStart(char: Char): Boolean {
      return PAIRS.firstOrNull { it.first == char } != null
    }

    fun isEnd(char: Char): Boolean {
      return PAIRS.firstOrNull { it.second == char } != null
    }

    fun getEndCharForStartChar(startChar: Char): Char {
      return PAIRS.firstOrNull { it.first == startChar }!!.second
    }

    private val PAIRS = listOf(
      Pair('(', ')'),
      Pair('[', ']'),
      Pair('{', '}'),
      Pair('<', '>'),
    )
  }
}

