package com.example.farmastudy.ui

import com.example.farmastudy.data.local.entity.QuizAttemptEntity
import java.util.Calendar

const val XP_PER_CORRECT = 10

data class UserStats(
    val level: Int,
    val levelTitle: String,
    val xp: Int,
    val xpIntoLevel: Int,
    val xpForNextLevel: Int,
    val streakDays: Int,
    val quizzesCompleted: Int,
    val totalCorrect: Int,
    val totalAnswered: Int,
    val bestPercent: Int
) {
    val xpProgress: Float
        get() = if (xpForNextLevel <= 0) 1f else (xpIntoLevel.toFloat() / xpForNextLevel).coerceIn(0f, 1f)
}

val LEVEL_TITLES = listOf(
    "Aprendiz de farmacia",
    "Mezclador de pociones",
    "Químico en ciernes",
    "Boticario",
    "Farmacéutico estrella",
    "Maestro de la farmacología"
)

fun xpForNextLevel(level: Int): Int = 100 + (level - 1) * 50

fun levelFromXp(xp: Int): Int {
    var level = 1
    var remaining = xp
    while (remaining >= xpForNextLevel(level)) {
        remaining -= xpForNextLevel(level)
        level++
    }
    return level
}

fun xpIntoLevel(xp: Int): Int {
    var remaining = xp
    var level = 1
    while (remaining >= xpForNextLevel(level)) {
        remaining -= xpForNextLevel(level)
        level++
    }
    return remaining
}

fun levelTitle(level: Int): String =
    LEVEL_TITLES[(level - 1).coerceIn(0, LEVEL_TITLES.size - 1)]

fun computeUserStats(attempts: List<QuizAttemptEntity>): UserStats {
    val xp = attempts.sumOf { it.score } * XP_PER_CORRECT
    val level = levelFromXp(xp)
    val bestPercent = attempts
        .filter { it.total > 0 }
        .maxOfOrNull { it.score * 100 / it.total }
        ?: 0
    return UserStats(
        level = level,
        levelTitle = levelTitle(level),
        xp = xp,
        xpIntoLevel = xpIntoLevel(xp),
        xpForNextLevel = xpForNextLevel(level),
        streakDays = streakDays(attempts.map { it.date }),
        quizzesCompleted = attempts.size,
        totalCorrect = attempts.sumOf { it.score },
        totalAnswered = attempts.sumOf { it.total },
        bestPercent = bestPercent
    )
}

fun streakDays(dates: List<Long>, today: Calendar = startOfToday()): Int {
    val days = dates.map { startOfDay(it) }.distinct().sortedDescending()
    if (days.isEmpty()) return 0

    val yesterday = (today.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -1) }
    var expected = if (days.any { it.timeInMillis == today.timeInMillis }) today else yesterday
    var streak = 0
    for (day in days) {
        if (day.timeInMillis == expected.timeInMillis) {
            streak++
            expected = (expected.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -1) }
        } else {
            break
        }
    }
    return streak
}

fun encouragementMessage(percent: Int): String = when {
    percent >= 90 -> "¡Impresionante! Dominas el tema"
    percent >= 70 -> "¡Muy bien! Sigue así"
    percent >= 50 -> "¡Buen trabajo! Un repasito más y lo dominas"
    else -> "¡No te rindas! Cada intento cuenta"
}

fun randomStudyMessage(): String = listOf(
    "Buena sesión de repaso",
    "Cada tarjeta que volteas te acerca al examen",
    "Gran constancia: tu cerebro lo agradece"
).random()

private fun startOfToday(): Calendar = Calendar.getInstance().apply {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}

private fun startOfDay(millis: Long): Calendar = Calendar.getInstance().apply {
    timeInMillis = millis
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}