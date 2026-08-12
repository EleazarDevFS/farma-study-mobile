package com.example.farmastudy.ui.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.farmastudy.data.local.entity.QuizAttemptEntity
import com.example.farmastudy.ui.QuizViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal fun quizPartLabel(quizPart: Int): String =
    if (quizPart == 0) "Quiz completo" else "Parte $quizPart"

internal fun formatAttemptDate(date: Long): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(date))

internal fun attemptPercent(attempt: QuizAttemptEntity): Int =
    if (attempt.total > 0) attempt.score * 100 / attempt.total else 0

@Composable
fun QuizHistoryScreen(
    username: String,
    viewModel: QuizViewModel,
    onBack: () -> Unit
) {
    val attempts by remember(username) { viewModel.attemptsFor(username) }
        .collectAsState(initial = emptyList())

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Historial de quizzes",
                style = MaterialTheme.typography.headlineSmall
            )
            if (attempts.isEmpty()) {
                Text(
                    text = "Sin intentos todavía",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(attempts, key = { it.id }) { attempt ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Text(
                                    text = quizPartLabel(attempt.quizPart),
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "${attempt.score} de ${attempt.total} (${attemptPercent(attempt)}%) · ${formatAttemptDate(attempt.date)}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Volver")
            }
        }
    }
}