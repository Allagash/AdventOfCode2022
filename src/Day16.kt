import java.io.File
import kotlin.math.max
import kotlin.system.measureTimeMillis

// Advent of Code 2022, Day 16, Proboscidea Volcanium


data class Valve(val name: String, val rate: Int, val tunnels: List<String>)

data class State(val time: Int, val totalRelease: Int, val valvesOn: Set<Valve>)

enum class Action {
    TURN_ON, MOVE
}

data class Route(val start: String, val end: String, val dist: Int)

data class Step(val action: Action, val valve: Valve, val currState: State, val timeCost: Int = 1)

val MAX_TIME = 30
val MAX_ELAPSED_MIN = 30


fun main() {

    fun readInputAsOneLine(name: String) = File("src", "$name.txt").readText().trim()

    fun scoreUpperBound(allValves: Set<Valve>, valvesOn: Set<Valve>, currValve: Valve,
                        time: Int, currRelease: Int) : Int {
        check(allValves.size >= valvesOn.size)

        var score = currRelease
        val valvesOff = (allValves - valvesOn).sortedByDescending { it.rate }.toMutableList()
        var currTime = time
        while (currTime < MAX_TIME && valvesOff.isNotEmpty()) {
            val nextValve = valvesOff.removeFirst()
            if (nextValve != currValve) {
                currTime++
            }
            score += (MAX_TIME - currTime) * nextValve.rate
        }
        return score
    }

    // For debugging: print each route in the map in the format
    //   AA 0->DD 20
    // with the name & rate for each node.
    // This output can be pasted into drawio to generate a map.
    // See https://www.diagrams.net/blog/insert-from-text
    fun printRoutes(valves: Map<String, Valve>) {
        valves.forEach {
            it.value.tunnels.forEach { next ->
                val nextRate = valves[next]!!.rate
                println("${it.key} ${it.value.rate}->$next $nextRate")
            }
        }
    }

    fun part1(input: String): Int {
        val valves = input.split("\n")
            .map {
                it.split(" ")
                    .drop(1) // initial empty
            }.map {
                Valve(it[0], it[3].split("=", ";")[1].toInt(),
                    it.subList(8, it.size).map { if (it.last() == ',') it.dropLast(1) else it })
            }.associateBy { it.name }.toMutableMap()

//        printRoutes(valves)


        // throw out nodes with 0 flow rate
        val routeDist = mutableMapOf<Pair<String, String>, Int>()
        val valveRoutes = mutableMapOf<String, MutableList<String>>()
        valves.forEach {
            val prev = it.key
            val next = mutableListOf<Route>()
            it.value.tunnels.forEach {
                next.add(Route(prev, it, 1))
            }

            val result =  mutableListOf<Route>()

            while (next.isNotEmpty()) {
                val nextRoute = next.removeFirst()
                // if (nextRoute.start != "AA" && valves[nextRoute.start]!!.rate == 0) continue
                val nextRate = valves[nextRoute.end]!!.rate
                if (nextRate == 0 && nextRoute.end != "AA") {
                    // get next, but not curr
                    val list = valves[nextRoute.end]!!.tunnels.filterNot { it == nextRoute.start }
                    list.forEach {
                        next.add(Route(nextRoute.end, it, nextRoute.dist + 1))
                    }
                } else {
                    result.add(nextRoute)
                }
            }

            result.forEach { r ->
                if (prev != "AA" && valves[prev]!!.rate == 0) return@forEach
                println("$prev -> ${r.end} takes ${r.dist}")
                // set up valves2 with these values & distances
                routeDist[(prev to r.end)] = r.dist // route from prev node to end node is distance
                if (valveRoutes[prev] == null) {
                    valveRoutes[prev] = mutableListOf()
                }
                valveRoutes[prev]?.add(r.end)
            }

        }
        // could do groupby
        val routedValves = mutableMapOf<String, Valve>()
        valveRoutes.keys.forEach {
            routedValves[it] = Valve(it, valves[it]!!.rate, valveRoutes[it]!!.toList())
        }
        valves.clear()

//        val valvesRateZero = valves.values.filter { it.rate == 0 }.toSet()

        var currValve = routedValves["AA"]!!
        var maxRelease = 0
        val startingState = State(0, 0, setOf()) // consider rate==0 to be on already
        // if flow rate == 0, set to "on"

        // Don't add turn on AA - rate is 0
        var nextSteps = currValve.tunnels.map {
            Step(
                Action.MOVE, routedValves[it]!!, startingState,
                routeDist[currValve.name to routedValves[it]!!.name]!!
            )
        }.sortedWith(compareByDescending { it.valve.rate }).toMutableList()
        var maxTime = 0
        //NEW - generate all moves
        // if moving to valve with rate - 0
          // - get all connecting valves that are not current valve

//        val cacheVistedValves = mutableSetOf(currValve to valvesRateZero)

        while (nextSteps.isNotEmpty()) {
            val step = nextSteps.removeFirst()
            currValve = step.valve
            val time = step.currState.time + step.timeCost
            //println("Time is $time, ${step.valve.name}, ${step.action}, ${step.currState.time}, ${step.currState.totalRelease}, ${step.currState.valvesOn.map { it.name }}")

            if (time > maxTime) {
                maxTime = time
                println("time is $maxTime, queue size is ${nextSteps.size}")
            }
            val currRelease = step.currState.totalRelease
            var valvesTurnedOn = step.currState.valvesOn
            if (time > MAX_ELAPSED_MIN) break
            if (step.action == Action.TURN_ON) {
                check(step.valve !in valvesTurnedOn)
            }
            val upperBound = scoreUpperBound(routedValves.values.toSet(), valvesTurnedOn, currValve, time, currRelease)
//            println("upper bound is $upperBound, max score = $maxRelease")
            if (upperBound < maxRelease) continue // we can't beat the current high score

            if (step.action == Action.TURN_ON && step.valve !in valvesTurnedOn) {
                valvesTurnedOn = valvesTurnedOn + listOf(step.valve!!)
                //println("Turn on ${step.valve.name}")
                val newRelease = currRelease + (MAX_TIME - time) * currValve.rate
                if (newRelease > maxRelease) println("max release = $newRelease")
                maxRelease = max(newRelease, maxRelease)
                // Generate next steps with this name on
                val next = currValve.tunnels.map {
                    Step(
                        Action.MOVE, routedValves[it]!!, State(time, newRelease, valvesTurnedOn),
                        routeDist[currValve.name to routedValves[it]!!.name]!!
                    )
                }.sortedWith(compareByDescending { it.valve.rate * (if (it.valve !in valvesTurnedOn) 1000 else -1000) })
//                println("sorted next valves: $next")
                nextSteps.addAll(next)
            } else {
                check(step.action == Action.MOVE)
                if (currValve !in valvesTurnedOn) {
                    nextSteps.add(Step(Action.TURN_ON, currValve, State(time, currRelease, valvesTurnedOn), 1))
                }
                nextSteps.addAll(currValve.tunnels.map {
                    Step(Action.MOVE, routedValves[it]!!, State(time, currRelease, valvesTurnedOn), routeDist[currValve.name to routedValves[it]!!.name]!!)
                })
            }
        }

        // move + open = 2 min
        // just move = 1 min, may want to move w/o open, argh


        // use groupby - map by name
        // start at aa
        // dfs, keep track of where visited, time, rate
        // subtract when go back - currmax and max


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
    println("time for real input part 1 is $timeInMillis")
}