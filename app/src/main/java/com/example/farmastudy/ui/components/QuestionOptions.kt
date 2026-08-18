package com.example.farmastudy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
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
                !answered -> MaterialTheme.colorScheme.surfaceVariant
                isCorrect -> CorrectGreen
                isSelected -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
            val contentColor = when {
                !answered -> MaterialTheme.colorScheme.onSurface
                isCorrect || isSelected -> Color.White
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
            val letterColor = when {
                !answered -> MaterialTheme.colorScheme.primary
                isCorrect || isSelected -> Color.White.copy(alpha = 0.9f)
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
            Button(
                onClick = { onSelectOption(letter) },
                enabled = !answered,
                colors = ButtonDefaults.buttonColors(
                    containerColor = containerColor,
                    contentColor = contentColor,
                    disabledContainerColor = containerColor,
                    disabledContentColor = contentColor
                ),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(letterColor.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = letter.uppercase(),
                            style = MaterialTheme.typography.labelLarge,
                            color = letterColor
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.weight(1f)
                    )
                    if (answered && isCorrect) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    } else if (answered && isSelected) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}