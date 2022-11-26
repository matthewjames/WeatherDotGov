package com.mattjamesdev.weatherdotgov.domain.model

data class LatLong(
    val lat: Double,
    val long: Double,
) {
    val isSet: Boolean
        get() = this != EMPTY

    override fun toString(): String = "$lat,$long"

    fun toPointForecast(): String = "${lat}°N ${long*-1}°W"

    companion object {
        val EMPTY = LatLong(
            lat = 0.0,
            long = 0.0
        )
    }
}