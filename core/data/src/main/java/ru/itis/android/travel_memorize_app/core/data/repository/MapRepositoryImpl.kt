package ru.itis.android.travel_memorize_app.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.itis.android.travel_memorize_app.core.data.utils.FirestoreMapper
import ru.itis.android.travel_memorize_app.core.data.utils.InMemoryCache
import ru.itis.android.travel_memorize_app.core.data.utils.MapboxErrorMapper
import ru.itis.android.travel_memorize_app.core.domain.model.map.*
import ru.itis.android.travel_memorize_app.core.domain.repository.AuthRepository
import ru.itis.android.travel_memorize_app.core.domain.repository.map.MapRepository
import ru.itis.android.travel_memorize_app.core.domain.utils.MapError
import ru.itis.android.travel_memorize_app.core.domain.utils.Result
import ru.itis.android.travel_memorize_app.network.api.MapboxApi
import ru.itis.android.travel_memorize_app.network.dto.toPlaceSearchResults
import ru.itis.android.travel_memorize_app.network.dto.toSelectedMapPoint
import javax.inject.Inject

class MapRepositoryImpl @Inject constructor(
    private val api: MapboxApi,
    private val firestoreMapper: FirestoreMapper,
    private val cache: InMemoryCache,
    private val authRepository: AuthRepository
) : MapRepository {

    private var placeMarksCache: List<PlaceMark> = emptyList()
    override fun getUserPlaceMarks(): Flow<List<PlaceMark>> = flow {
        emit(loadCurrentUserPlaceMarks())
    }
    override fun getCurrentUserPlaceMarks(): Flow<List<PlaceMark>> = flow {
        emit(loadCurrentUserPlaceMarks())
    }

    private suspend fun loadCurrentUserPlaceMarks(): List<PlaceMark> {
        val currentUser = authRepository.getCurrentUser()
        val uid = (currentUser as? Result.Success)?.data?.uid ?: return emptyList()
        return try {
            val remoteMarks = firestoreMapper.loadUserPlaceMarks(uid)
            placeMarksCache = remoteMarks
            remoteMarks
        } catch (_: Throwable) {
            placeMarksCache
        }
    }

    override suspend fun addPlaceMark(placeMark: PlaceMark): Result<Unit, MapError> {
        val currentUser = authRepository.getCurrentUser()
        val uid = (currentUser as? Result.Success)?.data?.uid
            ?: return Result.Error(MapError.NoLocationPermission)
        return try {
            firestoreMapper.addPlaceMark(placeMark, uid)
            placeMarksCache = placeMarksCache + placeMark
            Result.Success(Unit)
        } catch (_: Throwable) {
            Result.Error(MapError.Network)
        }
    }

    override suspend fun searchPlaces(
        query: String,
        language: String,
        limit: Int
    ): Result<List<PlaceSearchResult>, MapError> {
        if (query.isBlank()) return Result.Error(MapError.EmptyQuery)
        cache.getCachedSearch(query)?.let { return Result.Success(it) }
        return try {
            val response = api.searchPlaces(query = query, language = language, limit = limit)
            val results = response.toPlaceSearchResults()
            cache.saveSearch(query, results)
            Result.Success(results)
        } catch (t: Throwable) {
            Result.Error(MapboxErrorMapper.mapThrowable(t))
        }
    }

    override suspend fun getPlaceNameByCoordinates(
        point: GeoPoint,
        language: String
    ): Result<SelectedMapPoint, MapError> {
        cache.getCachedReverse(point)?.let { return Result.Success(it) }
        return try {
            val response = api.reverseGeocode(
                longitude = point.longitude,
                latitude = point.latitude,
                language = language
            )
            val result = response.toSelectedMapPoint()
                ?: return Result.Error(MapError.PlaceNotFound)
            cache.saveReverse(point, result)
            Result.Success(result)
        } catch (t: Throwable) {
            Result.Error(MapboxErrorMapper.mapThrowable(t))
        }
    }
}