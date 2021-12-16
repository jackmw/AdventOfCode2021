package com.jackmw.aoc

import com.jackmw.aoc.Utils.Companion.readFileAsLines
import java.util.PriorityQueue

fun main() {
  println(Bits.parsePackets("D2FE28"))
  println(Bits.parsePackets("38006F45291200"))
  println(Bits.parsePackets("EE00D40C823060"))
  println(Bits.parsePackets("8A004A801A8002F478"))
  println(Bits.parsePackets("620080001611562C8802118E34"))
  println(Bits.parsePackets("C0015000016115A2E0802F182340"))
  println(Bits.parsePackets("A0016C880162017C3686B18A3D4780"))
  println()
  part1("/day16/part1-test.txt")
  part1("/day16/part1-input.txt")
}


private fun part1(fileName: String) {
  val packets = Bits.parsePackets(readFileAsLines(fileName).first().trim().uppercase())
  println("$fileName -> sum of version: ${packets.sumOf { it.getSumOfVersion() }}")
}

private fun part2(fileName: String) {
}

class Bits {

  abstract class Packet(
    val version: Int,
    val type: Int,
  ) {
    open fun getSumOfVersion(): Int {
      return version
    }
  }

  class LiteralPacket(
    version: Int,
    private val content: Int,
  ): Packet(version, type = PACKET_TYPE_LITERAL) {
    override fun toString(): String {
      return "[version=$version, type=$type, content=$content]"
    }
  }

  class OperatorPacket(
    version: Int,
    type: Int,
    private val subPackets: List<Packet>
  ): Packet(version, type) {
    override fun toString(): String {
      return "[version=$version, type=$type, subPackets=$subPackets]"
    }

    override fun getSumOfVersion(): Int {
      return this.version + subPackets.sumOf { it.getSumOfVersion() }
    }
  }

  companion object {
    const val PACKET_TYPE_LITERAL = 4

    private val HEX_TO_BIN = mapOf(
      '0' to listOf('0', '0', '0', '0'),
      '1' to listOf('0', '0', '0', '1'),
      '2' to listOf('0', '0', '1', '0'),
      '3' to listOf('0', '0', '1', '1'),
      '4' to listOf('0', '1', '0', '0'),
      '5' to listOf('0', '1', '0', '1'),
      '6' to listOf('0', '1', '1', '0'),
      '7' to listOf('0', '1', '1', '1'),
      '8' to listOf('1', '0', '0', '0'),
      '9' to listOf('1', '0', '0', '1'),
      'A' to listOf('1', '0', '1', '0'),
      'B' to listOf('1', '0', '1', '1'),
      'C' to listOf('1', '1', '0', '0'),
      'D' to listOf('1', '1', '0', '1'),
      'E' to listOf('1', '1', '1', '0'),
      'F' to listOf('1', '1', '1', '1'),
    )

    fun parsePackets(input: String): List<Packet> {
      val chars = input.flatMap { char -> HEX_TO_BIN[char]!! }
      val (packets, _) = parsePackets(chars, 0)
      println("$input sumOfVersions=${packets.sumOf { it.getSumOfVersion() }}")
      return packets
    }

    private fun parsePackets(chars: List<Char>, startIndex: Int): Pair<List<Packet>, Int> {
      val packets = mutableListOf<Packet>()
      var index = startIndex
      while (index < chars.size) {
        val (packet, newIndex) = try {
          parsePacket(chars, index)
        } catch (e: Exception) {
          e.printStackTrace()
          break
        }
        index = newIndex
        packets.add(packet)
      }
      return packets to index
    }

    private fun parsePacket(chars: List<Char>, startIndex: Int): Pair<Packet, Int> {
      var index = startIndex
      val version = charBinToInt(chars.subList(index, index + 3))
      index += 3
      val type = charBinToInt(chars.subList(index, index + 3))
      index += 3
      if (type == PACKET_TYPE_LITERAL) {
        val (content, newIndex) = parseLiteralPacketContent(chars, index)
        index = newIndex
        return LiteralPacket(version, content) to index
      } else {
        val lengthType = chars[index]
        index++
        if (lengthType == '0') {
          val subPacketLength = charBinToInt(chars.subList(index, index + 15))
          index += 15
          val subPacketContent = chars.subList(index, index + subPacketLength)
          index += subPacketLength
          val (subPackets, _) = parsePackets(subPacketContent, 0)
          return OperatorPacket(version, type, subPackets) to index
        } else if (lengthType == '1') {
          val subPacketCount = charBinToInt(chars.subList(index, index + 11))
          index += 11
          val subPackets = mutableListOf<Packet>()
          for (count in 1 .. subPacketCount) {
            val (subPacket, newIndex) = parsePacket(chars, index)
            subPackets.add(subPacket)
            index = newIndex
          }
          return OperatorPacket(version, type, subPackets) to index
        } else {
          throw IllegalStateException("Invalid length type $lengthType")
        }
      }
    }

    private fun parseLiteralPacketContent(chars: List<Char>, startIndex: Int): Pair<Int, Int> {
      var index = startIndex
      val contents = mutableListOf<Char>()
      while (chars[index] == '1') {
        contents.addAll(chars.subList(index + 1, index + 5))
        index += 5
      }
      // add the last piece
      contents.addAll(chars.subList(index + 1, index + 5))
      index += 5
      return charBinToInt(contents) to index
    }

    private fun charBinToInt(chars: List<Char>): Int {
      val parseChars = chars.toMutableList()
      parseChars.add(0, '0')
      // println("JACK_DEBUG parsing $parseChars to dec")
      return String(parseChars.toCharArray()).toInt(2)
    }
  }
}