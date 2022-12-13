import java.io.File

// Advent of Code 2022, Day 13, Distress Signal

fun compare(first: Packet, second: Packet) : Int {
    first.forEachIndexed { i, firstEl ->
        if (i > second.lastIndex) {
            return 1 // first is longer
        }
        val secondEl = second[i]
        val diff = if (firstEl.int != null && secondEl.int != null) {
            firstEl.int - secondEl.int
        } else if (firstEl.list != null && secondEl.list != null) {
            compare(firstEl.list, secondEl.list)
        } else {
            val firstPart = firstEl.int?.let { listOf(PacketElement(it, null)) } ?: firstEl.list
            val secondPart = secondEl.int?.let { listOf(PacketElement(it, null)) } ?: secondEl.list
            compare(firstPart!!, secondPart!!)
        }
        if (diff != 0) {
            return diff
        }
    }
    return first.size - second.size
}

data class PacketElement(val int: Int?, val list: List<PacketElement>?) : Comparable<PacketElement> {
    override fun compareTo(other: PacketElement) : Int {
        val diff = if (this.int != null && other.int != null) {
            this.int - other.int
        } else if (this.list != null && other.list != null) {
            compare(this.list, other.list)
        } else {
            val firstPart = this.int?.let { listOf(PacketElement(it, null)) } ?: this.list
            val secondPart = other.int?.let { listOf(PacketElement(it, null)) } ?: other.list
            compare(firstPart!!, secondPart!!)
        }
        return diff
    }
}

typealias Packet = List<PacketElement>

private val aComparator: Comparator<Packet> = Comparator { lhs, rhs -> compare(lhs, rhs) }

fun main() {

    fun readInputAsOneLine(name: String) = File("src", "$name.txt").readText().trim()

    fun getIndexMatchingBracket(input: String) : Int {
        check(input[0]=='[')
        var nested = 0
        input.forEachIndexed { i, c ->
            if (c == '[') {
                nested++
            } else if (c == ']') {
                nested--
            }
            if (nested == 0) {
                return i
            }
        }
        check(false)
        return 0
    }

    fun parsePacket(input: String) : Packet {
        check(input[0]=='[')
        check(input[input.lastIndex]==']')
        val sub = input.substring(1 until input.lastIndex)
        var idx = 0
        val list = mutableListOf<PacketElement>()
        while(idx <= sub.lastIndex) {
            if (sub[idx] == '[') {
                val lastIdx = idx + getIndexMatchingBracket(sub.substring(idx))
                val packet = parsePacket(sub.substring(idx..lastIdx))
                list.add(PacketElement(null, packet))
                idx = lastIdx + 2 // need move past right bracket and comma
            } else {
                var lastIdx = sub.indexOf(',', idx)
                if (lastIdx == -1) {
                    lastIdx = sub.lastIndex + 1
                }
                val number = sub.substring(idx until lastIdx).toInt()
                list.add(PacketElement(number, null))
                idx = lastIdx + 1
            }
        }
        return list.toList()
    }

    fun parse(input: String): List<Pair<Packet, Packet>> =
        input.split("\n\n")
            .map {it.split("\n").map { parsePacket(it) }.let { it[0] to it[1] } }

    fun part1(input: String): Int {
        val parsed = parse(input)
        var result = 0
        parsed.forEachIndexed { i, pair ->
            val compare = compare(pair.first, pair.second)
            check(compare != 0)
            if (compare < 0) {
                result += i + 1 // 1-based index
            }
        }
        return result
    }

    fun part2(input: String) : Int  {
        val lines = input.split("\n").filter { it.isNotEmpty() }.toMutableList()
        lines.add("[[2]]")
        lines.add("[[6]]")
        val packets = lines.map { parsePacket(it) }.toMutableList()
        val two = packets[packets.lastIndex-1]
        val six = packets.last()
        packets.sortWith(aComparator)
        val twoIdx = packets.indexOf(two) + 1 // 1 based index
        val sixIdx = packets.indexOf(six) + 1
        return twoIdx * sixIdx
    }

    val testInput = readInputAsOneLine("Day13_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 140)

    val input = readInputAsOneLine("Day13")
    println(part1(input))
    println(part2(input))
}