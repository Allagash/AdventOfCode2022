import java.io.File
import java.lang.Exception
import java.util.*
import kotlin.math.abs

// Advent of Code 2022, Day 24: Blizzard Basin
class Day24(input: String) {

    private val startCol: Int
    private val endCol: Int
    private val dirs: List<List<List<Char>>>

    data class Pt(val r: Int, val c: Int) // row and column

    // Manhattan distance
    fun Pt.distance(other: Pt) = abs(this.r - other.r) + abs(this.c - other.c)


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
    }

    // A* search
    // https://www.redblobgames.com/pathfinding/a-star/implementation.html
    fun aStarSearch(start: Pt, goal: Pt) : Int {
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

    // what is the map position at this time
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
    private fun printMap(time: Int) {
        val width = dirs[0][0].size
        val height = dirs[0].size
        printBorder(width, startCol)
        for (row in 0 until height) {
            print('#')
            for (col in 0 until width) {
                val c = getPos(row, col, time)
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
        // have to add end position
        return possibleMoves.filter {
            (it == Pt(-1, startCol)) || // start position
                    ((it.r in 0 until height) &&
                     (it.c in 0 until width) &&
                     ('.' == getPos(it.r, it.c, time)))
        }
    }

    fun part1_astar() : Int {
        // cache position & time
        val start = Pt(-1, startCol)
        val end = Pt(dirs.lastIndex, endCol) // spot just above end position

        return aStarSearch(start, end)
    }

    fun part1(): Int {
        // cache position & time
        val start = Pt(-1, startCol)
        val end = Pt(dirs.lastIndex, endCol) // spot just above end position

        // can I wait in same spot?
        val queue = mutableListOf<State>()
        val startMoves = start.getMoves(1)
        startMoves.forEach {
            queue.add(State(it, 1))
        }
        val cache = hashSetOf<State>()
        var endTime = 0
        var maxTime = 0
        while (queue.isNotEmpty()) {
            val state = queue.removeFirst()
            if (state.pt == end) {
                endTime =  state.time + 1 // we're 1 away from actual end pos
                return endTime //!FIX need the min end time here? or not since BFS
            }
            if (state in cache) continue
            cache.add(state)
            if (state.time > maxTime) {
                maxTime = state.time
                println("time = $maxTime, cache size is ${cache.size}")
            }
            val newTime = state.time + 1
            val newMoves = state.pt.getMoves(newTime)
            newMoves.forEach {
                queue.add(State(it, newTime))
            }
        }
        return endTime
    }

    fun part2(): Int {
        return 0
    }
}

fun main() {

    fun readInputAsOneLine(name: String) = File("src", "$name.txt").readText()

    val testSolver = Day24(readInputAsOneLine("Day24_test"))
    val result = testSolver.part1_astar() + 1
    println("test input : $result")
    check(result == 18)

    val solver = Day24(readInputAsOneLine("Day24"))
    println(solver.part1_astar() + 1) // 319, 325 too low, 400 too high (325, 400) - 362 not right
}