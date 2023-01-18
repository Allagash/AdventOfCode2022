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

    enum class Direction(val num: Int, val c: Char) {
        RIGHT(0, '>'),
        DOWN(1, 'V'),
        LEFT(2, '<'),
        UP(3, '^');

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
        println("move is $moveAmt in ${dir.name} direction, start pos $startPos")
        var moves = moveAmt
        var pos = startPos
        while (moves > 0) {
            moves--
            if (dir == Direction.RIGHT) {
                var next = pos.first to pos.second + 1
                if (next.second > maze[pos.first].lastIndex) {
                    next = pos.first to 0
                }
                while (maze[next.first][next.second] != '.' &&
                    maze[next.first][next.second] != '#') {
                    next = next.first to next.second + 1 // this crashes - will go off the end
                    if (next.second > maze[pos.first].lastIndex) {
                        next = pos.first to 0
                    }
                }
                if (maze[next.first][next.second] == '.') {
                    maze[next.first][next.second] = dir.c
                    pos = next
                } else if (maze[next.first][next.second] == '#') {
                    break
                }
            } else if (dir == Direction.LEFT) {
                var next = pos.first to pos.second - 1
                if (next.second < 0) {
                    next = pos.first to maze[pos.first].lastIndex
                }
                while (maze[next.first][next.second] != '.' &&
                    maze[next.first][next.second] != '#') {
                    next = next.first to next.second - 1
                    if (next.second < 0) {
                        next = pos.first to maze[pos.first].lastIndex
                    }
                }
                if (maze[next.first][next.second] == '.') {
                    maze[next.first][next.second] = dir.c
                    pos = next
                } else if (maze[next.first][next.second] == '#') {
                    break
                }
            } else if (dir == Direction.DOWN) {
                var next = pos.first + 1 to pos.second
                if (next.first > maze.lastIndex) {
                    next = 0 to pos.second
                }
                while (maze[next.first][next.second] != '.' &&
                    maze[next.first][next.second] != '#') {
                    next = next.first + 1 to next.second
                    if (next.first > maze.lastIndex) {
                        next = 0 to pos.second
                    }
                }
                if (maze[next.first][next.second] == '.') {
                    maze[next.first][next.second] = dir.c
                    pos = next
                } else if (maze[next.first][next.second] == '#') {
                    break
                }
            } else {
                check(dir == Direction.UP)
                var next = pos.first - 1 to pos.second
                if (next.first < 0) {
                    next = maze.lastIndex to pos.second
                }
                while (maze[next.first][next.second] != '.' &&
                    maze[next.first][next.second] != '#') {
                    next = next.first - 1 to next.second
                    if (next.first < 0) {
                        next = maze.lastIndex to pos.second
                    }
                }
                if (maze[next.first][next.second] == '.') {
                    maze[next.first][next.second] = dir.c
                    pos = next
                } else if (maze[next.first][next.second] == '#') {
                    break
                }
            }
        }
        return pos
    }

    fun part1(): Long  {
        println(moveInstructions)

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
                maze.print()
                println()
                moveAmt = 0
                check(c == 'L' || c == 'R')
                dir = if (c == 'L') {
                    dir.turnLeft()
                } else {
                    dir.turnRight()
                }
                println("turning $c, new dir is ${dir.name}")
                maze[pos.first][pos.second] = dir.c
            }
        }
        // do last move
        pos = move(moveAmt, pos, dir)
        maze.print()

        return (pos.first + 1) * 1000L + (pos.second + 1) * 4 + dir.num
    }

    fun part2(): Long = 0L
}

fun main() {

    fun readInputAsOneLine(name: String) = File("src", "$name.txt").readText()

    val testSolver = Day22(readInputAsOneLine("Day22_test"))
    println(testSolver.part1())

    val solver = Day22(readInputAsOneLine("Day22"))
    println(solver.part1())
//    println(solver.part2())
}