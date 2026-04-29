package ru.itis.android.travel_memorize_app.core.utils.validation

import android.util.Patterns

object AuthValidators {

    private const val USERNAME_MIN_LENGTH = 3
    private const val USERNAME_MAX_LENGTH = 20
    private const val PASSWORD_MIN_LENGTH = 6
    private const val PASSWORD_MAX_LENGTH = 64

    private val usernameRegex = Regex("^[A-Za-z0-9]{$USERNAME_MIN_LENGTH,$USERNAME_MAX_LENGTH}$")

    fun filterUsername(value: String): String {
        return value
            .filter { it.isLetterOrDigit() && it.code < 128 }
            .take(USERNAME_MAX_LENGTH)
    }

    fun filterEmail(value: String): String {
        return value.trim().take(254)
    }

    fun filterPassword(value: String): String {
        return value.take(PASSWORD_MAX_LENGTH)
    }

    fun isUsernameValid(username: String): Boolean {
        return usernameRegex.matches(username.trim())
    }

    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    }

    fun isSignInPasswordValid(password: String): Boolean {
        return password.isNotBlank()
    }

    fun isSignUpPasswordValid(password: String): Boolean {
        return password.length >= PASSWORD_MIN_LENGTH
    }

    fun isConfirmPasswordValid(password: String, confirmPassword: String): Boolean {
        return confirmPassword.isNotBlank() && password == confirmPassword
    }
}