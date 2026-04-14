package ru.itis.android.travel_memorize_app.core.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import javax.inject.Inject
import ru.itis.android.travel_memorize_app.core.domain.model.User
import ru.itis.android.travel_memorize_app.core.domain.repository.AuthRepository
import ru.itis.android.travel_memorize_app.core.domain.utils.Result
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override suspend fun signUp(email: String, password: String, username: String): Result<User> {
        return try {
            val authResult = suspendCoroutine { continuation ->
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener { continuation.resume(it) }
                    .addOnFailureListener { continuation.resumeWithException(it) }
            }
            val firebaseUser = authResult.user ?: return Result.Error("User is null")
            val data = mapOf(
                "username" to username,
                "email" to email,
                "stats" to mapOf(
                    "countries" to 0,
                    "cities" to 0,
                    "memoriesCount" to 0
                ),
                "searchUsername" to username.lowercase(),
                "createdAt" to Timestamp.now()
            )
            suspendCoroutine<Unit> { continuation ->
                firestore.collection("users").document(firebaseUser.uid).set(data)
                    .addOnSuccessListener { continuation.resume(Unit) }
                    .addOnFailureListener { continuation.resumeWithException(it) }
            }
            Result.Success(User(firebaseUser.uid, email, username))
        } catch (throwable: Throwable) {
            Result.Error(throwable.message ?: "Sign up error", throwable)
        }
    }

    override suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val authResult = suspendCoroutine { continuation ->
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener { continuation.resume(it) }
                    .addOnFailureListener { continuation.resumeWithException(it) }
            }
            val firebaseUser = authResult.user ?: return Result.Error("User is null")
            val username = getUsername(firebaseUser.uid)
            Result.Success(
                User(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email.orEmpty(),
                    username = username
                )
            )
        } catch (throwable: Throwable) {
            Result.Error(throwable.message ?: "Sign in error", throwable)
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            suspendCoroutine<Unit> { continuation ->
                firebaseAuth.sendPasswordResetEmail(email)
                    .addOnSuccessListener { continuation.resume(Unit) }
                    .addOnFailureListener { continuation.resumeWithException(it) }
            }
            Result.Success(Unit)
        } catch (throwable: Throwable) {
            Result.Error(throwable.message ?: "Reset error", throwable)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        firebaseAuth.signOut()
        return Result.Success(Unit)
    }

    override suspend fun getCurrentUser(): Result<User?> {
        return try {
            val firebaseUser = firebaseAuth.currentUser ?: return Result.Success(null)
            val username = getUsername(firebaseUser.uid)
            Result.Success(
                User(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email.orEmpty(),
                    username = username
                )
            )
        } catch (throwable: Throwable) {
            Result.Error(throwable.message ?: "Current user error", throwable)
        }
    }

    private suspend fun getUsername(uid: String): String {
        val snapshot = suspendCoroutine { continuation ->
            firestore.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { continuation.resume(it) }
                .addOnFailureListener { continuation.resumeWithException(it) }
        }
        return snapshot.getString("username").orEmpty()
    }
}
