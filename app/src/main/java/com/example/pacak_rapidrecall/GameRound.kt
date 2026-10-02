package com.example.pacak_rapidrecall

import java.util.UUID

/**
 * Saves one completed attempt. Copies keep later edits from changing history.
 * The result is calculated here so callers cannot supply an invalid result.
 * A stable ID identifies each card, and time uses standard epoch milliseconds.
 * Known issues: records are not saved to disk.
 */
class GameRound(
    sequence: Sequence,
    response: List<Int>,
    val timeStamp: Long = System.currentTimeMillis(),
    val id: String = UUID.randomUUID().toString()
) {
    private val targetDigits = sequence.digits
    private val responseDigits = response.toList()
    val sequence: Sequence get() = Sequence(targetDigits)
    val response: List<Int> get() = responseDigits.toList()
    val overallResult: String = if (targetDigits == responseDigits) "T" else "F"

    init {
        require(targetDigits.size in 1..10) { "A round needs a target sequence" }
        require(responseDigits.size in 1..10 && responseDigits.all { it in 0..9 }) {
            "An answer must contain 1 to 10 digits"
        }
    }
}