import java.io.File

// Advent of Code 2022, Day 21: Monkey Math

private val DAY21_PATTERN_VAL = """(\w+): (\d+)""".toRegex()
private val DAY21_PATTERN_EXP = """(\w+): (\w+) ([-+*/]) (\w+)""".toRegex()

data class Day21Exp(val name: String, var value: Long? = null,  var var1: String? = null,
    var op:Char? = null, var var2: String? = null)

fun main() {

    fun readInputAsOneLine(name: String) = File("src", "$name.txt").readText().trim()

    fun part1(input: String): Long {

        val expressions = input.split("\n").map { line ->
            if (line.contains("[-+*/]".toRegex())){
                val (name, var1, op, var2) = requireNotNull(DAY21_PATTERN_EXP.matchEntire(line)) { line }.destructured
                Day21Exp(name, null, var1, op.first(), var2)
            } else {
                val (name, value) = requireNotNull(DAY21_PATTERN_VAL.matchEntire(line)) { line }.destructured
                Day21Exp(name, value.toLong())
            }
        }.associateBy { it.name }

        fun getValue(exp: Day21Exp) : Long {
            if (exp.value != null) return exp.value!!
            val val1 = getValue(expressions[exp.var1]!!)
            val val2 = getValue(expressions[exp.var2]!!)
            return when (exp.op) {
                '+' -> val1 + val2
                '-' -> val1 - val2
                '*' -> val1 * val2
                '/' -> val1 / val2
                else -> throw Exception("Bad op: ${exp.op}")
            }
        }

        return getValue(expressions["root"]!!)
    }

    fun part2(input: String): Long {

        val expressions = input.split("\n").map { line ->
            if (line.contains("[-+*/]".toRegex())) {
                val (name, var1, op, var2) = requireNotNull(DAY21_PATTERN_EXP.matchEntire(line)) { line }.destructured
                Day21Exp(name, null, var1, op.first(), var2)
            } else {
                val (name, value) = requireNotNull(DAY21_PATTERN_VAL.matchEntire(line)) { line }.destructured
                Day21Exp(name, value.toLong())
            }
        }.associateBy { it.name }

        fun containsHuman(exp: Day21Exp): Boolean {
            if (exp.name == "humn") return true
            if (exp.value != null) return false
            return containsHuman(expressions[exp.var1]!!) || containsHuman(expressions[exp.var2]!!)
        }

        fun getValue(exp: Day21Exp): Long {
            if (exp.value != null) return exp.value!!
            val val1 = getValue(expressions[exp.var1]!!)
            val val2 = getValue(expressions[exp.var2]!!)
            return when (exp.op) {
                '+' -> val1 + val2
                '-' -> val1 - val2
                '*' -> val1 * val2
                '/' -> val1 / val2
                else -> throw Exception("Bad op: ${exp.op}")
            }
        }

        fun getHumanValue(exp: Day21Exp, matchVal: Long): Long {
            if (exp.value != null) {
                // println("value for ${exp.name} is ${exp.value}")
                if (exp.name == "humn") {
                    return matchVal // ignore humn value
                }
                return exp.value!!
            }
            val left = expressions[exp.var1]!!
            val right = expressions[exp.var2]!!
            val leftHasHuman = containsHuman(left)
            val rightHasHuman = containsHuman(right)
            check(leftHasHuman != rightHasHuman)
            var otherSideVal: Long
            val humanExp: Day21Exp
            if (leftHasHuman) {
                humanExp = left
                otherSideVal = getValue(right)
//                println("expression ${exp.name} - ${left.name} has human, op is ${exp.op}, right is $otherSideVal")
            } else {
                humanExp = right
                otherSideVal = getValue(left)
//                println("expression ${exp.name} - left is $otherSideVal, op is ${exp.op}, ${right.name} has human")
            }

            if (exp.name == "root") {
                return getHumanValue(humanExp, otherSideVal)
            }

            if (leftHasHuman) {
                otherSideVal = when (exp.op) {
                    '+' -> matchVal - otherSideVal
                    '-' -> matchVal + otherSideVal
                    '*' -> matchVal / otherSideVal
                    '/' -> matchVal * otherSideVal
                    else -> throw Exception("Bad op: ${exp.op}")
                }
            } else {
                otherSideVal = when (exp.op) {
                    '+' -> matchVal - otherSideVal
                    '-' -> otherSideVal - matchVal
                    '*' -> matchVal / otherSideVal
                    '/' -> otherSideVal / matchVal
                    else -> throw Exception("Bad op: ${exp.op}")
                }
            }

            return getHumanValue(humanExp, otherSideVal)
        }

        return getHumanValue(expressions["root"]!!, 0)
    }

    val testInput = readInputAsOneLine("Day21_test")
    check(part1(testInput) == 152L)
    check(part2(testInput) == 301L)

    val input = readInputAsOneLine("Day21")
    println(part1(input))
    println(part2(input))
}