package ru.itis.android.travel_memorize_app.feature.memory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.itis.android.travel_memorize_app.core.domain.model.map.PlaceMark
import ru.itis.android.travel_memorize_app.core.domain.usecase.GetCurrentUserUseCase
import ru.itis.android.travel_memorize_app.core.domain.usecase.memory.GetAllMemoriesUseCase
import ru.itis.android.travel_memorize_app.core.domain.utils.MapError
import ru.itis.android.travel_memorize_app.core.domain.utils.Result
import javax.inject.Inject

enum class MemoriesLayoutMode {
    LIST,
    GRID
}

enum class MemoriesSortMode {
    NEWEST_FIRST,
    OLDEST_FIRST,
    TITLE_ASC,
    TITLE_DESC
}

data class MemoriesState(
    val memories: List<PlaceMark> = emptyList(),
    val layoutMode: MemoriesLayoutMode = MemoriesLayoutMode.LIST,
    val sortMode: MemoriesSortMode = MemoriesSortMode.NEWEST_FIRST,
    val isLoading: Boolean = false,
    val error: MapError? = null
) {
    val sortedMemories: List<PlaceMark>
        get() = when (sortMode) {
            MemoriesSortMode.NEWEST_FIRST -> memories.sortedByDescending { it.visitDate ?: it.createdAt }
            MemoriesSortMode.OLDEST_FIRST -> memories.sortedBy { it.visitDate ?: it.createdAt }
            MemoriesSortMode.TITLE_ASC -> memories.sortedBy { it.title.lowercase() }
            MemoriesSortMode.TITLE_DESC -> memories.sortedByDescending { it.title.lowercase() }
        }
}

sealed interface MemoriesEffect {
    data class ShowError(val error: MapError) : MemoriesEffect
}


class MemoriesViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getAllMemoriesUseCase: GetAllMemoriesUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(MemoriesState())
    val state: StateFlow<MemoriesState> = _state.asStateFlow()

    private val _effect = Channel<MemoriesEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        loadMemories()
    }

    fun loadMemories() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val user = (getCurrentUserUseCase() as? Result.Success)?.data
            if (user == null) {
                _state.update { it.copy(isLoading = false, error = MapError.Unknown) }
                _effect.send(MemoriesEffect.ShowError(MapError.Unknown))
                return@launch
            }

            when (val result = getAllMemoriesUseCase(user.uid)) {
                is Result.Success -> {
                    _state.update {
                        it.copy(
                            memories = result.data,
                            isLoading = false,
                            error = null
                        )
                    }
                }

                is Result.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.error
                        )
                    }
                    _effect.send(MemoriesEffect.ShowError(result.error))
                }
            }
        }
    }

    fun toggleLayoutMode() {
        _state.update {
            it.copy(
                layoutMode = when (it.layoutMode) {
                    MemoriesLayoutMode.LIST -> MemoriesLayoutMode.GRID
                    MemoriesLayoutMode.GRID -> MemoriesLayoutMode.LIST
                }
            )
        }
    }

    fun updateSortMode(sortMode: MemoriesSortMode) {
        _state.update { it.copy(sortMode = sortMode) }
    }
}