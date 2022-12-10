package solutions.d10

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import utils.FileReader
import kotlin.system.measureTimeMillis

class AdventOfCodeDay10 {

    companion object {
        val input = mutableListOf<String>()

        @JvmStatic
        @BeforeAll
        fun setUp() {
            val inputStream = FileReader.readFileLineByLineUsingForEachLine("d10/input.txt")
            inputStream?.bufferedReader()?.forEachLine { input.add(it) }
        }
    }

    @Test
    fun question1() {
        val result: Int
        val time = measureTimeMillis {
            result = solution()
        }

        println("D10 Q1 - Result: $result | Elapsed time: $time ms")
        Assertions.assertEquals(14720, result)
    }

    @Test
    fun question2() {
        val result: Boolean
        val time = measureTimeMillis {
            result = solution2()
        }

        println("D10 Q2 - Result: $result | Elapsed time: $time ms.")
        Assertions.assertEquals(true, result)
    }


    private var instructionCycle = 0
    private var accumulator = 1
    private fun solution(): Int {
        val commandRegex = "\\w+".toRegex()
        val dataRegex = "-?\\d+".toRegex()

        var command = ""
        var data = 0

        var cpuClock = 0
        var canFetchNewInstruction = true
        val inputIterator = input.iterator()
        val takeSampleAtClocks = listOf(20,60,100,140,180,220)

        var output = 0
        while (inputIterator.hasNext()){
            cpuClock++

            if(takeSampleAtClocks.any{ it == cpuClock}){
                output += cpuClock * accumulator
            }

            if(canFetchNewInstruction){
                val instruction = inputIterator.next()

                val r = instruction.split(" ")
                command = commandRegex.find(r[0])!!.value
                if(r.size > 1)
                data = Integer.parseInt(dataRegex.find(r[1])!!.value)
                instructionCycle = 0
            }

            when (command) {
                "noop" -> {
                    canFetchNewInstruction = true
                }
                "addx" -> {
                    addx(data)
                    canFetchNewInstruction = instructionCycle == 2
                }
            }
        }
        return output
    }

    private fun addx(data:Int){
        instructionCycle++
        if(instructionCycle == 2){
            accumulator += data
        }
    }

    private fun solution2() :Boolean{
        val commandRegex = "\\w+".toRegex()
        val dataRegex = "-?\\d+".toRegex()

        var command = ""
        var data = 0

        var cpuClock = 0
        var canFetchNewInstruction = true
        val inputIterator = input.iterator()
        val takeSampleAtClocks = listOf(20,60,100,140,180,220)

        var output = 0
        val screenLength = 40
        val CRT  = mutableListOf<String>()
        CRT.add("#")

        var spriteLocation:Pair<Int,Int>
        var CRTrow = 0

        while (inputIterator.hasNext()){
            cpuClock++

            if(takeSampleAtClocks.any{ it == cpuClock}){
                output += cpuClock * accumulator
            }

            if(canFetchNewInstruction){
                val instruction = inputIterator.next()

                val r = instruction.split(" ")
                command = commandRegex.find(r[0])!!.value
                if(r.size > 1)
                    data = Integer.parseInt(dataRegex.find(r[1])!!.value)
                instructionCycle = 0
            }

            when (command) {
                "noop" -> {
                    canFetchNewInstruction = true
                }
                "addx" -> {
                    addx(data)
                    canFetchNewInstruction = instructionCycle == 2
                }
            }

            spriteLocation = Pair(accumulator-1, accumulator +1)

            if(cpuClock % screenLength == 0){
                CRT.add("")
                CRTrow++
            }

            if(spriteLocation.first <= cpuClock %screenLength && spriteLocation.second >= cpuClock % screenLength){
                CRT[CRTrow] = CRT[CRTrow].plus("#")
            }
            else{
                CRT[CRTrow] = CRT[CRTrow].plus(".")
            }
        }
        CRT.forEach { println(it) }
        return true
    }
}