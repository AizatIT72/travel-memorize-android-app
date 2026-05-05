package ru.itis.android.travel_memorize_app.feature.memory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.itis.android.travel_memorize_app.core.domain.model.map.PlaceMark
import ru.itis.android.travel_memorize_app.core.domain.usecase.memory.DeleteMemoryUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.memory.GetMemoryUseCase
import ru.itis.android.travel_memorize_app.core.domain.utils.MapError
import ru.itis.android.travel_memorize_app.core.domain.utils.Result
import javax.inject.Inject

data class MemoryDetailsState(
    val memory: PlaceMark? = null,
    val isLoading: Boolean = false,
    val error: MapError? = null
)

sealed interface MemoryDetailsEffect {
    data class ShowError(val error: MapError) : MemoryDetailsEffect
    data object Deleted : MemoryDetailsEffect
}

class MemoryDetailsViewModel @Inject constructor(
    private val getMemoryUseCase: GetMemoryUseCase,
    private val deleteMemoryUseCase: DeleteMemoryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MemoryDetailsState())
    val state: StateFlow<MemoryDetailsState> = _state.asStateFlow()

    private val _effect = Channel<MemoryDetailsEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun loadMemory(memoryId: String) {
        if (memoryId.isBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = getMemoryUseCase(memoryId)) {
                is Result.Success -> {
                    _state.update {
                        it.copy(memory = result.data, isLoading = false)
                    }
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(isLoading = false, error = result.error)
                    }
                    _effect.send(MemoryDetailsEffect.ShowError(result.error))
                }
            }
        }
    }

    fun deleteMemory(memoryId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = deleteMemoryUseCase(memoryId)) {
                is Result.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(MemoryDetailsEffect.Deleted)
                }
                is Result.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.error) }
                    _effect.send(MemoryDetailsEffect.ShowError(result.error))
                }
            }
        }
    }
}