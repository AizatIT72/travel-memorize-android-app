package ru.itis.android.travel_memorize_app.core.domain.utils

sealed interface AuthError {
    data object InvalidEmailOrPassword : AuthError
    data object EmailAlreadyRegistered : AuthError
    data object WeakPassword : AuthError
    data object InvalidEmail : AuthError
    data object UserNotFound : AuthError
    data object Network : AuthError
    data object Unknown : AuthError }