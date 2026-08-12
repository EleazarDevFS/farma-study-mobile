package com.example.farmastudy.data.repository

import com.example.farmastudy.data.local.dao.MedicationDao
import com.example.farmastudy.data.local.entity.MedicationEntity
import kotlinx.coroutines.flow.Flow

class MedicationRepository(private val medicationDao: MedicationDao) {
    fun getAll(): Flow<List<MedicationEntity>> = medicationDao.getAll()

    fun getByCategory(category: ClassificationCategory): Flow<List<MedicationEntity>> = when (category){
        ClassificationCategory.THERAPEUTIC_USE -> medicationDao.getByTherapeuticUse()
        ClassificationCategory.ORGANIC_SYSTEM -> medicationDao.getByOrganicSystem()
        ClassificationCategory.CHEMICAL_STRUCTURE -> medicationDao.getByChemicalStructure()
        ClassificationCategory.MECHANISM -> medicationDao.getByMechanism()
    }

    fun getByRoute(route: String) = getByCategory(
        requireNotNull(
            ClassificationCategory.entries.find { it.route == route },
            {"Categoría desconocida: '$route'"}
        )
    )
}