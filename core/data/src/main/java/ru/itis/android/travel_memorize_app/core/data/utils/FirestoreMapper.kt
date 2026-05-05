package ru.itis.android.travel_memorize_app.core.data.utils

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import ru.itis.android.travel_memorize_app.core.domain.model.map.GeoPoint
import ru.itis.android.travel_memorize_app.core.domain.model.map.PlaceMark
import javax.inject.Inject

class FirestoreMapper @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val MEMORIES_COLLECTION = "memories"

    suspend fun loadUserPlaceMarks(uid: String): List<PlaceMark> {
        val snapshot = firestore.collection("memories")
            .whereEqualTo("userId", uid)
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            val latitude = doc.getDouble("latitude") ?: return@mapNotNull null
            val longitude = doc.getDouble("longitude") ?: return@mapNotNull null

            PlaceMark(
                id = doc.id,
                coordinates = GeoPoint(latitude, longitude),
                title = doc.getString("title").orEmpty(),
                description = doc.getString("story"),
                photos = doc.get("photoURLs") as? List<String> ?: emptyList(),
                createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                updatedAt = doc.getLong("updatedAt"),
                placeName = doc.getString("placeName"),
                visitDate = doc.getLong("visitDate"),
                city = doc.getString("city"),
                country = doc.getString("country"),
                userId = doc.getString("userId")
            )
        }
    }

    suspend fun addPlaceMark(placeMark: PlaceMark, uid: String) {
        val data = mapOf(
            "userId" to uid,
            "latitude" to placeMark.coordinates.latitude,
            "longitude" to placeMark.coordinates.longitude,
            "title" to placeMark.title,
            "description" to placeMark.description,
            "photos" to placeMark.photos,
            "createdAt" to placeMark.createdAt,
            "updatedAt" to placeMark.updatedAt,
            "placeName" to placeMark.placeName,
            "visitDate" to placeMark.visitDate,
            "city" to placeMark.city,
            "country" to placeMark.country
        )

        firestore.collection(MEMORIES_COLLECTION)
            .document(placeMark.id)
            .set(data)
            .await()
    }
}