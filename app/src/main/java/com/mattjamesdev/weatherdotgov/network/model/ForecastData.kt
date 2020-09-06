package com.mattjamesdev.weatherdotgov.network.model

class ForecastData(
    val properties: PropertiesY
)

data class PropertiesY(
    val forecastGenerator: String,
    val generatedAt: String,
    val periods: MutableList<Period>,
    val units: String,
    val updateTime: String,
    val updated: String,
    val validTimes: String
)

data class Period(
    val detailedForecast: String,
    val endTime: String,
    val icon: String,
    val isDaytime: Boolean,
    var name: String,
    val number: Int,
    val shortForecast: String,
    val startTime: String,
    val temperature: Int,
    val temperatureTrend: Any,
    val temperatureUnit: String,
    val windDirection: String,
    val windSpeed: String
)
