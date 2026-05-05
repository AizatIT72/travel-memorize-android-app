package ru.itis.android.travel_memorize_app.map.ui

import ru.itis.android.travel_memorize_app.core.domain.utils.MapError

sealed class MapUiError {
    object NoLocationPermission : MapUiError()
    object Network : MapUiError()
    object PlaceNotFound : MapUiError()
    object InvalidToken : MapUiError()
    object RateLimited : MapUiError()
    object Unknown : MapUiError()
}


fun MapError.toUiError(): MapUiError {
    return when (this) {
        MapError.NoLocationPermission -> MapUiError.NoLocationPermission
        MapError.Network -> MapUiError.Network
        MapError.PlaceNotFound -> MapUiError.PlaceNotFound
        MapError.InvalidToken -> MapUiError.InvalidToken
        MapError.RateLimited -> MapUiError.RateLimited
        MapError.Unknown, MapError.EmptyQuery -> MapUiError.Unknown
    }
}