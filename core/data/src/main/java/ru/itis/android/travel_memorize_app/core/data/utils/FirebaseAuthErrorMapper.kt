package ru.itis.android.travel_memorize_app.core.data.utils

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import ru.itis.android.travel_memorize_app.core.domain.utils.AppError

object FirebaseAuthErrorMapper {

    fun mapSignInError(throwable: Throwable): AppError.Auth {
        return when (throwable) {
            is FirebaseAuthInvalidCredentialsException,
            is FirebaseAuthInvalidUserException -> AppError.Auth.InvalidEmailOrPassword

            is FirebaseNetworkException -> AppError.Auth.Network

            else -> AppError.Auth.Unknown
        }
    }

    fun mapSignUpError(throwable: Throwable): AppError.Auth {
        return when (throwable) {
            is FirebaseAuthUserCollisionException -> AppError.Auth.EmailAlreadyRegistered
            is FirebaseAuthWeakPasswordException -> AppError.Auth.WeakPassword
            is FirebaseAuthInvalidCredentialsException -> AppError.Auth.InvalidEmail
            is FirebaseNetworkException -> AppError.Auth.Network
            else -> AppError.Auth.Unknown
        }
    }

    fun mapResetPasswordError(throwable: Throwable): AppError.Auth {
        return when (throwable) {
            is FirebaseAuthInvalidUserException -> AppError.Auth.UserNotFound
            is FirebaseAuthInvalidCredentialsException -> AppError.Auth.InvalidEmail
            is FirebaseNetworkException -> AppError.Auth.Network
            else -> AppError.Auth.Unknown
        }
    }
}