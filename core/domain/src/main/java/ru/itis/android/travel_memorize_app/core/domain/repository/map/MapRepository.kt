package ru.itis.android.travel_memorize_app.core.domain.repository.map

import kotlinx.coroutines.flow.Flow
import ru.itis.android.travel_memorize_app.core.domain.model.map.PlaceSearchResult
import ru.itis.android.travel_memorize_app.core.domain.model.map.GeoPoint
import ru.itis.android.travel_memorize_app.core.domain.model.map.PlaceMark
import ru.itis.android.travel_memorize_app.core.domain.model.map.SelectedMapPoint
import ru.itis.android.travel_memorize_app.core.domain.utils.MapError
import ru.itis.android.travel_memorize_app.core.domain.utils.Result

interface MapRepository {

    fun getUserPlaceMarks(): Flow<List<PlaceMark>>

    fun getCurrentUserPlaceMarks(): Flow<List<PlaceMark>>

    suspend fun addPlaceMark(placeMark: PlaceMark): Result<Unit, MapError>
    suspend fun searchPlaces(
        query: String,
        language: String,
        limit: Int
    ): Result<List<PlaceSearchResult>, MapError>

    suspend fun getPlaceNameByCoordinates(
        point: GeoPoint,
        language: String
    ): Result<SelectedMapPoint, MapError>
}