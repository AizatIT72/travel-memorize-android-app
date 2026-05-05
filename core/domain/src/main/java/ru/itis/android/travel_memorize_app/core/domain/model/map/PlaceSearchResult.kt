package ru.itis.android.travel_memorize_app.core.domain.model.map

//результат поиска через query (forward geocoding)
data class PlaceSearchResult(
    val id: String,
    val name: String,
    val fullAddress: String,
    val point: GeoPoint
)
