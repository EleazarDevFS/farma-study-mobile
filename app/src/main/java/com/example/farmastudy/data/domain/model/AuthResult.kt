package com.example.farmastudy.data.domain.model

data class AuthResult(
    val success: Boolean,
    val message: String? = null,
    val username: String? = null
)
