package com.mattjamesdev.weatherdotgov.domain.model

import com.google.android.gms.maps.model.LatLng

data class LatLong(
    val lat: Double,
    val long: Double,
) {
    val isSet: Boolean
        get() = this != EMPTY

    override fun toString(): String = "$lat,$long"

    fun toPointForecast(): String = "${lat}°N ${long * -1}°W"

    companion object {
        val EMPTY = LatLong(
            lat = 0.0,
            long = 0.0
        )
    }
}

fun LatLong.toLatLng(): LatLng = LatLng(
    this.lat,
    this.long
)

fun List<LatLong>.toLatLngList(): List<LatLng> = this.map { it.toLatLng() }