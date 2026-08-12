package com.example.farmastudy

import com.example.farmastudy.data.local.entity.MedicationEntity
import com.example.farmastudy.data.local.entity.QuestionEntity
import com.example.farmastudy.ui.RandomItem
import com.example.farmastudy.ui.buildRandomDeck
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class RandomDeckTest {

    private fun medication(id: Int) = MedicationEntity(
        id = id,
        name = "medication-$id",
        therapeuticUse = null,
        mechanism = null,
        chemicalStructure = null,
        organicSystem = null
    )

    private fun question(id: Int) = QuestionEntity(
        id = id,
        question = "question-$id",
        optionA = "a",
        optionB = "b",
        optionC = "c",
        optionD = "d",
        correctOption = "a",
        quizPart = 1
    )

    @Test
    fun deck_mixes_flashcards_and_questions() {
        val deck = buildRandomDeck(
            medications = listOf(medication(1), medication(2)),
            questions = listOf(question(1), question(2)),
            maxFlashcards = 1,
            random = Random(3)
        )
        assertEquals(3, deck.size)
        assertEquals(1, deck.count { it is RandomItem.Flashcard })
        assertEquals(2, deck.count { it is RandomItem.QuizQuestion })
    }

    @Test
    fun deck_limits_flashcards_to_max() {
        val medications = (1..50).map { medication(it) }
        val deck = buildRandomDeck(
            medications = medications,
            questions = emptyList(),
            maxFlashcards = 10,
            random = Random(7)
        )
        assertEquals(10, deck.size)
        assertTrue(deck.all { it is RandomItem.Flashcard })
    }

    @Test
    fun deck_keeps_all_questions() {
        val deck = buildRandomDeck(
            medications = emptyList(),
            questions = listOf(question(1), question(2), question(3)),
            random = Random(1)
        )
        assertEquals(3, deck.size)
        assertTrue(deck.all { it is RandomItem.QuizQuestion })
    }

    @Test
    fun deck_default_lists_15_flashcards() {
        val medications = (1..100).map { medication(it) }
        val deck = buildRandomDeck(
            medications = medications,
            questions = listOf(question(1)),
            random = Random(5)
        )
        assertEquals(16, deck.size)
        assertEquals(15, deck.count { it.isFlashcard })
        assertEquals(1, deck.count { it.isQuestion })
    }
}