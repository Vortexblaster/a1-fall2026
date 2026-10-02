package com.example.pacak_rapidrecall

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pacak_rapidrecall.ui.theme.PacakRapidRecallTheme
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

/**
 * Starts the app and shows the RapidRecall UI with the app theme.
 *
 * The activity handles Android setup and edge-to-edge display. The composables
 * handle the session and game screens, keeping that code out of the activity.
 *
 * History is restored after rotation using saved state.
 * Known issues: unfinished games reset when the activity is recreated.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PacakRapidRecallTheme {
                RapidRecallApp()
            }
        }
    }
}

/**
 * Keeps one session and opens the game dialog from the home screen.
 * Both screens use the same session so new attempts appear in the history.
 * Scaffold keeps content clear of the system bars. Saved state restores history.
 * Known issues: this is session storage, not permanent disk storage.
 */
@Composable
fun RapidRecallApp() {
    val session = rememberSaveable(saver = GameSession.Saver) { GameSession() }
    var showGame by remember { mutableStateOf(false) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        RapidRecallScreen(
            onNewGame = { showGame = true },
            session = session,
            modifier = Modifier.padding(innerPadding)
        )
    }
    if (showGame) {
        NewGameDialog(session = session, onClose = { showGame = false })
    }
}

/**
 * Handles choosing a length, showing digits, entering an answer, and feedback.
 * Keeping the game state in the dialog gives each new game a fresh start.
 * LaunchedEffect stops playback when the dialog closes, and GameSession saves
 * submitted answers. Each digit shows for 800 ms with a 200 ms gap.
 * Known issues: timing cannot be changed, closing the dialog discards unfinished
 * attempts, and the current game is not saved after activity recreation.
 */
@Composable
private fun NewGameDialog(session: GameSession, onClose: () -> Unit) {
    var length by remember { mutableStateOf(3f) }
    var sequence by remember { mutableStateOf<Sequence?>(null) }
    var displaying by remember { mutableStateOf(false) }
    var digit by remember { mutableStateOf("") }
    var input by remember { mutableStateOf("") }
    var completedRound by remember { mutableStateOf<GameRound?>(null) }

    LaunchedEffect(sequence) {
        val activeSequence = sequence ?: return@LaunchedEffect
        for (number in activeSequence.digits) {
            digit = number.toString()
            delay(800)
            digit = ""
            delay(200)
        }
        displaying = false
    }

    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("New game") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                val result = completedRound
                when {
                    result != null -> {
                        Text(if (result.overallResult == "T") "Correct!" else "Incorrect")
                        Text("Target: ${result.sequence.digits.joinToString("")}")
                        Text("Your input: ${result.response.joinToString("")}")
                    }
                    sequence == null -> {
                        Text("Sequence length: ${length.roundToInt()}")
                        Slider(
                            value = length,
                            onValueChange = { length = it },
                            valueRange = 1f..10f,
                            steps = 8
                        )
                    }
                    displaying -> {
                        Text("Remember the digits in order")
                        Text(digit.ifEmpty { " " }, style = MaterialTheme.typography.displayLarge)
                    }
                    else -> {
                        OutlinedTextField(
                            value = input,
                            onValueChange = { value ->
                                if (value.length <= 10 && value.all { it in '0'..'9' }) {
                                    input = value
                                }
                            },
                            label = { Text("Your sequence") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }
                }
            }
        },
        confirmButton = {
            when {
                completedRound != null -> TextButton(onClick = onClose) { Text("Done") }
                sequence == null -> Button(onClick = {
                    displaying = true
                    sequence = Sequence().apply { setSequence(length.roundToInt()) }
                }) { Text("Start") }
                !displaying -> Button(
                    enabled = input.isNotEmpty(),
                    onClick = {
                        val activeSequence = sequence ?: return@Button
                        val response = ArrayList(input.map { it.digitToInt() })
                        val round = session.startRound(activeSequence, response)
                        session.endRound(round)
                        completedRound = round
                    }
                ) { Text("Submit") }
            }
        },
        dismissButton = {
            if (completedRound == null) {
                TextButton(onClick = onClose) { Text("Cancel") }
            }
        }
    )
}
