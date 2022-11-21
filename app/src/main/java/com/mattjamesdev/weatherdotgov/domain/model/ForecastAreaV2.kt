package com.mattjamesdev.weatherdotgov.domain.model

data class ForecastAreaV2(
    val wfo: String,
    val gridX: Int,
    val gridY: Int,
    val zoneId: String,
    val city: String,
    val state: String,
    val latLong: LatLong
    )
