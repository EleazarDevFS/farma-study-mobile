package com.example.farmastudy.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.farmastudy.data.local.entity.QuestionEntity

private val CorrectGreen = Color(0xFF2E7D32)

@Composable
fun QuestionOptions(
    question: QuestionEntity,
    selectedOption: String?,
    onSelectOption: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(
            question.optionA to "a",
            question.optionB to "b",
            question.optionC to "c",
            question.optionD to "d"
        ).forEach { (text, letter) ->
            val answered = selectedOption != null
            val isCorrect = question.correctOption == letter
            val isSelected = selectedOption == letter
            val containerColor = when {
                !answered -> MaterialTheme.colorScheme.primary
                isCorrect -> CorrectGreen
                isSelected -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
            val contentColor = when {
                !answered -> MaterialTheme.colorScheme.onPrimary
                isCorrect || isSelected -> Color.White
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
            Button(
                onClick = { onSelectOption(letter) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = containerColor,
                    contentColor = contentColor
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text)
            }
        }
    }
}