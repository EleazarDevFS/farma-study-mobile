package com.example.farmastudy

import com.example.farmastudy.data.local.entity.QuizAttemptEntity
import com.example.farmastudy.ui.computeUserStats
import com.example.farmastudy.ui.levelFromXp
import com.example.farmastudy.ui.levelTitle
import com.example.farmastudy.ui.streakDays
import com.example.farmastudy.ui.xpIntoLevel
import com.example.farmastudy.ui.xpForNextLevel
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class GamificationTest {

    private fun attempt(score: Int, total: Int, date: Long, part: Int = 0) =
        QuizAttemptEntity(
            id = 0, username = "u", quizPart = part,
            score = score, total = total, date = date
        )

    @Test
    fun xpForNextLevel_scales_with_level() {
        assertEquals(100, xpForNextLevel(1))
        assertEquals(150, xpForNextLevel(2))
        assertEquals(250, xpForNextLevel(4))
    }

    @Test
    fun levelFromXp_starts_at_one() {
        assertEquals(1, levelFromXp(0))
        assertEquals(1, levelFromXp(99))
    }

    @Test
    fun levelFromXp_advances_each_threshold() {
        assertEquals(2, levelFromXp(100))
        assertEquals(3, levelFromXp(250))
        assertEquals(2, levelFromXp(249))
    }

    @Test
    fun xpIntoLevel_keeps_remaining_xp() {
        assertEquals(99, xpIntoLevel(99))
        assertEquals(0, xpIntoLevel(100))
        assertEquals(50, xpIntoLevel(300))
    }

    @Test
    fun levelTitle_caps_at_last_title() {
        assertEquals("Aprendiz de farmacia", levelTitle(1))
        assertEquals("Maestro de la farmacología", levelTitle(50))
    }

    @Test
    fun computeUserStats_aggregates_attempts() {
        val stats = computeUserStats(
            listOf(
                attempt(score = 8, total = 10, date = 1_752_000_000_000L),
                attempt(score = 5, total = 10, date = 1_752_086_400_000L)
            )
        )
        assertEquals(130, stats.xp)
        assertEquals(2, stats.quizzesCompleted)
        assertEquals(13, stats.totalCorrect)
        assertEquals(20, stats.totalAnswered)
        assertEquals(80, stats.bestPercent)
    }

    @Test
    fun computeUserStats_ignores_empty_attempts() {
        val stats = computeUserStats(emptyList())
        assertEquals(0, stats.xp)
        assertEquals(1, stats.level)
        assertEquals(0, stats.streakDays)
    }

    private fun startOfDay(day: Int): Calendar = Calendar.getInstance().apply {
    set(2026, Calendar.AUGUST, day, 0, 0, 0)
    set(Calendar.MILLISECOND, 0)
}

    @Test
    fun streakDays_counts_consecutive_days() {
        val today = startOfDay(18)
        val dayMillis = { day: Int -> startOfDay(day).timeInMillis }
        val dates = listOf(dayMillis(18), dayMillis(17), dayMillis(16), dayMillis(14))
        assertEquals(3, streakDays(dates, today))
    }

    @Test
    fun streakDays_allows_today_without_attempt() {
        val today = startOfDay(18)
        val yesterday = startOfDay(17).timeInMillis
        assertEquals(1, streakDays(listOf(yesterday), today))
    }

    @Test
    fun streakDays_returns_zero_for_no_attempts() {
        assertEquals(0, streakDays(emptyList()))
    }
}