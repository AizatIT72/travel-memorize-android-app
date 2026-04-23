package ru.itis.android.travel_memorize_app.feature.map.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.itis.android.travel_memorize_app.api.BuildConfigProvider
import ru.itis.android.travel_memorize_app.core.domain.model.PlaceMark
import ru.itis.android.travel_memorize_app.core.domain.repository.MapRepository
import java.util.UUID
import javax.inject.Inject
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class MapViewModel @AssistedInject constructor(
    private val mapRepository: MapRepository,
    private val buildConfigProvider: BuildConfigProvider
) : ViewModel() {

    fun getMapboxToken(): String = buildConfigProvider.getApiKey()

    @AssistedFactory
    interface Factory {
        fun create(): MapViewModel
    }

    private val _state = MutableStateFlow(MapState())
    val state: StateFlow<MapState> = _state.asStateFlow()

    fun onSearchPlace(query: String) {
        if (query.isBlank()) return
        
        viewModelScope.launch {
            try {
                val coords = mapRepository.searchPlace(query)
                if (coords != null) {
                    val newMark = PlaceMark(
                        id = UUID.randomUUID().toString(),
                        latitude = coords.first,
                        longitude = coords.second,
                        title = query
                    )
                    _state.update { it.copy(
                        selectedLocation = newMark,
                        placeMarks = it.placeMarks + newMark,
                        error = null
                    ) }
                } else {
                    _state.update { it.copy(error = "Место не найдено") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Ошибка поиска: ${e.message}") }
            }
        }
    }
}
