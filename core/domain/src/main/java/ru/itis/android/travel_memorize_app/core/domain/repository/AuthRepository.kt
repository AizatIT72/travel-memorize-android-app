package ru.itis.android.travel_memorize_app.core.domain.repository

import ru.itis.android.travel_memorize_app.core.domain.model.User
import ru.itis.android.travel_memorize_app.core.domain.utils.Result

interface AuthRepository {
    suspend fun signUp(email: String, password: String, username: String): Result<User>
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    suspend fun signOut(): Result<Unit>
    suspend fun getCurrentUser(): Result<User?>
}
