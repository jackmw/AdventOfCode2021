package com.jackmw.aoc2021

class Utils {
  companion object {
    fun readFileAsLines(fileName: String): List<String> {
      return Utils::class.java.getResource(fileName).readText().split("\n")
    }
  }
}