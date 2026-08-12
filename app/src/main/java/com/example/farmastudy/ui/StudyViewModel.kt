package com.example.farmastudy.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmastudy.FarmaApp
import com.example.farmastudy.data.local.entity.MedicationEntity
import com.example.farmastudy.data.local.entity.QuestionEntity
import com.example.farmastudy.data.repository.MedicationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

sealed class RandomItem {
    data class Flashcard(val medication: MedicationEntity) : RandomItem()
    data class QuizQuestion(val question: QuestionEntity) : RandomItem()

    val isQuestion: Boolean get() = this is QuizQuestion
    val isFlashcard: Boolean get() = this is Flashcard
}

fun buildRandomDeck(
    medications: List<MedicationEntity>,
    questions: List<QuestionEntity>,
    maxFlashcards: Int = 15,
    random: Random = Random.Default
): List<RandomItem> {
    val flashcards = medications.shuffled(random).take(maxFlashcards).map { RandomItem.Flashcard(it) }
    val quiz = questions.shuffled(random).map { RandomItem.QuizQuestion(it) }
    return (flashcards + quiz).shuffled(random)
}

data class RandomStudyUiState(
    val items: List<RandomItem> = emptyList(),
    val currentIndex: Int = 0,
    val flashcardRevealed: Boolean = false,
    val selectedOption: String? = null,
    val correctAnswers: Int = 0,
    val isLoading: Boolean = false
) {
    val currentItem: RandomItem? get() = items.getOrNull(currentIndex)
    val totalItems: Int get() = items.size
    val questionCount: Int get() = items.count { it.isQuestion }
    val isLastItem: Boolean get() = items.isNotEmpty() && currentIndex >= items.size - 1
    val finished: Boolean get() = items.isNotEmpty() && currentIndex >= items.size
}

class StudyViewModel(application: Application) : AndroidViewModel(application) {
    private val medicationRepository = MedicationRepository((application as FarmaApp).database.medicationDao())
    private val questionDao = (application as FarmaApp).database.questionDao()

    private val _randomState = MutableStateFlow(RandomStudyUiState())
    val randomState: StateFlow<RandomStudyUiState> = _randomState.asStateFlow()

    fun medicationsFor(category: String): Flow<List<MedicationEntity>> =
        medicationRepository.getByRoute(category)

    fun startRandomStudy() {
        viewModelScope.launch {
            _randomState.update { it.copy(isLoading = true) }
            val medications = medicationRepository.getAll().first()
            val questions = questionDao.getAll().first()
            _randomState.value = RandomStudyUiState(
                items = buildRandomDeck(medications, questions)
            )
        }
    }

    fun toggleReveal() {
        _randomState.update { it.copy(flashcardRevealed = !it.flashcardRevealed) }
    }

    fun selectOption(option: String) {
        _randomState.update { state ->
            if (state.selectedOption != null) return@update state
            val item = state.currentItem as? RandomItem.QuizQuestion ?: return@update state
            state.copy(
                selectedOption = option,
                correctAnswers = state.correctAnswers + if (item.question.correctOption == option) 1 else 0
            )
        }
    }

    fun next() {
        _randomState.update {
            it.copy(currentIndex = it.currentIndex + 1, flashcardRevealed = false, selectedOption = null)
        }
    }
}