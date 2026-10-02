package com.example.pacak_rapidrecall

import kotlin.random.Random

/**
 * Makes a random sequence and provides a copy of its digits.
 * All positions can be zero. Copying the initial digits protects saved rounds.
 * The UI handles displaying the digits, so this class only stores the target.
 * Known issues: an empty sequence is allowed until a target is generated.
 */
class Sequence(initialDigits: List<Int> = emptyList()) {
    private var values = initialDigits.toList()
    val digits: List<Int> get() = values.toList()

    init {
        require(values.size <= 10 && values.all { it in 0..9 }) { "Invalid sequence digits" }
    }

    fun setSequence(n: Int) {
        require(n in 1..10) { "Sequence length must be between 1 and 10" }
        values = List(n) { Random.nextInt(0, 10) }
    }

}