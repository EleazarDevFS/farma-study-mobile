package com.example.farmastudy

import com.example.farmastudy.data.domain.usecase.Validators
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidatorsTest {

    @Test
    fun isValidPassword_accepts_8OrMoreCharacters() {
        assertTrue(Validators.isValidPassword("12345678"))
        assertFalse(Validators.isValidPassword("1234567"))
        assertFalse(Validators.isValidPassword(""))
    }

    @Test
    fun isMailCorrect_rejects_invalidCharsBeforeAt() {
        assertFalse(Validators.isMailCorrect("user.name@mail.com"))
        assertFalse(Validators.isMailCorrect("user;name@mail.com"))
        assertTrue(Validators.isMailCorrect("username@mail.com"))
        assertFalse(Validators.isMailCorrect("username@mail"))
        assertFalse(Validators.isMailCorrect("sinarroba"))
    }

    @Test
    fun isValidAge_accepts_6to99() {
        assertTrue(Validators.isValidAge(6))
        assertTrue(Validators.isValidAge(99))
        assertFalse(Validators.isValidAge(5))
        assertFalse(Validators.isValidAge(100))
    }

    @Test
    fun isBlank_usernameAndName_rejected() {
        assertFalse(Validators.isValidUsername(""))
        assertTrue(Validators.isValidUsername("farma42"))
        assertFalse(Validators.isValidName("  "))
    }
}