package ru.itis.android.travel_memorize_app.feature.auth.viewmodel
import ru.itis.android.travel_memorize_app.core.domain.utils.AuthError

data class ForgotPasswordUiState(
    val email: String = "",
    val emailError: Boolean = false,
    val commonError: AuthError? = null,
    val isLoading: Boolean = false,
    val isSubmitEnabled: Boolean = false,
    val wasSubmitted: Boolean = false
)