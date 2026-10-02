package com.example.pacak_rapidrecall

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.listSaver
import kotlin.math.roundToInt

/**
 * Keeps completed rounds and calculates the summary.
 * A private state list updates Compose without allowing direct history edits.
 * The saver restores history after rotation without adding a database.
 * Known issues: history is not saved to disk. Very large sessions could exceed
 * Android's saved-state limit. Summary values are attempts, correct, accuracy.
 */
class GameSession {
    private val history = mutableStateListOf<GameRound>()
    val roundsPlayed: List<GameRound> get() = history.toList()

    fun showRound(round: Int): GameRound {
        require(round in history.indices) { "Non-existent round input" }
        return history[round]
    }

    fun startRound(sequence: Sequence, response: List<Int>): GameRound =
        GameRound(sequence, response)

    fun endRound(round: GameRound) {
        require(history.none { it.id == round.id }) { "Round already recorded" }
        history.add(round)
    }

    fun gameSummary(): List<Int> {
        val correct = history.count { it.overallResult == "T" }
        val accuracy = if (history.isEmpty()) 0 else
            (correct * 100.0 / history.size).roundToInt()
        return listOf(history.size, correct, accuracy)
    }

    companion object {
        val Saver = listSaver<GameSession, String>(
            save = { session ->
                session.roundsPlayed.map { round ->
                    listOf(round.id, round.timeStamp.toString(),
                        round.sequence.digits.joinToString(""), round.response.joinToString(""))
                        .joinToString("|")
                }
            },
            restore = { records ->
                GameSession().apply {
                    records.forEach { record ->
                        val fields = record.split('|')
                        endRound(GameRound(
                            Sequence(fields[2].map { it.digitToInt() }),
                            fields[3].map { it.digitToInt() }, fields[1].toLong(), fields[0]
                        ))
                    }
                }
            }
        )
    }
}