import java.lang.Long.max

// Day 01, Advent of Code 2022, Calorie Counting

fun main() {
    fun part1(input: List<String>): Long {
        var max = -1L
        var count = 0L
        input.forEach {
            if (it.isEmpty()) {
                max = max(count ,max)
                count = 0
            } else {
                count += it.toInt()
            }
        }
        max = max(count ,max)
        return max
    }

    fun part2(input: List<String>): Long {
        val calCount = mutableListOf<Long>()
        var count = 0L
        input.forEach {
            if (it.isEmpty()) {
                calCount.add(count)
                count = 0
            } else {
                count += it.toInt()
            }
        }
        return calCount.sortedDescending().subList(0, 3).sum()
    }

    // test if implementation meets criteria from the description, like:
    //val testInput = readInput("Day01_test")
    //check(part1(testInput) == 1)

    val input = readInput("Day01")

    println(part1(input))
    println(part2(input))
}
