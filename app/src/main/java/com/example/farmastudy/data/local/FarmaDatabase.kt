package com.example.farmastudy.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.farmastudy.data.local.dao.MedicationDao
import com.example.farmastudy.data.local.dao.QuestionDao
import com.example.farmastudy.data.local.dao.UserDao
import com.example.farmastudy.data.local.entity.MedicationEntity
import com.example.farmastudy.data.local.entity.QuestionEntity
import com.example.farmastudy.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class, MedicationEntity::class, QuestionEntity::class],
    version = 1
)
abstract class FarmaDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun medicationDao(): MedicationDao
    abstract fun questionDao(): QuestionDao

    companion object {
        @Volatile
        private var INSTANCE: FarmaDatabase? = null

        fun getInstance(context: Context): FarmaDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    FarmaDatabase::class.java,
                    "farma_study.db"
                ).build().also { INSTANCE = it }
            }
    }
}