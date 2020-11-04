package com.mattjamesdev.weatherdotgov.model

data class ForecastArea(
    val properties: ForecastAreaProperties
)

data class ForecastAreaProperties(
    val county: String,
    val cwa: String,
    val fireWeatherZone: String,
    val forecast: String,
    val forecastGridData: String,
    val forecastHourly: String,
    val forecastOffice: String,
    val forecastZone: String,
    val gridId: String,
    val gridX: Int,
    val gridY: Int,
    val observationStations: String,
    val radarStation: String,
    val relativeLocation: RelativeLocation,
    val timeZone: String
)

data class RelativeLocation(
    val properties: RelativeLocationProperties,
    val geometry: PointGeometry,
    val type: String
)

data class PointGeometry(
    val coordinates: List<Double>
)

data class RelativeLocationProperties(
    val city: String,
    val state: String
)
