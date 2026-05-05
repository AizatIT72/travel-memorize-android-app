package ru.itis.android.travel_memorize_app.core.data.utils

import ru.itis.android.travel_memorize_app.core.domain.model.map.SelectedMapPoint
import ru.itis.android.travel_memorize_app.core.domain.model.map.GeoPoint
import ru.itis.android.travel_memorize_app.core.domain.model.map.PlaceSearchResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryCache @Inject constructor() {

    private val searchCache = mutableMapOf<String, List<PlaceSearchResult>>()
    private val reverseCache = mutableMapOf<GeoPoint, SelectedMapPoint>()
    fun getCachedSearch(query: String): List<PlaceSearchResult>? = searchCache[query]

    fun saveSearch(query: String, results: List<PlaceSearchResult>) {
        searchCache[query] = results
    }
    fun getCachedReverse(point: GeoPoint): SelectedMapPoint? = reverseCache[point]

    fun saveReverse(point: GeoPoint, result: SelectedMapPoint) {
        reverseCache[point] = result
    }
}