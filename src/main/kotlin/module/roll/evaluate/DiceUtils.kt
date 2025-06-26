package uk.akane.fatal.module.roll.evaluate

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import uk.akane.fatal.utils.RollNumberLessThanOneException
import uk.akane.fatal.utils.RollNumberOutOfBoundsException
import java.util.concurrent.ThreadLocalRandom

object DiceUtils {
    fun rollDice(numRolls: Int, sides: Int): List<Long> = runBlocking {
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

    const val ROLL_COUNT_MAX = 1_000_000
    const val SIDE_COUNT_MAX = 1_000_000
}