import java.io.File
import kotlin.math.abs
import kotlin.math.min
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

        //!FIX 3rd one screws up - needs to go down one more
        repeat(2022) {i ->
            var rock = shapes[i % shapes.size]
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

    fun part2(input: String): Int {

        return 0
    }

    val testInput = readInputAsOneLine("Day17_test")
    println(part1(testInput))

    val input = readInputAsOneLine("Day17")
    println(part1(input))
//    println(part1(input, 2_000_000))
//    println(part2(input, 4_000_000))
}