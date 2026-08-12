package com.example.farmastudy.data.repository

import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.farmastudy.data.domain.model.AuthResult
import com.example.farmastudy.data.local.dao.UserDao
import com.example.farmastudy.data.local.entity.UserEntity

class UserRepository(private val userDao: UserDao) {
    suspend fun register(
        username: String,
        name: String,
        age: Int,
        email: String,
        password: String
    ): AuthResult {
        if (userDao.findByUsername(username) != null) {
            return AuthResult(success = false, message = "Username ya existe, elige otro")
        }
        val hash = BCrypt.withDefaults().hashToString(12, password.toCharArray())
        userDao.insert(
            UserEntity(
                username = username,
                password = hash,
                email = email,
                age = age,
                gender = null
            )
        )
        return AuthResult(success = true)
    }
    suspend fun login(username: String, password: String): AuthResult {
        val user = userDao.findByUsername(username)
            ?: return AuthResult(success = false, message = "Usuario no encontrado")
        val verifed = BCrypt.verifyer().verify(password.toCharArray(), user.password).verified
        return if (verifed){
            AuthResult(success = true, username = user.username)
        } else{
            AuthResult(success = false, message = "Contraseña incorrecta")
        }
    }
}