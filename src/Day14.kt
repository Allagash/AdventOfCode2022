import java.io.File
import kotlin.math.min
import kotlin.math.max


// Advent of Code 2022, Day 14, Regolith Reservoir

fun main() {

    fun readInputAsOneLine(name: String) = File("src", "$name.txt").readText().trim()

    fun part1(input: String) : Int {
        val inputLines = input.split("\n").map { it.split(" -> ") }
        println(inputLines)
        val drawLines = mutableListOf<Pair<Pair<Int, Int>, Pair<Int, Int>>>()
        var max = Pair(Int.MIN_VALUE, Int.MIN_VALUE)
        var min = Pair(Int.MAX_VALUE, Int.MAX_VALUE)
        inputLines.forEach {
            for (i in 0 until it.lastIndex) {
                val firstPt = it[i].split(",").map{it.toInt()}//.map{it[0].toInt() to it[1].toInt()}
                val secondPt = it[i+1].split(",").map{it.toInt()}
                val firstPair = firstPt[0] to firstPt[1]
                val secondPair = secondPt[0] to secondPt[1]
                max = Pair(maxOf(max.first, firstPair.first, secondPair.first), maxOf(max.second, firstPair.second, secondPair.second))
                min = Pair(minOf(min.first, firstPair.first, secondPair.first), minOf(min.second, firstPair.second, secondPair.second))
                drawLines.add(Pair(firstPair, secondPair))
            }
//            val chunked = it.chunked(2).filter { it.size == 2 }
//            val pairs = chunked.map{ it.split(",")}
        }
        println(drawLines)
        println("max is $max, min is $min")
        max = Pair(max.first + 1, max.second + 1) // enlarge the grid
        min = Pair(min.first - 1, min.second - 1 - 5)  // will start piling up, need more vertical space

        val xSize = max.first - min.first + 1
        val ySize = max.second - min.second + 1

        val graph = Array(xSize) {CharArray(ySize){'.'} }
        drawLines.forEach {
            val x1 = it.first.first - min.first
            val y1 = it.first.second - min.second

            val x2 = it.second.first - min.first
            val y2 = it.second.second - min.second

            if (x1 == x2) {
                for (y in min(y1, y2)..max(y1, y2) ) {
                    graph[x1][y] = '#'
                }
            } else {
                for (x in min(x1, x2)..max(x1, x2) ) {
                    graph[x][y1] = '#'
                }
            }
        }

        var done = false
        var count = 0
        while(!done) {
            count++
            var sand = Pair(500 - min.first, 0)
            check(graph[sand.first][sand.second] == '.') { "count is $count" }
            while (sand.second != ySize - 1 &&
                (graph[sand.first][sand.second + 1] == '.' ||
                 graph[sand.first-1][sand.second + 1] == '.' ||
                 graph[sand.first+1][sand.second + 1] == '.')
            ) {
                if (graph[sand.first][sand.second + 1] == '.') { // can optimize - first in array not .
                    sand = Pair(sand.first, sand.second + 1)
                    continue
                }
                if (graph[sand.first-1][sand.second + 1] == '.') {
                    sand = Pair(sand.first-1, sand.second + 1)
                    continue
                }
                if (graph[sand.first+1][sand.second + 1] == '.') {
                    sand = Pair(sand.first+1, sand.second + 1)
                    continue
                }

            }
            if (sand.second == ySize - 1) done = true

            graph[sand.first][sand.second] = 'o'
        }


        for (y in 0 until ySize) {
            for (x in 0 until xSize) {
                val c = graph[x][y]
                print(c)
            }
            println()
        }
        return count - 1 // don't count last one which fell through
    }

    fun part2(input: String) : Int  {
        return 0
    }

    val testInput = readInputAsOneLine("Day14_test")
    check(part1(testInput) == 24)
    //check(part2(testInput) == 140)

    val input = readInputAsOneLine("Day14")
    println(part1(input))
//    println(part2(input))
}