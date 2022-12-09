import java.io.File
import kotlin.math.abs
import kotlin.math.sign

// Advent of Code 2022, Day 09, Rope Bridge

fun main() {

    fun readInput(name: String) = File("src", "$name.txt").readLines()

    data class Pos(var x: Int, var y: Int)

    fun getHeadMoves(input: List<String>) : List<Pos> {
        val headMoves = mutableListOf <Pos>()
        var head = Pos(0, 0)
        headMoves.add(head)
        input.forEach {
            val move = it.split(" ")
            val newDir = Pos(0, 0)
            when (move[0]) {
                "R" -> newDir.x += 1
                "L" -> newDir.x -= 1
                "U" -> newDir.y += 1
                "D" -> newDir.y -= 1
                else -> check(false)
            }
            repeat(move[1].toInt()) {
                head = Pos(head.x + newDir.x, head.y + newDir.y)
                headMoves.add(head)
            }
        }
        return headMoves
    }

    fun part1(input: List<String>): Int {
        val headMoves = getHeadMoves(input)
        val tailMoves = mutableSetOf <Pos>()
        var tail = Pos(0, 0)
        var headPrev = Pos(0, 0)
        tailMoves.add(tail)
        headMoves.forEach {
            val movedALot = abs(it.x - tail.x) > 1 || abs(it.y - tail.y) > 1
            if (movedALot) {
                tail = headPrev
                tailMoves.add(tail)
//                val moveX = (it.x - tail.x).sign
//                val moveY = (it.y - tail.y).sign
//                tail.x += moveX
//                tail.y += moveY
//                tailMoves.add(tail)
            }
            headPrev = it
        }
        return tailMoves.size
    }

    fun printGrid(ropePos: Array<Pos>) {
        for (j in 30 downTo -15) {
            for (i in -15..15) {
                when (val idx = ropePos.indexOfFirst { it == Pos(i, j) }) {
                    -1 -> if (i==0 && j== 0) print('s') else print('.')
                    else -> print(idx)
                }
            }
            print('\n')
        }
        print('\n')
    }

    fun part2(input: List<String>): Int {
        val headMoves = getHeadMoves(input)
        val tailMoves = mutableSetOf <Pos>()
        tailMoves.add(Pos(0, 0))
        val ropePos = Array(10) {Pos(0, 0)}
        //val prevPos = Array<Pos>(9) {Pos(0, 0)}
        var count = 0
        headMoves.forEach {
//            println("head move is $it")
            ropePos[0] = it
            repeat(9) {i ->
                val lead = ropePos[i]
                val next = ropePos[i + 1]
                val movedALot = abs(lead.x - next.x) > 1 || abs(lead.y - next.y) > 1
                if (movedALot) {
                    val moveX = (lead.x - next.x).sign
                    val moveY = (lead.y - next.y).sign
                    ropePos[i + 1].x += moveX
                    ropePos[i + 1].y += moveY
//                    println("moved a lot, tail is not at $tail")
                } // else break?
                //prevPos[i] = lead
            }
            tailMoves.add(ropePos.last())
            //println("after move $count")
            // printGrid(ropePos)
            count++
        }
        return tailMoves.size
    }

    val testInput = readInput("Day09_test")
    //println(part2(testInput))
    val testInput2 = readInput("Day09_test2")
    println(part2(testInput2))

    check(part1(testInput) == 13)
    //check(part2(testInput) == 8)

    val input = readInput("Day09")
    println(part1(input))
    //println(part2(input)) // 2288 too low, 4898 too high
}