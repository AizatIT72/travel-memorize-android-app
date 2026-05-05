package ru.itis.android.travel_memorize_app.feature.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.itis.android.travel_memorize_app.core.domain.usecase.SendPasswordResetUseCase
import ru.itis.android.travel_memorize_app.core.domain.utils.Result
import ru.itis.android.travel_memorize_app.core.utils.validation.AuthValidators

class ForgotPasswordViewModel @Inject constructor(
    private val sendPasswordResetUseCase: SendPasswordResetUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(ForgotPasswordUiState())
    val state: StateFlow<ForgotPasswordUiState> = _state.asStateFlow()
    private val _effect = Channel<AuthEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()
    fun onEmailChanged(value: String) {
        val filteredValue = AuthValidators.filterEmail(value)
        _state.update { currentState ->
            val newState = currentState.copy(
                email = filteredValue,
                emailError = if (currentState.wasSubmitted) {
                    !AuthValidators.isEmailValid(filteredValue)
                } else {
                    false
                },
                commonError = null
            )
            newState.copy(isSubmitEnabled = canSubmit(newState))
        }
    }

    fun sendResetLink() {
        val currentState = state.value
        val emailError = !AuthValidators.isEmailValid(currentState.email)
        if (currentState.isLoading) return
        if (emailError) {
            _state.update {
                it.copy(
                    emailError = true,
                    isSubmitEnabled = false
                )
            }
            return
        }
        viewModelScope.launch {
            _state.update {
                val newState = it.copy(
                    emailError = true,
                    wasSubmitted = true,
                    commonError = null
                )
                newState.copy(isSubmitEnabled = canSubmit(newState))
            }
            when (val result = sendPasswordResetUseCase(currentState.email.trim())) {
                is Result.Success -> {
                    _effect.send(AuthEffect.PasswordResetSent)
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(commonError = result.error)
                    }
                }
            }
            _state.update { currentState ->
                val newState = currentState.copy(isLoading = false)
                newState.copy(isSubmitEnabled = canSubmit(newState))
            }
        }
    }

    private fun canSubmit(state: ForgotPasswordUiState): Boolean {
        return state.email.isNotBlank() && !state.isLoading
    }

}