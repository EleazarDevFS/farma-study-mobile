package com.example.farmastudy.ui.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.farmastudy.data.local.entity.QuizAttemptEntity
import com.example.farmastudy.ui.QuizViewModel
import com.example.farmastudy.ui.XP_PER_CORRECT
import com.example.farmastudy.ui.components.EmptyState
import com.example.farmastudy.ui.components.ScreenHeader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal fun quizPartLabel(quizPart: Int): String = when (quizPart) {
    0 -> "Quiz completo"
    QuizAttemptEntity.RANDOM_QUIZ_PART -> "Estudio random"
    else -> "Parte $quizPart"
}

internal fun formatAttemptDate(date: Long): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(date))

internal fun attemptPercent(attempt: QuizAttemptEntity): Int =
    if (attempt.total > 0) attempt.score * 100 / attempt.total else 0

internal fun percentColor(percent: Int, errorColor: Color): Color = when {
    percent >= 70 -> Color(0xFF2E7D32)
    percent >= 50 -> Color(0xFFF9A825)
    else -> errorColor
}

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
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            ScreenHeader(
                title = "Historial",
                subtitle = "Tus resultados y XP ganada",
                onBack = onBack
            )
            Spacer(Modifier.height(16.dp))
            if (attempts.isEmpty()) {
                EmptyState(
                    icon = Icons.Filled.History,
                    title = "Sin intentos todavía",
                    description = "Completa un quiz o un estudio random para ver tus resultados aquí",
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(attempts, key = { it.id }) { attempt ->
                        AttemptCard(attempt)
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun AttemptCard(attempt: QuizAttemptEntity) {
    val percent = attemptPercent(attempt)
    val isRandom = attempt.quizPart == QuizAttemptEntity.RANDOM_QUIZ_PART
    val color = percentColor(percent, MaterialTheme.colorScheme.error)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isRandom) Icons.Filled.AutoAwesome else Icons.Filled.Quiz,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = quizPartLabel(attempt.quizPart),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "$percent%",
                    style = MaterialTheme.typography.titleMedium,
                    color = color
                )
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { percent / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = color,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${attempt.score} de ${attempt.total} aciertos",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "+${attempt.score * XP_PER_CORRECT} XP",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = formatAttemptDate(attempt.date),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}