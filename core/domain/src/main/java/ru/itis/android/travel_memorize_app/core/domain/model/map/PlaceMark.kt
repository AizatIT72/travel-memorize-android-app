package ru.itis.android.travel_memorize_app.core.domain.model.map

data class PlaceMark(
    val id: String,
    val coordinates: GeoPoint,
    val title: String,
    val description: String? = null,
    val photos: List<String> = emptyList(),
    val createdAt: Long,
    val updatedAt: Long? = null,
    val placeName: String? = null,
    val visitDate: Long? = null,
    val city: String? = null,
    val country: String? = null,
    val userId: String? = null
)