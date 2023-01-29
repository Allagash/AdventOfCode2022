import java.io.File

// Advent of Code 2022, Day 18, Boiling Boulders

fun main() {

    fun readInputAsOneLine(name: String) = File("src", "$name.txt").readText().trim()

    fun getNeighbors(cube: List<Int>) : List<List<Int>> {
        return listOf(
            listOf(cube[0], cube[1], cube[2] + 1),
            listOf(cube[0], cube[1] + 1, cube[2]),
            listOf(cube[0] + 1, cube[1], cube[2]),
            listOf(cube[0], cube[1], cube[2] - 1),
            listOf(cube[0], cube[1] - 1, cube[2]),
            listOf(cube[0] - 1, cube[1], cube[2]),
        )
    }

    fun surfaceArea(cubes: Set<List<Int>>): Int {
        val touching = cubes.fold(0) { sum, cube ->
            sum + getNeighbors(cube).count{ cubes.contains(it) }
        }
        return cubes.size * 6 - touching
    }

    fun part1(input: String): Int {
        val cubes = input.split("\n").map { it ->
            it.split(",").map { it.toInt() }
        }.toSet()

        return surfaceArea(cubes)
    }

    fun part2(input: String): Int {
        val cubes = input.split("\n").map { it ->
            it.split(",").map { it.toInt() }
        }.toSet()

        val maxX = cubes.maxOfOrNull { it[0] }!! + 1
        val minX = cubes.minOfOrNull { it[0] }!! - 1
        val maxY = cubes.maxOfOrNull { it[1] }!! + 1
        val minY = cubes.minOfOrNull { it[1] }!! - 1
        val maxZ = cubes.maxOfOrNull { it[2] }!! + 1
        val minZ = cubes.minOfOrNull { it[2] }!! - 1

        var currCube = listOf(minX, minY, minZ)
        val queue = mutableListOf<List<Int>>()
        queue.add(currCube)
        val outside = mutableSetOf<List<Int>>()
        var outsideSurfaces = 0
        while (queue.isNotEmpty()) {
            currCube = queue.removeFirst()
            if (currCube[0] < minX || currCube[0] > maxX ||
                currCube[1] < minY || currCube[1] > maxY ||
                currCube[2] < minZ || currCube[2] > maxZ) continue
            if (currCube in outside) continue
            if (currCube in cubes) continue
            outside.add(currCube)
            outsideSurfaces += getNeighbors(currCube).count{ cubes.contains(it) }
            val neighbors = getNeighbors(currCube)
            queue.addAll(neighbors)
        }
        return outsideSurfaces
    }

    val testInput = readInputAsOneLine("Day18_test")
    check(part1(testInput)==64)
    check(part2(testInput)==58)

    val input = readInputAsOneLine("Day18")
    println(part1(input))
    println(part2(input))
}