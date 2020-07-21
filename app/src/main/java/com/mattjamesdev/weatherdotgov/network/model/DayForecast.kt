package com.mattjamesdev.weatherdotgov.network.model

data class DayForecast(
    var date: String? = null,
    var hourly: List<Period>? = null,
    var high: Period? = null,
    var low: Period? = null,
    var tempUnit: String? = null
)