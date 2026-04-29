package ru.itis.android.travel_memorize_app.feature.auth.viewmodel

import ru.itis.android.travel_memorize_app.core.domain.utils.AppError

data class ForgotPasswordUiState(
    val email: String = "",
    val emailError: Boolean = false,
    val commonError: AppError? = null,
    val isLoading: Boolean = false,
    val isSubmitEnabled: Boolean = false,
    val wasSubmitted: Boolean = false
)