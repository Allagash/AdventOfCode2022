import java.io.File

// Advent of Code 2022, Day 06, Tuning Trouble

fun main() {
    fun readInputAsOneLine(name: String) = File("src", "$name.txt").readText().trim()

    fun solve(input: String, uniqueChar: Int): Int {
        for(i in uniqueChar..input.lastIndex) {
            val uniqueChars = input.subSequence(i - uniqueChar, i).toSet().size
            if (uniqueChars == uniqueChar) {
                return i
            }
        }
        return 0
    }

    val testInput = readInputAsOneLine("Day06_test")
    check(solve(testInput, 4) == 7)
    check(solve(testInput, 14) == 19)

    val input = readInputAsOneLine("Day06")
    println(solve(input, 4))
    println(solve(input, 14))
}