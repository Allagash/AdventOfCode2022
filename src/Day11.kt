import java.io.File

// Advent of Code 2022, Day 11, Monkey in the Middle

fun main() {

    fun readInputAsOneLine(name: String) = File("src", "$name.txt").readText().trim()

    data class Monkey(
        val items: MutableList<Long>,
        val operation: List<String>,
        val test: Int,
        val monkeyTrue: Int,
        val monkeyFalse: Int
    )

    fun parse(input: String): List<Monkey> {
        val output = mutableListOf<Monkey>()
        val monkeyInput = input.split("\n\n")
        monkeyInput.forEach { it ->
            val lines = it.split("\n").map { it.trim() }
            output.add(
                Monkey(
                    lines[1].substringAfter(":").split(",").map { j -> j.trim().toLong() }.toMutableList(),
                    lines[2].substringAfter("Operation: new = old ").split(" "),
                    lines[3].substringAfter("Test: divisible by ").toInt(),
                    lines[4].substringAfter("If true: throw to monkey ").toInt(),
                    lines[5].substringAfter("If false: throw to monkey ").toInt()
                )
            )
        }
        return output
    }

    fun applyOp(level: Long, op: List<String>): Long {
        var output = level
        val second = if (op[1] == "old") level else op[1].toLong()
        when (op[0]) {
            "*" -> output *= second
            "+" -> output += second
            else -> check(false)
        }
        return output
    }

    fun solve(monkeys: List<Monkey>, part1: Boolean): Long {
        val modVal = monkeys.fold(1L) { acc, monkey -> acc * monkey.test.toLong() }
        val rounds = if (part1) 20 else 10_000
        val inspections = LongArray(monkeys.size)

        repeat(rounds) {
            monkeys.forEachIndexed() { i, monkey ->
                inspections[i] = inspections[i] + monkey.items.size
                while (monkey.items.isNotEmpty()) {
                    val item = monkey.items.removeFirst()
                    var newLevel = applyOp(item, monkey.operation)
                    newLevel = if (part1) newLevel / 3 else newLevel % modVal
                    val newMonkey = if (newLevel % monkey.test == 0L) monkey.monkeyTrue else monkey.monkeyFalse
                    monkeys[newMonkey].items.add(newLevel)
                }
            }
        }
        // monkeys.forEachIndexed { index, monkey -> println("Monkey $index, ${monkey.inspections} inspections")}
        return inspections
            .sortedDescending()
            .take(2)
            .reduce { acc, l -> acc * l }
    }

    var testInput = parse(readInputAsOneLine("Day11_test"))
    check(solve(testInput.toList(), true) == 10605L)
    testInput = parse(readInputAsOneLine("Day11_test"))
    check(solve(testInput, false) == 2713310158L)

    var input = parse(readInputAsOneLine("Day11"))
    println(solve(input, true))
    input = parse(readInputAsOneLine("Day11"))
    println(solve(input, false))
}