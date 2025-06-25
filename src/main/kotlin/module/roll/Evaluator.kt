package uk.akane.fatal.module.roll

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import uk.akane.fatal.utils.isNotInteger
import uk.akane.fatal.utils.isNumber
import uk.akane.fatal.utils.repeat
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

class Evaluator {

    private var lastRollList: MutableList<RollResult> = mutableListOf()

    fun evaluate(expression: String): Double {
        lastRollList.clear()
        val tokens = tokenize(expression)
        val postfix = toPostfix(tokens)
        return evaluatePostfix(postfix)
    }

    fun evaluateFormatted(expression: String): String {
        lastRollList.clear()
        val result = evaluate(expression)

        val regex = Regex("([0-9]+(?:\\.[0-9]+)?)|([+\\-*/^()%d])|([()])")
        val tokens = regex.findAll(expression).map { it.value }.toList()
        val formattedExpression = buildString {
            for (token in tokens) {
                when (token) {
                    "(", ")" -> append(token)
                    in listOf("+", "-", "*", "/", "^", "%", "d") -> append(" $token ")
                    else -> append(token)
                }
            }
        }

        val length = formattedExpression.length
        val diceRanges = mutableListOf<Pair<Int, Int>>()
        var i = 0
        while (i < length) {
            if (formattedExpression[i] == 'd') {
                var numEnd = i - 1
                while (numEnd >= 0 && formattedExpression[numEnd].isWhitespace()) numEnd--

                var numStart: Int
                if (numEnd >= 0 && formattedExpression[numEnd] == ')') {
                    var parenCount = 1
                    var pos = numEnd - 1
                    while (pos >= 0 && parenCount > 0) {
                        when (formattedExpression[pos]) {
                            ')' -> parenCount++
                            '(' -> parenCount--
                        }
                        pos--
                    }
                    // If there are extra parentheses, we skip the opening parenthesis as well
                    numStart = pos + 1
                } else {
                    var pos = numEnd
                    while (pos >= 0 && formattedExpression[pos].isDigit()) pos--
                    numStart = pos + 1
                }

                var sidesStart = i + 1
                while (sidesStart < length && formattedExpression[sidesStart].isWhitespace()) sidesStart++

                var fullExprEnd: Int
                if (sidesStart < length && formattedExpression[sidesStart] == '(') {
                    var parenCount = 1
                    var pos = sidesStart + 1
                    while (pos < length && parenCount > 0) {
                        when (formattedExpression[pos]) {
                            '(' -> parenCount++
                            ')' -> parenCount--
                        }
                        pos++
                    }
                    fullExprEnd = pos
                } else {
                    var pos = sidesStart
                    while (pos < length && (formattedExpression[pos].isDigit() || formattedExpression[pos] == '.')) pos++
                    fullExprEnd = pos
                }

                // Check for matching parentheses and handle the removal of unnecessary ones
                if (formattedExpression[max(numStart - 1, 0)] == '(' &&
                    formattedExpression[min(fullExprEnd, formattedExpression.count() - 1)] == ')') {
                    numStart -= 1 // Remove the opening parenthesis from N expression
                    fullExprEnd += 1 // Remove the closing parenthesis from X expression
                }

                diceRanges.add(numStart to fullExprEnd)
                i = fullExprEnd
            } else {
                i++
            }
        }

        val resultBuilder = StringBuilder(formattedExpression.length + 64)
        var lastIndex = 0
        var rollIndex = 0

        for ((start, end) in diceRanges) {
            if (lastIndex < start) {
                resultBuilder.append(formattedExpression, lastIndex, start)
            }
            val replacement = if (rollIndex < lastRollList.size) lastRollList[rollIndex++].toString() else formattedExpression.substring(start, end)
            resultBuilder.append(replacement)
            lastIndex = end
        }
        if (lastIndex < length) {
            resultBuilder.append(formattedExpression, lastIndex, length)
        }

        return "$resultBuilder = $result"
    }

    private fun tokenize(expression: String): List<String> {
        val regex = Regex("([0-9]+(?:\\.[0-9]+)?)|([+\\-*/^()%d])")
        return regex.findAll(expression).map { it.value }.toList()
    }

