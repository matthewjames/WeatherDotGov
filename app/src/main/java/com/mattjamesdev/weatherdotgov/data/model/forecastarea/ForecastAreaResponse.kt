package com.mattjamesdev.weatherdotgov.data.model.forecastarea


import com.google.gson.annotations.SerializedName
import com.mattjamesdev.weatherdotgov.domain.model.ForecastAreaV2
import com.mattjamesdev.weatherdotgov.domain.model.LatLong

data class ForecastAreaResponse(
    @SerializedName("geometry")
    val geometry: Geometry?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("properties")
    val properties: Properties?,
    @SerializedName("type")
    val type: String?
)

fun ForecastAreaResponse.toDomain(): ForecastAreaV2 {
    return ForecastAreaV2(
        wfo = this.properties?.gridId.orEmpty(),
        gridX = this.properties?.gridX ?: 0,
        gridY = this.properties?.gridY ?: 0,
        zoneId = this.properties?.forecastZone?.substringAfter("https://api.weather.gov/zones/forecast/").orEmpty(),
        city = this.properties?.relativeLocation?.properties?.city.orEmpty(),
        state = this.properties?.relativeLocation?.properties?.state.orEmpty(),
        latLong = LatLong(
            lat = this.properties?.relativeLocation?.geometry?.coordinates?.get(1) ?: 0.0,
            long = this.properties?.relativeLocation?.geometry?.coordinates?.get(0) ?: 0.0
        )
    )
}