fun main(args: Array<String>) {
  part1("day1/part1-test.txt")
  part1("day1/part1-input.txt" )

  part2("day1/part2-test.txt")
  part2("day1/part2-input.txt")
}

private fun part1(fileName: String) {
  println("$fileName -> ${ WindowCounter().part1(fileName)}")
}

private fun part2(fileName: String) {
  println("$fileName -> ${ WindowCounter().part2(fileName)}")
}
