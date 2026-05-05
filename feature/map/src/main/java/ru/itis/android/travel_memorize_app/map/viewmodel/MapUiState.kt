package ru.itis.android.travel_memorize_app.map.viewmodel

import androidx.compose.runtime.Immutable
import ru.itis.android.travel_memorize_app.core.domain.model.map.PlaceSearchResult
import ru.itis.android.travel_memorize_app.core.domain.model.map.SelectedMapPoint
import ru.itis.android.travel_memorize_app.core.domain.model.map.MapMode
import ru.itis.android.travel_memorize_app.core.domain.model.map.PlaceMark
import ru.itis.android.travel_memorize_app.core.domain.utils.MapError
import ru.itis.android.travel_memorize_app.map.ui.MapUiError

@Immutable
data class MapUiState(
    val mapMode: MapMode = MapMode.Browsing,
    val selectedPoint: SelectedMapPoint? = null,
    val placeMarks: List<PlaceMark> = emptyList(),
    val searchResults: List<PlaceSearchResult> = emptyList(),
    val isLoading: Boolean = false,
    val error: MapError? = null
)