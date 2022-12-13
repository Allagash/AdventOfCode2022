import java.io.File
import java.util.*
import kotlin.math.abs

// Advent of Code 2022, Day 12, Hill Climbing Algorithm

typealias Graph = List<List<Int>>
typealias Cell = Pair<Int, Int>

// Manhattan distance
fun Cell.distance(other: Cell) = abs(this.first - other.first) + abs(this.second - other.second)

fun Graph.neighbors(curr: Cell, part1: Boolean) : List<Cell> {
    val neighbors = mutableListOf<Cell>()
    val currVal = this[curr.first][curr.second]
    for (i in listOf(Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1))) {
        val pt = Pair(curr.first + i.first, curr.second + i.second)
        if (pt.first >= 0 && pt.first < this.size && pt.second >=0 && pt.second < this[0].size) {
            val nextVal = this[pt.first][pt.second]
            if ((part1 && currVal + 1 >= nextVal) || (!part1 && currVal <= nextVal + 1)) {
                neighbors.add(pt)
            }
        }
    }
    return neighbors
}

// A* search
// https://www.redblobgames.com/pathfinding/a-star/implementation.html
fun aStarSearch(graph: Graph, start: Cell, goal: Set<Cell>, part1: Boolean) : Int {
    val openSet = PriorityQueue {t1: Pair<Cell, Int>, t2 : Pair<Cell, Int> -> (t1.second - t2.second) }
    openSet.add(Pair(start, 0))
    val cameFrom = mutableMapOf<Cell, Cell?>()
    val costSoFar = mutableMapOf<Cell, Int>()
    cameFrom[start] = null
    costSoFar[start] = 0
    var cost = 0
    while (openSet.isNotEmpty()) {
        val current = openSet.remove()
        if (current.first in goal) {
            cost = costSoFar[current.first]!!
            break
        }
        val neighbors = graph.neighbors(current.first, part1)
        for (next in neighbors) {
            val newCost = costSoFar[current.first]!! + 1
            if (next !in costSoFar || newCost < costSoFar[next]!!) {
                costSoFar[next] = newCost
                val priority = newCost + goal.minOf{next.distance(it)}
                openSet.add(Pair(next, priority))
                cameFrom[next] = current.first
            }
        }
    }
    return cost
}

fun main() {

    val START_MARKER = -1
    val END_MARKER = 'z'.code - 'a'.code + 1

    // Read a 2D grid as input. Each cell is one letter.
    fun readSingleDigitGrid(name: String): List<List<Int>> {
        // convert each ASCII char to an int, so 'a' -> 0, 'b' -> 1, etc.
        return File("src", "$name.txt").readLines().map {
            it.trim().map { j ->
                when (j) {
                    'S' -> START_MARKER
                    'E' -> END_MARKER
                    else -> j.code - 'a'.code
                }
            }
        }
    }

    fun part1(grid: List<List<Int>>): Int {
        var start = Pair(0, 0)
        var end = Pair(0, 0)
        for (i in grid.indices) {
            for (j in grid[i].indices) {
                if (grid[i][j] == START_MARKER) {
                    start = Pair(i, j)
                } else if (grid[i][j] == END_MARKER) {
                    end = Pair(i, j)
                }
            }
        }
        return aStarSearch(grid, start, setOf(end), true)
    }

    fun part2(grid: List<List<Int>>): Int {
        var start = Pair(0, 0)
        val end = mutableSetOf<Pair<Int, Int>>()
        for (i in grid.indices) {
            for (j in grid[i].indices) {
                if (grid[i][j] <= 0) {
                    end.add(Pair(i, j))
                } else if (grid[i][j] == END_MARKER) {
                    start = Pair(i, j)
                }
            }
        }
        return aStarSearch(grid, start, end, false)
    }

    val testInput = readSingleDigitGrid("Day12_test")
    check(part1(testInput) == 31)
    check(part2(testInput) == 29)

    val input = readSingleDigitGrid("Day12")
    println(part1(input))
    println(part2(input))
}