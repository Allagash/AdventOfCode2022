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

    fun part1(input: List<String>): Int {
        val blueprints = input.map { line ->
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

        blueprints.forEach { cost->
            val states = PriorityQueue(stateComparator)
            val bluePrint = cost.blueprintNum
            val maxOreCost = maxOf(cost.oreRobotCost, cost.clayRobotOreCost, cost.obsidianRobotOreCost, cost.geodeRobotOreCost)
            val initialState = OreState(1, 0, 0, 0, 0, 0, 0, 0, 24)
            states.add(initialState)
            val cache = hashSetOf<OreState>()
            var maxGeodes = Int.MIN_VALUE
            while (states.isNotEmpty()) {
                val state = states.remove()
                if (state in cache) continue
                cache.add(state)
                maxGeodes = maxOf(maxGeodes, state.numGeode)
                //println("$bluePrint - $state")
                if (state.time <= 0) continue

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
                    state.numObsidianRobots < cost.geodeRobotObsidianCost) {  // make obsidian robot
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
            println("$bluePrint * $maxGeodes")
            result += bluePrint * maxGeodes
            println()
        }
        return result
    }

    fun part2(input: String): Int {

        return 0
    }

    val testInput = readInput("Day19_test")
    check(part1(testInput) == 33)

    val input = readInput("Day19")
    println(part1(input))
}