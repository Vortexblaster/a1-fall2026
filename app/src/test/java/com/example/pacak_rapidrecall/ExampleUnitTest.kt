package com.example.pacak_rapidrecall

import org.junit.Test

import org.junit.Assert.*

/**
 * Tests sequence digits, round snapshots, and session statistics.
 *
 * Fixed digits make these tests repeatable without needing an Android device.
 *
 * Known issues: UI interactions and actual activity recreation need device tests.
 */
class ExampleUnitTest {
    @Test
    fun sequenceCopiesInitialDigitsAndPreservesZero() {
        val digits = arrayListOf(0, 4, 4)
        val sequence = Sequence(digits)
        digits.clear()
        assertEquals(listOf(0, 4, 4), sequence.digits)
    }

    @Test
    fun generationUsesValidLengthsAndDigits() {
        val sequence = Sequence(listOf(1))
        for (length in 1..10) {
            sequence.setSequence(length)
            assertEquals(length, sequence.digits.size)
            assertTrue(sequence.digits.all { it in 0..9 })
        }
        assertThrows(IllegalArgumentException::class.java) { sequence.setSequence(0) }
        assertThrows(IllegalArgumentException::class.java) { sequence.setSequence(11) }
    }

    @Test
    fun roundsKeepCopiesAndCalculateResults() {
        val sequence = Sequence(listOf(0, 2))
        val answer = arrayListOf(0, 2)
        val round = GameRound(sequence, answer, 123L)
        sequence.setSequence(10)
        answer.clear()
        round.sequence.setSequence(1)
        assertEquals(listOf(0, 2), round.sequence.digits)
        assertEquals(listOf(0, 2), round.response)
        assertEquals("T", round.overallResult)
        assertEquals(123L, round.timeStamp)
        assertEquals("F", GameRound(Sequence(listOf(1, 2)), listOf(2, 1)).overallResult)
        assertThrows(IllegalArgumentException::class.java) {
            GameRound(Sequence(listOf(1)), listOf(10))
        }
    }

    @Test
    fun summaryHandlesEmptyAndMixedSessions() {
        val session = GameSession()
        assertEquals(listOf(0, 0, 0), session.gameSummary())
        val sequence = Sequence(listOf(1))
        val correct = session.startRound(sequence, listOf(1))
        session.endRound(correct)
        session.endRound(session.startRound(sequence, listOf(2)))
        session.endRound(session.startRound(sequence, listOf(3)))
        assertEquals(listOf(3, 1, 33), session.gameSummary())
        assertSame(correct, session.showRound(0))
        assertThrows(IllegalArgumentException::class.java) { session.showRound(-1) }
        assertThrows(IllegalArgumentException::class.java) { session.showRound(3) }
        assertThrows(IllegalArgumentException::class.java) { session.endRound(correct) }
    }

    @Test
    fun saverRestoresHistoryAndIds() {
        val session = GameSession()
        session.endRound(GameRound(Sequence(listOf(0, 1)), listOf(0, 1), 123L))
        val scope = object : androidx.compose.runtime.saveable.SaverScope {
            override fun canBeSaved(value: Any): Boolean = true
        }
        val saved = with(GameSession.Saver) { scope.save(session) }
        val restored = GameSession.Saver.restore(requireNotNull(saved))!!
        assertEquals(session.gameSummary(), restored.gameSummary())
        assertEquals(session.showRound(0).id, restored.showRound(0).id)
        assertEquals(listOf(0, 1), restored.showRound(0).sequence.digits)
        assertEquals(123L, restored.showRound(0).timeStamp)
    }
}