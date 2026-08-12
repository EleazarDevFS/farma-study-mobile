package com.example.farmastudy

import com.example.farmastudy.data.local.entity.QuizAttemptEntity
import com.example.farmastudy.ui.screens.history.attemptPercent
import com.example.farmastudy.ui.screens.history.formatAttemptDate
import com.example.farmastudy.ui.screens.history.quizPartLabel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuizHistoryFormatTest {

    @Test
    fun quizPartLabel_maps_part0_to_full_quiz() {
        assertEquals("Quiz completo", quizPartLabel(0))
        assertEquals("Parte 3", quizPartLabel(3))
    }

    @Test
    fun attemptPercent_computes_percentage() {
        val attempt = QuizAttemptEntity(
            id = 1, username = "u", quizPart = 0,
            score = 25, total = 50, date = 0
        )
        assertEquals(50, attemptPercent(attempt))
    }

    @Test
    fun attemptPercent_avoids_division_by_zero() {
        val attempt = QuizAttemptEntity(
            id = 1, username = "u", quizPart = 0,
            score = 0, total = 0, date = 0
        )
        assertEquals(0, attemptPercent(attempt))
    }

    @Test
    fun formatAttemptDate_renders_readable_date() {
        val formatted = formatAttemptDate(1_752_000_000_000L)
        assertTrue(formatted.contains("/"))
        assertTrue(formatted.contains(":"))
        assertTrue(formatted.matches(Regex("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}")))
    }
}