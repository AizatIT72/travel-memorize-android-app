package ru.itis.android.travel_memorize_app.feature.auth.ui

import androidx.annotation.StringRes
import ru.itis.android.travel_memorize_app.core.domain.utils.AuthError
import ru.itis.android.travel_memorize_app.ui.R

@StringRes
fun AuthError.toMessageRes(): Int {
    return when (this) {
        AuthError.InvalidEmailOrPassword -> R.string.error_invalid_email_or_password
        AuthError.EmailAlreadyRegistered -> R.string.error_email_already_registered
        AuthError.WeakPassword -> R.string.error_weak_password
        AuthError.InvalidEmail -> R.string.error_invalid_email
        AuthError.UserNotFound -> R.string.error_reset_email_not_found
        AuthError.Network -> R.string.error_network
        AuthError.Unknown -> R.string.error_unknown
    }
}