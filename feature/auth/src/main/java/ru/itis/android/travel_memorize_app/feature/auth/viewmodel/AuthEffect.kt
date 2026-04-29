package ru.itis.android.travel_memorize_app.feature.auth.viewmodel

sealed interface AuthEffect {
    data object NavigateToMap : AuthEffect
    data object PasswordResetSent : AuthEffect
}