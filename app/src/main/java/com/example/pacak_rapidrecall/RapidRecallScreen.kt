package com.example.pacak_rapidrecall

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pacak_rapidrecall.ui.theme.PacakRapidRecallTheme
import java.text.DateFormat
import java.util.Date

/**
 * Shows the title, new game button, summary, and session history.
 *
 * The caller provides [session] and handles [onNewGame], so this screen just
 * displays the information. A lazy list makes the history scrollable, with the
 * newest attempt first. Colors come from the theme and times use the device format.
 *
 * Stable IDs keep cards matched to their records. Results come from the model.
 * Known issues: history is not saved to disk or loaded in pages.
 */
@Composable
fun RapidRecallScreen(
    onNewGame: () -> Unit,
    modifier: Modifier = Modifier,
    session: GameSession
) {
    val attempts = session.roundsPlayed
    val (totalAttempts, correctAttempts, accuracy) = session.gameSummary()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("pacak-RapidRecall", style = MaterialTheme.typography.headlineLarge)
                Text(
                    "Challenge your memory, one digit at a time. Recall sequences of 1–10 digits.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        item {
            Button(onClick = onNewGame, modifier = Modifier.fillMaxWidth()) {
                Text("New game")
            }
        }
        item {
            Text("Session history", style = MaterialTheme.typography.titleLarge)
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SessionStatistic("Attempts", totalAttempts.toString(), Modifier.weight(1f))
                    SessionStatistic("Correct", correctAttempts.toString(), Modifier.weight(1f))
                    SessionStatistic("Accuracy", "$accuracy%", Modifier.weight(1f))
                }
            }
        }
        if (attempts.isEmpty()) {
            item {
                Text(
                    "No attempts yet. Start a new game to test your memory!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            itemsIndexed(attempts.asReversed(), key = { _, attempt -> attempt.id }) { index, attempt ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            "Attempt ${attempts.size - index} · " +
                                if (attempt.overallResult == "T") "Correct" else "Incorrect",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (attempt.overallResult == "T") MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.error
                        )
                        Text("Sequence length: ${attempt.sequence.digits.size}")
                        Text("Target: ${attempt.sequence.digits.joinToString("")}")
                        Text("Your input: ${attempt.response.joinToString("")}")
                        Text(
                            DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                                .format(Date(attempt.timeStamp)),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionStatistic(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(value, style = MaterialTheme.typography.titleLarge)
        Text(label, style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview(showBackground = true)
@Composable
private fun RapidRecallScreenPreview() {
    val session = remember {
        GameSession().apply {
            val sequence = Sequence().apply { setSequence(3) }
            endRound(GameRound(sequence, sequence.digits, 0L))
        }
    }
    PacakRapidRecallTheme {
        RapidRecallScreen(
            onNewGame = {},
            session = session
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RapidRecallEmptyScreenPreview() {
    PacakRapidRecallTheme {
        RapidRecallScreen(onNewGame = {}, session = remember { GameSession() })
    }
}