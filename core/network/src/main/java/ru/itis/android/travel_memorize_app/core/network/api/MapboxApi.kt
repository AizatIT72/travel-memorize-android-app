package ru.itis.android.travel_memorize_app.core.network.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.itis.android.travel_memorize_app.core.network.dto.GeocodingResponse

interface MapboxApi {
    @GET("geocoding/v5/mapbox.places/{longitude},{latitude}.json")
    suspend fun reverseGeocode(
        @Path("longitude") longitude: Double,
        @Path("latitude") latitude: Double,
        @Query("access_token") accessToken: String,
        @Query("types") types: String = "address,place"
    ): GeocodingResponse

    @GET("geocoding/v5/mapbox.places/{query}.json")
    suspend fun forwardGeocode(
        @Path("query") query: String,
        @Query("access_token") accessToken: String,
        @Query("limit") limit: Int = 1
    ): GeocodingResponse
}
