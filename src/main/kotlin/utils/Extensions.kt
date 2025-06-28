package uk.akane.fatal.utils

import uk.akane.fatal.module.roll.evaluate.DiceUtils
import java.util.PriorityQueue

fun List<Long>.take(n: Long) = this.take(n.toInt())

fun List<Long>.keepHighest(count: Long): List<Long> {
    if (count >= this.size) return this

    val minHeap = PriorityQueue<Long>()
    for (x in this) {
        if (minHeap.size < count) {
            minHeap.add(x)
        } else if (x > minHeap.peek()) {
            minHeap.poll()
            minHeap.add(x)
        }
    }
    return minHeap.sortedDescending()
}

fun List<Long>.keepLowest(count: Long): List<Long> {
    if (count >= this.size) return this

    val maxHeap = PriorityQueue<Long>(compareByDescending { it })
    for (x in this) {
        if (maxHeap.size < count) {
            maxHeap.add(x)
        } else if (x < maxHeap.peek()) {
            maxHeap.poll()
            maxHeap.add(x)
        }
    }
    return maxHeap.sorted()
}

fun List<Long>.roundUpToMinimum(minimum: Long): List<Long> =
    map { value -> maxOf(value, minimum) }

fun List<Long>.rerollWithCondition(
    condition: (Long) -> Boolean,
    rerollSides: Int
): Pair<List<Long>, String> {
    val sb = StringBuilder("[")
    val result = mapIndexed { i, v ->
        if (condition(v)) {
            val r = DiceUtils.rollDice(1, rerollSides).first()
            sb.append("${v.toFancyStrikethrough()} → $r")
            r
        } else {
            sb.append(v)
            v
        }.also {
            sb.append(if (i != lastIndex) ", " else "]")
        }
    }
    return result to sb.toString()
}

fun Long.toFancyStrikethrough() =
    toString().let { if (it.length == 1) "$it\u0338" else it.flatMap { listOf(it, '\u0336') }.joinToString("") }
