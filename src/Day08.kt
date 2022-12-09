import java.io.File
import kotlin.math.max
import kotlin.math.min
import kotlin.math.abs

// Advent of Code 2022, Day 08, Treetop Tree House

fun main() {

    // Read a 2D grid as input. Each cell is one digit.
    fun readSingleDigitGrid(name: String) : List<List<Int>> {
        // convert each ASCII char to an int, so '0' -> 0, '1' -> 1, etc.
        return File("src", "$name.txt").readLines().map { it.trim().map { j -> j.code - '0'.code } }
    }

    fun setupVisibility(heights: List<List<Int>>): Array<BooleanArray>  {
        val rows = heights.size
        val cols = heights[0].size

        val visible = Array(rows) { BooleanArray(cols) }
        for (i in 0 until rows) {
            visible[i][0] = true
            visible[i][cols-1] = true
        }
        for (i in 0 until cols) {
            visible[0][i] = true
            visible[rows-1][i] = true
        }

        return visible
    }

    fun setupViewDistance(heights: List<List<Int>>): Array<IntArray>  {
        val rows = heights.size
        val cols = heights[0].size

        val distance = Array(rows) { IntArray(cols) { 1 } }
        for (i in 0 until rows) {
            distance[i][0] = 0
            distance[i][cols-1] = 0
        }
        for (i in 0 until cols) {
            distance[0][i] = 0
            distance[rows-1][i] = 0
        }

        return distance
    }

    fun part1(heights: List<List<Int>>): Int {
        val visible = setupVisibility(heights)
        val rows = heights.size
        val cols = heights[0].size

        // skip border rows, cols
        for (i in 1..rows-2 ) {
            val row = heights[i]
            var maxHeight = row[0]
            for (j in 1..cols-2 ) {
                val currHeight = heights[i][j]
                if (currHeight > maxHeight) {
                    visible[i][j] = true
                    maxHeight = currHeight
                }
            }
        }
        for (i in 1..rows-2 ) {
            val row = heights[i]
            var maxHeight = row[cols-1]
            for (j in cols-2 downTo 1 ) {
                val currHeight = heights[i][j]
                if (currHeight > maxHeight) {
                    visible[i][j] = true
                    maxHeight = currHeight
                }
            }
        }

        for (j in 1..cols-2 ) {
           // val row = heights[i]
            var maxHeight = heights[0][j]
            for (i in 1..rows-2 ) {
                val currHeight = heights[i][j]
                if (currHeight > maxHeight) {
                    visible[i][j] = true
                    maxHeight = currHeight
                }
            }
        }
        for (j in 1..cols-2 ) {
            //val row = heights[i]
            var maxHeight = heights[rows-1][j]
            for (i in rows-2 downTo 1 ) {
                val currHeight = heights[i][j]
                if (currHeight > maxHeight) {
                    visible[i][j] = true
                    maxHeight = currHeight
                }
            }
        }

        var count = 0
        for (row in visible) {
            count += row.count { it }
        }

        return count
    }


    fun part2(heights: List<List<Int>>): Int {
        val distance = setupViewDistance(heights)
        val rows = heights.size
        val cols = heights[0].size

        // There are only 10 height values, 0..9
        // Index is the height value, store the last row or col index we saw that height
        // If we haven't encountered the height, value is MIN_VALUE
        val prevIndex = IntArray(10)
        // skip outside edge of grid
        for (i in 1..rows-2 ) {
            prevIndex.fill(Int.MIN_VALUE)
            prevIndex[heights[i][0]] = 0
            for (j in 1..cols-2 ) {
                val prevHeight = heights[i][j-1]
                val currHeight = heights[i][j]
                if (currHeight > prevHeight) {
                    var visibility = Int.MAX_VALUE
                    for (k in currHeight until 10) {
                        if (prevIndex[k] > 0) {
                            visibility = min(visibility, abs(j - prevIndex[k]))
                        }
                    }
                    distance[i][j] *= min(j, visibility)
                }
                prevIndex[currHeight] = j
            }
        }
        for (i in 1..rows-2 ) {
            prevIndex.fill(Int.MAX_VALUE)
            prevIndex[heights[i][cols-1]] = 0
            for (j in cols-2 downTo 1 ) {
                val prevHeight = heights[i][j+1]
                val currHeight = heights[i][j]
                if (currHeight > prevHeight) {
                    var visibility = Int.MAX_VALUE
                    for (k in currHeight until 10) {
                        if (prevIndex[k] > 0) {
                            visibility = min(visibility, abs(j - prevIndex[k]))
                        }
                    }
                    distance[i][j] *= min(cols-1-j, visibility)
                }
                prevIndex[currHeight] = j
            }
        }

        for (j in 1..cols-2 ) {
            prevIndex.fill(Int.MAX_VALUE)
            prevIndex[heights[0][j]] = 0
            for (i in 1..rows-2 ) {
                val prevHeight = heights[i-1][j]
                val currHeight = heights[i][j]
                if (currHeight > prevHeight) {
                    var visibility = Int.MAX_VALUE
                    for (k in currHeight until 10) {
                        if (prevIndex[k] > 0) {
                            visibility = min(visibility, abs(i - prevIndex[k]))
                        }
                    }
                    distance[i][j] *= min(i, visibility)
                }
                prevIndex[currHeight] = i
            }
        }

        for (j in 1..cols-2 ) {
            prevIndex.fill(Int.MAX_VALUE)
            prevIndex[heights[rows-1][j]] = 0
            for (i in rows-2 downTo 1 ) {
                val prevHeight = heights[i+1][j]
                val currHeight = heights[i][j]
                if (currHeight > prevHeight) {
                    var visibility = Int.MAX_VALUE
                    for (k in currHeight until 10) {
                        if (prevIndex[k] > 0) {
                            visibility = min(visibility, abs(i - prevIndex[k]))
                        }
                    }
                    distance[i][j] *= min(rows-1-i, visibility)
                }
                prevIndex[currHeight] = i
            }
        }

        var maxDist = 0
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                val dist = distance[i][j]
                maxDist = max(maxDist, dist)
            }
        }

        return maxDist
    }

    val testInput = readSingleDigitGrid("Day08_test")
    check(part1(testInput) == 21)
    check(part2(testInput) == 8)

    val input = readSingleDigitGrid("Day08")
    println(part1(input))
    println(part2(input))
}