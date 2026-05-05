package ru.itis.android.travel_memorize_app.feature.map.viewmodel

import ru.itis.android.travel_memorize_app.core.domain.model.map.*
import ru.itis.android.travel_memorize_app.core.domain.utils.MapError

sealed interface MapEffect {
    data class NavigateToAddMemory(val selectedPoint: SelectedMapPoint) : MapEffect
    data class ShowError(val message: MapError) : MapEffect
    data class ShowSearchResults(val results: List<PlaceSearchResult>) : MapEffect
}