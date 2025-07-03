package uk.akane.fatal.utils

import kotlinx.coroutines.*
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.contact.Group
import uk.akane.fatal.data.database.group.GroupsTableDao
import uk.akane.fatal.data.database.profile.ProfilesTableDao
import uk.akane.fatal.module.roll.evaluate.Expr
import uk.akane.fatal.module.roll.evaluate.Lexeme
import uk.akane.fatal.module.roll.evaluate.Parser
import java.util.concurrent.ThreadLocalRandom

object DiceUtils {
    fun rollDice(numRolls: Int, sides: Int): List<Long> = runBlocking {
        require(numRolls >= 1) { "Roll number must be >= 1" }
        require(sides >= 1) { "Side count must be >= 1" }
        require(numRolls <= ROLL_COUNT_MAX && sides <= SIDE_COUNT_MAX) {
            "Roll number or side count exceeds allowed limits"
        }

        val coreCount = SystemUtils.systemCores.coerceAtMost(numRolls)
        val chunkSize = numRolls / coreCount
        val remainder = numRolls % coreCount

        val returnVal = coroutineScope {
            (0 until coreCount).map { index ->
                async(Dispatchers.Default) {
                    val rolls = if (index == coreCount - 1) chunkSize + remainder else chunkSize
                    LongArray(rolls) {
                        ThreadLocalRandom.current().nextLong(1L, sides.toLong() + 1L)
                    }.asList()
                }
            }.awaitAll().flatten()
        }

        return@runBlocking returnVal
    }


    fun getDefaultDice(context: Contact): Long =
        when (context) {
            is Friend -> ProfilesTableDao.getDiceCount(0, context.id)
            is Group -> GroupsTableDao.getDiceCount(context.id)
            else -> DEFAULT_DICE
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