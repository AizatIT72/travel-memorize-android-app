package ru.itis.android.travel_memorize_app.network.api

import retrofit2.http.GET
import retrofit2.http.Query
import ru.itis.android.travel_memorize_app.network.dto.MapboxGeocodingResponseDto

interface MapboxApi {

    @GET("search/geocode/v6/forward")
    suspend fun searchPlaces(
        @Query("q") query: String,
        @Query("language") language: String,
        @Query("limit") limit: Int,
    ): MapboxGeocodingResponseDto

    @GET("search/geocode/v6/reverse")
    suspend fun reverseGeocode(
        @Query("longitude") longitude: Double,
        @Query("latitude") latitude: Double,
        @Query("language") language: String,
        @Query("limit") limit: Int = 1,
    ): MapboxGeocodingResponseDto
}