package ru.itis.android.travel_memorize_app.feature.auth.viewmodel

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.itis.android.travel_memorize_app.core.domain.usecase.SignInUseCase
import ru.itis.android.travel_memorize_app.core.domain.utils.Result

class SignInViewModel(
    private val signInUseCase: SignInUseCase
) : ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var emailError by mutableStateOf<String?>(null)
    var passwordError by mutableStateOf<String?>(null)
    var commonError by mutableStateOf<String?>(null)
    var loading by mutableStateOf(false)
    var success by mutableStateOf(false)

    fun onEmailChanged(value: String) {
        email = value
        emailError = validateEmail()
    }

    fun onPasswordChanged(value: String) {
        password = value
        passwordError = validatePassword()
    }

    fun canSubmit(): Boolean {
        return validateEmail() == null && validatePassword() == null
    }

    private fun validateEmail(): String? {
        return if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) null else "email"
    }

    private fun validatePassword(): String? {
        return if (password.length >= 6) null else "password"
    }

    private fun showAllValidationErrors() {
        emailError = validateEmail()
        passwordError = validatePassword()
    }

    fun signIn() {
        showAllValidationErrors()
        if (!canSubmit()) return
        loading = true
        commonError = null
        viewModelScope.launch {
            when (val result = signInUseCase(email, password)) {
                is Result.Success -> success = true
                is Result.Error -> commonError = result.message
            }
            loading = false
        }
    }
}
