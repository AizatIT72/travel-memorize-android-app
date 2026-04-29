package ru.itis.android.travel_memorize_app.core.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import kotlinx.coroutines.tasks.await
import ru.itis.android.travel_memorize_app.core.data.utils.FirebaseAuthErrorMapper
import ru.itis.android.travel_memorize_app.core.domain.model.User
import ru.itis.android.travel_memorize_app.core.domain.repository.AuthRepository
import ru.itis.android.travel_memorize_app.core.domain.utils.AppError
import ru.itis.android.travel_memorize_app.core.domain.utils.Result

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {
    override suspend fun signUp(
        email: String,
        password: String,
        username: String
    ): Result<User> {
        return try {
            val trimmedEmail = email.trim()
            val trimmedUsername = username.trim()
            val authResult = firebaseAuth
                .createUserWithEmailAndPassword(trimmedEmail, password)
                .await()
            val firebaseUser = authResult.user
                ?: return Result.Error(AppError.Auth.Unknown)
            val userData = mapOf(
                USERNAME_FIELD to trimmedUsername,
                EMAIL_FIELD to trimmedEmail,
                STATS_FIELD to mapOf(
                    COUNTRIES_FIELD to 0,
                    CITIES_FIELD to 0,
                    MEMORIES_COUNT_FIELD to 0
                ),
                SEARCH_USERNAME_FIELD to trimmedUsername.lowercase(),
                CREATED_AT_FIELD to Timestamp.now()
            )
            firestore.collection(USERS_COLLECTION)
                .document(firebaseUser.uid)
                .set(userData)
                .await()
            Result.Success(
                User(
                    uid = firebaseUser.uid,
                    email = trimmedEmail,
                    username = trimmedUsername
                )
            )
        } catch (throwable: Throwable) {
            Result.Error(FirebaseAuthErrorMapper.mapSignUpError(throwable))
        }
    }

    override suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val trimmedEmail = email.trim()
            val authResult = firebaseAuth
                .signInWithEmailAndPassword(trimmedEmail, password)
                .await()
            val firebaseUser = authResult.user
                ?: return Result.Error(AppError.Auth.Unknown)
            val username = getUsername(firebaseUser.uid)
            Result.Success(
                User(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email.orEmpty(),
                    username = username
                )
            )
        } catch (throwable: Throwable) {
            Result.Error(FirebaseAuthErrorMapper.mapSignInError(throwable))
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            firebaseAuth
                .sendPasswordResetEmail(email.trim())
                .await()
            Result.Success(Unit)
        } catch (throwable: Throwable) {
            Result.Error(FirebaseAuthErrorMapper.mapResetPasswordError(throwable))
        }
    }
    override suspend fun signOut(): Result<Unit> {
        firebaseAuth.signOut()
        return Result.Success(Unit)
    }

    override suspend fun getCurrentUser(): Result<User?> {
        return try {
            val firebaseUser = firebaseAuth.currentUser
                ?: return Result.Success(null)
            val username = getUsername(firebaseUser.uid)
            Result.Success(
                User(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email.orEmpty(),
                    username = username
                )
            )
        } catch (throwable: Throwable) {
            Result.Error(AppError.Auth.Unknown)
        }
    }

    private suspend fun getUsername(uid: String): String {
        val snapshot = firestore.collection(USERS_COLLECTION)
            .document(uid)
            .get()
            .await()
        return snapshot.getString(USERNAME_FIELD).orEmpty()
    }

    private companion object {
        const val USERS_COLLECTION = "users"
        const val USERNAME_FIELD = "username"
        const val EMAIL_FIELD = "email"
        const val STATS_FIELD = "stats"
        const val SEARCH_USERNAME_FIELD = "searchUsername"
        const val CREATED_AT_FIELD = "createdAt"
        const val COUNTRIES_FIELD = "countries"
        const val CITIES_FIELD = "cities"
        const val MEMORIES_COUNT_FIELD = "memoriesCount"
    }
}