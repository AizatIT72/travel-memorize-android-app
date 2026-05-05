package ru.itis.android.travel_memorize_app.network.dto

import com.google.gson.annotations.SerializedName
import ru.itis.android.travel_memorize_app.core.domain.model.map.GeoPoint
import ru.itis.android.travel_memorize_app.core.domain.model.map.PlaceSearchResult
import ru.itis.android.travel_memorize_app.core.domain.model.map.SelectedMapPoint

data class MapboxGeocodingResponseDto(
    @SerializedName("features")
    val features: List<MapboxFeatureDto> = emptyList()
)

data class MapboxFeatureDto(
    @SerializedName("type")
    val type: String? = null,

    @SerializedName("properties")
    val properties: MapboxPropertiesDto? = null,

    @SerializedName("geometry")
    val geometry: MapboxGeometryDto? = null
)

data class MapboxPropertiesDto(
    @SerializedName("mapbox_id")
    val mapboxId: String? = null,

    @SerializedName("feature_type")
    val featureType: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("name_preferred")
    val namePreferred: String? = null,

    @SerializedName("full_address")
    val fullAddress: String? = null,

    @SerializedName("place_formatted")
    val placeFormatted: String? = null,

    @SerializedName("context")
    val context: MapboxContextDto? = null
)

data class MapboxContextDto(
    @SerializedName("country")
    val country: MapboxContextItemDto? = null,

    @SerializedName("region")
    val region: MapboxContextItemDto? = null,

    @SerializedName("place")
    val place: MapboxContextItemDto? = null,

    @SerializedName("district")
    val district: MapboxContextItemDto? = null,

    @SerializedName("locality")
    val locality: MapboxContextItemDto? = null,

    @SerializedName("neighborhood")
    val neighborhood: MapboxContextItemDto? = null,

    @SerializedName("postcode")
    val postcode: MapboxContextItemDto? = null
)

data class MapboxContextItemDto(
    @SerializedName("name")
    val name: String? = null,

    @SerializedName("name_preferred")
    val namePreferred: String? = null,

    @SerializedName("mapbox_id")
    val mapboxId: String? = null,

    @SerializedName("country_code")
    val countryCode: String? = null,

    @SerializedName("country_code_alpha_3")
    val countryCodeAlpha3: String? = null
)

data class MapboxGeometryDto(
    @SerializedName("coordinates")
    val coordinates: List<Double>? = null
)

fun MapboxGeocodingResponseDto.toPlaceSearchResults(): List<PlaceSearchResult> {
    return features.mapNotNull { feature ->
        val props = feature.properties ?: return@mapNotNull null
        val coords = feature.geometry?.coordinates ?: return@mapNotNull null
        if (coords.size < 2) return@mapNotNull null

        val id = props.mapboxId ?: return@mapNotNull null
        val name = props.namePreferred ?: props.name ?: return@mapNotNull null

        PlaceSearchResult(
            id = id,
            name = name,
            fullAddress = props.fullAddress
                ?: listOfNotNull(name, props.placeFormatted).joinToString(", "),
            point = GeoPoint(
                latitude = coords[1],
                longitude = coords[0]
            )
        )
    }
}

fun MapboxGeocodingResponseDto.toSelectedMapPoint(): SelectedMapPoint? {
    val feature = features.firstOrNull() ?: return null
    val props = feature.properties ?: return null
    val coords = feature.geometry?.coordinates ?: return null
    if (coords.size < 2) return null

    val name = props.namePreferred ?: props.name
    val placeName = props.fullAddress
        ?: listOfNotNull(name, props.placeFormatted).joinToString(", ")
            .takeIf { it.isNotBlank() }

    return SelectedMapPoint(
        coordinates = GeoPoint(
            latitude = coords[1],
            longitude = coords[0]
        ),
        placeName = placeName,
        city = props.context?.place?.name
            ?: props.context?.locality?.name
            ?: props.context?.district?.name,
        country = props.context?.country?.name
    )
}