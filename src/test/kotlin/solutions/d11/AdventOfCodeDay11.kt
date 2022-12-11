package solutions.d11

import org.junit.jupiter.api.*
import utils.FileReader
import kotlin.math.floor
import kotlin.system.measureTimeMillis

class AdventOfCodeDay11 {

    companion object {
        val input = mutableListOf<String>()

        @JvmStatic
        @BeforeAll
        fun setUp() {
            val inputStream = FileReader.readFileLineByLineUsingForEachLine("d11/input.txt")
            inputStream?.bufferedReader()?.forEachLine { input.add(it) }
        }
    }

    private fun prepareOperation(input: String): (Item) -> Item {
        val regex = "[\\+\\-\\*\\/]".toRegex()
        val operand = input.substringAfterLast("=").split(regex)[1].trim()

        when (regex.find(input)!!.value) {
            "*" -> return {
                when (operand) {
                    "old" -> Item(it.worryLevel * it.worryLevel)
                    else -> Item(it.worryLevel * Integer.parseInt(operand))
                }
            }

            "+" -> return {
                when (operand) {
                    "old" -> Item(it.worryLevel + it.worryLevel)
                    else -> Item(it.worryLevel + Integer.parseInt(operand))
                }
            }

            else -> return { Item(0) }
        }
    }

    var monkeys: List<Monkey> = mutableListOf()

    @BeforeEach
    fun setUpEach(){
        monkeys = initMonkeys()
    }

    @AfterEach
    fun tearDown(){
        monkeys = mutableListOf()
    }

    @Test
    fun question1() {
        val result: Int
        val time = measureTimeMillis {
            result = solution(monkeys)
        }

        println("D11 Q1 - Result: $result | Elapsed time: $time ms")
        Assertions.assertEquals(54054, result)
    }

    @Test
    fun question2() {
        val result: Long
        val time = measureTimeMillis {
            result = solution2(monkeys)
        }

        println("D11 Q2 - Result: $result | Elapsed time: $time ms.")
        Assertions.assertEquals(true, result)
    }

    private fun solution(monkeys: List<Monkey>): Int {
        repeat(20) {
            for (monkey in monkeys) {

                val tempItems = monkey.items.toList()
                val itemIterator = tempItems.iterator()

                while (itemIterator.hasNext()) {
                    val item = itemIterator.next()

                    val evaluatedItem = monkey.operation(item)
                    evaluatedItem.worryLevel = floor(evaluatedItem.worryLevel.toDouble() / 3).toInt()
                    val isTestSuccess = monkey.test(evaluatedItem.worryLevel)

                    if (isTestSuccess) {
                        monkey.takeItem(item)
                        monkey.giveItemTo(monkey.monkeyOnSuccessTest, evaluatedItem)
                    } else {
                        monkey.takeItem(item)
                        monkey.giveItemTo(monkey.monkeyOnFailTest, evaluatedItem)
                    }
                }
            }
        }

        return monkeys.sortedByDescending { it.inspectionTime }.take(2).map { it.inspectionTime }
            .reduce { acc, i -> acc * i }
    }

    private fun solution2(monkeys: List<Monkey>): Long {
        repeat(10000) {
            for (monkey in monkeys) {

                val tempItems = monkey.items.toList()
                val itemIterator = tempItems.iterator()

                while (itemIterator.hasNext()) {
                    val item = itemIterator.next()

                    val evaluatedItem = monkey.operation(item)
                    //evaluatedItem.worryLevel = floor(evaluatedItem.worryLevel.toDouble() / 3).toInt()
                    val isTestSuccess = monkey.test(evaluatedItem.worryLevel)

                    if (isTestSuccess) {
                        monkey.takeItem(item)
                        monkey.giveItemTo(monkey.monkeyOnSuccessTest, evaluatedItem)
                    } else {
                        monkey.takeItem(item)
                        monkey.giveItemTo(monkey.monkeyOnFailTest, evaluatedItem)
                    }
                }
            }
        }

        monkeys.forEach { println("Monkey ${it.name} inspected items ${it.inspectionTime} times.") }

        return monkeys.sortedByDescending { it.inspectionTime }.take(2).map { it.inspectionTime }
            .reduce { acc, i -> acc * i }.toLong()
    }

    class Monkey(val name: String, val items: MutableList<Item>, val operation: (Item) -> Item, val testVal: Int) {
        lateinit var monkeyOnSuccessTest: Monkey
        lateinit var monkeyOnFailTest: Monkey

        var inspectionTime = 0
        val test = fun(itemValue: Int): Boolean {
            inspectionTime++
            return itemValue % testVal == 0
        }

        fun giveItemTo(monkey: Monkey, item: Item) {
            monkey.items.add(item)
        }

        fun takeItem(item: Item) {
            items.remove(item)
        }
    }

    class Item(var worryLevel: Int)

    private fun initMonkeys() : List<Monkey>{
        val monkeys : MutableList<Monkey> =  mutableListOf()
        val regexToFindNumber = "\\d+".toRegex()
        for (i in 0..input.size step 7) {
            val monkeyName = input[i].split(" ")[1].replace(":", "")
            val items = input[i + 1].split(":")[1].split(",").map { it.trim() }.map { it.toInt() }.map { Item(it) }
            val operation = prepareOperation(input[i + 2])
            val testValue = Integer.parseInt(regexToFindNumber.find(input[i + 3])!!.value)

            val monkey = Monkey(monkeyName, items.toMutableList(), operation, testValue)
            monkeys.add(monkey)
        }

        for (i in 0..input.size step 7) {
            val monkey = monkeys[i / 7]
            val throwIfTrue = Integer.parseInt(regexToFindNumber.find(input[i + 4])!!.value)
            val throwIfFalse = Integer.parseInt(regexToFindNumber.find(input[i + 5])!!.value)

            monkey.monkeyOnSuccessTest = monkeys[throwIfTrue]
            monkey.monkeyOnFailTest = monkeys[throwIfFalse]
        }

        return monkeys
    }
}