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
import ru.itis.android.travel_memorize_app.core.domain.usecase.SignInUseCase
import ru.itis.android.travel_memorize_app.core.domain.utils.Result
import ru.itis.android.travel_memorize_app.core.utils.validation.AuthValidators

class SignInViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(SignInUiState())
    val state: StateFlow<SignInUiState> = _state.asStateFlow()
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

    fun onPasswordChanged(value: String) {
        val filteredValue = AuthValidators.filterPassword(value)

        _state.update { currentState ->
            val newState = currentState.copy(
                password = filteredValue,
                passwordError = if (currentState.wasSubmitted) {
                    !AuthValidators.isSignInPasswordValid(filteredValue)
                } else {
                    false
                },
                commonError = null
            )
            newState.copy(isSubmitEnabled = canSubmit(newState))
        }
    }

    fun signIn() {
        val currentState = state.value
        if (currentState.isLoading) return

        val emailError = !AuthValidators.isEmailValid(currentState.email)
        val passwordError = !AuthValidators.isSignInPasswordValid(currentState.password)

        if (emailError || passwordError) {
            _state.update {
                val newState = it.copy(
                    emailError = emailError,
                    passwordError = passwordError,
                    wasSubmitted = true,
                    commonError = null
                )
                newState.copy(isSubmitEnabled = canSubmit(newState))
            }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    commonError = null,
                    isSubmitEnabled = false
                )
            }

            when (
                val result = signInUseCase(
                    email = currentState.email.trim(),
                    password = currentState.password
                )
            ) {
                is Result.Success -> _effect.send(AuthEffect.NavigateToMap)

                is Result.Error -> {
                    _state.update {
                        it.copy(commonError = result.error)
                    }
                }
            }

            _state.update { current ->
                val newState = current.copy(isLoading = false)
                newState.copy(isSubmitEnabled = canSubmit(newState))
            }
        }
    }

    private fun canSubmit(state: SignInUiState): Boolean {
        return state.email.isNotBlank() &&
                state.password.isNotBlank() &&
                !state.isLoading
    }
}