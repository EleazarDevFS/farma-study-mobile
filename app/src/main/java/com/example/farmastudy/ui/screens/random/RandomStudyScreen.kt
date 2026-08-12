package com.example.farmastudy.ui.screens.random

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.farmastudy.data.local.entity.MedicationEntity
import com.example.farmastudy.ui.RandomItem
import com.example.farmastudy.ui.StudyViewModel
import com.example.farmastudy.ui.components.QuestionOptions

@Composable
fun RandomStudyScreen(
    viewModel: StudyViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.randomState.collectAsState()

    LaunchedEffect(Unit) {
        if (state.items.isEmpty()) viewModel.startRandomStudy()
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                state.isLoading -> CircularProgressIndicator()

                state.finished -> {
                    Text("Sesión completada", style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(24.dp))
                    Text(
                        text = "Aciertos: ${state.correctAnswers} de ${state.questionCount} preguntas",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(32.dp))
                    Button(
                        onClick = viewModel::startRandomStudy,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Repetir sesión")
                    }
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                        Text("Volver al inicio")
                    }
                }

                else -> {
                    Text(
                        text = "${state.currentIndex + 1} de ${state.totalItems}",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(Modifier.height(16.dp))

                    when (val item = state.currentItem) {
                        is RandomItem.Flashcard -> FlashcardContent(
                            medication = item.medication,
                            revealed = state.flashcardRevealed
                        )

                        is RandomItem.QuizQuestion -> {
                            Text(
                                text = item.question.question,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(16.dp))
                            QuestionOptions(
                                question = item.question,
                                selectedOption = state.selectedOption,
                                onSelectOption = viewModel::selectOption
                            )
                        }

                        null -> Unit
                    }

                    Spacer(Modifier.height(16.dp))

                    when (val item = state.currentItem) {
                        is RandomItem.Flashcard -> {
                            Button(
                                onClick = viewModel::toggleReveal,
                                enabled = !state.flashcardRevealed,
                                modifier = Modifier.fillMaxWidth()
                            ) {
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
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (state.isLastItem) "Finalizar" else "Siguiente")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FlashcardContent(medication: MedicationEntity, revealed: Boolean) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = medication.name,
                style = MaterialTheme.typography.titleLarge
            )
            if (revealed) {
                Spacer(Modifier.height(12.dp))
                medicationFields(medication).forEach { line ->
                    Text(
                        text = line,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

private fun medicationFields(medication: MedicationEntity): List<String> = listOfNotNull(
    medication.therapeuticUse?.let { "Uso terapéutico: $it" },
    medication.mechanism?.let { "Mecanismo: $it" },
    medication.chemicalStructure?.let { "Estructura química: $it" },
    medication.organicSystem?.let { "Sistema orgánico: $it" }
)