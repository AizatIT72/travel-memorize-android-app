package ru.itis.android.travel_memorize_app.core.domain.usecase

import ru.itis.android.travel_memorize_app.core.domain.model.PlaceMark
import ru.itis.android.travel_memorize_app.core.domain.repository.MapRepository
import javax.inject.Inject

class AddPlaceMarkUseCase @Inject constructor(
    private val repository: MapRepository
) {
    suspend operator fun invoke(placeMark: PlaceMark) = repository.addPlaceMark(placeMark)
}
