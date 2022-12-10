package solutions.d9

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import utils.FileReader
import kotlin.math.abs
import kotlin.system.measureTimeMillis

class AdventOfCodeDay9 {

    companion object {
        val input = mutableListOf<String>()

        @JvmStatic
        @BeforeAll
        fun setUp() {
            val inputStream = FileReader.readFileLineByLineUsingForEachLine("d9/input.txt")
            inputStream?.bufferedReader()?.forEachLine { input.add(it) }
        }
    }

    @Test
    fun question1() {
        val result: Int
        val time = measureTimeMillis {
            result = solution(input, 1)
        }

        println("D9 Q1 - Result: $result | Elapsed time: $time ms")
        Assertions.assertEquals(5858, result)
    }

    @Test
    fun question2() {
        val result: Int
        val time = measureTimeMillis {
            result = solution(input, 9)
        }

        println("D9 Q2 - Result: $result | Elapsed time: $time ms.")
        Assertions.assertEquals(2602, result)
    }

    private fun solution(input: MutableList<String>, numberOfTails: Int): Int {
        val tails = mutableListOf<Tail>()

        val head = Head(0, 0)

        repeat(numberOfTails) {
            tails.add(Tail(0, 0))
        }

        tails[0].setHead(head)

        for (i in 0 until tails.size - 1) {
            tails[i + 1].setHead(tails[i])
        }

        for (row in input) {
            val r = row.split(" ")
            val direction = r[0]
            val times = Integer.parseInt(r[1])

            repeat(times) {
                when (direction) {
                    Direction.LEFT.code -> head.moveLeft()
                    Direction.RIGHT.code -> head.moveRight()
                    Direction.DOWN.code -> head.moveDown()
                    Direction.UP.code -> head.moveUp()
                }

                for (tail in tails) {
                    tail.moveTail()
                }
            }
        }

        return tails[numberOfTails - 1].getVisitedPoints().size
    }

    enum class Direction(val code: String) {
        LEFT("L"), RIGHT("R"), UP("U"), DOWN("D"),
    }

    abstract class MapObject(private var x: Int, private var y: Int) {
        fun getX(): Int = this.x
        fun getY(): Int = this.y

        fun setX(x: Int) {
            this.x = x
        }

        fun setY(y: Int) {
            this.y = y
        }
    }

    class Head(x: Int, y: Int) : MapObject(x, y) {
        fun moveRight() {
            this.setX(this.getX() + 1)
        }

        fun moveLeft() {
            this.setX(this.getX() - 1)
        }

        fun moveUp() {
            this.setY(this.getY() + 1)
        }

        fun moveDown() {
            this.setY(this.getY() - 1)
        }
    }

    class Tail(x: Int, y: Int) : MapObject(x, y) {
        private val visitedPositions: HashMap<Pair<Int, Int>, Boolean> = hashMapOf(Pair(x, y) to false)
        private lateinit var head: MapObject

        fun setHead(head: MapObject) {
            this.head = head
        }

        fun getVisitedPoints(): List<Pair<Int, Int>> =
            visitedPositions.filter { it.value }.map { Pair(it.key.first, it.key.second) }

        private fun setVisited(x: Int, y: Int) {
            this.visitedPositions[Pair(x, y)] = true
        }

        fun moveTail() {
            if (abs(this.getY() - head.getY()) == 2 || abs(this.getX() - head.getX()) == 2) {
                if (this.getY() > head.getY()) {
                    this.setY(this.getY() - 1)
                }

                if (this.getX() > head.getX()) {
                    this.setX(this.getX() - 1)
                }

                if (this.getY() < head.getY()) {
                    this.setY(this.getY() + 1)
                }

                if (this.getX() < head.getX()) {
                    this.setX(this.getX() + 1)
                }
            }
            this.setVisited(this.getX(), this.getY())
        }
    }
}