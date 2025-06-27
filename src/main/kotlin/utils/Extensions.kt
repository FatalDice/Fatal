package uk.akane.fatal.utils

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