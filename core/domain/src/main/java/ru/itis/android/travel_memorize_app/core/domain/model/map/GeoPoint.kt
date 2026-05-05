package ru.itis.android.travel_memorize_app.core.domain.model.map

data class GeoPoint(
    val latitude: Double,
    val longitude: Double
)

data class SelectedMapPoint(
    val coordinates: GeoPoint,
    val placeName: String? = null,
    val city: String? = null,
    val country: String? = null
)

enum class MapMode {
    Browsing,
    SelectingPoint
}