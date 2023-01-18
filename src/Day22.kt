import java.io.File

// Advent of Code 2022, Day 22: Monkey Map

class Day22(input: String) {

    private val moveInstructions: String
    private val maze: Array<CharArray>

    init {
        val inputParts = input.split("\n\n")
        moveInstructions = inputParts[1]

        val mazeInput = inputParts[0].split("\n")
        val maxLen = mazeInput.maxOf { it.length }

        maze = Array (mazeInput.size) { CharArray(maxLen){' '} }

        mazeInput.forEachIndexed { idx, s ->
            s.toCharArray().mapIndexed { index, c ->
                maze[idx][index] = c
            }
        }
    }

    private fun Array<CharArray>.print() {
        this.forEach {
            println(it)
        }
    }

    enum class Direction(val num: Int, val c: Char, val vec: Pair<Int, Int>) {
        RIGHT(0, '>', Pair(0, 1)),
        DOWN(1, 'V', Pair(1, 0)),
        LEFT(2, '<', Pair(0, -1)),
        UP(3, '^', Pair(-1, 0));

        fun turnRight() : Direction {
            return when (this) {
                RIGHT -> DOWN
                DOWN -> LEFT
                LEFT -> UP
                UP -> RIGHT
            }
        }

        fun turnLeft() : Direction {
            return when (this) {
                RIGHT -> UP
                DOWN -> RIGHT
                LEFT -> DOWN
                UP -> LEFT
            }
        }
    }

    private fun move(moveAmt: Int, startPos: Pair<Int, Int>, dir: Direction) : Pair<Int, Int> {
//        println("move is $moveAmt in ${dir.name} direction, start pos $startPos")
        var moves = moveAmt
        var pos = startPos
        while (moves > 0) {
            moves--
            var next = Pair(pos.first + dir.vec.first, pos.second + dir.vec.second)
            next = clamp(next, pos)
            // wrap around
            while (maze[next.first][next.second] !in listOf('.', '#', '^', 'V', '>', '<') ) {
                next = Pair(next.first + dir.vec.first, next.second + dir.vec.second)
                next = clamp(next, pos)
            }

            if (maze[next.first][next.second] == '#') {
                break
            } else {
                maze[next.first][next.second] = dir.c
                pos = next
            }
        }
        return pos
    }

    private fun clamp(
        next: Pair<Int, Int>,
        pos: Pair<Int, Int>
    ): Pair<Int, Int> {
        var next1 = next
        if (next1.second > maze[pos.first].lastIndex) {
            next1 = pos.first to 0
        }
        if (next1.second < 0) {
            next1 = pos.first to maze[pos.first].lastIndex
        }
        if (next1.first > maze.lastIndex) {
            next1 = 0 to pos.second
        }
        if (next1.first < 0) {
            next1 = maze.lastIndex to pos.second
        }
        return next1
    }

    fun part1(): Long  {
        val startCol = maze[0].indexOfFirst { it == '.' }
        var pos = Pair(0, startCol)
        var dir = Direction.RIGHT

        maze[pos.first][pos.second] = Direction.RIGHT.c

        // if reach end of array, if reach end of ., if reach wall
        var moveAmt = 0
        moveInstructions.forEach { c ->
            if (c.isDigit()) {
                moveAmt = moveAmt * 10 + (c.code - '0'.code)
            } else {
                if (c.code == 10 ) return@forEach // line feed
                // do the move
                pos = move(moveAmt, pos, dir)
//                maze.print()
//                println()
                moveAmt = 0
                check(c == 'L' || c == 'R')
                dir = if (c == 'L') {
                    dir.turnLeft()
                } else {
                    dir.turnRight()
                }
//                println("turning $c, new dir is ${dir.name}")
                maze[pos.first][pos.second] = dir.c
            }
        }
        // do last move
        pos = move(moveAmt, pos, dir)
//        maze.print()

        return (pos.first + 1) * 1000L + (pos.second + 1) * 4 + dir.num
    }

    fun part2(): Long = 0L
}

fun main() {

    fun readInputAsOneLine(name: String) = File("src", "$name.txt").readText()

    val testSolver = Day22(readInputAsOneLine("Day22_test"))
    check(testSolver.part1()==6032L)

    val solver = Day22(readInputAsOneLine("Day22"))
    println(solver.part1())
}