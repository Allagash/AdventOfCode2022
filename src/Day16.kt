import java.io.File
import java.util.*
import kotlin.math.max
import kotlin.system.measureTimeMillis

// Advent of Code 2022, Day 16, Proboscidea Volcanium


data class ValveData(val name: String, val index: Int, val rate: Int, val tunnels: List<String>)

data class State(val currLocn: ValveData, val time: Int, val totalRelease: Int, val valvesOn: Set<ValveData>)
data class State2(val currLocn: List<ValveData?>, val time: List<Int>, val totalRelease: Int, val valvesOn: Set<ValveData>)

val MAX_ELAPSED_MIN = 30


fun main() {

    fun readInputAsOneLine(name: String) = File("src", "$name.txt").readText().trim()

    fun scoreUpperBound(allValves: Set<ValveData>, valvesOn: Set<ValveData>,
                        time: Int, currRelease: Int, part1: Boolean, maxTime: Int) : Int {
        check(allValves.size >= valvesOn.size)

        var score = currRelease
        val valvesOff = (allValves - valvesOn).sortedByDescending { it.rate }.toMutableList()
        var currTime = time
        while (currTime < maxTime && valvesOff.isNotEmpty()) {
            var nextValve = valvesOff.removeFirst()
            score += (maxTime - currTime) * nextValve.rate
            if (!part1 && valvesOff.isNotEmpty()) {
                nextValve = valvesOff.removeFirst() // simultaneous elephant valve
                score += (maxTime - currTime) * nextValve.rate
            }
            currTime++
        }
        return score
    }

    fun parse(input: String): Pair<MutableMap<String, ValveData>, Array<IntArray>> {
        val valves = input.split("\n")
            .map {
                it.split(" ")
                    .drop(1) // initial empty
            }.mapIndexed { idx, it ->
                ValveData(it[0], idx, it[3].split("=", ";")[1].toInt(),
                    it.subList(8, it.size).map { if (it.last() == ',') it.dropLast(1) else it })
            }.associateBy { it.name }.toMutableMap()

        // Floyd-Warshall
        val dist =
            Array(valves.keys.size) { IntArray(valves.size) { Int.MAX_VALUE / 3 } } // div by 3 so we don't overflow
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
        return Pair(valves, dist)
    }

    fun part1(input: String, MAX_TIME: Int): Int {
        val (valves, dist) = parse(input)

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
            val maxPossible = scoreUpperBound(relevantValves, state.valvesOn, state.time, state.totalRelease,
                true, MAX_TIME)
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

    fun part2(input: String, MAX_TIME: Int): Int {
        val (valves, dist) = parse(input)

        // We always start from Valve AA
        val indexAA = valves["AA"]!!.index
        val nextValves = valves.values.filter {it.rate != 0}
        val queue = PriorityQueue {t1: State2, t2: State2 -> (t2.totalRelease - t1.totalRelease) }
        // val queue = mutableListOf<State2>()
        // v -> Node B and e -> Node C has the same result as v->C and e->B, so only create half the possible travels
        for (vIdx in nextValves.indices) {
            val v = nextValves[vIdx]
            val time0 = dist[indexAA][v.index] + 1 // takes 1 min to turn on valve
            if (time0 > MAX_TIME) continue //!FIX but e travel may be OK? have handle case where 1 traveller stuck & other isn't
            val rel0 = (MAX_TIME - time0) * v.rate
            for (eIdx in vIdx+1..nextValves.lastIndex) {
                val e = nextValves[eIdx]
                check(v != e)
                val time1 = dist[indexAA][e.index] + 1 // takes 1 min to turn on valve
                if (time1 > MAX_TIME) continue
                val rel1 = (MAX_TIME - time1) * e.rate
                queue.add(State2(listOf(v, e), listOf(time0, time1), rel0 + rel1, setOf(v, e) ))
            }
        }

        val relevantValves = valves.values.filter { it.rate != 0 }.toSet()

        var maxTime = 0
        var maxRelease = 0
        while (queue.isNotEmpty()) {
            var state = queue.remove()
            //!FIX but what if only 1 time is over - continue with other time?
            if (state.time.max() > MAX_TIME) continue
            // don't count the person/elephant who has exceeded time, but the other one keeps moving
//            if (state.time[0] > MAX_TIME) {
//                state = state.copy(
//                    currLocn = listOf(null, state.currLocn[1])
//                )
//            }
//            if (state.time[1] > MAX_TIME) {
//                state = state.copy(
//                    currLocn = listOf(state.currLocn[0], null)
//                )
//            }
            //!FIX have to fix upperbound to deal with simultaneous movement - add 2 valves per minute
            val treatAsPart1 = state.currLocn.filterNotNull().size != 2
            val maxPossible = scoreUpperBound(relevantValves, state.valvesOn, state.time.min(), state.totalRelease,
                treatAsPart1, MAX_TIME)
            if (maxPossible < maxRelease) continue
            if (state.time.max() > maxTime) {
                maxTime = state.time.max()
                println("time is $maxTime, queue size is ${queue.size}")
            }
            if (state.totalRelease > maxRelease) {
                println("max release = ${state.totalRelease}, queue size is ${queue.size}, valves on ${state.valvesOn.map { it.name }}")
            }
            maxRelease = max(state.totalRelease, maxRelease)
            val valvesOff = relevantValves - state.valvesOn
            for (v in valvesOff) {
                val time0 = dist[state.currLocn[0]!!.index][v.index] + 1 // takes 1 min to turn on valve
                if (state.time[0] + time0 >= MAX_TIME) continue
                val rel0 = (MAX_TIME - (state.time[0] + time0)) * v.rate
                for (e in valvesOff) {
                    if (v == e) continue
//                    val time1 = if (state.currLocn[0] != null) {
//                    } else 0
//                    val time2 = if (state.currLocn[1] != null) {
                        val time1 = dist[state.currLocn[1]!!.index][e.index] + 1 // takes 1 min to turn on valve
                    if (state.time[1] + time1 >= MAX_TIME) continue
//                    } else 0
//                    val rel1 = if (time1 != 0)  (MAX_TIME - time1) * v.rate  else 0
//                    val rel2 = if (time2 != 0) (MAX_TIME - time2) * e.rate else 0
                    val rel1 =  (MAX_TIME - (state.time[1] + time1)) * e.rate
                    queue.add(State2(listOf(v, e), listOf(time0 + state.time[0], time1 + state.time[1]),
                        state.totalRelease + rel0 + rel1, state.valvesOn + setOf(v, e) ))
                }
            }
        }
        return maxRelease
    }

    val testInput = readInputAsOneLine("Day16_test")
//    var timeInMillis = measureTimeMillis {
//        check(part1(testInput, 30) == 1651)
//    }
//    println("time for test input, part 1 is $timeInMillis ms")

    var timeInMillis = measureTimeMillis {
        println(part2(testInput, 26) ) // should be 1707 for 26 min
    }
    println("time for test input, part 2 is $timeInMillis ms")
    println("\nNow real input")
//
//    timeInMillis = measureTimeMillis {
//        val input = readInputAsOneLine("Day16")
//        println(part1(input, 30))
//    }
//    // Part 1 time is 1 min for real input
//    println("time for real input part 1 is $timeInMillis ms")
//
    timeInMillis = measureTimeMillis {
        val input = readInputAsOneLine("Day16")
        println(part2(input, 26)) // 2206 is too low
    }
//    // Part 1 time is 1 min for real input
    println("time for real input part 2 is $timeInMillis ms") // 77 sec
}