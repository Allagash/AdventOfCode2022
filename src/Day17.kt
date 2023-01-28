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

data class Offset(val x: Int, val y: Int)

data class Point(val x: Long, val y: Long)

data class Day17CacheItem(val rockIdx: Long, val moveIdx: Long, val yHeight: ULongArray) {
    @OptIn(ExperimentalUnsignedTypes::class)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Day17CacheItem

        if (rockIdx != other.rockIdx) return false
        if (moveIdx != other.moveIdx) return false
        if (!yHeight.contentEquals(other.yHeight)) return false

        return true
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun hashCode(): Int {
        var result = rockIdx
        result = 31 * result + moveIdx
        result = 31 * result + yHeight.contentHashCode()
        return result.toInt()
    }
}

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

        fun printWorld(highestY: Long, world: Set<Point>) {
            for (y in highestY + 4 downTo 0L) {
                print('|')
                for (x in 0L..6L) {
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

        fun getCacheItem(rockIdx: Long, moveIdx: Long, yHeight: LongArray, world: Set<Point>) : Day17CacheItem {
            val arr = ULongArray(7)
            val max = yHeight.max()
            for (x in 0L..6L) {
                var bitmask : ULong = 0u
                val startVal = max(0, max - 64 + 1).toLong()
                check(max - startVal + 1 <= 64)
                for (i in startVal..max) {
                    val bit : UInt = if (Point(x, i) in world) 1u else 0u
                    bitmask = bitmask.shl(1) + bit
                }
                arr[x.toInt()] = bitmask
            }
            return Day17CacheItem(rockIdx, moveIdx, arr)
        }

        fun findHeight() : Long {
            var highestY = -1L
            var highYPrev = 0L
            var moveIdx : Long = -1
            var prevRock = -1L
            val deltaYs = mutableListOf<Long>()
            val world = mutableSetOf<Point>()
            val yHeight = LongArray(7)
            val cache = mutableSetOf<Day17CacheItem>()
            var useCache = true
            var yOffset = -1L
            println("jets len = ${jets.length}")

            var rocksLeft = 1_000_000_000_000
            while (rocksLeft > 0) {
                val i = 1_000_000_000_000 - rocksLeft
                rocksLeft--
                println("rocksLeft = $rocksLeft")
                val rockIdx = (i % rocks.size).toLong()
                val rock = rocks[rockIdx.toInt()]
                var pt = Point(2, highestY + 4)
                while (true) {
                    moveIdx = (moveIdx+1) % jets.length
                    val cacheItem = getCacheItem(rockIdx, moveIdx, yHeight, world)
                    if (useCache && cacheItem in cache) {
                        println("Hit the cache at i $i, move $moveIdx, rock $rockIdx, high Y $highestY, delta Y = ${highestY - highYPrev}, delta rock = ${i - prevRock}")
                        val deltaY = highestY - highYPrev
                        if (deltaY in deltaYs) {
                            useCache = false
                            val deltaRock = i - prevRock
                            val cyclesLeft = rocksLeft / deltaRock
                            rocksLeft -= cyclesLeft * deltaRock
                            yOffset = cyclesLeft * deltaY
                        } else {
                            deltaYs.add(highestY - highYPrev)
                        }
                        highYPrev = highestY
                        prevRock  = i
                        cache.clear()
                    }
                    cache.add(cacheItem)

                    check(jets[moveIdx.toInt()] == '<' || jets[moveIdx.toInt()] == '>')
                    val push = if (jets[moveIdx.toInt()] == '<') -1 else 1
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
                    highestY = max(highestY, y )
                    yHeight[x.toInt()] = max(y + 1, yHeight[x.toInt()])
                }
//                printWorld(highestY, world)
            }
            println(yHeight.toList())

            return highestY + yOffset + 1 // first row filled in is y = 0
        }

    }

    fun part2(input: String): Long {
        val d = Day17(input)
        return d.findHeight()
    }

    val testInput = readInputAsOneLine("Day17_test")
    check(part1(testInput)==3068)
//    println("${part2(testInput)} should equal 3068")

    val input = readInputAsOneLine("Day17")
//    println(part1(input))
    println(part2(input))
//    println(part2(input, 4_000_000))
//    println(part2(input, 4_000_000))
}