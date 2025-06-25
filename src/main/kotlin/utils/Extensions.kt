package uk.akane.fatal.utils

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

fun String.isNumber(): Boolean {
    return this.toDoubleOrNull() != null
}

fun Double.isNotInteger(): Boolean {
    return this.rem(1.0) != 0.0
}

@OptIn(ExperimentalContracts::class)
inline fun repeat(times: Long, action: (Long) -> Unit) {
    contract { callsInPlace(action) }

    for (index in 0L until times) {
        action(index)
    }
}
