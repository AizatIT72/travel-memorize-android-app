package ru.itis.android.travel_memorize_app.core.data.repository

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import ru.itis.android.travel_memorize_app.core.domain.model.map.GeoPoint
import ru.itis.android.travel_memorize_app.core.domain.model.map.PlaceMark
import ru.itis.android.travel_memorize_app.core.domain.repository.memory.MemoryRepository
import ru.itis.android.travel_memorize_app.core.domain.utils.MapError
import ru.itis.android.travel_memorize_app.core.domain.utils.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoryRepositoryImpl @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    private val firestore: FirebaseFirestore
) : MemoryRepository {
    private companion object {
        const val MEMORIES_COLLECTION = "memories"
        const val STORAGE_FOLDER = "memories"
    }

    override suspend fun uploadPhoto(
        uri: Uri,
        userId: String
    ): Result<String, MapError> {
        return try {
            val fileName = "$STORAGE_FOLDER/$userId/${System.currentTimeMillis()}.jpg"
            val ref = firebaseStorage.reference.child(fileName)
            ref.putFile(uri).await()
            Result.Success(ref.downloadUrl.await().toString())
        } catch (_: Throwable) {
            Result.Error(MapError.Network)
        }
    }

    override suspend fun addMemory(
        placeMark: PlaceMark
    ): Result<Unit, MapError> {
        return try {
            val docRef = firestore.collection(MEMORIES_COLLECTION).document()
            docRef.set(
                placeMark.copy(id = docRef.id).toFirestoreMap()
            ).await()
            Result.Success(Unit)
        } catch (_: Throwable) {
            Result.Error(MapError.Network)
        }
    }

    override suspend fun updateMemory(
        placeMark: PlaceMark
    ): Result<Unit, MapError> {
        return try {
            firestore.collection(MEMORIES_COLLECTION)
                .document(placeMark.id)
                .set(placeMark.toFirestoreMap())
                .await()
            Result.Success(Unit)
        } catch (_: Throwable) {
            Result.Error(MapError.Network)
        }
    }

    override suspend fun deleteMemory(
        placeMarkId: String
    ): Result<Unit, MapError> {
        return try {
            firestore.collection(MEMORIES_COLLECTION)
                .document(placeMarkId)
                .delete()
                .await()
            Result.Success(Unit)
        } catch (_: Throwable) {
            Result.Error(MapError.Network)
        }
    }

    override suspend fun getMemory(
        placeMarkId: String
    ): Result<PlaceMark, MapError> {
        return try {
            val doc = firestore.collection(MEMORIES_COLLECTION)
                .document(placeMarkId)
                .get()
                .await()

            val memory = doc.toPlaceMark()
                ?: return Result.Error(MapError.PlaceNotFound)
            Result.Success(memory)
        } catch (_: Throwable) {
            Result.Error(MapError.Network)
        }
    }

    override suspend fun getAllMemories(
        userId: String
    ): Result<List<PlaceMark>, MapError> {
        return try {
            val snapshot = firestore.collection(MEMORIES_COLLECTION)
                .whereEqualTo("userId", userId)
                .get()
                .await()

            Result.Success(snapshot.documents.mapNotNull { it.toPlaceMark() })
        } catch (_: Throwable) {
            Result.Error(MapError.Network)
        }
    }

    private fun PlaceMark.toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "title" to title,
            "story" to description.orEmpty(),
            "placeName" to placeName,
            "latitude" to coordinates.latitude,
            "longitude" to coordinates.longitude,
            "photoURLs" to photos,
            "visitDate" to visitDate,
            "createdAt" to createdAt,
            "updatedAt" to System.currentTimeMillis(),
            "city" to city,
            "country" to country,
            "userId" to userId
        )
    }

    private fun DocumentSnapshot.toPlaceMark(): PlaceMark? {
        val latitude = getDouble("latitude") ?: return null
        val longitude = getDouble("longitude") ?: return null
        return PlaceMark(
            id = id,
            coordinates = GeoPoint(
                latitude = latitude,
                longitude = longitude
            ),
            title = getString("title").orEmpty(),
            description = getString("story"),
            photos = get("photoURLs") as? List<String> ?: emptyList(),
            createdAt = getLong("createdAt") ?: System.currentTimeMillis(),
            updatedAt = getLong("updatedAt"),
            placeName = getString("placeName"),
            visitDate = getLong("visitDate"),
            city = getString("city"),
            country = getString("country"),
            userId = getString("userId")
        )
    }
}