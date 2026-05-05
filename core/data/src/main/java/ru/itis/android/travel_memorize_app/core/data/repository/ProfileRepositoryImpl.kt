package ru.itis.android.travel_memorize_app.core.data.repository

import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import ru.itis.android.travel_memorize_app.core.domain.model.profile.UserProfile
import ru.itis.android.travel_memorize_app.core.domain.repository.profile.ProfileRepository
import ru.itis.android.travel_memorize_app.core.domain.utils.MapError
import ru.itis.android.travel_memorize_app.core.domain.utils.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) : ProfileRepository {

    override suspend fun getProfile(uid: String): Result<UserProfile, MapError> = try {
        val userDoc = firestore.collection(USERS)
            .document(uid)
            .get()
            .await()

        if (!userDoc.exists()) {
            val currentUser = auth.currentUser
            val fallbackProfile = UserProfile(
                uid = uid,
                username = currentUser?.email?.substringBefore("@").orEmpty(),
                email = currentUser?.email.orEmpty(),
                createdAt = System.currentTimeMillis()
            )

            firestore.collection(USERS)
                .document(uid)
                .set(fallbackProfile.toMap(), SetOptions.merge())
                .await()
            return Result.Success(fallbackProfile)
        }

        val memories = firestore.collection(MEMORIES)
            .whereEqualTo(USER_ID_FIELD, uid)
            .get()
            .await()
            .documents

        val cities = memories.mapNotNull { it.getString(CITY_FIELD) }.filter { it.isNotBlank() }.toSet()
        val countries = memories.mapNotNull { it.getString(COUNTRY_FIELD) }.filter { it.isNotBlank() }.toSet()

        val statsMap = userDoc.get(STATS_FIELD) as? Map<*, *>
        val profile = UserProfile(
            uid = uid,
            username = userDoc.getString(USERNAME_FIELD)
                ?: auth.currentUser?.displayName
                ?: auth.currentUser?.email?.substringBefore("@")
                ?: "",
            email = userDoc.getString(EMAIL_FIELD) ?: auth.currentUser?.email.orEmpty(),
            avatarUrl = userDoc.getString(AVATAR_URL_FIELD),
            bio = userDoc.getString(BIO_FIELD),
            city = userDoc.getString(CITY_FIELD),
            country = userDoc.getString(COUNTRY_FIELD),
            memoriesCount = memories.size.takeIf { it > 0 }
                ?: (statsMap?.get(MEMORIES_COUNT_FIELD) as? Number)?.toInt()
                ?: 0,
            citiesCount = cities.size.takeIf { it > 0 }
                ?: (statsMap?.get(CITIES_FIELD) as? Number)?.toInt()
                ?: 0,
            countriesCount = countries.size.takeIf { it > 0 }
                ?: (statsMap?.get(COUNTRIES_FIELD) as? Number)?.toInt()
                ?: 0,
            createdAt = userDoc.getLongCompat(CREATED_AT_FIELD) ?: System.currentTimeMillis(),
            updatedAt = userDoc.getLongCompat(UPDATED_AT_FIELD)
        )
        Result.Success(profile)
    } catch (_: Throwable) {
        Result.Error(MapError.Network)
    }

    override suspend fun updateProfile(profile: UserProfile): Result<Unit, MapError> = try {
        firestore.collection(USERS)
            .document(profile.uid)
            .set(profile.toMap(), SetOptions.merge())
            .await()

        Result.Success(Unit)
    } catch (_: Throwable) {
        Result.Error(MapError.Network)
    }

    override suspend fun uploadAvatar(uri: Uri, uid: String): Result<String, MapError> = try {
        val ref = storage.reference.child("avatars/$uid/${System.currentTimeMillis()}.jpg")
        ref.putFile(uri).await()
        Result.Success(ref.downloadUrl.await().toString())
    } catch (_: Throwable) {
        Result.Error(MapError.Network)
    }
    override suspend fun deleteAccount(uid: String): Result<Unit, MapError> = try {
        val memories = firestore.collection(MEMORIES)
            .whereEqualTo(USER_ID_FIELD, uid)
            .get()
            .await()

        memories.documents.forEach { doc ->
            firestore.collection(MEMORIES).document(doc.id).delete().await()
        }
        firestore.collection(USERS).document(uid).delete().await()
        auth.currentUser?.delete()?.await()

        Result.Success(Unit)
    } catch (_: Throwable) {
        Result.Error(MapError.Network)
    }

    private fun UserProfile.toMap(): Map<String, Any?> = mapOf(
        UID_FIELD to uid,
        USERNAME_FIELD to username,
        EMAIL_FIELD to email,
        AVATAR_URL_FIELD to avatarUrl,
        BIO_FIELD to bio,
        CITY_FIELD to city,
        COUNTRY_FIELD to country,
        SEARCH_USERNAME_FIELD to username.lowercase(),
        CREATED_AT_FIELD to Timestamp(createdAt / 1000, ((createdAt % 1000) * 1_000_000).toInt()),
        UPDATED_AT_FIELD to Timestamp.now()
    )

    private fun com.google.firebase.firestore.DocumentSnapshot.getLongCompat(field: String): Long? {
        return when (val value = get(field)) {
            is Long -> value
            is Int -> value.toLong()
            is Double -> value.toLong()
            is Timestamp -> value.toDate().time
            else -> null
        }
    }

    private companion object {
        const val USERS = "users"
        const val MEMORIES = "memories"


        const val UID_FIELD = "uid"
        const val USER_ID_FIELD = "userId"
        const val USERNAME_FIELD = "username"
        const val SEARCH_USERNAME_FIELD = "searchUsername"
        const val EMAIL_FIELD = "email"
        const val AVATAR_URL_FIELD = "avatarUrl"
        const val BIO_FIELD = "bio"
        const val CITY_FIELD = "city"
        const val COUNTRY_FIELD = "country"
        const val CREATED_AT_FIELD = "createdAt"
        const val UPDATED_AT_FIELD = "updatedAt"
        const val STATS_FIELD = "stats"
        const val COUNTRIES_FIELD = "countries"
        const val CITIES_FIELD = "cities"
        const val MEMORIES_COUNT_FIELD = "memoriesCount"
    }
}