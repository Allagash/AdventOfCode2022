import java.io.File

// Advent of Code 2022, Day 23: Unstable Diffusion
class Day23(input: String) {

    data class Pt(val x: Int, val y: Int) {
        fun generateNeighbors() : List<Pt>  =
            listOf( Pt(x-1, y-1), Pt(x-1, y), Pt (x-1, y+1),
            Pt(x, y-1),Pt(x, y+1),
            Pt(x+1, y-1), Pt(x+1, y), Pt(x+1, y+1))

        operator fun plus(other: Pt): Pt {
            return Pt(x + other.x, y + other.y)
        }
    }

    private var points: Set<Pt>

    init {
        points = input.split("\n").mapIndexed { row, s ->
            s.foldIndexed(emptyList<Pt>()) { col, sum, c ->
                val newList = if (c == '#') {
                    sum + listOf(Pt(row, col))
                } else {
                    sum
                }
                newList
            }
        }.flatten().toSet()
    }

    enum class Dir(val testPts: List<Pt>, val movePt: Pt) {
        NORTH( listOf( Pt(-1, -1), Pt(-1, 0), Pt(-1, 1)), Pt(-1, 0)),
        SOUTH( listOf( Pt(1, -1), Pt(1, 0), Pt(1, 1)), Pt(1, 0)),
        WEST( listOf( Pt(-1, -1), Pt(0, -1), Pt(1, -1)), Pt(0, -1)),
        EAST( listOf( Pt(-1, 1), Pt(0, 1), Pt(1, 1)), Pt(0, 1)),
    }

    private fun Set<Pt>.print() {
        for (r in minOf{it.x}..maxOf{it.x}) {
            for (c in minOf{it.y}..maxOf{it.y}) {
                print(if (Pt(r, c) in this) '#' else '.')
            }
            println()
        }
    }

    fun part1(): Int  {
        val dirs = mutableListOf(Dir.NORTH, Dir.SOUTH, Dir.WEST, Dir.EAST)
        repeat(10) {
            // for each point, add new proposed point
            val newPts = proposeMoves(dirs)
            moveElves(newPts)
            dirs.add(dirs.removeFirst())
        }
        val xLength =  points.maxOf{it.x} - points.minOf{it.x} + 1
        val yLength =  points.maxOf{it.y} - points.minOf{it.y} + 1
        return xLength * yLength - points.size
    }

    private fun proposeMoves(dirs: MutableList<Dir>): MutableMap<Pt, List<Pt>> {
        val newPts = mutableMapOf<Pt, List<Pt>>()
        points.forEach nextPt@{ p ->
            if (p.generateNeighbors().none { it in points }) {
                check(p !in newPts)
                newPts[p] = listOf(p)
                return@nextPt // no neighbors, don't move
            }
            dirs.forEach { d ->
                if (d.testPts.none { (p + it) in points }) {
                    val movers = newPts.getOrElse(p + d.movePt) { emptyList() }
                    newPts[p + d.movePt] = movers + listOf(p)
                    return@nextPt
                }
            }
            check(p !in newPts)
            newPts[p] = listOf(p)  // can't move, add original position
        }
        return newPts
    }

    fun part2(): Int {
        val dirs = mutableListOf(Dir.NORTH, Dir.SOUTH, Dir.WEST, Dir.EAST)
        var count = 0
        do {
            count++
            val prevPts = points.toSet()
            val newPts = proposeMoves(dirs)
            moveElves(newPts)
            dirs.add(dirs.removeFirst())
        } while (points != prevPts)
        return count
    }

    private fun moveElves(newPts: MutableMap<Pt, List<Pt>>) {
        val newConfig = mutableSetOf<Pt>()
        for ((pt, movers) in newPts) {
            if (movers.size == 1) {
                newConfig.add(pt) // new pos for 1 elf
            } else {
                newConfig.addAll(movers) // these elves can't move
            }
        }
        check(newConfig.size == points.size) { "${newConfig.size}, old ${points.size}" }
        points = newConfig
    }
}

fun main() {

    fun readInputAsOneLine(name: String) = File("src", "$name.txt").readText()

    val testInput = readInputAsOneLine("Day23_test2")
    check( Day23(testInput).part1() == 110)
    check(Day23(testInput).part2() == 20)

    val input = readInputAsOneLine("Day23")
    println(Day23(input).part1())
    println(Day23(input).part2())
}