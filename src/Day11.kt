import java.io.File

// Advent of Code 2022, Day 11, Monkey in the Middle

fun main() {

    fun readInputAsOneLine(name: String) = File("src", "$name.txt").readText().trim()

    data class Monkey(val items: MutableList<Long>, val operation: List<String>, val test: Int, val monkeys: List<Int>, var inspections: Long = 0)

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
                        items = it.substringAfter(":").split(",").map{it.trim().toLong()}
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

    fun part1(monkeys: List<Monkey>): Long {
        repeat(20) {
            monkeys.forEach {monkey->
                monkey.inspections += monkey.items.size
                while (monkey.items.isNotEmpty()) {
                    val item = monkey.items.removeFirst()
                    val newLevel = applyOp(item, monkey.operation) / 3
                    val newMonkey = if (newLevel % monkey.test == 0L) monkey.monkeys[0] else monkey.monkeys[1]
                    monkeys[newMonkey].items.add(newLevel)
                }
            }
        }
        val sortedMonkeys = monkeys.sortedByDescending { it.inspections }
        return sortedMonkeys[0].inspections * sortedMonkeys[1].inspections
    }

    fun part2(monkeys: List<Monkey>) : Long {
        val modVal = monkeys.fold(1L) { acc, monkey ->  acc * monkey.test.toLong()}

        repeat(10_000) {
            monkeys.forEach {monkey->
                monkey.inspections += monkey.items.size
                while (monkey.items.isNotEmpty()) {
                    val item = monkey.items.removeFirst()
                    val newLevel = applyOp(item, monkey.operation) % modVal
                    val newMonkey = if (newLevel % monkey.test == 0L) monkey.monkeys[0] else monkey.monkeys[1]
                    monkeys[newMonkey].items.add(newLevel)
                }
            }
        }
        monkeys.forEachIndexed { index, monkey -> println("Monkey $index, ${monkey.inspections} inspections")}
        val sortedMonkeys = monkeys.sortedByDescending { it.inspections }
        return sortedMonkeys[0].inspections * sortedMonkeys[1].inspections
    }

    var testInput = parse(readInputAsOneLine("Day11_test"))
    check(part1(testInput) == 10605L)
    testInput = parse(readInputAsOneLine("Day11_test"))
    check(part2(testInput) == 2713310158L)

    var input = parse(readInputAsOneLine("Day11"))
    println(part1(input))
    input = parse(readInputAsOneLine("Day11"))
    println(part2(input)) // 13320449198 too log
}