package ru.itis.android.travel_memorize_app.feature.memory.ui

import androidx.annotation.StringRes
import ru.itis.android.travel_memorize_app.core.domain.utils.MapError
import ru.itis.android.travel_memorize_app.ui.R

@StringRes
fun MapError.toMemoryMessageRes(): Int {
    return when (this) {
        MapError.NoLocationPermission -> R.string.error_no_location_permission
        MapError.Network -> R.string.error_network
        MapError.PlaceNotFound -> R.string.error_place_not_found
        MapError.InvalidToken -> R.string.error_invalid_token
        MapError.RateLimited -> R.string.error_rate_limited
        MapError.EmptyQuery,
        MapError.Unknown -> R.string.error_unknown
    }
}