// Day 03, Advent of Code 2022, Rucksack Reorganization

fun main() {

    fun getScore(input: Char) =
        when (input) {
            in 'a'..'z' -> input.code - 'a'.code + 1
            in 'A'..'Z' -> input.code - 'A'.code + 27
            else -> {
                check(false)
                -100000
            }
        }

    fun part1(input: List<String>): Int {
        var total = 0
        input.forEach {
            val size = it.length
            val first = it.substring(0, size / 2).toSet()
            val second = it.substring(size / 2).toSet()

            val intersect = first.intersect(second)
            //println(intersect)
            check(intersect.size == 1)
            total += getScore(intersect.first())
        }
        return total
    }

    fun part2(input: List<String>): Int {
        val chunks = input.chunked(3)
        var total = 0
        chunks.forEach {
            val set1 = it[0].toSet()
            val set2 = it[1].toSet()
            val set3 = it[2].toSet()
            val intersect = set1.intersect(set2).intersect(set3)
            //println(intersect)
            check(intersect.size == 1)
            total += getScore(intersect.first())
        }
        return total
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 157)
    check(part2(testInput) == 70)

    val input = readInput("Day03")

    println(part1(input))
    println(part2(input))
}
