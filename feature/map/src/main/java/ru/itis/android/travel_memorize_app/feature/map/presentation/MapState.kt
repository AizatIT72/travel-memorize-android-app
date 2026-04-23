package ru.itis.android.travel_memorize_app.feature.map.presentation

import ru.itis.android.travel_memorize_app.core.domain.model.PlaceMark

data class MapState(
    val placeMarks: List<PlaceMark> = emptyList(),
    val selectedLocation: PlaceMark? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
