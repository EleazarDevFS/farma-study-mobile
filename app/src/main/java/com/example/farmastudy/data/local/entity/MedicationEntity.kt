package com.example.farmastudy.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import javax.crypto.ExemptionMechanism

@Entity(tableName = "medications")
data class MedicationEntity(
    @PrimaryKey val id: Int = 0,
    val name: String,
    @ColumnInfo(name = "therapeutic_use") val therapeuticUse: String?,
    val mechanism: String?,
    @ColumnInfo(name = "chemical_structure") val chemicalStructure: String?,
    @ColumnInfo(name = "organic_system") val  organicSystem: String?
)
