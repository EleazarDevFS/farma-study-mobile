package com.example.farmastudy.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmastudy.FarmaApp
import com.example.farmastudy.data.local.entity.QuestionEntity
import com.example.farmastudy.data.local.entity.QuizAttemptEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class QuizUiState(
    val quizPart: Int = 0,
    val questions: List<QuestionEntity> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOption: String? = null,
    val correctAnswers: Int = 0,
    val isLoading: Boolean = false,
    val partsCount: Map<Int, Int> = emptyMap()
) {
    val totalQuestions: Int get() = questions.size
    val currentQuestion: QuestionEntity? get() = questions.getOrNull(currentIndex)
    val isLastQuestion: Boolean get() = questions.isNotEmpty() && currentIndex >= questions.size - 1
    val finished: Boolean get() = questions.isNotEmpty() && currentIndex >= questions.size
}

class QuizViewModel(application: Application) : AndroidViewModel(application) {
    private val questionDao = (application as FarmaApp).database.questionDao()
    private val quizAttemptDao = (application as FarmaApp).database.quizAttemptDao()

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var attemptRecorded = false

    fun loadPartsInfo() {
        viewModelScope.launch {
            if (_uiState.value.partsCount.isNotEmpty()) return@launch
            val counts = (1..5).associateWith { part -> questionDao.getByQuizPart(part).size }
            _uiState.update { it.copy(partsCount = counts) }
        }
    }

    fun startQuiz(quizPart: Int) {
        viewModelScope.launch {
            attemptRecorded = false
            _uiState.update { it.copy(isLoading = true) }
            val questions = if (quizPart == 0) {
                questionDao.getAll().first()
            } else {
                questionDao.getByQuizPart(quizPart)
            }
            _uiState.value = QuizUiState(
                quizPart = quizPart,
                questions = questions,
                partsCount = _uiState.value.partsCount
            )
        }
    }

    fun ensureQuiz(quizPart: Int) {
        if (_uiState.value.quizPart == quizPart && _uiState.value.questions.isNotEmpty()) return
        startQuiz(quizPart)
    }

    fun setCurrentIndex(index: Int) {
        _uiState.update { it.copy(currentIndex = index, selectedOption = null) }
    }

    fun selectOption(option: String) {
        _uiState.update { state ->
            if (state.selectedOption != null) return@update state
            val question = state.currentQuestion ?: return@update state
            state.copy(
                selectedOption = option,
                correctAnswers = state.correctAnswers + if (question.correctOption == option) 1 else 0
            )
        }
    }

    fun next() {
        _uiState.update { it.copy(currentIndex = it.currentIndex + 1, selectedOption = null) }
    }

    fun recordAttempt(username: String) {
        val state = _uiState.value
        if (attemptRecorded || !state.finished) return
        attemptRecorded = true
        if (username.isBlank()) return
        viewModelScope.launch {
            quizAttemptDao.insert(
                QuizAttemptEntity(
                    username = username,
                    quizPart = state.quizPart,
                    score = state.correctAnswers,
                    total = state.totalQuestions,
                    date = System.currentTimeMillis()
                )
            )
        }
    }

    fun attemptsFor(username: String) = quizAttemptDao.getByUsername(username)
}