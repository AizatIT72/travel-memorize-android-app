package ru.itis.android.travel_memorize_app.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.maps.CameraOptions
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.itis.android.travel_memorize_app.core.domain.model.map.*
import ru.itis.android.travel_memorize_app.core.domain.usecase.map.*
import ru.itis.android.travel_memorize_app.core.domain.utils.Result
import ru.itis.android.travel_memorize_app.feature.map.viewmodel.MapEffect
import javax.inject.Inject

class MapViewModel @Inject constructor(
    private val getPlaceMarksUseCase: GetPlaceMarksUseCase,
    private val addPlaceMarkUseCase: AddPlaceMarkUseCase,
    private val reverseGeocodeUseCase: ReverseGeocodeUseCase,
    private val searchPlacesUseCase: SearchPlacesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MapUiState())
    private val _cameraState = MutableStateFlow<CameraOptions?>(null)
    private var loadMarksJob: Job? = null
    val cameraState: StateFlow<CameraOptions?> = _cameraState.asStateFlow()
    val state: StateFlow<MapUiState> = _state.asStateFlow()
    private val _effect = Channel<MapEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()
    init {
        loadPlaceMarks()
    }

    fun updateCameraState(camera: CameraOptions) {
        _cameraState.value = camera
    }

    private fun loadPlaceMarks() {
        loadMarksJob?.cancel()
        loadMarksJob = viewModelScope.launch {
            getPlaceMarksUseCase().collect { marks ->
                _state.update { it.copy(placeMarks = marks) }
            }
        }
    }

    fun searchPlaces(query: String, language: String = "en", limit: Int = 5) {
        if (query.isBlank()) return
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            when (val result = searchPlacesUseCase(query, language, limit)) {
                is Result.Success -> {
                    _state.update {
                        it.copy(searchResults = result.data, isLoading = false)
                    }
                    _effect.send(MapEffect.ShowSearchResults(result.data))
                }
                is Result.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.error) }
                    _effect.send(MapEffect.ShowError(result.error))
                }
            }
        }
    }

    fun selectPoint(point: GeoPoint, language: String = "en") {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            when (val result = reverseGeocodeUseCase(point, language)) {
                is Result.Success -> {
                    _state.update {
                        it.copy(selectedPoint = result.data, isLoading = false)
                    }
                    _effect.send(MapEffect.NavigateToAddMemory(result.data))
                }
                is Result.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.error) }
                    _effect.send(MapEffect.ShowError(result.error))
                }
            }
        }
    }

    fun setMapMode(mode: MapMode) {
        _state.update { it.copy(mapMode = mode) }

    }

    fun addPlaceMark(placeMark: PlaceMark) {
        viewModelScope.launch {
            when (val result = addPlaceMarkUseCase(placeMark)) {
                is Result.Success -> loadPlaceMarks()
                is Result.Error -> _effect.send(MapEffect.ShowError(result.error))
            }
        }
    }

    fun clearSelectedPoint() {
        _state.update { it.copy(selectedPoint = null) }
    }

    fun refreshPlaceMarks() {
        loadPlaceMarks()
    }
}