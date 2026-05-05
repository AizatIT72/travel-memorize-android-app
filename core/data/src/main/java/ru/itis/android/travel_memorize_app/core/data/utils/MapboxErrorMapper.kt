package ru.itis.android.travel_memorize_app.core.data.utils

import retrofit2.HttpException
import java.io.IOException
import ru.itis.android.travel_memorize_app.core.domain.utils.MapError

object MapboxErrorMapper {

    fun mapThrowable(throwable: Throwable): MapError {
        return when (throwable) {
            is IOException -> MapError.Network
            is HttpException -> mapHttpException(throwable)
            else -> MapError.Unknown
        }
    }
    private fun mapHttpException(http: HttpException): MapError {
        return when (http.code()) {
            401 -> MapError.InvalidToken
            404 -> MapError.PlaceNotFound
            429 -> MapError.RateLimited
            in 500..599 -> MapError.Network
            else -> MapError.Unknown
        }
    }
}