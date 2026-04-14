package ru.itis.android.travel_memorize_app.feature.auth.viewmodel

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.itis.android.travel_memorize_app.core.domain.usecase.SignUpUseCase
import ru.itis.android.travel_memorize_app.core.domain.utils.Result

class SignUpViewModel(
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {
    var username by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var usernameError by mutableStateOf<String?>(null)
    var emailError by mutableStateOf<String?>(null)
    var passwordError by mutableStateOf<String?>(null)
    var confirmPasswordError by mutableStateOf<String?>(null)
    var commonError by mutableStateOf<String?>(null)
    var loading by mutableStateOf(false)
    var success by mutableStateOf(false)
    private var confirmTouched by mutableStateOf(false)

    fun onUsernameChanged(value: String) {
        username = value
        usernameError = validateUsername()
    }

    fun onEmailChanged(value: String) {
        email = value
        emailError = validateEmail()
    }

    fun onPasswordChanged(value: String) {
        password = value
        passwordError = validatePassword()
        if (confirmTouched) {
            confirmPasswordError = validateConfirmPassword()
        }
    }

    fun onConfirmPasswordChanged(value: String) {
        confirmPassword = value
        confirmTouched = true
        confirmPasswordError = validateConfirmPassword()
    }

    fun canSubmit(): Boolean {
        return validateUsername() == null &&
            validateEmail() == null &&
            validatePassword() == null &&
            validateConfirmPassword() == null
    }

    private fun validateUsername(): String? {
        val usernameRegex = Regex("^[A-Za-z0-9]{3,20}$")
        return if (usernameRegex.matches(username)) null else "username"
    }

    private fun validateEmail(): String? {
        return if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) null else "email"
    }

    private fun validatePassword(): String? {
        return if (password.length >= 6) null else "password"
    }

    private fun validateConfirmPassword(): String? {
        return if (password == confirmPassword && confirmPassword.isNotEmpty()) null else "confirm"
    }

    private fun showAllValidationErrors() {
        confirmTouched = true
        usernameError = validateUsername()
        emailError = validateEmail()
        passwordError = validatePassword()
        confirmPasswordError = validateConfirmPassword()
    }

    fun signUp() {
        showAllValidationErrors()
        if (!canSubmit()) return
        loading = true
        commonError = null
        viewModelScope.launch {
            when (val result = signUpUseCase(email, password, username)) {
                is Result.Success -> success = true
                is Result.Error -> commonError = result.message
            }
            loading = false
        }
    }
}
