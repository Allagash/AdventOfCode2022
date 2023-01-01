import java.io.File
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.max

// Advent of Code 2022, Day 15, Beacon Exclusion Zone

typealias Pt = Pair<Int, Int>

fun main() {

    // Manhattan distance
    fun Pt.distance(other: Pt) = abs(this.first - other.first) + abs(this.second - other.second)

    fun readInputAsOneLine(name: String) = File("src", "$name.txt").readText().trim()


    fun part1(input: String, inputRow: Int): Int {
        val readings = input.split("\n")
            .map {
                it.split("Sensor at x=", ", y=", ": closest beacon is at x=")
                    .drop(1) // initial empty
                    .map { it.toInt() }
            }.map {
                Pair(it[0] to it[1], it[2] to it[3])
            }

        val sensors = readings.filter {
            // can reach inputRow
            val range = it.first.distance(it.second)
            range >= it.first.distance(Pair(it.first.first, inputRow))
        }

        val ptsInRange = mutableSetOf<Pt>()
        val beaconLocations = sensors.map { it.second }.toSet()
        sensors.forEach {
            val range = it.first.distance(it.second)
            for (x in (it.first.first - range)..(it.first.first + range)) {
                val pt = x to inputRow
                if (pt !in beaconLocations && it.first.distance(pt) <= range) {
                    ptsInRange.add(pt)
                }
            }
        }
        return ptsInRange.size
    }

    fun part2(input: String, maxCoord: Int): Long {
        val readings = input.split("\n")
            .map {
                it.split("Sensor at x=", ", y=", ": closest beacon is at x=")
                    .drop(1) // initial empty
                    .map { it.toInt() }
            }.map {
                val sensor = it[0] to it[1]
                val dist = sensor.distance(it[2] to it[3])
                Pair(sensor, dist)
            }

        repeat(maxCoord+1) {inputRow ->
            var xCoord = mutableListOf<Pair<Int, Int>>() // x ranges, inclusive
            xCoord.add(0 to maxCoord)

            run pointCheck@{
                readings.forEachIndexed { i, it ->
                    val range = it.second
                    val distToRow = abs(it.first.second - inputRow)
                    if (range < distToRow) {
                        return@forEachIndexed // can't reach this row
                    }
                    val newxCoord = mutableListOf<Pair<Int, Int>>()
                    val rowRange = range - abs(inputRow - it.first.second)
                    val removeRange = max(0, (it.first.first - rowRange)) to min(maxCoord, (it.first.first + rowRange))
                    xCoord.forEach { xRange ->
                        if (removeRange.first > xRange.second || removeRange.second < xRange.first) {
                            newxCoord.add(xRange)
                            return@forEach // doesn't intersect this x coord range
                        }
                        // we know it intersects
                        if (removeRange.first <= xRange.first && xRange.second <= removeRange.second) {
                            return@forEach // completely intersects, don't add
                        } else if (xRange.first < removeRange.first && removeRange.second < xRange.second) {
                            // split
                            newxCoord.add(xRange.first to removeRange.first - 1)
                            newxCoord.add(removeRange.second + 1 to xRange.second)
                        } else {
                            val truncatedRange =
                                if (removeRange.second < xRange.second ) {
                                    removeRange.second + 1 to xRange.second
                                } else {
                                    xRange.first to removeRange.first - 1
                                }
                            check(truncatedRange.first <= truncatedRange.second)
                            newxCoord.add(truncatedRange)
                        }
                    }
                    xCoord = newxCoord
                    if (xCoord.isEmpty()) return@forEachIndexed
                }
                if (xCoord.isNotEmpty()) return@pointCheck
            }
            if (xCoord.isNotEmpty()) {
                return xCoord.first().first * 4000000L + inputRow
            }
        }
        return 0
    }

    val testInput = readInputAsOneLine("Day15_test")
    check(part1(testInput, 10) == 26)
    check(part2(testInput, 20) == 56000011L)

    val input = readInputAsOneLine("Day15")
    println(part1(input, 2_000_000)) // 4919282 too high
    println(part2(input, 4_000_000))
}