    private fun toPostfix(tokens: List<String>): List<String> {
        val output = mutableListOf<String>()
        val operators = mutableListOf<String>()

        for (token in tokens) {
            when {
                token.isNumber() -> output.add(token)
                token == "(" -> operators.add(token)
                token == ")" -> {
                    while (operators.isNotEmpty() && operators.last() != "(") {
                        output.add(operators.removeAt(operators.size - 1))
                    }
                    operators.removeAt(operators.size - 1)
                }
                else -> {
                    while (operators.isNotEmpty() && precedence(token) <= precedence(operators.last())) {
                        output.add(operators.removeAt(operators.size - 1))
                    }
                    operators.add(token)
                }
            }
        }

        while (operators.isNotEmpty()) {
            output.add(operators.removeAt(operators.size - 1))
        }

        return output
    }

    private fun evaluatePostfix(postfix: List<String>): Double {
        val stack = mutableListOf<Double>()

        for (token in postfix) {
            when {
                token.isNumber() -> stack.add(token.toDouble())
                else -> {
                    val right = stack.removeAt(stack.size - 1)
                    val left = stack.removeAt(stack.size - 1)
                    val result = when (token) {
                        "+" -> left + right
                        "-" -> left - right
                        "*" -> left * right
                        "/" -> left / right
                        "%" -> left % right
                        "^" -> left.pow(right)
                        "√" -> sqrt(right)
                        "d" -> rollDice(left, right).sum().toDouble()
                        else -> throw IllegalArgumentException("Unsupported operator: $token")
                    }
                    stack.add(result)
                }
            }
        }

        return stack.last()
    }

    private fun precedence(operator: String): Int {
        return when (operator) {
            "+", "-" -> 1
            "*", "/" -> 2
            "%", "^" -> 3
            "√" -> 4
            "d" -> 5
            else -> 0
        }
    }

    private fun rollDice(numRolls: Double, sides: Double): List<Long> = runBlocking {
        if (numRolls.isNotInteger() || sides.isNotInteger()) throw RollNumberNotIntegerException()
        if (numRolls < 1.0 || sides < 1.0) throw RollNumberLessThanOneException()
        if (numRolls > ROLL_COUNT_MAX || sides > SIDE_COUNT_MAX) throw RollNumberOutOfBoundsException()

        val results = mutableListOf<Long>()

        val coreNumber = Runtime.getRuntime().availableProcessors()
        val taskPerCoroutine = numRolls.toLong() / coreNumber
        val extraTask = numRolls.toLong() % coreNumber

        val jobs = mutableListOf<Deferred<List<Long>>>()

        coroutineScope {
            repeat(coreNumber) { index ->
                jobs.add(async(Dispatchers.Default) {
                    val localResults = mutableListOf<Long>()

                    repeat(if (index == coreNumber - 1) taskPerCoroutine + extraTask else taskPerCoroutine) {
                        localResults.add(ThreadLocalRandom.current().nextLong(1L, sides.toLong() + 1L))
                    }
                    localResults
                })
            }
            val jobResults = jobs.awaitAll()
            jobResults.forEach { results.addAll(it) }
        }

        lastRollList.add(RollResult(results, numRolls > SHOW_STEP_ROLL_COUNT_MAX))

        return@runBlocking results
    }

    inner class RollResult(
        val results: List<Long>,
        private val hasDisabledStepCount: Boolean
    ) {
        override fun toString(): String {
            return if (hasDisabledStepCount) "[${results.sum()}]" else results.toString()
        }
    }

    open inner class RollException(message: String) : Exception(message)

    inner class RollNumberNotIntegerException(message: String = "Roll number can only be integer!") :
        RollException(message)

    inner class RollNumberLessThanOneException(message: String = "Roll number can only less than one!") :
        RollException(message)

    inner class RollNumberOutOfBoundsException(message: String = "Roll number cannot be larger than 1,000,000!") :
        RollException(message)

    companion object {
        const val ROLL_COUNT_MAX = 1000000
        const val SIDE_COUNT_MAX = 1000000
        const val SHOW_STEP_ROLL_COUNT_MAX = 50
    }

}