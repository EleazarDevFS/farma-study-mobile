package com.example.farmastudy.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.farmastudy.data.local.entity.QuizAttemptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizAttemptDao {
    @Insert
    suspend fun insert(attempt: QuizAttemptEntity): Long

    @Query("SELECT * FROM quiz_attempts WHERE username = :username ORDER BY date DESC")
    fun getByUsername(username: String): Flow<List<QuizAttemptEntity>>
}