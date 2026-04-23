package ru.itis.android.travel_memorize_app.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import ru.itis.android.travel_memorize_app.api.BuildConfigProvider
import ru.itis.android.travel_memorize_app.core.domain.model.PlaceMark
import ru.itis.android.travel_memorize_app.core.domain.repository.MapRepository
import ru.itis.android.travel_memorize_app.core.network.api.MapboxApi
import javax.inject.Inject

class MapRepositoryImpl @Inject constructor(
    private val mapboxApi: MapboxApi,
    private val buildConfigProvider: BuildConfigProvider
) : MapRepository {

    override fun getPlaceMarks(): Flow<List<PlaceMark>> = flowOf(emptyList())

    override suspend fun addPlaceMark(placeMark: PlaceMark) {}

    override suspend fun getPlaceName(latitude: Double, longitude: Double): String {
        return try {
            val response = mapboxApi.reverseGeocode(
                longitude = longitude,
                latitude = latitude,
                accessToken = buildConfigProvider.getApiKey()
            )
            response.features.firstOrNull()?.text ?: "Unknown"
        } catch (e: Exception) {
            "Unknown"
        }
    }

    override suspend fun searchPlace(query: String): Pair<Double, Double>? {
        return try {
            val response = mapboxApi.forwardGeocode(
                query = query,
                accessToken = buildConfigProvider.getApiKey()
            )
            val feature = response.features.firstOrNull() ?: return null
            val center = feature.center
            if (center.size >= 2) {
                Pair(center[1], center[0]) // [latitude, longitude]
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
