package ru.itis.android.travel_memorize_app.core.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.itis.android.travel_memorize_app.core.domain.model.PlaceMark

interface MapRepository {
    fun getPlaceMarks(): Flow<List<PlaceMark>>
    suspend fun addPlaceMark(placeMark: PlaceMark)
    suspend fun getPlaceName(latitude: Double, longitude: Double): String
    suspend fun searchPlace(query: String): Pair<Double, Double>?
}
