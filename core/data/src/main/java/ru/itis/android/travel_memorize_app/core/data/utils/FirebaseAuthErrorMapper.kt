package ru.itis.android.travel_memorize_app.core.data.utils

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import ru.itis.android.travel_memorize_app.core.domain.utils.AuthError

object FirebaseAuthErrorMapper {

    fun mapSignInError(throwable: Throwable): AuthError {
        return when (throwable) {
            is FirebaseAuthInvalidCredentialsException,
            is FirebaseAuthInvalidUserException -> AuthError.InvalidEmailOrPassword
            is FirebaseNetworkException -> AuthError.Network
            else -> AuthError.Unknown
        }
    }

    fun mapSignUpError(throwable: Throwable): AuthError {
        return when (throwable) {
            is FirebaseAuthUserCollisionException -> AuthError.EmailAlreadyRegistered
            is FirebaseAuthWeakPasswordException -> AuthError.WeakPassword
            is FirebaseAuthInvalidCredentialsException -> AuthError.InvalidEmail
            is FirebaseNetworkException -> AuthError.Network
            else -> AuthError.Unknown
        }
    }

    fun mapResetPasswordError(throwable: Throwable): AuthError {
        return when (throwable) {
            is FirebaseAuthInvalidUserException -> AuthError.UserNotFound
            is FirebaseAuthInvalidCredentialsException -> AuthError.InvalidEmail
            is FirebaseNetworkException -> AuthError.Network
            else -> AuthError.Unknown
        }
    }
}