package ru.itis.android.travel_memorize_app.core.domain.usecase.memory

import ru.itis.android.travel_memorize_app.core.domain.model.map.PlaceMark
import ru.itis.android.travel_memorize_app.core.domain.repository.memory.MemoryRepository
import ru.itis.android.travel_memorize_app.core.domain.utils.MapError
import ru.itis.android.travel_memorize_app.core.domain.utils.Result
import javax.inject.Inject

class GetAllMemoriesUseCase @Inject constructor(
    private val repository: MemoryRepository
) {
    suspend operator fun invoke(userId: String): Result<List<PlaceMark>, MapError> =
        repository.getAllMemories(userId)
}