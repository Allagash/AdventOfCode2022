import java.io.File
import kotlin.math.max
import kotlin.system.measureTimeMillis

// Advent of Code 2022, Day 16, Proboscidea Volcanium


data class ValveData(val name: String, val rate: Int, val tunnels: List<String>)

data class State(val currLocn: ValveData, val time: Int, val totalRelease: Int, val valvesOn: Set<ValveData>)

val MAX_TIME = 30
val MAX_ELAPSED_MIN = 30


fun main() {

    fun readInputAsOneLine(name: String) = File("src", "$name.txt").readText().trim()

    fun scoreUpperBound(allValves: Set<ValveData>, valvesOn: Set<ValveData>, currValveData: ValveData,
                        time: Int, currRelease: Int) : Int {
        check(allValves.size >= valvesOn.size)

        var score = currRelease
        val valvesOff = (allValves - valvesOn).sortedByDescending { it.rate }.toMutableList()
        var currTime = time
        while (currTime < MAX_TIME && valvesOff.isNotEmpty()) {
            val nextValve = valvesOff.removeFirst()
            if (nextValve != currValveData) {
                currTime++
            }
            score += (MAX_TIME - currTime) * nextValve.rate
        }
        return score
    }

    fun part1(input: String): Int {
        // list of valve objects, no strings
        // floyd-warshall algorithm distances between all valves that need to be turned on
        // BFS all to all with cache - see if cache is used

        val valves = input.split("\n")
            .map {
                it.split(" ")
                    .drop(1) // initial empty
            }.map {
                ValveData(it[0], it[3].split("=", ";")[1].toInt(),
                    it.subList(8, it.size).map { if (it.last() == ',') it.dropLast(1) else it })
            }.associateBy { it.name }.toMutableMap()

        // Floyd-Warshall
        val valveNames = valves.keys.toList()
        val dist = Array (valveNames.size) { IntArray(valveNames.size) { Int.MAX_VALUE / 3} } // div by 3 so we don't overflow
        for (i in 0..valveNames.lastIndex) {
            dist[i][i] = 0
        }
        for (v in valveNames) {
            for (next in valves[v]!!.tunnels) {
                val i1 = valveNames.indexOf(v)
                val i2 = valveNames.indexOf(next)
                dist[i1][i2] = 1
            }
        }
        for (k in 0..valveNames.lastIndex) {
            for (i in 0..valveNames.lastIndex) {
                for (j in 0..valveNames.lastIndex) {
                    if (dist[i][j] > dist[i][k] + dist[k][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j]
                    }
                }
            }
        }

        // state is time, rates, valves on
        // We always start from Valve AA
        val indexAA = valveNames.indexOf("AA")
        val queue = valves.values.filter {it.rate != 0}.map {
            val idx =  valveNames.indexOf(it.name)
            val time = dist[indexAA][idx] + 1 // takes 1 min to turn on valve
            State(it, time, (MAX_TIME - time) * it.rate, setOf(it))
        }.toMutableList()

        val relevantValves = valves.values.filter { it.rate != 0 }.toSet()

        var maxTime1 = 0
        var maxRelease1 = 0
        while (queue.isNotEmpty()) {
            val state = queue.removeFirst()
            if (state.time > MAX_TIME) continue
            if (state.time > maxTime1) {
                maxTime1 = state.time
                println("time is $maxTime1, queue size is ${queue.size}")
            }
            if (state.totalRelease > maxRelease1) println("max release = ${state.totalRelease}")
            maxRelease1 = max(state.totalRelease, maxRelease1)
            val valvesOff = relevantValves - state.valvesOn
            val nextVisits = valvesOff.map {
                val idx1 = valveNames.indexOf(state.currLocn.name)
                val idx2 = valveNames.indexOf(it.name)
                val time = dist[idx1][idx2] + 1 + state.time // takes 1 min to turn on valve
                State(it, time, state.totalRelease + (MAX_TIME - time) * it.rate, state.valvesOn + setOf(it))
            }
            queue.addAll(nextVisits)
        }
        println("max = $maxRelease1")
        return maxRelease1
    }

    fun part2(input: String): Int {

        return 0
    }

    val testInput = readInputAsOneLine("Day16_test")
    var timeInMillis = measureTimeMillis {
        check(part1(testInput) == 1651)
    }
    println("time for test input, part 1 is $timeInMillis")

    println("\nNow real input")

    timeInMillis = measureTimeMillis {
        val input = readInputAsOneLine("Day16")
        println(part1(input))
    }
    // Part 1 time is 4 min for real input
    println("time for real input part 1 is $timeInMillis")
}