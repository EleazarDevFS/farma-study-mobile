package com.example.farmastudy.ui.screens.home

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    username: String,
    onStudyByClassification: () -> Unit,
    onRandomStudy: () -> Unit,
    onQuiz: () -> Unit,
    onHistory: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Hola, $username",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.height(32.dp))
            Button(onClick = onStudyByClassification, modifier = Modifier.fillMaxWidth()) {
                Text("Estudio por clasificación")
            }
            Spacer(Modifier.height(12.dp))
            Button(onClick = onRandomStudy, modifier = Modifier.fillMaxWidth()) {
                Text("Estudio random")
            }
            Spacer(Modifier.height(12.dp))
            Button(onClick = onQuiz, modifier = Modifier.fillMaxWidth()) {
                Text("Quiz general")
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = onHistory, modifier = Modifier.fillMaxWidth()) {
                Text("Historial de quizzes")
            }
            Spacer(Modifier.height(32.dp))
            OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
                Text("Cerrar sesión")
            }
        }
    }
}