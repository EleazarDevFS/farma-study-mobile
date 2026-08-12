package com.example.farmastudy.ui.screens.study

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.farmastudy.data.local.entity.MedicationEntity
import com.example.farmastudy.data.repository.ClassificationCategory
import com.example.farmastudy.ui.StudyViewModel

@Composable
fun StudyByCategoryScreen(
    category: String,
    viewModel: StudyViewModel,
    onBack: () -> Unit
) {
    val medications by remember(category) {
        if (category.isBlank()) kotlinx.coroutines.flow.flowOf(emptyList())
        else viewModel.medicationsFor(category)
    }.collectAsState(initial = emptyList())
    var expandedId by remember { mutableStateOf<Int?>(null) }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Medicamentos",
                style = MaterialTheme.typography.headlineSmall
            )
            if (medications.isEmpty()) {
                Text(
                    text = "Sin medicamentos para esta categoría",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(medications, key = { it.id }) { medication ->
                        val expanded = expandedId == medication.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expandedId = if (expanded) null else medication.id
                                }
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(
                                    text = medication.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                AnimatedVisibility(visible = expanded) {
                                    Text(
                                        text = detailFor(medication, category) ?: "Sin información",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Volver")
            }
        }
    }
}

private fun detailFor(medication: MedicationEntity, route: String): String? = when (route) {
    ClassificationCategory.THERAPEUTIC_USE.route -> medication.therapeuticUse
    ClassificationCategory.MECHANISM.route -> medication.mechanism
    ClassificationCategory.CHEMICAL_STRUCTURE.route -> medication.chemicalStructure
    ClassificationCategory.ORGANIC_SYSTEM.route -> medication.organicSystem
    else -> null
}