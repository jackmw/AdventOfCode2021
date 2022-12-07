package com.jackmw.aoc2022

import com.jackmw.aoc2021.Utils

fun main(args: Array<String>) {
  part1("/2022/day7/part1-test.txt")
  part1("/2022/day7/part1-input.txt")

  part2("/2022/day7/part1-test.txt")
  part2("/2022/day7/part1-input.txt")
}

private fun part1(fileName: String) {
  val cmdsWithOutput = parseFile(fileName)
  val directories = buildDirectory(cmdsWithOutput)
  val sum = directories.values.filter { it.getSize() <= 100000 }.sumOf { it.getSize() }
  println("part1: $fileName -> sum: $sum")
}

private fun part2(fileName: String) {
  val cmdsWithOutput = parseFile(fileName)
  val directories = buildDirectory(cmdsWithOutput)
  val root = directories["/"]!!
  val totalSpace = 70000000L
  val requiredFree = 30000000L
  val currentUsed = root.getSize()
  val minDeleteSize = requiredFree - (totalSpace - currentUsed)
  val dir = directories.values.asSequence()
    .filter { it.getSize() >= minDeleteSize }
    .minBy { it.getSize() }
  println("part2: $fileName -> dir: ${dir.dirName} size: ${dir.getSize()}")
}


fun buildDirectory(cmdsWithOutput: List<CmdWithOutput>): Map<String, Directory> {
  var currDir: Directory? = null
  val dirsByName: MutableMap<String, Directory> = mutableMapOf()
  for ((cmd, output) in cmdsWithOutput) {
    when (cmd) {
      is Ls -> {
        val subDirectories = mutableListOf<Directory>()
        val files = mutableListOf<FileItem>()
        for (lsItem in output) {
          if (lsItem is DirLsOutput) {
            val subDirectory = Directory(lsItem.dirName, parent = currDir!!)
            dirsByName[subDirectory.fullPath] = subDirectory
            subDirectories.add(subDirectory)
          } else if (lsItem is FileLsOutput) {
            val fileItem = FileItem(fileName = lsItem.fileName, fileSize = lsItem.fileSize, parent = currDir!!)
            files.add(fileItem)
          }
        }
        currDir!!.subDirectories.clear()
        currDir!!.subDirectories.addAll(subDirectories)
        currDir!!.files.clear()
        currDir!!.files.addAll(files)
      }

      is CdUp -> {
        currDir = currDir!!.parent
      }

      is CdRoot -> {
        if (dirsByName["/"] == null) {
          dirsByName["/"] = Directory("/", parent = null)
        }
        currDir = dirsByName["/"]!!
      }

      is Cd -> {
        currDir =  currDir!!.subDirectories.single { it.dirName == cmd.dirName }
      }
    }
  }
  return dirsByName
}

data class CmdWithOutput(
  val command: Command,
val output: List<LsOutput> = listOf(),
) {
  override fun toString(): String {
    val sb: StringBuilder = StringBuilder()
    sb.append(command)
    sb.append("\n")
    for (outputItem in output) {
      sb.append("  ").append(outputItem).append("\n")
    }
    return sb.toString()
  }
}

sealed interface Command

class Ls() : Command {
  override fun toString(): String {
    return "ls"
  }
}
class CdUp(): Command {
  override fun toString(): String {
    return "cd .."
  }
}
class CdRoot(): Command {
  override fun toString(): String {
    return "cd /"
  }
}
data class Cd(val dirName: String): Command {
  override fun toString(): String {
    return "cd $dirName"
  }
}


sealed interface LsOutput
data class DirLsOutput(val dirName: String): LsOutput {
  override fun toString(): String {
    return "dir $dirName"
  }
}
data class FileLsOutput(val fileName: String, val fileSize: Long): LsOutput {
  override fun toString(): String {
    return "$fileSize $fileName"
  }
}

data class Directory(
  val dirName: String,
  val parent: Directory?,
  val subDirectories: MutableList<Directory> = mutableListOf(),
  val files: MutableList<FileItem> = mutableListOf(),
) {
  fun getSize(): Long {
    return files.sumOf { it.fileSize} + subDirectories.sumOf { it.getSize() }
  }
  val fullPath: String
    get() {
      val separator = if (parent == null) "" else "/"
      return (parent?.fullPath ?: "") + dirName + separator
    }

  val depth: Int = if (parent == null) 0 else 1 + parent.depth
  override fun toString(): String {
    val sb = StringBuilder()
    repeat(depth) {
      sb.append("  ")
    }
    sb.append("- $dirName (dir)")
    if (subDirectories.isNotEmpty()) {
      for (subDirectory in subDirectories) {
        sb.append("\n")
        sb.append(subDirectory)
      }
    }
    if (files.isNotEmpty()) {
      for (file in files) {
        sb.append("\n")
        sb.append(file)
      }
    }
    return sb.toString()
  }
}

data class FileItem(
  val fileName: String,
  val parent: Directory,
  val fileSize: Long,
) {
  private val depth: Int = 1 + parent.depth
  override fun toString(): String {
    val sb = StringBuilder()
    repeat(depth) {
      sb.append("  ")
    }
    sb.append("- $fileName (file, size=$fileSize)")
    return sb.toString()
  }
}

private fun parseFile(fileName: String): List<CmdWithOutput> {
  val lines = Utils.readFileAsLines(fileName)
  var currentCommand: Command? = null
  var commandOutput: MutableList<LsOutput>? = null
  val cmdsWithOutput: MutableList<CmdWithOutput> = mutableListOf()
  for (line in lines) {
    val parsedCommand: Command? = parseCommand(line)
    if (parsedCommand != null) {
      if (currentCommand != null) {
        cmdsWithOutput.add(CmdWithOutput(currentCommand, commandOutput ?: listOf()))
      }
      currentCommand = parsedCommand
      commandOutput = mutableListOf()
    } else {
      commandOutput?.add(parseLsOutput(line))
    }

  }
  if (currentCommand != null) {
    cmdsWithOutput.add(CmdWithOutput(currentCommand, commandOutput?: listOf()))
  }
  return cmdsWithOutput
}

private fun parseCommand(line: String): Command? {
 if (!line.startsWith("$")) {
   return null
 }
  val command = line.drop(2)
  return when {
    command == "ls" -> Ls()
    command == "cd .." -> CdUp()
    command == "cd /" -> CdRoot()
    command.startsWith("cd ") -> {
      val dirName = command.drop(3)
      Cd(dirName = dirName)
    }
    else -> throw IllegalArgumentException("Unknown command $command")
  }
}

private fun parseLsOutput(line: String): LsOutput {
  return when {
    line.startsWith("dir ") -> DirLsOutput(line.drop(4))
    else -> {
      val fileSize = line.substringBefore(' ').toLong()
      val fileName = line.substringAfter(' ')
      FileLsOutput(fileName = fileName, fileSize = fileSize)
    }
  }
}
