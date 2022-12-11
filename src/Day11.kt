import java.io.File

// Advent of Code 2022, Day 11, Monkey in the Middle

fun main() {

    fun readInputAsOneLine(name: String) = File("src", "$name.txt").readText().trim()

    data class Monkey(val items: MutableList<Long>, val operation: List<String>, val test: Int, val monkeys: List<Int>)

    fun parse(input: String) : List<Monkey> {
        val output = mutableListOf<Monkey>()

        val monkeyInput = input.split("\n\n")
        monkeyInput.forEach { it ->
            var items = listOf<Long>()
            var op = listOf<String>()
            var test = 0
            val monkeys = mutableListOf<Int>()
            val lines = it.split("\n").map{it.trim()}
            lines.forEach {
                when {
                    it.startsWith("Starting items:") -> {
                        items = it.substringAfter(":").split(",").map{j -> j.trim().toLong()}
                    }
                    it.startsWith("Operation:") -> {
                        op = it.substringAfter("Operation: new = old ").split(" ")
                    }
                    it.startsWith("Test: divisible by ") -> {
                        test = it.substringAfter("Test: divisible by ").toInt()
                    }
                    it.startsWith("If true: throw to monkey ") -> {
                        monkeys.add(0, it.substringAfter("If true: throw to monkey ").toInt())
                    }
                    it.startsWith("If false: throw to monkey ") -> {
                        monkeys.add(it.substringAfter("If false: throw to monkey ").toInt())
                    }
                }
            }
            output.add(Monkey(items.toMutableList(), op, test, monkeys))
        }
        return output
    }

    fun applyOp(level: Long, op: List<String>) : Long {
        var output = level
        val second = if (op[1] == "old") level else op[1].toLong()
        when(op[0]) {
            "*" -> output *= second
            "+" -> output += second
            else -> check(false)
        }
        return output
    }

    fun solve(monkeys: List<Monkey>, part1: Boolean) : Long {
        val modVal = monkeys.fold(1L) { acc, monkey ->  acc * monkey.test.toLong()}
        val rounds = if (part1) 20 else 10_000
        val inspections = LongArray(monkeys.size)

        repeat(rounds) {
            monkeys.forEachIndexed() {i, monkey->
                inspections[i] = inspections[i] + monkey.items.size
                while (monkey.items.isNotEmpty()) {
                    val item = monkey.items.removeFirst()
                    var newLevel = applyOp(item, monkey.operation)
                    newLevel = if (part1) newLevel / 3 else newLevel % modVal
                    val newMonkey = if (newLevel % monkey.test == 0L) monkey.monkeys[0] else monkey.monkeys[1]
                    monkeys[newMonkey].items.add(newLevel)
                }
            }
        }
        // monkeys.forEachIndexed { index, monkey -> println("Monkey $index, ${monkey.inspections} inspections")}
        return inspections
            .sortedDescending()
            .take(2)
            .reduce { acc, l ->  acc * l}
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