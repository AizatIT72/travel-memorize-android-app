package ru.itis.android.travel_memorize_app.core.domain.usecase.map

import ru.itis.android.travel_memorize_app.core.domain.model.map.GeoPoint
import ru.itis.android.travel_memorize_app.core.domain.model.map.SelectedMapPoint
import javax.inject.Inject
import ru.itis.android.travel_memorize_app.core.domain.repository.map.MapRepository
import ru.itis.android.travel_memorize_app.core.domain.utils.MapError
import ru.itis.android.travel_memorize_app.core.domain.utils.Result

class SelectPointUseCase @Inject constructor(
    private val repository: MapRepository
) {
    suspend operator fun invoke(
        point: GeoPoint,
        language: String
    ): Result<SelectedMapPoint, MapError> {
        return repository.getPlaceNameByCoordinates(point, language)
    }
}