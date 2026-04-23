package ru.itis.android.travel_memorize_app.core.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.itis.android.travel_memorize_app.core.domain.model.PlaceMark
import ru.itis.android.travel_memorize_app.core.domain.repository.MapRepository
import javax.inject.Inject

class GetPlaceMarksUseCase @Inject constructor(
    private val repository: MapRepository
) {
    operator fun invoke(): Flow<List<PlaceMark>> = repository.getPlaceMarks()
}
