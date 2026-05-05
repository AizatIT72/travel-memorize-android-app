package ru.itis.android.travel_memorize_app.core.domain.usecase.memory

import ru.itis.android.travel_memorize_app.core.domain.repository.memory.MemoryRepository
import ru.itis.android.travel_memorize_app.core.domain.utils.MapError
import ru.itis.android.travel_memorize_app.core.domain.utils.Result
import javax.inject.Inject

class DeleteMemoryUseCase @Inject constructor(
    private val repository: MemoryRepository
) {
    suspend operator fun invoke(placeMarkId: String): Result<Unit, MapError> =
        repository.deleteMemory(placeMarkId)
}