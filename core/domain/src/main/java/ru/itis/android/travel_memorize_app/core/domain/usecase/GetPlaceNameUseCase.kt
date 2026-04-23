package ru.itis.android.travel_memorize_app.core.domain.usecase

import ru.itis.android.travel_memorize_app.core.domain.repository.MapRepository
import javax.inject.Inject

class GetPlaceNameUseCase @Inject constructor(
    private val repository: MapRepository
) {
    suspend operator fun invoke(latitude: Double, longitude: Double): String =
        repository.getPlaceName(latitude, longitude)
}
