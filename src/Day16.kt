import java.io.File
import kotlin.math.max
import kotlin.system.measureTimeMillis

// Advent of Code 2022, Day 16, Proboscidea Volcanium


data class Valve(val name: String, val rate: Int, val tunnels: List<String>)

data class State(val time: Int, val totalRelease: Int, val valvesOn: Set<Valve>)

enum class Action {
    TURN_ON, MOVE
}

data class Step(val action: Action, val valve: Valve, val currState: State)

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


    fun part1(input: String): Int {
        val valves = input.split("\n")
            .map {
                it.split(" ")
                    .drop(1) // initial empty
            }.map {
                Valve(it[0], it[3].split("=", ";")[1].toInt(),
                    it.subList(8, it.size).map { if (it.last() == ',') it.dropLast(1) else it })
            }.associateBy { it.name }

        //println(valves)

        val valvesRateZero = valves.values.filter { it.rate == 0 }.toSet()

        var currValve = valves["AA"]!!
        var maxRelease = 0
        val startingState = State(0, 0, valvesRateZero) // consider rate==0 to be on already
        // if flow rate == 0, set to "on"

        // Don't add turn on AA - rate is 0
        var nextSteps = currValve.tunnels.map { Step(Action.MOVE, valves[it]!!, startingState) }
            .sortedWith(compareByDescending { it.valve.rate  }).toMutableList()
        var maxTime = 0

//        val cacheVistedValves = mutableSetOf(currValve to valvesRateZero)

        while (nextSteps.isNotEmpty()) {
            val step = nextSteps.removeFirst()
            currValve = step.valve
            val time = step.currState.time + 1
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
            val upperBound = scoreUpperBound(valves.values.toSet(), valvesTurnedOn, currValve, time, currRelease)
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
                    Step(Action.MOVE, valves[it]!!, State(time, newRelease, valvesTurnedOn))
                }.sortedWith(compareByDescending { it.valve.rate * (if (it.valve !in valvesTurnedOn) 1000 else -1000) })
//                println("sorted next valves: $next")
                nextSteps.addAll(next)
            } else {
                check(step.action == Action.MOVE)
                if (currValve !in valvesTurnedOn) {
                    nextSteps.add(Step(Action.TURN_ON, currValve, State(time, currRelease, valvesTurnedOn)))
                }
                nextSteps.addAll(currValve.tunnels.map {
                    Step(Action.MOVE, valves[it]!!, State(time, currRelease, valvesTurnedOn))
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