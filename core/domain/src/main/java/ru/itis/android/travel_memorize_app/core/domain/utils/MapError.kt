package ru.itis.android.travel_memorize_app.core.domain.utils

sealed interface MapError {
    data object EmptyQuery : MapError
    data object PlaceNotFound : MapError
    data object Network : MapError
    data object InvalidToken : MapError
    data object Unknown : MapError
    data object RateLimited : MapError
    data object NoLocationPermission : MapError
}