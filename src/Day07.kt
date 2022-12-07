import java.io.File

// Advent of Code 2022, Day 07, No Space Left On Device

fun main() {

    fun readInputAsOneLine(name: String) = File("src", "$name.txt").readText().trim()

    data class SubFile(val name: String, val size: Int)

    data class Node(val name: String,
                    val parent: Node?,
                    val files: MutableMap<String, SubFile> = mutableMapOf(),
                    val subDirs: MutableMap<String, Node> =  mutableMapOf(),
                    var totalSize: Long = 0
    )

    fun parse(input: String) : Node {
        val root = Node("/", null)
        var curr: Node = root

        val commands = input.split("$")

        for (cmd in commands) {
            val line = cmd.trim()
            if (line.isEmpty()) continue

            if (line == "cd /") {
                curr = root
            } else if (line ==  "cd ..") {
                curr = curr.parent!! // but in macOS it's ok to cd.. from /
            } else if (line.substring(0, 2) == "ls") {
                val contents = line.split("\n").map { it.trim() }
                for (sub in contents.drop(1)) {
                    //println("sub is $sub")
                    val parts = sub.split(" ").map{ it.trim() }
                    if (parts[0].isEmpty()) continue
                    if (parts[0] == "dir" ) {
                        curr.subDirs[parts[1]] = Node(parts[1], curr)
                    } else {
                        curr.files[parts[1]] = SubFile(parts[1], parts[0].toInt())
                    }
                }
            } else {
                check("cd " == line.substring(0..2))
                val dir = line.substring(3)
                check(curr.subDirs.contains(dir))
                curr = curr.subDirs[dir]!!
            }

        }
        return root
    }

    var overall = 0L

    val nodeList = mutableListOf<Node>()

    fun calcTotal(input:Node): Long {
        var total = 0L
        input.subDirs.forEach {
            total += calcTotal(it.value)
        }
        input.files.forEach {
            total += it.value.size
        }
        input.totalSize = total
        if (total <= 100000) {
            overall += total
        }
        nodeList.add(input)
        return total
    }


    fun part1(input:Node): Long {
        overall = 0
        nodeList.clear()
        calcTotal(input)

        return overall
    }

    fun part2(input:Node): Long {
        overall = 0
        nodeList.clear()
        calcTotal(input)

        val freeSpace = 70000000L - input.totalSize
        check(freeSpace < 30000000L)

        val needToFree = 30000000L - freeSpace

        nodeList.sortBy { it.totalSize }
        for (node in nodeList) {
            if (node.totalSize >= needToFree) {
                return node.totalSize
            }
        }

        return 0
    }

    val testInput = parse(readInputAsOneLine("Day07_test"))
    check(part1(testInput) == 95437L)
    check(part2(testInput) == 24933642L)

    val input = readInputAsOneLine("Day07")
    println(part1(parse(input)))
    println(part2(parse(input)))
}