package com.example.farmastudy.data.repository

import com.example.farmastudy.data.local.dao.MedicationDao
import com.example.farmastudy.data.local.entity.MedicationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MedicationRepository(private val medicationDao: MedicationDao) {
    fun getAll(): Flow<List<MedicationEntity>> = medicationDao.getAll()

    fun getByCategory(category: ClassificationCategory): Flow<List<MedicationEntity>> = when (category){
        ClassificationCategory.THERAPEUTIC_USE -> medicationDao.getByTherapeuticUse()
        ClassificationCategory.ORGANIC_SYSTEM -> medicationDao.getByOrganicSystem()
        ClassificationCategory.CHEMICAL_STRUCTURE -> medicationDao.getByChemicalStructure()
        ClassificationCategory.MECHANISM -> medicationDao.getByMechanism()
    }.map { list -> list.filter { hasDetail(it, category) }
    }

    private fun  hasDetail(medication: MedicationEntity, category: ClassificationCategory): Boolean =
        when(category) {
            ClassificationCategory.THERAPEUTIC_USE -> medication.therapeuticUse.hasContent()
            ClassificationCategory.ORGANIC_SYSTEM -> medication.organicSystem.hasContent()
            ClassificationCategory.CHEMICAL_STRUCTURE -> medication.chemicalStructure.hasContent()
            ClassificationCategory.MECHANISM -> medication.mechanism.hasContent()
        }

    private fun String?.hasContent(): Boolean = !isNullOrBlank() && this != "null"
    fun getByRoute(route: String) = getByCategory(
        requireNotNull(
            ClassificationCategory.entries.find { it.route == route },
            {"Categoría desconocida: '$route'"}
        )
    )
}