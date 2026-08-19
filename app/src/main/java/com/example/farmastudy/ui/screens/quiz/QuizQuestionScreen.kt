package com.example.farmastudy.ui.screens.quiz

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import com.example.farmastudy.ui.QuizViewModel
import com.example.farmastudy.ui.components.AnswerFeedbackAnimated
import com.example.farmastudy.ui.components.ConfirmExitDialog
import com.example.farmastudy.ui.components.ProgressHeader
import com.example.farmastudy.ui.components.QuestionOptions
import com.example.farmastudy.ui.components.ScreenHeader

@Composable
fun QuizQuestionScreen(
    quizPart: Int,
    questionIndex: Int,
    viewModel: QuizViewModel,
    onNext: (Int) -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(quizPart) { viewModel.ensureQuiz(quizPart) }
    LaunchedEffect(questionIndex) { viewModel.setCurrentIndex(questionIndex) }

    val question = state.currentQuestion

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
            if (question == null) {
                Spacer(Modifier.height(48.dp))
                CircularProgressIndicator()
                return@Column
            }

            val quizTitle = if (state.quizPart == 0) "Quiz completo" else "Quiz parte ${state.quizPart}"
            ScreenHeader(
                title = quizTitle,
                subtitle = "Aciertos: ${state.correctAnswers}",
                onBack = { showExitDialog = true },
                trailing = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "${state.correctAnswers}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
            Spacer(Modifier.height(16.dp))
            ProgressHeader(
                current = state.currentIndex + 1,
                total = state.totalQuestions,
                label = "Pregunta ${state.currentIndex + 1} de ${state.totalQuestions}"
            )
            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = question.question,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(16.dp))
                    QuestionOptions(
                        question = question,
                        selectedOption = state.selectedOption,
                        onSelectOption = viewModel::selectOption
                    )
                }
            }

            AnswerFeedbackAnimated(state.selectedOption, question.correctOption, question.correctAnswerText)

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { onNext(state.currentIndex + 1) },
                enabled = state.selectedOption != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(
                    imageVector = if (state.isLastQuestion) Icons.Filled.Flag else Icons.Filled.ArrowForward,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(if (state.isLastQuestion) "Ver resultado" else "Siguiente")
            }
        }
    }

    if (showExitDialog) {
        ConfirmExitDialog(
            title = "¿Salir del quiz?",
            message = "Si sales, perderás el progreso de este quiz y no se guardará tu resultado. Tu XP no contará para tu nivel.",
            confirmText = "Salir",
            dismissText = "Seguir estudiando",
            onConfirm = onBack,
            onDismiss = { showExitDialog = false }
        )
    }
}