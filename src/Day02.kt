// Day 02, Advent of Code 2022, Rock Paper Scissors

fun main() {

    fun getScore(input: Char) =
        when (input) {
            'A' -> 1
            'B' -> 2
            'C' -> 3
            'X' -> 1
            'Y' -> 2
            'Z' -> 3
            else -> -100000
        }

    fun part1(input: List<String>): Long {
        var total = 0L
        input.forEach {
            val oppScore = getScore(it[0])
            val myScore = getScore(it[2])
            check(oppScore > 0 && myScore > 0)
            total += myScore
            total += when {
                myScore == 1 && oppScore == 3 -> 6 // my rock beats their scissors
                myScore == 3 && oppScore == 1 -> 0 // their rock beats my scissors
                myScore > oppScore -> 6
                myScore < oppScore -> 0
                else -> 3 // draw
            }
        }
        return total
    }

    fun part2(input: List<String>): Long {
        val moves = mutableListOf<String>()
        val oppToMyLookup = mapOf('A' to 'X', 'B' to 'Y', 'C' to 'Z')
        input.forEach {
            val oppMove = it[0]
            val myStrat = it[2]
            check(myStrat in 'X'..'Z')
            val newInc = when(it[2]) {
                'X' -> -1
                'Y' -> 0
                'Z' -> 1
                else -> {
                    check(false)
                    -10000
                }
            }
            var myMove = oppToMyLookup[oppMove]?.plus(newInc)!!
            if (myMove < 'X') myMove = 'Z'
            if (myMove > 'Z') myMove = 'X'

            val round = it[0] + " " + myMove
            moves.add(round)
        }
        check(moves.size == input.size)
        return part1(moves)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 15L)
    check(part2(testInput) == 12L)

    val input = readInput("Day02")

    println(part1(input))
    println(part2(input))
}
