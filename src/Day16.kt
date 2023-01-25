import java.io.File
import kotlin.math.max
import kotlin.system.measureTimeMillis

// Advent of Code 2022, Day 16, Proboscidea Volcanium


data class ValveData(val name: String, val index: Int, val rate: Int, val tunnels: List<String>)

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
        val valves = input.split("\n")
            .map {
                it.split(" ")
                    .drop(1) // initial empty
            }.mapIndexed { idx, it ->
                ValveData(it[0], idx, it[3].split("=", ";")[1].toInt(),
                    it.subList(8, it.size).map { if (it.last() == ',') it.dropLast(1) else it })
            }.associateBy { it.name }.toMutableMap()

        // Floyd-Warshall
        val dist = Array (valves.keys.size) { IntArray(valves.size) { Int.MAX_VALUE / 3} } // div by 3 so we don't overflow
        for (i in 0 until valves.keys.size) {
            dist[i][i] = 0
        }
        for (v in valves.values) {
            for (next in v.tunnels) {
                dist[v.index][valves[next]!!.index] = 1
            }
        }
        for (k in 0 until valves.keys.size) {
            for (i in 0 until valves.keys.size) {
                for (j in 0 until valves.keys.size) {
                    if (dist[i][j] > dist[i][k] + dist[k][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j]
                    }
                }
            }
        }

        // We always start from Valve AA
        val indexAA = valves["AA"]!!.index
        val queue = valves.values.filter {it.rate != 0}.map {
            val time = dist[indexAA][it.index] + 1 // takes 1 min to turn on valve
            State(it, time, (MAX_TIME - time) * it.rate, setOf(it))
        }.toMutableList()

        val relevantValves = valves.values.filter { it.rate != 0 }.toSet()

//        var maxTime = 0
        var maxRelease = 0
        while (queue.isNotEmpty()) {
            val state = queue.removeFirst()
            if (state.time > MAX_TIME) continue
            val maxPossible = scoreUpperBound(relevantValves, state.valvesOn, state.currLocn, state.time, state.totalRelease)
            if (maxPossible < maxRelease) continue
//            if (state.time > maxTime) {
//                maxTime = state.time
//                println("time is $maxTime, queue size is ${queue.size}")
//            }
//            if (state.totalRelease > maxRelease) println("max release = ${state.totalRelease}")
            maxRelease = max(state.totalRelease, maxRelease)
            val valvesOff = relevantValves - state.valvesOn
            val nextVisits = valvesOff.map {
                val time = dist[state.currLocn.index][it.index] + 1 + state.time // takes 1 min to turn on valve
                State(it, time, state.totalRelease + (MAX_TIME - time) * it.rate, state.valvesOn + setOf(it))
            }
            queue.addAll(nextVisits)
        }
        return maxRelease
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
    // Part 1 time is 1 min for real input
    println("time for real input part 1 is $timeInMillis")
}