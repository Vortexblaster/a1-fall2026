package com.example.pacak_rapidrecall

import kotlin.random.Random

/**
 * Makes a random sequence and returns its digits in order.
 * All positions can be zero. Copying the initial digits protects saved rounds.
 * null marks the end of playback instead of a special number.
 * Known issues: the playback position is not saved after activity recreation.
 */
class Sequence(initialDigits: List<Int> = emptyList()) {
    private var values = initialDigits.toList()
    private var position = 0
    val digits: List<Int> get() = values.toList()

    init {
        require(values.size <= 10 && values.all { it in 0..9 }) { "Invalid sequence digits" }
    }

    fun setSequence(n: Int) {
        require(n in 1..10) { "Sequence length must be between 1 and 10" }
        values = List(n) { Random.nextInt(0, 10) }
        position = 0
    }

    fun nextNumber(): Int? = if (position < values.size) values[position++] else null
}