package solutions.d7

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import utils.FileReader
import kotlin.system.measureTimeMillis

class AdventOfCodeDay7 {
    companion object {
        val lineList = mutableListOf<String>()

        @JvmStatic
        @BeforeAll
        fun setUp() {
            val inputStream = FileReader.readFileLineByLineUsingForEachLine("d7/input.txt")
            inputStream?.bufferedReader()?.forEachLine { lineList.add(it) }
        }
    }

    @Test
    fun question1() {
        val result: Int
        val time = measureTimeMillis {
            result = findSmallerFiles(solution(lineList))
        }

        println("D7 Q1 - Result: $result | Elapsed time: $time ms")
        Assertions.assertEquals(1908462, result)
    }

    @Test
    fun question2() {
        val result: Int
        val time = measureTimeMillis {
            result = solution2()
        }
        println("D7 Q1 - Result: $result | Elapsed time: $time ms")
        Assertions.assertEquals(3979145, result)
    }

    private fun addLumpSum(node: Node) {
        var tempNode = node
        while (tempNode.parent != null) {
            tempNode.parent!!.size += tempNode.size
            tempNode = tempNode.parent!!
        }
    }

    private var smallerFoldersSum = 0
    private fun findSmallerFiles(root: Node): Int {
        if (root.size <= 100000) {
            smallerFoldersSum += root.size
        }

        root.childNodes.forEach { findSmallerFiles(it) }

        return smallerFoldersSum
    }

    private val smallerFoldersFrom = ArrayList<Node>()
    private fun findBiggerFoldersFrom(root: Node, criteria: Int): ArrayList<Node> {
        if (root.size >= criteria) {
            smallerFoldersFrom.add(root)
        }

        root.childNodes.forEach { findBiggerFoldersFrom(it, criteria) }

        return smallerFoldersFrom
    }

    private fun solution(input: List<String>): Node {
        val lsRegex = "^\\$ ls".toRegex()
        val cdRegex = "^\\$ cd".toRegex()

        val rowIterator = input.listIterator()

        val root = Node("/", null)
        var tempNode = root

        while (rowIterator.hasNext()) {
            val row = rowIterator.next()

            if (cdRegex.containsMatchIn(row)) {
                val c = row.split(" ")

                when (val operand = c[2]) {
                    ".." -> {
                        val node = tempNode.parent
                        node!!.size += tempNode.size
                        tempNode = node!!
                    }

                    "/" -> {
                        continue
                    }

                    else -> {
                        val node = tempNode.childNodes.find { it.name == operand }
                        tempNode = node!!
                    }
                }
            }

            if (lsRegex.containsMatchIn(row)) {
                do {
                    if (rowIterator.hasNext()) {
                        val output = rowIterator.next()
                        if (cdRegex.containsMatchIn(output)) {
                            break
                        }
                        val o = output.split(" ")
                        val operator = o[0]
                        val operand = o[1]
                        when (operator) {
                            "dir" -> {
                                tempNode.childNodes.add(Node(operand, tempNode))
                            }

                            else -> {
                                val file = File(operand, Integer.parseInt(operator))
                                tempNode.data.add(file)
                                tempNode.size += file.size
                            }
                        }
                    } else {
                        addLumpSum(tempNode)
                        break
                    }
                } while (true)

                rowIterator.previous()
            }
        }

        return root
    }


    private fun solution2(): Int {
        val root = solution(lineList)

        val usedSize = root.size

        val freeSpace = 70000000 - usedSize

        val nominatedFolders = findBiggerFoldersFrom(root, 30000000 - freeSpace)

        val sortedNominatedFolders = nominatedFolders.sortedWith(compareBy { it.size })

        val folderToDelete = sortedNominatedFolders.first()
        return folderToDelete.size
    }
}
