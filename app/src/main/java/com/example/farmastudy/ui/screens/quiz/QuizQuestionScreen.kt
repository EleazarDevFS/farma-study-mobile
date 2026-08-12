package com.example.farmastudy.ui.screens.quiz

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.farmastudy.ui.QuizViewModel
import com.example.farmastudy.ui.components.QuestionOptions

@Composable
fun QuizQuestionScreen(
    quizPart: Int,
    questionIndex: Int,
    viewModel: QuizViewModel,
    onNext: (Int) -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(quizPart) { viewModel.ensureQuiz(quizPart) }
    LaunchedEffect(questionIndex) { viewModel.setCurrentIndex(questionIndex) }

    val question = state.currentQuestion

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (question == null) {
                CircularProgressIndicator()
                return@Column
            }

            Text(
                text = "Pregunta ${state.currentIndex + 1} de ${state.totalQuestions}",
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(Modifier.height(16.dp))
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
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { onNext(state.currentIndex + 1) },
                enabled = state.selectedOption != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.isLastQuestion) "Ver resultado" else "Siguiente")
            }
            TextButton(onClick = onBack) {
                Text("Salir del quiz")
            }
        }
    }
}