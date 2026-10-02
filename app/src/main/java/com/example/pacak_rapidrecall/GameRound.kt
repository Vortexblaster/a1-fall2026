package com.example.pacak_rapidrecall

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class GameRound @OptIn(ExperimentalTime::class) constructor(
    val sequence: Sequence,
    val response: ArrayList<Int>,
    val overallResult: String,
    val timeStamp: Instant
)