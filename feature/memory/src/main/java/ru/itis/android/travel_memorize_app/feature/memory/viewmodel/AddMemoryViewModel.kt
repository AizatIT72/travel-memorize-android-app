package ru.itis.android.travel_memorize_app.feature.memory.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.itis.android.travel_memorize_app.core.domain.model.map.GeoPoint
import ru.itis.android.travel_memorize_app.core.domain.model.map.PlaceMark
import ru.itis.android.travel_memorize_app.core.domain.usecase.GetCurrentUserUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.memory.AddMemoryUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.memory.UploadPhotoUseCase
import ru.itis.android.travel_memorize_app.core.domain.utils.MapError
import javax.inject.Inject
import ru.itis.android.travel_memorize_app.core.domain.utils.Result

sealed class AddMemoryEffect {
    object Finish : AddMemoryEffect()
    data class ShowError(val error: MapError) : AddMemoryEffect()
}

data class AddMemoryState(
    val photos: List<String> = emptyList(),
    val title: String = "",
    val description: String = "",
    val visitDate: Long = System.currentTimeMillis(),
    val isLoading: Boolean = false
)

class AddMemoryViewModel @Inject constructor(
    private val addMemoryUseCase: AddMemoryUseCase,
    private val uploadPhotoUseCase: UploadPhotoUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AddMemoryState())
    val state: StateFlow<AddMemoryState> = _state.asStateFlow()

    private val _effect = Channel<AddMemoryEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun updateTitle(title: String) {
        _state.update { it.copy(title = title) }
    }

    fun updateDescription(desc: String) {
        _state.update { it.copy(description = desc) }
    }
    fun updateVisitDate(date: Long) {
        _state.update { it.copy(visitDate = date) }
    }
    fun addPhoto(uri: Uri) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val user = (getCurrentUserUseCase() as? Result.Success)?.data
            if (user == null) {
                _state.update { it.copy(isLoading = false) }
                _effect.send(AddMemoryEffect.ShowError(MapError.NoLocationPermission))
                return@launch
            }
            when (val result = uploadPhotoUseCase(uri, user.uid)) {
                is Result.Success -> {
                    _state.update {
                        it.copy(
                            photos = it.photos + result.data,
                            isLoading = false
                        )
                    }
                }
                is Result.Error -> {
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(AddMemoryEffect.ShowError(result.error))
                }
            }
        }
    }
    fun removePhoto(url: String) {
        _state.update { it.copy(photos = it.photos - url) }
    }
    fun saveMemory(coordinates: GeoPoint, placeName: String? = null, city: String? = null, country: String? = null) {
        val s = _state.value
        if (s.title.isBlank() || s.photos.isEmpty() || s.isLoading) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val user = (getCurrentUserUseCase() as? Result.Success)?.data
            if (user == null) {
                _state.update { it.copy(isLoading = false) }
                _effect.send(AddMemoryEffect.ShowError(MapError.NoLocationPermission))
                return@launch
            }
            val placeMark = PlaceMark(
                id = System.currentTimeMillis().toString(),
                coordinates = coordinates,
                title = s.title,
                description = s.description,
                photos = s.photos,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                placeName = placeName,
                visitDate = s.visitDate,
                city = city,
                country = country,
                userId = user.uid
            )
            when (val result = addMemoryUseCase(placeMark)) {
                is Result.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(AddMemoryEffect.Finish)
                }
                is Result.Error -> {
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(AddMemoryEffect.ShowError(result.error))
                }
            }
        }
    }
}