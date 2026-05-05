package ru.itis.android.travel_memorize_app.core.domain.usecase.map

import ru.itis.android.travel_memorize_app.core.domain.model.map.PlaceMark
import ru.itis.android.travel_memorize_app.core.domain.repository.map.MapRepository
import ru.itis.android.travel_memorize_app.core.domain.utils.MapError
import ru.itis.android.travel_memorize_app.core.domain.utils.Result
import javax.inject.Inject

class AddPlaceMarkUseCase @Inject constructor(
    private val repository: MapRepository
) {
    suspend operator fun invoke(placeMark: PlaceMark): Result<Unit, MapError> =
        repository.addPlaceMark(placeMark)
}