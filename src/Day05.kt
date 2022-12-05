// Advent of Code 2022, Day 05, Supply Stacks

fun main() {

    data class Day05(val stacks: MutableList<ArrayDeque<Char>>, val moves: List<List<Int>>)

    fun part1(input: Day05): String {
        val stacks = input.stacks
        val moves = input.moves
        moves.forEach {
            for (i in 1..it[0]) {
                val c = stacks[it[1]].removeLast()
                stacks[it[2]].add(c)
            }
        }
        return stacks.fold("") { acc, next -> acc + next.last() }
    }

    fun part2(input: Day05): String {
        val stacks = input.stacks
        val moves = input.moves
        moves.forEach {
            val all = mutableListOf<Char>()
            for (i in 1..it[0]) {
                val c = stacks[it[1]].removeLast()
                all.add(0, c)
            }
            stacks[it[2]].addAll(all)
        }
        return stacks.fold("") { acc, next -> acc + next.last() }
    }

    fun parse(input: List<String>) : Day05 {
        // get size
        var size = 0
        run size@ {
            input.forEach {
                if (!it.contains("[")) {
                    val indicies = it.trim().split("\\s+".toRegex())
                    size = indicies.last().toInt()
                    return@size
                }
            }
        }
        //println("size is $size")

        val stacks = MutableList(size) {ArrayDeque<Char>()}
        val moves =  mutableListOf<List<Int>>()
        run moveStart@ {
            input.forEach {
                if (it.contains("[")) {
                    var i = 0
                    var stackIdx = 0
                    while (i < it.lastIndex) {
                        if (it[i] == '[') {
                            stacks[stackIdx].addFirst(it[i+1])
                        }
                        i+= 4
                        stackIdx++
                    }
                    return@forEach // continue
                } else if (it.contains("move")) {
                    val move = it.trim().split("move ", " from ", " to ")
                    // subtract 1 for 0-based indices
                    moves.add(listOf(move[1].toInt(), move[2].toInt()-1, move[3].toInt()-1))
                    //println(moves)
                }
            }
        }
        //println(stacks)
        return Day05(stacks, moves)
    }

     var parsedTestInput = parse(readInput("Day05_test"))
    //println(parsedTestInput)
    check(part1(parsedTestInput) == "CMZ")
    parsedTestInput = parse(readInput("Day05_test"))  // reset mutable parsed input
    check(part2(parsedTestInput) == "MCD")

    var input = parse(readInput("Day05"))
    println(part1(input))
    input = parse(readInput("Day05"))
    println(part2(input))
}
