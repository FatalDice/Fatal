package uk.akane.fatal.utils

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.contact.Group
import uk.akane.fatal.data.database.GroupsTableDao
import uk.akane.fatal.data.database.ProfilesTableDao
import uk.akane.fatal.module.roll.evaluate.Expr
import uk.akane.fatal.module.roll.evaluate.Lexeme
import uk.akane.fatal.module.roll.evaluate.Parser
import java.util.concurrent.ThreadLocalRandom

object DiceUtils {
    fun rollDice(numRolls: Int, sides: Int) : List<Long> = runBlocking {
        if (numRolls < 1 || sides < 1)
            throw RollNumberLessThanOneException("Roll number can only less than one!")
        if (numRolls > ROLL_COUNT_MAX || sides > SIDE_COUNT_MAX)
            throw RollNumberOutOfBoundsException("Roll number cannot be larger than 1,000,000!")

        val results = mutableListOf<Long>()

        val coreNumber = Runtime.getRuntime().availableProcessors()
        val taskPerCoroutine = numRolls / coreNumber
        val extraTask = numRolls % coreNumber

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

        return@runBlocking results
    }

    fun getDefaultDice(context: Contact): Long =
        when (context) {
            is Friend -> ProfilesTableDao.getDiceCount(0, context.id)
            is Group -> GroupsTableDao.getDiceCount(context.id)
            else -> throw IllegalArgumentException("Contact type is illegal")
        }

    fun setDefaultDice(context: Contact, faceCount: Long?) {
        println("Default dice: $faceCount")
        if (faceCount == null) throw RollNumberOutOfBoundsException("faceCount illegal!")
        when (context) {
            is Friend -> ProfilesTableDao.setDefaultDice(context.id, 0, faceCount)
            is Group -> GroupsTableDao.setDefaultDice(context.id, faceCount)
            else -> throw IllegalArgumentException("Contact type is illegal")
        }
    }

    fun evaluateExpression(input: String, context: Contact): String {
        val (evaluation, ast) = evaluateExpressionRaw(input, context)
        return Expr.reassembleExpression(ast, evaluation.second) + " = " + evaluation.first
    }

    fun evaluateExpressionRaw(input: String, context: Contact): Pair<Pair<Long, Bundle>, Expr.Expr> {
        val defaultDice = getDefaultDice(context)
        val tokens = Lexeme.tokenize(input.ifBlank { "d" }.legalizeDiceExpression(1, defaultDice))
        val (ast, _) = Parser.parse(tokens)
        val evaluation = Expr.evaluate(ast)
        return evaluation to ast
    }

    const val ROLL_COUNT_MAX = 1_000_000
    const val SIDE_COUNT_MAX = 1_000_000
    const val SHOW_STEP_COUNT_MAX = 8

    const val DEFAULT_DICE = 20L
}