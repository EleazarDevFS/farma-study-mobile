package com.example.farmastudy.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmastudy.FarmaApp
import com.example.farmastudy.data.domain.usecase.Validators
import com.example.farmastudy.data.local.SessionStore
import com.example.farmastudy.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UserRepository((
            application as FarmaApp).database.userDao())
    private val sessionStore = SessionStore(application)

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val session: Flow<String?> = sessionStore.username

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            setError("Ingresa usuario y contraseña")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.login(username.trim(), password)
            if (result.success) {
                sessionStore.save(result.username!!)
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
            }
        }
    }

    fun register(
        username: String,
        name: String,
        age: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        val ageInt = age.toIntOrNull()
        val validationError = when {
            !Validators.isValidUsername(username) -> "Elige un nombre de usuario"
            !Validators.isValidName(name) -> "Ingresa tu nombre"
            ageInt == null || !Validators.isValidAge(ageInt) -> "La edad debe estar entre 6 y 99"
            !Validators.isMailCorrect(email) -> "Correo inválido: caracteres no válidos antes del @"
            !Validators.isValidPassword(password) -> "La contraseña debe tener al menos 8 caracteres"
            password != confirmPassword -> "Las contraseñas no coinciden"
            else -> null
        }
        if (validationError != null) {
            setError(validationError)
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.register(
                username = username,
                name = name,
                age = ageInt!!,
                email = email,
                password = password
            )
            if (result.success) {
                _uiState.update {
                    it.copy(isLoading = false, infoMessage = "Registro exitoso, inicia sesión con tu usuario")
                }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
            }
        }
    }

    fun logout() {
        viewModelScope.launch { sessionStore.clear() }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, infoMessage = null) }
    }

    private fun setError(message: String) {
        _uiState.update { it.copy(errorMessage = message) }
    }
}