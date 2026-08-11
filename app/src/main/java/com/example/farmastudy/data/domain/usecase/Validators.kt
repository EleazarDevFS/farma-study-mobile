package com.example.farmastudy.data.domain.usecase

object Validators {
    fun isValidName(name: String): Boolean = name.isNotBlank()
    fun isValidUsername(userName: String): Boolean = userName.isNotBlank()
    fun isValidAge(age: Int): Boolean = age in 6..99
    fun isValidPassword(password: String): Boolean = password.length >= 8
    fun isMailCorrect(mail: String): Boolean  {
        val parts = mail.split("@")
        if(parts.size != 2) return false
        val beforeAt = parts[0]
        val invalidChars = listOf('.', ',', '"', '\'', ';', ':', '+', '*', '`')
        return beforeAt.none {
            it in invalidChars
        } && parts[1].contains(".")
    }
}