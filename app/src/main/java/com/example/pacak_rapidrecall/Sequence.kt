package com.example.pacak_rapidrecall

import androidx.compose.runtime.mutableStateListOf
import kotlin.random.Random

class Sequence {
    private val _sequence = mutableStateListOf(0)
    private var _n = 0
    private var _sequencePosition = 0

    fun setSequence(n: Int) {
        _n = n
        val firstDigit = Random.nextInt(1,10).toString()
        val remainingDigits = buildString {
            repeat(n -1) {
                append(Random.nextInt(0,10))
            }
        }
        val s = (firstDigit + remainingDigits).toList()
        for (i in 0..n) {
            _sequence[i] = s[i].digitToInt()
        }
    }

    fun nextNumber(): Int {
        if ( _sequencePosition != _n - 1) {
            _sequencePosition += 1
            return _sequence[_sequencePosition]
        }
        else {
            return 10
        }
    }
}