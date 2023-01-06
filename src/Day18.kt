import java.io.File

// Advent of Code 2022, Day 17, Boiling Boulders

fun main() {

    fun readInputAsOneLine(name: String) = File("src", "$name.txt").readText().trim()

    fun part1(input: String): Int {
        val cubes = input.split("\n").map {
            it.split(",").map { it.toInt() }
        }.toSet()

        var touching = 0
        cubes.forEach {
            if (cubes.contains(listOf(it[0], it[1], it[2]+1))) touching++
            if (cubes.contains(listOf(it[0], it[1]+1, it[2]))) touching++
            if (cubes.contains(listOf(it[0]+1, it[1], it[2]))) touching++
            if (cubes.contains(listOf(it[0], it[1], it[2]-1))) touching++
            if (cubes.contains(listOf(it[0], it[1]-1, it[2]))) touching++
            if (cubes.contains(listOf(it[0]-1, it[1], it[2]))) touching++
        }
        return cubes.size * 6 - touching
    }

    fun part2(input: String): Int {

        return 0
    }

    val testInput = readInputAsOneLine("Day18_test")
    check(part1(testInput)==64)

    val input = readInputAsOneLine("Day18")
    println(part1(input))
}