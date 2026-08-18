package com.example.farmastudy.ui.screens.random

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.farmastudy.data.local.entity.MedicationEntity
import com.example.farmastudy.data.local.entity.QuestionEntity
import com.example.farmastudy.ui.RandomItem
import com.example.farmastudy.ui.RandomStudyUiState
import com.example.farmastudy.ui.StudyViewModel
import com.example.farmastudy.ui.components.AnswerFeedback
import com.example.farmastudy.ui.components.ConfirmExitDialog
import com.example.farmastudy.ui.components.ProgressHeader
import com.example.farmastudy.ui.components.ScreenHeader
import com.example.farmastudy.ui.components.QuestionOptions
import com.example.farmastudy.ui.randomStudyMessage
import com.example.farmastudy.ui.XP_PER_CORRECT

@Composable
fun RandomStudyScreen(
    username: String,
    viewModel: StudyViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.randomState.collectAsState()
    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (state.items.isEmpty()) viewModel.startRandomStudy()
    }
    LaunchedEffect(state.finished) {
        if (state.finished) viewModel.recordAttempt(username)
    }

    BackHandler { showExitDialog = true }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))
            when {
                state.isLoading || state.items.isEmpty() -> {
                    Spacer(Modifier.height(48.dp))
                    CircularProgressIndicator()
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Preparando tu sesión...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                state.finished -> FinishedContent(
                    correct = state.correctAnswers,
                    total = state.questionCount,
                    onRetry = viewModel::startRandomStudy,
                    onBack = onBack
                )

                else -> StudyContent(
                    state = state,
                    viewModel = viewModel,
                    onBack = { showExitDialog = true }
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }

    if (showExitDialog) {
        ConfirmExitDialog(
            title = "¿Salir del estudio?",
            message = "Si sales, perderás el progreso de esta sesión y no se guardará tu resultado ni tus XP.",
            confirmText = "Salir",
            dismissText = "Seguir estudiando",
            onConfirm = onBack,
            onDismiss = { showExitDialog = false }
        )
    }
}

@Composable
private fun StudyContent(
    state: RandomStudyUiState,
    viewModel: StudyViewModel,
    onBack: () -> Unit
) {
    ScreenHeader(
        title = "Estudio random",
        subtitle = "Tarjetas y preguntas mezcladas",
        onBack = onBack
    )
    Spacer(Modifier.height(16.dp))
    ProgressHeader(
        current = state.currentIndex + 1,
        total = state.totalItems,
        label = "Tarjeta ${state.currentIndex + 1} de ${state.totalItems}"
    )
    Spacer(Modifier.height(16.dp))

    when (val item = state.currentItem) {
        is RandomItem.Flashcard -> FlashcardContent(
            medication = item.medication,
            revealed = state.flashcardRevealed
        )

        is RandomItem.QuizQuestion -> {
            QuestionCard(
                question = item.question,
                selectedOption = state.selectedOption,
                onSelectOption = viewModel::selectOption
            )
            AnimatedVisibility(
                visible = state.selectedOption != null,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Spacer(Modifier.height(12.dp))
                AnswerFeedback(
                    isCorrect = state.selectedOption == item.question.correctOption,
                    correctAnswer = item.question.correctAnswerText
                )
            }
        }

        null -> Unit
    }

    Spacer(Modifier.height(16.dp))

    when (val item = state.currentItem) {
        is RandomItem.Flashcard -> {
            Button(
                onClick = viewModel::toggleReveal,
                enabled = !state.flashcardRevealed,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Filled.Visibility, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Mostrar información")
            }
        }

        is RandomItem.QuizQuestion -> Unit
        null -> Unit
    }

    if (state.flashcardRevealed || state.selectedOption != null) {
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = viewModel::next,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Icon(
                imageVector = if (state.isLastItem) Icons.Filled.Flag else Icons.Filled.ArrowForward,
                contentDescription = null
            )
            Spacer(Modifier.width(8.dp))
            Text(if (state.isLastItem) "Finalizar" else "Siguiente")
        }
    }
}

@Composable
private fun QuestionCard(
    question: QuestionEntity,
    selectedOption: String?,
    onSelectOption: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = question.question,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(12.dp))
            QuestionOptions(
                question = question,
                selectedOption = selectedOption,
                onSelectOption = onSelectOption
            )
        }
    }
}

@Composable
private fun FlashcardContent(medication: MedicationEntity, revealed: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (revealed) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = medication.name,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            AnimatedVisibility(
                visible = revealed,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Spacer(Modifier.height(16.dp))
                    medicationFields(medication).forEach { (label, value) ->
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = value,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FinishedContent(
    correct: Int,
    total: Int,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    val percent = if (total > 0) correct * 100 / total else 0
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Icon(
            imageVector = Icons.Filled.EmojiEvents,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(56.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text("Sesión completada", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text(
            text = randomStudyMessage(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Aciertos: $correct de $total",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "$percent% · +${correct * XP_PER_CORRECT} XP",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Icon(Icons.Filled.Refresh, contentDescription = null)
            Spacer(Modifier.size(8.dp))
            Text("Repetir sesión")
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Icon(Icons.Filled.Home, contentDescription = null)
            Spacer(Modifier.size(8.dp))
            Text("Volver al inicio")
        }
    }
}

private fun medicationFields(medication: MedicationEntity): List<Pair<String, String>> = listOfNotNull(
    medication.therapeuticUse?.let { "Uso terapéutico" to it },
    medication.mechanism?.let { "Mecanismo" to it },
    medication.chemicalStructure?.let { "Estructura química" to it },
    medication.organicSystem?.let { "Sistema orgánico" to it }
)