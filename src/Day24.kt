import java.io.File
import java.lang.Exception
import java.util.*
import kotlin.math.abs
import kotlin.system.measureTimeMillis

// Advent of Code 2022, Day 24: Blizzard Basin
class Day24(input: String) {

    private val startCol: Int
    private val endCol: Int
    private val startPos: Pt
    private val endPos: Pt
    private val dirs: List<List<List<Char>>>

    data class Pt(val r: Int, val c: Int) // row and column

    // Manhattan distance
    private fun Pt.distance(other: Pt) = abs(this.r - other.r) + abs(this.c - other.c)

    data class State(val pt: Pt, val time: Int)

    companion object {
        val DIR_LIST =  listOf('>', '<', '^', 'v')
    }

    init {
        // start pos, for the end pos we just need the next to last position & then we can add 1
        val lines = input.split("\n").filter { it.isNotEmpty() }
        startCol = lines[0].indexOf('.') - 1 // don't count left border
        endCol = lines[lines.lastIndex].indexOf('.') -1 // don't count left border
        dirs = DIR_LIST.map {dir ->
            lines.drop(1).dropLast(1).map {
                it.drop(1).dropLast(1).map { c ->
                    if (c == dir) dir else '.'
                }
            }
        }
        startPos = Pt(-1, startCol)
        endPos = Pt(dirs[0].lastIndex + 1, endCol)
    }

    // A* search
    // https://www.redblobgames.com/pathfinding/a-star/implementation.html
    private fun aStarSearch(start: Pt, goal: Pt) : Int {
        // store Point, time, priority
        val openSet = PriorityQueue {t1: Triple<Pt, Int, Int>, t2 : Triple<Pt, Int, Int> -> (t1.third - t2.third) }
        openSet.add(Triple(start, 0, 0))
        val cameFrom = mutableMapOf<Pair<Pt, Int>, Pair<Pt, Int>?>()
        val costSoFar = mutableMapOf<Pair<Pt, Int>, Int>()
        cameFrom[start to 0] = null
        costSoFar[start to 0] = 0
        var cost = 0
        while (openSet.isNotEmpty()) {
            val current = openSet.remove()
            if (current.first == goal) {
                cost = costSoFar[current.first to current.second]!!
                break
            }
            val newTime = current.second + 1
            val neighbors = current.first.getMoves(newTime)
            for (next in neighbors) {
                val newCost = costSoFar[current.first to current.second]!! + 1
                if ((next to newCost) !in costSoFar || newCost < costSoFar[next to newCost]!!) {
                    costSoFar[next to newCost] = newCost
                    val priority = newCost + next.distance(goal)
                    openSet.add(Triple(next, newTime, priority))
                    cameFrom[next to newTime] = current.first to current.second
                }
            }
        }
        return cost
    }

    // Return '.', blizzard direction, or '2', '3', '4' for number of blizzards at this location & time
    private fun getPos(row: Int, col: Int, time: Int) : Char {
        val chars = DIR_LIST.mapIndexed { idx, c ->
            val width = dirs[idx][row].size
            val height = dirs[idx].size
            val pos = when (c) {
                DIR_LIST[0] -> { // right
                    var offset = (col - time) % width
                    if (offset < 0) offset += width
                    dirs[idx][row][offset]
                }
                DIR_LIST[1] -> dirs[idx][row][(col + time) % width] // left
                DIR_LIST[2] -> dirs[idx][(row + time) % height][col] // up
                DIR_LIST[3] -> { // down
                    var offset = (row - time) % height
                    if (offset < 0) offset += height
                    dirs[idx][offset][col]
                }
                else -> throw Exception("bad direction")
            }
            pos
        }
        val blizzards = chars.filter { it != '.' }
        return when (blizzards.size) {
            0 -> '.'
            1 -> blizzards.first()
            else -> blizzards.size.toString().first() // we know size is 2, 3 or 4
        }
    }

    // print top or bottom of map
    private fun printBorder(len: Int, emptySpace: Int) {
        for (j in 0..len + 1) {
            val c = if (j - 1 == emptySpace) '.' else '#'
            print(c)
        }
        println()
    }

    // print the map
    private fun printMap(time: Int, path: List<Pt> = emptyList()) {
        val width = dirs[0][0].size
        val height = dirs[0].size
        val pathDisplay = ('a'..'z').toList() + ('A'..'Z').toList()
        printBorder(width, startCol)
        for (row in 0 until height) {
            print('#')
            for (col in 0 until width) {
                val idx = path.indexOf(Pt(row, col))
                val c = if (idx >= 0) {
                    pathDisplay[idx % pathDisplay.size]
                } else {
                    getPos(row, col, time)
                }
                print(c)
            }
            println("#")
        }
        printBorder(width, endCol)
    }

    // get possible moves from this point
    private fun Pt.getMoves(time: Int): List<Pt> {
        // include current position
        val possibleMoves = listOf(Pt(r, c), Pt(r - 1, c), Pt(r + 1, c), Pt(r, c - 1), Pt(r, c + 1))
        val width = dirs[0][0].size
        val height = dirs[0].size
        return possibleMoves.filter {
            (it == startPos) || (it == endPos) ||
                    ((it.r in 0 until height) &&
                     (it.c in 0 until width) &&
                     ('.' == getPos(it.r, it.c, time)))
        }
    }

    fun part1_astar() : Int {
        return aStarSearch(startPos, endPos)
    }

    fun part1(): Int {
        val queue = mutableListOf<State>()
        val startMoves = startPos.getMoves(1)
        queue.addAll(startMoves.map { State(it, 1) })
        val cache = hashSetOf<State>()
        while (queue.isNotEmpty()) {
            val state = queue.removeFirst()
            if (state.pt == endPos) {
                return state.time
            }
            if (state in cache) continue
            cache.add(state)
            val newTime = state.time + 1
            val newMoves = state.pt.getMoves(newTime)
            queue.addAll(newMoves.map{State(it, newTime)})
        }
        return 0 // we failed
    }

    fun part2(): Int {
        return 0
    }
}

fun main() {

    fun readInputAsOneLine(name: String) = File("src", "$name.txt").readText()

    val testSolver = Day24(readInputAsOneLine("Day24_test"))
    check(testSolver.part1_astar() == 18)

    val solver = Day24(readInputAsOneLine("Day24"))
    var result2 : Int
    var timeInMillis = measureTimeMillis {
        result2 = solver.part1_astar()
    }
    println("a-star $result2, time is $timeInMillis")
    timeInMillis = measureTimeMillis {
        result2 = solver.part1()
    }
    println("BFS $result2, time is $timeInMillis")
}