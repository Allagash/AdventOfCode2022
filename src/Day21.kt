import java.io.File

// Advent of Code 2022, Day 21: Monkey Math

data class Day21Exp(val name: String, var value: Long? = null,  var var1: String? = null,
    var op:Char? = null, var var2: String? = null)

class Day21(input: String) {

    companion object {
        private val DAY21_PATTERN_VAL = """(\w+): (\d+)""".toRegex()
        private val DAY21_PATTERN_EXP = """(\w+): (\w+) ([-+*/]) (\w+)""".toRegex()
        private const val HUMAN_ID = "humn"
    }

    private val expressions : Map<String, Day21Exp>

    init {
        expressions = input.split("\n").map { line ->
            if (line.contains("[-+*/]".toRegex())){
                val (name, var1, op, var2) = requireNotNull(DAY21_PATTERN_EXP.matchEntire(line)) { line }.destructured
                Day21Exp(name, null, var1, op.first(), var2)
            } else {
                val (name, value) = requireNotNull(DAY21_PATTERN_VAL.matchEntire(line)) { line }.destructured
                Day21Exp(name, value.toLong())
            }
        }.associateBy { it.name }
    }

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

    private fun containsHuman(exp: Day21Exp): Boolean {
        if (exp.name == HUMAN_ID) return true
        if (exp.value != null) return false
        return containsHuman(expressions[exp.var1]!!) || containsHuman(expressions[exp.var2]!!)
    }

    private fun getHumanValue(exp: Day21Exp, matchVal: Long): Long {
        if (exp.value != null) {
            return if (exp.name == HUMAN_ID) matchVal else exp.value!!
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
        } else {
            humanExp = right
            otherSideVal = getValue(left)
        }

        if (exp.name != "root") {
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
        }

        return getHumanValue(humanExp, otherSideVal)
    }

    fun part1(): Long = getValue(expressions["root"]!!)

    fun part2(): Long =  getHumanValue(expressions["root"]!!, 0)
}

fun main() {

    fun readInputAsOneLine(name: String) = File("src", "$name.txt").readText().trim()

    val testSolver = Day21(readInputAsOneLine("Day21_test"))
    check(testSolver.part1() == 152L)
    check(testSolver.part2() == 301L)

    val solver = Day21(readInputAsOneLine("Day21"))
    println(solver.part1())
    println(solver.part2())
}