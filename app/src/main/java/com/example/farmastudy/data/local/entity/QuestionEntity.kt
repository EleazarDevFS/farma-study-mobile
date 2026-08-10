package com.example.farmastudy.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class QuestionEntity(
    @PrimaryKey val id: Int,
    val question: String,
    @ColumnInfo(name = "option_a") val optionA: String,
    @ColumnInfo(name = "option_b") val optionB: String,
    @ColumnInfo(name = "option_c") val optionC: String,
    @ColumnInfo(name = "option_d") val optionD: String,
    @ColumnInfo(name = "correct_option") val correctOption: String,
    @ColumnInfo(name = "quiz_part") val quizPart: Int
)
