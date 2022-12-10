package solutions.d8

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import utils.FileReader
import kotlin.system.measureTimeMillis

class AdventOfCodeDay8 {

    companion object {
        val input = mutableListOf<String>()

        @JvmStatic
        @BeforeAll
        fun setUp() {
            val inputStream = FileReader.readFileLineByLineUsingForEachLine("d8/input.txt")
            inputStream?.bufferedReader()?.forEachLine { input.add(it) }
        }
    }

    @Test
    fun question1() {
        val result: Int

        val time = measureTimeMillis {
            result = solution(input)
        }

        println("D8 Q1 - Result: $result | Elapsed time: $time ms")

        assertEquals(1796, result)
    }

    @Test
    fun question2() {
        val result: Int
        val time = measureTimeMillis {
            result = solution2()
        }

        println("D8 Q2 - Result: $result | Elapsed time: $time ms")
        assertEquals(288120, result)
    }

    private fun solution2(): Int {
        val scores = mutableListOf<Int>()
        val forest = mutableListOf<Tree>()

        var colIndex = 0
        for ((rowIndex, line) in input.withIndex()) {
            forest.addAll(line.map { Tree(Character.getNumericValue(it), rowIndex, colIndex++ % line.length) })
        }

        for (tree in forest) {
            scores.add(tree.treeScore(forest))
        }

        return scores.maxOf { it }
    }

    private fun solution(input: MutableList<String>): Int {
        var result = 0
        val forest = mutableListOf<Tree>()

        var colIndex = 0
        for ((rowIndex, line) in input.withIndex()) {
            forest.addAll(line.map { Tree(Character.getNumericValue(it), rowIndex, colIndex++ % line.length) })
        }

        for (tree in forest) {
            if (tree.isVisible(forest)) result++
        }

        return result
    }

    class Tree(
        private val height: Int, private val rowIndex: Int, private val colIndex: Int
    ) {
        private val numberOfVisibleTrees =
            mutableMapOf(Direction.WEST to 0, Direction.EAST to 0, Direction.SOUTH to 0, Direction.NORTH to 0)

        private fun isVisibleFromLeft(forest: List<Tree>): Boolean {
            for (tree in forest.slice(0 until forest.indexOf(this))) {
                if (tree.height >= this.height) return false
            }

            return true
        }

        private fun isVisibleFromRight(forest: List<Tree>): Boolean {
            for (tree in forest.slice(forest.indexOf(this) + 1 until forest.size)) {
                if (tree.height >= this.height) return false
            }

            return true
        }

        private fun isVisibleFromUp(forest: List<Tree>): Boolean {
            for (tree in forest.slice(0 until forest.indexOf(this))) {
                if (tree.height >= this.height) return false
            }

            return true
        }

        private fun isVisibleFromDown(forest: List<Tree>): Boolean {
            for (tree in forest.slice(forest.indexOf(this) + 1 until forest.size)) {
                if (tree.height >= this.height) return false
            }

            return true
        }

        private fun isVisibleHorizontal(forest: List<Tree>): Boolean {
            val treesInTheSameRow = forest.filter { it.rowIndex == this.rowIndex }
            return isVisibleFromLeft(treesInTheSameRow) || isVisibleFromRight(treesInTheSameRow)
        }

        private fun isVisibleVertical(forest: List<Tree>): Boolean {
            val treesInTheSameColumn = forest.filter { it.colIndex == this.colIndex }
            return isVisibleFromUp(treesInTheSameColumn) || isVisibleFromDown(treesInTheSameColumn)
        }

        val isVisible = fun(forest: List<Tree>): Boolean {
            return (isVisibleHorizontal(forest) || isVisibleVertical(forest))
        }

        private fun numberOfVisibleTrees(forest: List<Tree>): Map<Direction, Int> {
            val treesInTheSameRow = forest.filter { it.rowIndex == this.rowIndex }
            val treesInTheSameColumn = forest.filter { it.colIndex == this.colIndex }

            for (tree in treesInTheSameRow.slice(treesInTheSameRow.indexOf(this) + 1 until treesInTheSameRow.size)) {
                this.numberOfVisibleTrees.merge(Direction.EAST, 1, Int::plus)
                if (tree.height >= this.height) break
            }

            for (tree in treesInTheSameRow.slice(0 until treesInTheSameRow.indexOf(this)).reversed()) {
                this.numberOfVisibleTrees.merge(Direction.WEST, 1, Int::plus)
                if (tree.height >= this.height) break
            }

            for (tree in treesInTheSameColumn.slice(treesInTheSameColumn.indexOf(this) + 1 until treesInTheSameColumn.size)) {
                this.numberOfVisibleTrees.merge(Direction.SOUTH, 1, Int::plus)
                if (tree.height >= this.height) break
            }

            for (tree in treesInTheSameColumn.slice(0 until treesInTheSameColumn.indexOf(this)).reversed()) {
                this.numberOfVisibleTrees.merge(Direction.NORTH, 1, Int::plus)
                if (tree.height >= this.height) break
            }

            return numberOfVisibleTrees
        }

        val treeScore = fun(forest: List<Tree>): Int {
            var score = 1
            this.numberOfVisibleTrees(forest).forEach { (_, value) -> score *= value }
            return score
        }
    }

    enum class Direction {
        NORTH, SOUTH, EAST, WEST
    }
}