package ru.itis.android.travel_memorize_app.feature.auth.viewmodel

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.itis.android.travel_memorize_app.core.domain.usecase.SendPasswordResetUseCase
import ru.itis.android.travel_memorize_app.core.domain.utils.Result

class ForgotPasswordViewModel(
    private val sendPasswordResetUseCase: SendPasswordResetUseCase
) : ViewModel() {
    var email by mutableStateOf("")
    var emailError by mutableStateOf<String?>(null)
    var loading by mutableStateOf(false)
    var commonError by mutableStateOf<String?>(null)
    var sent by mutableStateOf(false)

    fun onEmailChanged(value: String) {
        email = value
        emailError = validateEmail()
    }

    fun canSubmit(): Boolean = validateEmail() == null

    private fun validateEmail(): String? {
        return if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) null else "email"
    }

    fun sendResetLink() {
        emailError = validateEmail()
        if (!canSubmit()) return
        loading = true
        commonError = null
        viewModelScope.launch {
            when (val result = sendPasswordResetUseCase(email)) {
                is Result.Success -> sent = true
                is Result.Error -> commonError = result.message
            }
            loading = false
        }
    }
}
