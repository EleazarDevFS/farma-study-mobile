package com.example.farmastudy.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onStudyByClassification: () -> Unit,
    onRandomStudy: () -> Unit,
    onQuiz: () -> Unit,
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
            Text(text = "Menú principal")
            Button(onClick = onStudyByClassification) {
                Text("Estudio por clasificación")
            }
            Button(onClick = onRandomStudy) {
                Text("Estudio random")
            }
            Button(onClick = onQuiz) {
                Text("Quiz general")
            }
            Button(onClick = onLogout) {
                Text("Cerrar sesión")
            }
        }
    }
}