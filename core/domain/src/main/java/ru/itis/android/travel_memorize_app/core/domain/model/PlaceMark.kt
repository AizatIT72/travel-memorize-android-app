package ru.itis.android.travel_memorize_app.core.domain.model

data class PlaceMark(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val title: String,
    val description: String = ""
)
