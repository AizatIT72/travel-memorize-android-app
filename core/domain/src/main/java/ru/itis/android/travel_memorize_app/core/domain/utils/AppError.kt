package ru.itis.android.travel_memorize_app.core.domain.utils

sealed interface AppError {
    sealed interface Auth : AppError {
        data object InvalidEmailOrPassword : Auth
        data object EmailAlreadyRegistered : Auth
        data object WeakPassword : Auth
        data object InvalidEmail : Auth
        data object UserNotFound : Auth
        data object Network : Auth
        data object Unknown : Auth
    }
    data object Unknown : AppError
}