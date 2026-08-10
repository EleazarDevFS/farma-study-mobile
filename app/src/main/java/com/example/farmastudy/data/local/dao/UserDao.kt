package com.example.farmastudy.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.farmastudy.data.local.entity.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE username=:username LIMIT 1")
    suspend fun finByUsername(username: String): UserEntity?

    @Insert
    suspend fun insert(user: UserEntity): Long
}