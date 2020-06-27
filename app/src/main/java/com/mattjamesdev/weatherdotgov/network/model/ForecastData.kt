package com.mattjamesdev.weatherdotgov.network.model

data class ForecastData(
    val properties: PropertiesY
)

data class Period(
    val detailedForecast: String,
    val endTime: String,
    val icon: String,
    val isDaytime: Boolean,
    val name: String,
    val number: Int,
    val shortForecast: String,
    val startTime: String,
    val temperature: Int,
    val temperatureTrend: Any,
    val temperatureUnit: String,
    val windDirection: String,
    val windSpeed: String
)

data class PropertiesY(
    val forecastGenerator: String,
    val generatedAt: String,
    val periods: List<Period>,
    val units: String,
    val updateTime: String,
    val updated: String,
    val validTimes: String
)