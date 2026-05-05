package ru.itis.android.travel_memorize_app.feature.memory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.itis.android.travel_memorize_app.core.domain.model.map.PlaceMark
import ru.itis.android.travel_memorize_app.core.domain.usecase.map.GetPlaceMarksUseCase
import javax.inject.Inject

data class MemorySearchState(
    val query: String = "",
    val memories: List<PlaceMark> = emptyList(),
    val filteredMemories: List<PlaceMark> = emptyList(),
    val isLoading: Boolean = false
)

class MemorySearchViewModel @Inject constructor(
    private val getPlaceMarksUseCase: GetPlaceMarksUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MemorySearchState(isLoading = true))
    val state: StateFlow<MemorySearchState> = _state.asStateFlow()

    init {
        loadMemories()
    }
    fun updateQuery(query: String) {
        _state.update {
            it.copy(
                query = query,
                filteredMemories = filterMemories(it.memories, query)
            )
        }
    }
    fun clearQuery() {
        updateQuery("")
    }
    private fun loadMemories() {
        viewModelScope.launch {
            getPlaceMarksUseCase().collect { memories ->
                _state.update {
                    it.copy(
                        memories = memories,
                        filteredMemories = filterMemories(memories, it.query),
                        isLoading = false
                    )
                }
            }
        }

    }

    private fun filterMemories(
        memories: List<PlaceMark>,
        query: String
    ): List<PlaceMark> {
        val normalized = query.trim().lowercase()
        if (normalized.isBlank()) return memories
        return memories.filter { memory ->
            listOfNotNull(
                memory.title,
                memory.placeName,
                memory.city,
                memory.country
            ).any { value ->
                value.lowercase().contains(normalized)
            }
        }


    }
}