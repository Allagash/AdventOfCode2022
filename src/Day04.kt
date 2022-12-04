// Advent of Code 2022, Day 04, Camp Cleanup

fun main() {

    fun part1(input: List<String>): Int {
        var total = 0
        input.forEach { line ->
            val ass = line.split(",", "-").map { it.toInt() }
            if ((ass[0] <= ass[2] && ass[1] >= ass[3]) || (ass[0] >= ass[2] && ass[1] <= ass[3])) {
                total++
            }
        }
        return total
    }

    fun part2(input: List<String>): Int {
        var total = 0
        input.forEach { line ->
            val ass = line.split(",", "-").map { it.toInt() }
            if (ass[2] <= ass[1] && ass[3] >= ass[0])  {
                total++
            }
        }
        return total
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val input = readInput("Day04")

    println(part1(input))
    println(part2(input))
}
