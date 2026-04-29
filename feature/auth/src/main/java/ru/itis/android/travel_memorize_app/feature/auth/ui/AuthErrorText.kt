package ru.itis.android.travel_memorize_app.feature.auth.ui

import androidx.annotation.StringRes
import ru.itis.android.travel_memorize_app.core.domain.utils.AppError
import ru.itis.android.travel_memorize_app.ui.R

@StringRes
fun AppError.toMessageRes(): Int {
    return when (this) {
        AppError.Auth.InvalidEmailOrPassword -> R.string.error_invalid_email_or_password
        AppError.Auth.EmailAlreadyRegistered -> R.string.error_email_already_registered
        AppError.Auth.WeakPassword -> R.string.error_weak_password
        AppError.Auth.InvalidEmail -> R.string.error_invalid_email
        AppError.Auth.UserNotFound -> R.string.error_reset_email_not_found
        AppError.Auth.Network -> R.string.error_network
        AppError.Auth.Unknown -> R.string.error_unknown
        AppError.Unknown -> R.string.error_unknown
    }
}