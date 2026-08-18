package com.example.farmastudy.ui.screens.classification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.farmastudy.data.repository.ClassificationCategory
import com.example.farmastudy.ui.components.ScreenHeader

private data class CategoryInfo(
    val category: ClassificationCategory,
    val icon: ImageVector,
    val description: String
)

private val categories = listOf(
    CategoryInfo(
        ClassificationCategory.THERAPEUTIC_USE,
        Icons.Filled.MenuBook,
        "Para qué se usa cada fármaco"
    ),
    CategoryInfo(
        ClassificationCategory.MECHANISM,
        Icons.Filled.Science,
        "Cómo actúa en el cuerpo"
    ),
    CategoryInfo(
        ClassificationCategory.CHEMICAL_STRUCTURE,
        Icons.Filled.Biotech,
        "Composición y estructura química"
    ),
    CategoryInfo(
        ClassificationCategory.ORGANIC_SYSTEM,
        Icons.Filled.MonitorHeart,
        "Sistema orgánico que afecta"
    )
)

@Composable
fun ClassificationScreen(
    onSelectCategory: (String) -> Unit,
    onBack: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            ScreenHeader(
                title = "Por clasificación",
                subtitle = "Elige un tema para repasar",
                onBack = onBack
            )
            Spacer(Modifier.height(8.dp))
            categories.forEach { info ->
                CategoryCard(
                    info = info,
                    onClick = { onSelectCategory(info.category.route) }
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CategoryCard(info: CategoryInfo, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.surface, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = info.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = info.category.label,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = info.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
                )
            }
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}