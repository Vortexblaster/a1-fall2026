package com.example.pacak_rapidrecall

import androidx.compose.runtime.mutableStateListOf
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class GameSession {
    val _roundsPlayed = mutableStateListOf<GameRound>()

    fun showRound(round: Int): GameRound {
        if (round < _roundsPlayed.size) {
            return _roundsPlayed[round]
        } else {
            throw IllegalArgumentException("Non-existent round input")
        }
    }

    @OptIn(ExperimentalTime::class)
    fun startRound(sequence: Sequence, response: ArrayList<Int>): GameRound {
        val round = GameRound(sequence, response, "", Clock.System.now())
        return round
    }

    fun endRound(round: GameRound) {
        _roundsPlayed.add(round)
    }

    fun gameSummary(): List<Int> {
        val totalAttempts = _roundsPlayed.size
        val correctAttempts = (_roundsPlayed.count { it.overallResult == "T" })
        val overallAccuracy = (correctAttempts/totalAttempts)
        val summary = listOf(totalAttempts, correctAttempts, overallAccuracy)
        return summary
    }
}