package ru.itis.android.travel_memorize_app.core.network.dto

import com.google.gson.annotations.SerializedName

data class GeocodingResponse(
    @SerializedName("features") val features: List<FeatureDto>
)

data class FeatureDto(
    @SerializedName("id") val id: String,
    @SerializedName("text") val text: String,
    @SerializedName("center") val center: List<Double> // [longitude, latitude]
)
