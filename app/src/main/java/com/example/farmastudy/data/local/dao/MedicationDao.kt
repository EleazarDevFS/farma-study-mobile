package com.example.farmastudy.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.farmastudy.data.local.entity.MedicationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {
    @Query("SELECT * FROM medications WHERE therapeutic_use IS NOT NULL")
    fun getByTherapeuticUse(): Flow<List<MedicationEntity>>

    @Query("SELECT * FROM medications WHERE  mechanism IS NOT NULL")
    fun getByMechanism(): Flow<List<MedicationEntity>>

    @Query("SELECT * FROM medications WHERE chemical_structure IS NOT NULL")
    fun getByChemicalStructure(): Flow<List<MedicationEntity>>

    @Query("SELECT * FROM medications WHERE organic_system IS NOT NULL")
    fun getByOrganicSystem(): Flow<List<MedicationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(medications: List<MedicationEntity>)
}