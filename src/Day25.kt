import java.io.File
import kotlin.math.pow

// Advent of Code 2022, Day 25: Full of Hot Air
class Day25(input: String) {

    private val lines: List<String>

    init {
        lines = input.split("\n").filter { it.isNotEmpty() }
    }

    private fun snafuToDec(input: String) : Long {
        return input.reversed().foldIndexed(0L) { idx, sum, c ->
            when (c) {
                '0' -> sum
                '1' -> sum + 5.0.pow((idx).toDouble()).toLong()
                '2' -> sum + 2 * 5.0.pow((idx).toDouble()).toLong()
                '-' -> sum - 5.0.pow((idx).toDouble()).toLong()
                '=' -> sum - 2 * 5.0.pow((idx).toDouble()).toLong()
                else -> throw Exception("bad char in SNAFU number: $c in $input")
            }
        }
    }

    private fun decToSnafu(input: Long) : String {
        var numInput = input
        val numOut = mutableListOf<Char>()
        do {
            val rem = numInput % 5
            numOut.add(0, rem.toString().first())
            numInput -= rem
            numInput /= 5
        } while (numInput != 0L)

        val blah = mutableListOf<Char>()
        var carry = 0
        numOut.reversed().forEach { c ->
            var d = c + carry
            carry = 0
            if (d == '5') {
                d = '0'
                carry = 1
            }
            blah.add(
                when (d) {
                    '0' -> '0'
                    '1' -> '1'
                    '2' -> '2'
                    '3' -> {
                        carry = 1
                        '='
                    }
                    '4' -> {
                        carry = 1
                        '-'
                    }
                    else -> throw Exception("bad char $c")
                }
            )
        }
        if (carry == 1 ) {
            blah.add('1')
        }
        return blah.reversed().joinToString(separator = "")
    }

    fun part1(): String {
        val decNumbers = lines.map {
            snafuToDec(it)
        }
        return decToSnafu(decNumbers.sum())
    }
}

fun main() {
    fun readInputAsOneLine(name: String) = File("src", "$name.txt").readText()

    val testSolver = Day25(readInputAsOneLine("Day25_test"))
    check(testSolver.part1()=="2=-1=0")

    val solver = Day25(readInputAsOneLine("Day25"))
    println(solver.part1())
}