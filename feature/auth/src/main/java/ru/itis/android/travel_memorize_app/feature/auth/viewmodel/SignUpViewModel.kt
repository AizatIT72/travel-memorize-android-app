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
import ru.itis.android.travel_memorize_app.core.domain.usecase.SignUpUseCase
import ru.itis.android.travel_memorize_app.core.domain.utils.Result
import ru.itis.android.travel_memorize_app.core.utils.validation.AuthValidators


class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SignUpUiState())
    val state: StateFlow<SignUpUiState> = _state.asStateFlow()
    private val _effect = Channel<AuthEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onUsernameChanged(value: String) {
        val filteredValue = AuthValidators.filterUsername(value)
        _state.update { currentState ->
            val newState = currentState.copy(
                username = filteredValue,
                usernameError = if (currentState.wasSubmitted) {
                    !AuthValidators.isUsernameValid(filteredValue)
                } else {
                    false
                },
                commonError = null
            )
            newState.copy(isSubmitEnabled = canSubmit(newState))
        }
    }

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
                    !AuthValidators.isSignUpPasswordValid(filteredValue)
                } else {
                    false
                },
                commonError = null
            )
            newState.copy(isSubmitEnabled = canSubmit(newState))
        }
    }

    fun onConfirmPasswordChanged(value: String) {
        val filteredValue = AuthValidators.filterPassword(value)
        _state.update { currentState ->
            val newState = currentState.copy(
                confirmPassword = filteredValue,
                confirmPasswordError = if (currentState.wasSubmitted) {
                    !AuthValidators.isConfirmPasswordValid(
                        password = currentState.password,
                        confirmPassword = filteredValue
                    )
                } else {
                    false
                },
                commonError = null
            )
            newState.copy(isSubmitEnabled = canSubmit(newState))
        }
    }

    fun signUp() {

        val currentState = state.value
        val usernameError = !AuthValidators.isUsernameValid(currentState.username)
        val emailError = !AuthValidators.isEmailValid(currentState.email)
        val passwordError = !AuthValidators.isSignUpPasswordValid(currentState.password)
        val confirmPasswordError = !AuthValidators.isConfirmPasswordValid(
            password = currentState.password,
            confirmPassword = currentState.confirmPassword
        )
        if (currentState.isLoading) return
        if (usernameError || emailError || passwordError || confirmPasswordError) {
            _state.update {
                val newState = it.copy(
                    usernameError = usernameError,
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmPasswordError,
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
                val result = signUpUseCase(
                    email = currentState.email.trim(),
                    password = currentState.password,
                    username = currentState.username.trim()
                )
            ) {
                is Result.Success -> {
                    _effect.send(AuthEffect.NavigateToMap)
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

    private fun canSubmit(state: SignUpUiState): Boolean {
        return state.username.isNotBlank() &&
                state.email.isNotBlank() &&
                state.password.isNotBlank() &&
                state.confirmPassword.isNotBlank() &&
                !state.isLoading
    }
}