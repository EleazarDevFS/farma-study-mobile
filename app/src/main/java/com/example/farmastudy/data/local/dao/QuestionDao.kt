package com.example.farmastudy.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.farmastudy.data.local.entity.QuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions ORDER BY id")
    fun getAll(): Flow<List<QuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<QuestionEntity>)

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun count(): Int
}