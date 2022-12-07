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

    fun calcTotal(input:Node): Long {
        input.totalSize = input.subDirs.values.fold(0L) { acc, next -> acc + calcTotal(next) }
        input.totalSize += input.files.values.sumOf { it.size }
        return input.totalSize
    }

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
                for (sub in contents.drop(1)) { // drop initial empty string
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
        calcTotal(root)
        return root
    }


    fun part1(input:Node): Long {
        var overall = 0L
        val nodeQueue = mutableListOf<Node>()
        nodeQueue.add(input)
        while(nodeQueue.isNotEmpty()) {
            val node = nodeQueue.removeFirst()
            if (node.totalSize <= 100_000) {
                overall += node.totalSize
            }
            nodeQueue.addAll(node.subDirs.values)
        }
        return overall
    }

    fun part2(input:Node): Long {
        val freeSpace = 70_000_000L - input.totalSize
        val needToFree = 30_000_000L - freeSpace
        check(freeSpace > 0)

        val nodeQueue = mutableListOf<Node>()
        nodeQueue.add(input)
        var size = Long.MAX_VALUE
        while(nodeQueue.isNotEmpty()) {
            val node = nodeQueue.removeFirst()
            if (node.totalSize in needToFree until size) {
                size = node.totalSize
            }
            nodeQueue.addAll(node.subDirs.values)
        }
        return size
    }

    val testInput = parse(readInputAsOneLine("Day07_test"))
    check(part1(testInput) == 95437L)
    check(part2(testInput) == 24933642L)

    val input = readInputAsOneLine("Day07")
    println(part1(parse(input)))
    println(part2(parse(input)))
}