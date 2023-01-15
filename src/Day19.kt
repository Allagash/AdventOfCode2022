import java.io.File
import java.util.PriorityQueue

// Advent of Code 2022, Day 19: Not Enough Minerals

private const val PATTERN =
    """Blueprint (\d+): Each ore robot costs (\d+) ore. Each clay """ +
    """robot costs (\d+) ore. Each obsidian robot costs (\d+) ore and (\d+) clay. """ +
    """Each geode robot costs (\d+) ore and (\d+) obsidian."""

data class Costs(val blueprintNum: Int, val oreRobotCost: Int, val clayRobotOreCost: Int, val obsidianRobotOreCost: Int,
                 val obsidianRobotClayCost: Int, val geodeRobotOreCost: Int, val geodeRobotObsidianCost: Int)

data class OreState(val numOreRobots: Int, val numOre: Int, val numClayRobots: Int, val numClay: Int,
    val numObsidianRobots: Int, val numObsidian: Int, val numGeodeRobots: Int, val numGeode: Int, val time: Int)


fun main() {

    fun readInput(name: String) = File("src", "$name.txt")
        .readLines()

    fun solve(input: List<String>, part1: Boolean): Int {
        val size = if (part1) input.size else 3
        val blueprints = input.take(size).map { line ->
            val (blueprint, oreRobotCost, clayRobotCost, obsidianRobotOreCost, obsidianRobotClayCost,
                geodeRobotOreCost, geodeRobotObsidianCost) =
                requireNotNull(PATTERN.toRegex().matchEntire(line)) { line }.destructured
            Costs(blueprint.toInt(), oreRobotCost.toInt(), clayRobotCost.toInt(), obsidianRobotOreCost.toInt(),
                obsidianRobotClayCost.toInt(), geodeRobotOreCost.toInt(), geodeRobotObsidianCost.toInt())
        }

        val stateComparator =  Comparator<OreState> { a, b ->
                when { // sort descending
                    a.numGeode != b.numGeode -> b.numGeode - a.numGeode
                    a.numObsidian != b.numObsidian -> b.numObsidian - a.numObsidian
                    a.numClay != b.numClay -> b.numClay - a.numClay
                    else -> b.numOre - a.numOre
                }
            }
        var result = 0
        val totalTime = if (part1) 24 else 32
        val results = mutableListOf<Int>()

        blueprints.forEach { cost->
            val states = PriorityQueue(stateComparator)
            val bluePrint = cost.blueprintNum
            val maxOreCost = maxOf(cost.oreRobotCost, cost.clayRobotOreCost, cost.obsidianRobotOreCost, cost.geodeRobotOreCost)
            val initialState = OreState(1, 0, 0, 0, 0, 0, 0, 0, totalTime)
            states.add(initialState)
            val cache = hashSetOf<OreState>()
            var maxGeodes = Int.MIN_VALUE
            var prevMaxGeodes = Int.MIN_VALUE
            while (states.isNotEmpty()) {
                var state = states.remove()
                maxGeodes = maxOf(maxGeodes, state.numGeode)
                if (state in cache || state.time <= 0) continue
                // if we can't beat the max score, cull
                // assume we can make 1 geode robot per term
                val maxGeodesPossible = state.numGeode + (state.numGeodeRobots * state.time) + (state.time * (state.time - 1)) /2
                if (maxGeodes > maxGeodesPossible) continue
                //if (cache.size > 100_000_000) cache.clear()
                cache.add(state)
                if (maxGeodes > 0) {
                    if (maxGeodes > prevMaxGeodes) {
                        prevMaxGeodes = maxGeodes
//                        println("$bluePrint - time ${state.time}, geodes $maxGeodes, queue size ${states.size}, cache size ${cache.size}")
                    }
                }

                // See https://github.com/ritesh-singh/aoc-2022-kotlin/blob/main/src/day19/Day19.kt
                // Reduce state by removing redundant robots
//                if (state.numOreRobots >= maxOreCost) {
//                    state = state.copy(
//                        numOreRobots = maxOreCost
//                    )
//                }
//                if (state.numClayRobots >= cost.obsidianRobotClayCost) {
//                    state = state.copy(
//                        numClayRobots = cost.obsidianRobotClayCost
//                    )
//                }
//                if (state.numObsidianRobots >= cost.geodeRobotObsidianCost) {
//                    state = state.copy(
//                        numObsidianRobots = cost.geodeRobotObsidianCost
//                    )
//                }

                // Reduce state by removing resources which won't be used
                // Reduce state by removing resources not required per minute
                if (state.numOre >= state.time * maxOreCost - state.numOreRobots * (state.time - 1)) {
                    state = state.copy(
                        numOre = state.time * maxOreCost - state.numOreRobots * (state.time - 1)
                    )
                }
                if (state.numClay >= state.time * cost.obsidianRobotClayCost - state.numClayRobots * (state.time - 1)) {
                    state = state.copy(
                        numClay = state.time * cost.obsidianRobotClayCost - state.numClayRobots * (state.time - 1)
                    )
                }
                if (state.numObsidian >= state.time * cost.geodeRobotObsidianCost - state.numObsidianRobots * (state.time - 1)) {
                    state = state.copy(
                        numObsidian = state.time * cost.geodeRobotObsidianCost - state.numObsidianRobots * (state.time - 1)
                    )
                }


                // mine more minerals
                states.add(
                    state.copy(
                        numOre = state.numOreRobots + state.numOre,
                        numClay = state.numClayRobots + state.numClay,
                        numObsidian = state.numObsidianRobots + state.numObsidian,
                        numGeode = state.numGeodeRobots + state.numGeode,
                        time = state.time - 1
                    )
                )

                // Can only make 1 robot per turn
                if (state.numOre >= cost.oreRobotCost && state.numOreRobots < maxOreCost) { // make ore robot
                    val oreLeft = state.numOre - cost.oreRobotCost
                    states.add(
                        state.copy(
                            numOre = state.numOreRobots + oreLeft,
                            numClay = state.numClayRobots + state.numClay,
                            numObsidian = state.numObsidianRobots + state.numObsidian,
                            numGeode = state.numGeodeRobots + state.numGeode,
                            numOreRobots = state.numOreRobots + 1,
                            time = state.time - 1
                        )
                    )
                }

                if (state.numOre >= cost.clayRobotOreCost && state.numClayRobots < cost.obsidianRobotClayCost) {  // make clay robot
                    val oreLeft = state.numOre - cost.clayRobotOreCost
                    states.add(
                        state.copy(
                            numOre = state.numOreRobots + oreLeft,
                            numClay = state.numClayRobots + state.numClay,
                            numObsidian = state.numObsidianRobots + state.numObsidian,
                            numGeode = state.numGeodeRobots + state.numGeode,
                            numClayRobots = state.numClayRobots + 1,
                            time = state.time - 1
                        )
                    )
                }

                if (state.numOre >= cost.obsidianRobotOreCost && state.numClay >= cost.obsidianRobotClayCost &&
                    state.numObsidianRobots < cost.geodeRobotObsidianCost) {// make obsidian robot
                    val oreLeft = state.numOre - cost.obsidianRobotOreCost
                    val clayLeft = state.numClay - cost.obsidianRobotClayCost
                    states.add(
                        state.copy(
                            numOre = state.numOreRobots + oreLeft,
                            numClay = state.numClayRobots + clayLeft,
                            numObsidian = state.numObsidianRobots + state.numObsidian,
                            numGeode = state.numGeodeRobots + state.numGeode,
                            numObsidianRobots = state.numObsidianRobots + 1,
                            time = state.time - 1
                        )
                    )
                }

                if (state.numOre >= cost.geodeRobotOreCost && state.numObsidian >= cost.geodeRobotObsidianCost) {  // make geode robot
                    val oreLeft = state.numOre - cost.geodeRobotOreCost
                    val obsidianLeft = state.numObsidian - cost.geodeRobotObsidianCost
                    states.add(
                        state.copy(
                            numOre = state.numOreRobots + oreLeft,
                            numClay = state.numClayRobots + state.numClay,
                            numObsidian = state.numObsidianRobots + obsidianLeft,
                            numGeode = state.numGeodeRobots + state.numGeode,
                            numGeodeRobots = state.numGeodeRobots + 1,
                            time = state.time - 1
                        )
                    )
                }
            }
//            println("$bluePrint * $maxGeodes")
            results.add(maxGeodes)
            if (part1) {
                result += bluePrint * maxGeodes
            }
//            println()
        }
        return if (part1) result else results.fold(1) {total, it -> total * it }
    }


    val testInput = readInput("Day19_test")
    check(solve(testInput, true) == 33)
    check(solve(testInput, false) == 56 * 62)
//
    val input = readInput("Day19")
    println(solve(input, true))
    println(solve(input, false))
}