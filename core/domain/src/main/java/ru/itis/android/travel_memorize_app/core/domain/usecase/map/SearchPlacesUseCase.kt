package ru.itis.android.travel_memorize_app.core.domain.usecase.map

import javax.inject.Inject
import ru.itis.android.travel_memorize_app.core.domain.model.map.PlaceSearchResult
import ru.itis.android.travel_memorize_app.core.domain.repository.map.MapRepository
import ru.itis.android.travel_memorize_app.core.domain.utils.MapError
import ru.itis.android.travel_memorize_app.core.domain.utils.Result

class SearchPlacesUseCase @Inject constructor(
    private val repository: MapRepository
) {
    suspend operator fun invoke(
        query: String,
        language: String,
        limit: Int = DEFAULT_LIMIT
    ): Result<List<PlaceSearchResult>, MapError> {
        if (query.isBlank()) return Result.Error(MapError.EmptyQuery)
        return repository.searchPlaces(query.trim(), language, limit)
    }

    private companion object {
        const val DEFAULT_LIMIT = 5
    }
}