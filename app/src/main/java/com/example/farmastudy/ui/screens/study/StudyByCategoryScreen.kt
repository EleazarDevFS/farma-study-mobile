package com.example.farmastudy.ui.screens.study

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.farmastudy.data.local.entity.MedicationEntity
import com.example.farmastudy.data.repository.ClassificationCategory
import com.example.farmastudy.ui.StudyViewModel
import com.example.farmastudy.ui.components.EmptyState
import com.example.farmastudy.ui.components.ScreenHeader

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

    val categoryLabel = ClassificationCategory.entries
        .firstOrNull { it.route == category }
        ?.label

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            ScreenHeader(
                title = categoryLabel ?: "Medicamentos",
                subtitle = "Toca una tarjeta para ver el detalle",
                onBack = onBack
            )
            Spacer(Modifier.height(16.dp))
            if (medications.isEmpty()) {
                EmptyState(
                    icon = Icons.Filled.Science,
                    title = "Sin medicamentos",
                    description = "Todavía no hay medicamentos en esta categoría",
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(medications, key = { it.id }) { medication ->
                        val expanded = expandedId == medication.id
                        MedicationCard(
                            medication = medication,
                            detail = detailFor(medication, category),
                            expanded = expanded,
                            onClick = { expandedId = if (expanded) null else medication.id }
                        )
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun MedicationCard(
    medication: MedicationEntity,
    detail: String?,
    expanded: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (expanded) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (expanded) 0.dp else 1.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = medication.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (!expanded && detail != null) {
                        Text(
                            text = detail,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (expanded) "Contraer" else "Expandir",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Text(
                    text = detail ?: "Sin información",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(top = 12.dp)
                )
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