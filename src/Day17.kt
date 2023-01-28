import java.io.File
import kotlin.math.max

// Advent of Code 2022, Day 17, Pyroclastic Flow
interface Rock {
    val rows: List<String> // 4 strings, 4 char long
    val height: Int
    val width: Int
}

class Rock1 : Rock {
    // maybe reverse list?
    override val rows = listOf("....", "....", "....", "####").reversed()
    override val height = 1
    override val width = 4
}

class Rock2 : Rock {
    override val rows = listOf("....", ".#..", "###.", ".#..").reversed()
    override val height = 3
    override val width = 3
}

class Rock3 : Rock {
    override val rows = listOf("....", "..#.", "..#.", "###.").reversed()
    override val height = 3
    override val width = 3
}

class Rock4 : Rock {
    override val rows = listOf("#...", "#...", "#...", "#...").reversed()
    override val height = 4
    override val width = 1
}

class Rock5 : Rock {
    override val rows = listOf("....", "....", "##..", "##..").reversed()
    override val height = 2
    override val width = 2
}

data class Offset(var x: Int, var y: Int)

data class Point(var x: Int, var y: Int)

val shapes = listOf(Rock1(), Rock2(), Rock3(), Rock4(), Rock5())

fun main() {

    fun readInputAsOneLine(name: String) = File("src", "$name.txt").readText().trim()

    fun intersect(pt: Offset, rock: Rock, world: Array<CharArray>): Boolean {
        if (pt.x < 0 || pt.y < 0 || pt.x + rock.width > 7) return true

        for (y in rock.rows.indices) {
            for (x in rock.rows[y].indices) {
                if (rock.rows[y][x] != '#') continue
                val worldPt = world[pt.y + y][pt.x + x]
                if (worldPt != ' ' && worldPt != '.') {
                    return true
                }
            }
        }
        return false
    }

    fun printWorld(highestY: Int, world: Array<CharArray>) {
        for (y in highestY + 4 downTo 0) {
            print('|')
            print(world[y])
            println('|')
        }
        println("+-------+")
    }

    fun part1(input: String): Int {
        val world = Array(2022 * 4 + 10) { CharArray(7) {' '} }
        var highestY = -1
        var moveIdx = -1

        repeat(2022) {i ->
            val rock = shapes[i % shapes.size]
            var pt = Offset(2, highestY + 4)
            while (true) {
                moveIdx = (moveIdx+1) % input.length
                val push = if (input[moveIdx] == '<') -1 else 1
                var nextPt = Offset(pt.x + push, pt.y)
                if (!intersect(nextPt, rock, world)) {
                    pt = nextPt
                }
                nextPt = Offset(pt.x, pt.y - 1)
                if (intersect(nextPt, rock, world)) {
                    break
                }
                pt = nextPt
            }
            // freeze it
            for (y in rock.rows.indices) {
                for (x in rock.rows[y].indices) {
                    if (rock.rows[y][x] != '#') continue
                    world[pt.y + y][pt.x + x] = rock.rows[y][x]
                }
            }
            highestY = max(highestY, pt.y + rock.height - 1)
        }

        //printWorld(highestY, world)
        return highestY + 1
    }

    class Day17(input: String) {
        val jets = input

        val rock0 = listOf(Point(0, 0), Point(1, 0), Point(2, 0), Point(3, 0))
        val rock1 = listOf(Point(1, 0), Point(0, 1), Point(1, 1), Point(2, 1), Point(1, 2))
        val rock2 = listOf(Point(0, 0), Point(1, 0), Point(2, 0), Point(2, 1), Point(2,2))
        val rock3 = listOf(Point(0, 0), Point(0, 1), Point(0, 2), Point(0, 3))
        val rock4 = listOf(Point(0, 0), Point(1, 0), Point(0, 1), Point(1, 1))
        val rocks = listOf(rock0, rock1, rock2, rock3, rock4)

        fun printWorld(highestY: Int, world: Set<Point>) {
            for (y in highestY + 4 downTo 0) {
                print('|')
                for (x in 0..6) {
                    val c = if (Point(x, y) in world) '#' else '.'
                    print(c)
                }
                println('|')
            }
            println("+-------+\n")
        }

        fun intersect(pt: Point, rock: List<Point>, world: Set<Point>): Boolean {
            rock.forEach {
                val x = it.x + pt.x
                val y = it.y + pt.y
                if (x < 0 || y < 0 || x >= 7) return true // >= 7 or just >?

                if (Point(x,y) in world) return true
            }
            return false
        }

        fun findHeight() : Long {
            var highestY = -1
            var moveIdx = -1
            val world = mutableSetOf<Point>()

            repeat(2022) {i ->
                val rock = rocks[i % rocks.size]
                var pt = Point(2, highestY + 4)
                while (true) {
                    moveIdx = (moveIdx+1) % jets.length
                    check(jets[moveIdx] == '<' || jets[moveIdx] == '>')
                    val push = if (jets[moveIdx] == '<') -1 else 1
                    var nextPt = Point(pt.x + push, pt.y)
                    if (!intersect(nextPt, rock, world)) {
                        pt = nextPt
                    }
                    nextPt = Point(pt.x, pt.y - 1)
                    if (intersect(nextPt, rock, world)) {
                        break
                    }
                    pt = nextPt
                }
                // freeze it
                rock.forEach {
                    val x = it.x + pt.x
                    val y = it.y + pt.y
                    world.add(Point(x,y))
                    highestY = max(highestY, y)
                }
//                printWorld(highestY, world)
            }

            return highestY + 1L
        }

    }

    fun part2(input: String): Long {
        val d = Day17(input)
        return d.findHeight()
    }

    val testInput = readInputAsOneLine("Day17_test")
    check(part1(testInput)==3068)
    println("${part2(testInput)} should equal 3068")

    val input = readInputAsOneLine("Day17")
    println(part1(input))
//    println(part2(input, 4_000_000))
}