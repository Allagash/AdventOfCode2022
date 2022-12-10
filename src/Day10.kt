import java.io.File

// Advent of Code 2022, Day 10, Cathode-Ray Tube

fun main() {

    fun readInput(name: String) = File("src", "$name.txt")
        .readLines()

    fun part1(input: List<String>): Int {
        val importantCycles = listOf(20, 60, 100, 140, 180, 220)
        var sum = 0
        var cycle = 1
        var regX = 1
        input.forEach {
            if (cycle in importantCycles) sum += cycle * regX
            val instruction = it.split(" ")
            if (instruction[0] == "addx") {
                cycle++
                if (cycle in importantCycles) sum += cycle * regX
                regX += instruction[1].toInt()
            }
            cycle++
        }
        return sum
    }

    fun part2(input: List<String>) {
        val crt = mutableListOf<String>()
        var line = ""
        var cycle = 1
        var regX = 1
        input.forEach {
            line += if ((cycle -1) % 40 in regX-1..regX+1) "#" else "."
            if (line.length >= 40) {
                crt.add(line)
                line = ""
            }

            val instruction = it.split(" ")
            if (instruction[0] == "addx") {
                cycle++
                line += if ((cycle -1) % 40 in regX-1..regX+1) "#" else "."
                if (line.length >= 40) {
                    crt.add(line)
                    line = ""
                }
                regX += instruction[1].toInt()
            }
            cycle++
        }
        println("CRT is:")
        crt.forEach { println(it) }
        println()
    }

    val testInput = readInput("Day10_test")
    check(part1(testInput) == 13140)
    part2(testInput)

    val input = readInput("Day10")
    println(part1(input))
    part2(input)
}