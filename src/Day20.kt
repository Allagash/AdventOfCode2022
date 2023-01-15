import java.io.File

// Advent of Code 2022, Day 20: Grove Positioning System

// Keep the original index in order to make each value unique
// In input, there are duplicate values.
data class Day20(val num: Long, val origIdx: Int) {
    override fun toString(): String = num.toString()
}
fun main() {

    fun readInputAsOneLine(name: String) = File("src", "$name.txt").readText().trim()

    fun solve(input: String, key: Int, repeatTimes: Int): Long {
        val numsBackup = input.split("\n").mapIndexed { idx, s -> Day20(s.toLong() * key, idx) }
        val nums = numsBackup.toMutableList()
        val size = numsBackup.size
        val zero = nums.first { it.num == 0L }

        repeat(repeatTimes) {
            numsBackup.forEach {
                val idx = nums.indexOf(it)
                val thing = nums.removeAt(idx)
                val newIdx = (idx + thing.num).mod(size-1)
                nums.add(newIdx, thing)
            }
        }
        val zeroIdx = nums.indexOf(zero)
        val list = mutableListOf<Long>()
        list.add(nums[(1_000 + zeroIdx).mod(size)].num)
        list.add(nums[(2_000 + zeroIdx).mod(size)].num)
        list.add(nums[(3_000 + zeroIdx).mod(size)].num)

        return list.sum()
    }

    val testInput = readInputAsOneLine("Day20_test")
    check(solve(testInput, 1, 1)==3L)
    check(solve(testInput, 811589153, 10)==1623178306L)

    val input = readInputAsOneLine("Day20")
    println(solve(input, 1, 1))
    println(solve(input, 811589153, 10))
}