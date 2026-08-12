package com.example.farmastudy.ui.screens.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.farmastudy.ui.QuizViewModel

@Composable
fun QuizResultScreen(
    viewModel: QuizViewModel,
    onRetry: (Int) -> Unit,
    onBackHome: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val total = state.totalQuestions
    val correct = state.correctAnswers
    val percent = if (total > 0) correct * 100 / total else 0

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Resultado", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(24.dp))
            Text(
                text = "$correct de $total aciertos",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "$percent%",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = { onRetry(state.quizPart) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reintentar")
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = onBackHome,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver al inicio")
            }
        }
    }
}