package ru.itis.android.travel_memorize_app.feature.memory.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.itis.android.travel_memorize_app.core.domain.model.map.PlaceMark
import ru.itis.android.travel_memorize_app.core.domain.usecase.GetCurrentUserUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.memory.DeleteMemoryUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.memory.GetMemoryUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.memory.UpdateMemoryUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.memory.UploadPhotoUseCase
import ru.itis.android.travel_memorize_app.core.domain.utils.MapError
import ru.itis.android.travel_memorize_app.core.domain.utils.Result
import javax.inject.Inject

data class EditMemoryState(
    val memory: PlaceMark? = null,
    val photos: List<String> = emptyList(),
    val title: String = "",
    val description: String = "",
    val visitDate: Long = System.currentTimeMillis(),
    val isInitialLoading: Boolean = false,
    val isLoading: Boolean = false,
    val hasChanges: Boolean = false
)

sealed interface EditMemoryEffect {
    data object Saved : EditMemoryEffect
    data object Deleted : EditMemoryEffect
    data class ShowError(val error: MapError) : EditMemoryEffect
}

class EditMemoryViewModel @Inject constructor(
    private val getMemoryUseCase: GetMemoryUseCase,
    private val updateMemoryUseCase: UpdateMemoryUseCase,
    private val uploadPhotoUseCase: UploadPhotoUseCase,
    private val deleteMemoryUseCase: DeleteMemoryUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EditMemoryState())
    val state: StateFlow<EditMemoryState> = _state.asStateFlow()

    private val _effect = Channel<EditMemoryEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun loadMemory(memoryId: String) {
        if (memoryId.isBlank()) return
        if (_state.value.memory?.id == memoryId) return

        viewModelScope.launch {
            _state.update { it.copy(isInitialLoading = true) }

            when (val result = getMemoryUseCase(memoryId)) {
                is Result.Success -> {
                    val memory = result.data
                    _state.update {
                        it.copy(
                            memory = memory,
                            photos = memory.photos,
                            title = memory.title,
                            description = memory.description.orEmpty(),
                            visitDate = memory.visitDate ?: System.currentTimeMillis(),
                            isInitialLoading = false,
                            hasChanges = false
                        )
                    }
                }

                is Result.Error -> {
                    _state.update { it.copy(isInitialLoading = false) }
                    _effect.send(EditMemoryEffect.ShowError(result.error))
                }
            }
        }
    }

    fun updateTitle(value: String) {
        _state.update { it.copy(title = value) }
        updateChanges()
    }

    fun updateDescription(value: String) {
        _state.update { it.copy(description = value) }
        updateChanges()
    }

    fun updateVisitDate(value: Long) {
        _state.update { it.copy(visitDate = value) }
        updateChanges()
    }
    fun removePhoto(url: String) {
        _state.update { it.copy(photos = it.photos - url) }
        updateChanges()
    }
    fun addPhoto(uri: Uri) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val user = (getCurrentUserUseCase() as? Result.Success)?.data
            if (user == null) {
                _state.update { it.copy(isLoading = false) }
                _effect.send(EditMemoryEffect.ShowError(MapError.NoLocationPermission))
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
                    updateChanges()
                }
                is Result.Error -> {
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(EditMemoryEffect.ShowError(result.error))
                }
            }
        }
    }

    fun save() {
        val current = _state.value
        val memory = current.memory ?: return
        if (current.title.isBlank() || current.photos.isEmpty() || current.isLoading) return
        if (!current.hasChanges) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val updatedMemory = memory.copy(
                title = current.title.trim(),
                description = current.description.trim(),
                photos = current.photos,
                visitDate = current.visitDate,
                updatedAt = System.currentTimeMillis()
            )

            when (val result = updateMemoryUseCase(updatedMemory)) {
                is Result.Success -> {
                    _state.update { it.copy(isLoading = false, hasChanges = false) }
                    _effect.send(EditMemoryEffect.Saved)
                }

                is Result.Error -> {
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(EditMemoryEffect.ShowError(result.error))
                }
            }
        }
    }

    fun deleteMemory() {
        val memoryId = _state.value.memory?.id ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = deleteMemoryUseCase(memoryId)) {
                is Result.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(EditMemoryEffect.Deleted)
                }

                is Result.Error -> {
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(EditMemoryEffect.ShowError(result.error))
                }
            }
        }
    }

    private fun updateChanges() {
        _state.update { state ->
            state.copy(hasChanges = state.hasRealChanges())
        }
    }
    private fun EditMemoryState.hasRealChanges(): Boolean {
        val original = memory ?: return false
        return photos != original.photos ||
                title.trim() != original.title ||
                description.trim() != original.description.orEmpty() ||
                visitDate != (original.visitDate ?: visitDate)
    }
}