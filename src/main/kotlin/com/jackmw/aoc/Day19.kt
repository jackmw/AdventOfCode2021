package com.jackmw.aoc

import com.jackmw.aoc.Utils.Companion.readFileAsLines

fun main() {
}

data class BeaconCoord(
  val x: Int,
  val y: Int,
  val z: Int,
)

data class ScannerReport(
  val scannerName: String,
  val coords: List<BeaconCoord>,
) {
  companion object {
    fun parseReports(lines: List<String>): List<ScannerReport> {
      val reportBuilder = mutableMapOf<String, MutableList<BeaconCoord>>()
      var currentReport: MutableList<BeaconCoord> = mutableListOf()
      lines.forEach { line ->
        if (line.isBlank()) {
          // do nothing
        }else if(line.startsWith("---")) {
          val scannerName = line.replace("---", "").trim()
          currentReport = mutableListOf()
          reportBuilder[scannerName] = currentReport
        } else {
          val pieces = line.split(",")
          val x = pieces[0].toInt()
          val y = pieces[1].toInt()
          val z = pieces[2].toInt()
          currentReport.add(BeaconCoord(x, y, z))
        }
      }
      return reportBuilder.map { entry ->
        ScannerReport(entry.key, entry.value)
      }
    }
  }
}


